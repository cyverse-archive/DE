package org.iplantc.de.client.windows;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.events.FileSavedEvent.FileSavedEventHandler;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorGetManifest;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.events.SaveFileEvent;
import org.iplantc.de.client.viewer.presenter.FileViewerPresenter;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

import java.util.logging.Logger;

/**
 * @author sriram
 * 
 */
public class FileViewerWindow extends IplantWindowBase implements IsMaskable, FileSavedEventHandler {

    private static final class GetManifestCallback implements AsyncCallback<String> {

        private final IplantErrorStrings errorStrings;
        private FileViewerWindowConfig config;
        private Window window;
        private JSONObject manifest;

        /**
         * @param config the window config with a non-null file.
         * @param window the parent window.
         * @param errorStrings strings for possible errors.
         */
        GetManifestCallback(final FileViewerWindowConfig config,
                            final Window window,
                            final IplantErrorStrings errorStrings){

            this.config = config;
            this.window = window;
            this.errorStrings = errorStrings;
        }

        @Override
         public void onSuccess(String result) {
             if (result != null) {
                 manifest = JsonUtil.getObject(result);
                 FileViewerPresenter presenter = new FileViewerPresenter(config.getFile(),
                                                     manifest,
                                                     config.isEditing(),
                                                     config.isVizTabFirst());
                 presenter.go(window,
                              config.getParentFolder());
                 presenter.setTitle(window.getHeader().getText());
                 window.unmask();
             } else {
                 onFailure(null);
             }
         }

        @Override
         public void onFailure(Throwable caught) {
             window.unmask();
             DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
             String message = caught.getMessage();
             window.hide();

             if (JsonUtils.safeToEval(message)) {
                 AutoBean<ErrorGetManifest> errorBean = AutoBeanCodex.decode(factory,
                                                                             ErrorGetManifest.class,
                                                                             message);
                 ErrorHandler.post(errorBean.as(), caught);
             } else {
                 ErrorHandler.post(errorStrings.retrieveStatFailed(),
                                   caught);
             }
         }
    }

    private final IplantDisplayStrings displayStrings;
    private final FileEditorServiceFacade fileEditorService;
    private final Scheduler scheduler;
    private IplantErrorStrings errorStrings;

    private PlainTabPanel tabPanel;
    protected JSONObject manifest;
    protected File file;
    private final FileViewerWindowConfig configAB;
    private FileViewer.Presenter presenter;

    Logger LOG = Logger.getLogger(FileViewerWindow.class.getName());

    public FileViewerWindow(final FileViewerWindowConfig config,
                            final IplantDisplayStrings displayStrings,
                            final IplantErrorStrings errorStrings,
                            final FileEditorServiceFacade fileEditorService,
                            final Scheduler scheduler) {
        super(null, config);
        this.configAB = config;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.fileEditorService = fileEditorService;
        this.scheduler = scheduler;

        this.file = configAB.getFile();
        tabPanel = new PlainTabPanel();
        setSize("800", "480");
        add(tabPanel);

        if (file != null) {
            setHeadingText(file.getName());
        } else {
            setHeadingText("Untitled-" + Math.random());
        }

        getFileManifest();
    }

    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (file == null) {
            // If it was a new file, re-initialize tabs
            file = event.getFile();
            tabPanel.clear();
            tabPanel = null;
            getFileManifest();
            setHeadingText(file.getName());
            presenter.setTitle(file.getName());
        }
        if (presenter != null) {
            presenter.setViewDirtyState(false);
        }
    }

    @Override
    public void doHide() {
        if (presenter != null
                && presenter.isDirty()
                && configAB.isEditing()) {
            final MessageBox cmb = new MessageBox(displayStrings.save(), displayStrings.unsavedChanges());
            cmb.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        SaveFileEvent sfe = new SaveFileEvent();
                        tabPanel.getActiveWidget().fireEvent(sfe);
                    } else if (PredefinedButton.NO.equals(event.getHideButton())) {
                        FileViewerWindow.super.doHide();
                    }

                }
            });
            cmb.show();
        } else {
            super.doHide();
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
        mask(displayStrings.loadingMask());
        if (file != null) {
            fileEditorService.getManifest(file.getPath(), new GetManifestCallback(configAB, this, errorStrings));
        } else {
            if (configAB.isEditing()) {
                JSONObject manifest = new JSONObject();
                if (configAB.getContentType() != null) {
                    manifest.put("content-type", new JSONString(configAB.getContentType().toString()));
                }

                if (configAB instanceof TabularFileViewerWindowConfig) {
                    processTabularFileEditingConfig(manifest, (TabularFileViewerWindowConfig)configAB);
                }
                FileViewerPresenter presenter = new FileViewerPresenter(file,
                                                    manifest,
                                                    configAB.isEditing(),
                                                    configAB.isVizTabFirst());
                presenter.go(FileViewerWindow.this, configAB.getParentFolder());
                presenter.setTitle(getHeader().getText());
                unmask();
            }
        }
    }

    private void processTabularFileEditingConfig(JSONObject manifest,
                                                 final TabularFileViewerWindowConfig config) {
        if (config.getSeparator().equals(",")) {
            manifest.put("info-type", new JSONString("csv"));
        } else if (config.getSeparator().equals("\t")) {
            manifest.put("info-type", new JSONString("tsv"));
        }
        manifest.put(FileViewer.COLUMNS_KEY, new JSONNumber(config.getColumns()));
    }

}
