package org.iplantc.de.client.windows;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.viewer.events.DirtyStateChangedEvent;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 * 
 */
public class FileViewerWindow extends IplantWindowBase implements IsMaskable,
                                                                  DirtyStateChangedEvent.DirtyStateChangedEventHandler {
    private class CriticalPathCallback implements AsyncCallback<String> {
        @Override
        public void onFailure(Throwable caught) {
            FileViewerWindow.this.hide();
        }

        @Override
        public void onSuccess(String result) { }
    }

    private final IplantDisplayStrings displayStrings;

    protected JSONObject manifest;
    protected File file;
    private final FileViewerWindowConfig configAB;
    private FileViewer.Presenter presenter;

    Logger LOG = Logger.getLogger(FileViewerWindow.class.getName());

    public FileViewerWindow(final FileViewerWindowConfig config,
                            final IplantDisplayStrings displayStrings,
                            final FileViewer.Presenter presenter) {
        super(null, config);
        this.configAB = config;
        this.displayStrings = displayStrings;

        this.file = configAB.getFile();

        this.presenter = presenter;
        this.presenter.addDirtyStateChangedEventHandler(this);

        setSize("800", "480");

        if(file != null){
            setHeadingText(file.getName());
            presenter.go(this,
                         file,
                         configAB.getParentFolder(),
                         config.isEditing(),
                         config.isVizTabFirst(),
                         new CriticalPathCallback());
        } else {
            String title = "Untitled-" + Math.random();
            setHeadingText(title);
            boolean isTabularFile = config instanceof TabularFileViewerWindowConfig;
            String delimiter = isTabularFile ? ((TabularFileViewerWindowConfig)config).getSeparator() : "";
            Integer columns = isTabularFile ? ((TabularFileViewerWindowConfig)config).getColumns() : null;
            presenter.newFileGo(this,
                                title,
                                config.getContentType(),
                                config.getParentFolder(),
                                config.isEditing(),
                                config.isVizTabFirst(),
                                isTabularFile,
                                columns,
                                delimiter);
        }
    }

    @Override
    public void onEditorDirtyStateChanged(DirtyStateChangedEvent event) {
        if(event.isDirty()){
            setHeadingText(getHeader().getText() + "<span style='color:red; vertical-align: super'> * </span>");
        } else {
            setHeadingText(presenter.getTitle());
        }
    }

    @Override
    public void doHide() {
        if (presenter.isDirty() && configAB.isEditing()) {
            final MessageBox cmb = new MessageBox(displayStrings.save(), displayStrings.unsavedChanges());
            cmb.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        presenter.saveFile();
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
    public WindowState getWindowState() {
        return createWindowState(configAB);
    }

}
