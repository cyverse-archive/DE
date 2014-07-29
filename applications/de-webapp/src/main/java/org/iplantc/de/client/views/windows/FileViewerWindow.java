package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.FileEditorWindowClosedEvent;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.events.FileSavedEvent.FileSavedEventHandler;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorGetManifest;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.events.EditNewTabFileEvent;
import org.iplantc.de.client.viewer.events.SaveFileEvent;
import org.iplantc.de.client.viewer.presenter.FileViewerPresenter;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

import java.util.logging.Logger;

/**
 * @author sriram
 * 
 */
public class FileViewerWindow extends IplantWindowBase implements IsMaskable {

    private final class GetManifestCallback implements AsyncCallback<String> {
        @Override
         public void onSuccess(String result) {
             if (result != null) {
                 manifest = JsonUtil.getObject(result);
                 presenter = new FileViewerPresenter(file,
                                                     manifest,
                                                     configAB.isEditing(),
                                                     configAB.isVizTabFirst());
                 initWidget();
                 presenter.go(FileViewerWindow.this,
                              configAB.getParentFolder());
                 presenter.setTitle(getTitle());
                 unmask();
             } else {
                 onFailure(null);
             }
         }

        @Override
         public void onFailure(Throwable caught) {
             unmask();
             DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
             String message = caught.getMessage();
             FileViewerWindow.this.hide();

             if (JsonUtils.safeToEval(message)) {
                 AutoBean<ErrorGetManifest> errorBean = AutoBeanCodex.decode(factory,
                                                                             ErrorGetManifest.class,
                                                                             message);
                 ErrorHandler.post(errorBean.as(), caught);
             } else {
                 ErrorHandler.post(I18N.ERROR.retrieveStatFailed(),
                                   caught);
             }
         }
    }

    private PlainTabPanel tabPanel;
    protected JSONObject manifest;
    protected File file;
    private final FileViewerWindowConfig configAB;
    private final EventBus eventBus;
    private FileViewer.Presenter presenter;

    Logger LOG = Logger.getLogger("Viewer");

    public FileViewerWindow(FileViewerWindowConfig config, EventBus eventBus) {
        super(null, null);
        this.configAB = config;
        this.eventBus = eventBus;
        eventBus.addHandler(FileSavedEvent.TYPE, new FileSavedEventHandler() {

            @Override
            public void onFileSaved(FileSavedEvent event) {
                if (file == null) {
                    file = event.getFile();
                    tabPanel = null;
                    presenter.cleanUp();
                    getFileManifest();
                    setTitle(file.getName());
                    presenter.setTitle(file.getName());
                }
                if (presenter != null) {
                    presenter.setVeiwDirtyState(false);
                }
            }

        });
        init();
    }

    private void init() {
        setSize("800", "480");
        this.file = configAB.getFile();
        if (file != null) {
            setTitle(file.getName());
        } else {
            setTitle("Untitled-" + Math.random());
        }
        getFileManifest();

    }

    private void initWidget() {
        if (tabPanel == null) {
            tabPanel = new PlainTabPanel();
            add(tabPanel);
            forceLayout();
        }
    }

    /**
     * Returns an array from the manifest for a given key, or null if no array exists under that key.
     * 
     * @param key
     * @return
     */
    protected JSONValue getItems(String key) {
        return (key != null && manifest != null && manifest.containsKey(key)) ? manifest.get(key) : null;
    }

    @Override
    public void doHide() {
        if (presenter != null && presenter.isDirty() && configAB.isEditing()) {
            final MessageBox cmb = new MessageBox(I18N.DISPLAY.save(), I18N.DISPLAY.unsavedChanges());
            cmb.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        SaveFileEvent sfe = new SaveFileEvent();
                        tabPanel.getActiveWidget().fireEvent(sfe);
                    } else if (PredefinedButton.NO.equals(event.getHideButton())) {
                        presenter.cleanUp();
                        FileViewerWindow.super.doHide();
                        doClose();
                    }

                }
            });
            cmb.show();
        } else {
            presenter.cleanUp();
            super.doHide();
            doClose();
        }
    }

    private void doClose() {
        if (file != null) {
            FileEditorWindowClosedEvent event = new FileEditorWindowClosedEvent(file.getId());
            eventBus.fireEvent(event);
        }
    }

    @Override
    public PlainTabPanel getWidget() {
        return tabPanel;
    }

    @Override
    public WindowState getWindowState() {
        return createWindowState(configAB);
    }

    private void getFileManifest() {
        mask(I18N.DISPLAY.loadingMask());
        if (file != null) {
            ServicesInjector.INSTANCE.getFileEditorServiceFacade()
                                     .getManifest(file.getPath(), new GetManifestCallback());
        } else {
            if (configAB.isEditing()) {
                JSONObject manifest = new JSONObject();
                if (configAB.getContentType() != null) {
                    manifest.put("content-type", new JSONString(configAB.getContentType().toString()));
                }

                if (configAB instanceof TabularFileViewerWindowConfig) {
                    processTabularFileEditingConfig(manifest);
                }
                presenter = new FileViewerPresenter(file,
                                                    manifest,
                                                    configAB.isEditing(),
                                                    configAB.isVizTabFirst());
                initWidget();
                presenter.go(FileViewerWindow.this, configAB.getParentFolder());
                presenter.setTitle(getTitle());
                unmask();
            }
        }
    }

    private void processTabularFileEditingConfig(JSONObject manifest) {
        if (((TabularFileViewerWindowConfig)configAB).getSeparator().equals(",")) {
            manifest.put("info-type", new JSONString("csv"));
        } else if (((TabularFileViewerWindowConfig)configAB).getSeparator().equals("\t")) {
            manifest.put("info-type", new JSONString("tsv"));
        }

        Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
                eventBus.fireEvent(new EditNewTabFileEvent(((TabularFileViewerWindowConfig)configAB).getColumns(),
                                                           ((TabularFileViewerWindowConfig)configAB).getSeparator()));
            }
        });
    }

}
