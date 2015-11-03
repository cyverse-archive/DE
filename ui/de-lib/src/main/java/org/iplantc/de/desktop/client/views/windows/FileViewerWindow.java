package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.commons.client.views.window.configs.PathListWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.fileViewers.client.events.DirtyStateChangedEvent;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class FileViewerWindow extends IplantWindowBase implements IsMaskable,
                                                                  DirtyStateChangedEvent.DirtyStateChangedEventHandler {
    private class CriticalPathCallback implements AsyncCallback<String> {
        @Override
        public void onFailure(Throwable caught) {
            FileViewerWindow.this.hide();
        }

        @Override
        public void onSuccess(String result) {
        }
    }

    protected File file;
    protected JSONObject manifest;
    Logger LOG = Logger.getLogger(FileViewerWindow.class.getName());
    private FileViewerWindowConfig configAB;
    private final IplantDisplayStrings displayStrings;
    private final FileViewer.Presenter presenter;

    @Inject
    FileViewerWindow(final IplantDisplayStrings displayStrings,
                     final FileViewer.Presenter presenter) {
        this.displayStrings = displayStrings;

        this.presenter = presenter;
        this.presenter.addDirtyStateChangedEventHandler(this);

        setSize("800", "480");

    }

    @Override
    public <C extends WindowConfig> void show(C windowConfig, String tag,
                                              boolean isMaximizable) {

        final FileViewerWindowConfig fileViewerWindowConfig = (FileViewerWindowConfig) windowConfig;
        this.configAB = fileViewerWindowConfig;
        this.file = configAB.getFile();
        if (file != null) {
            setHeadingText(file.getName());
            presenter.go(this,
                         file,
                         configAB.getParentFolder(),
                         fileViewerWindowConfig.isEditing(),
                         fileViewerWindowConfig.isVizTabFirst(),
                         new CriticalPathCallback());
        } else {
            String title = "Untitled-" + Math.random();
            setHeadingText(title);
            boolean isTabularFile = windowConfig instanceof TabularFileViewerWindowConfig;
            boolean isPathListFile = windowConfig instanceof PathListWindowConfig;
            String delimiter = isTabularFile ? ((TabularFileViewerWindowConfig) windowConfig).getSeparator() : "";
            Integer columns = isTabularFile ? ((TabularFileViewerWindowConfig) windowConfig).getColumns() : null;
            presenter.newFileGo(this,
                                title,
                                fileViewerWindowConfig.getContentType(),
                                fileViewerWindowConfig.getParentFolder(),
                                fileViewerWindowConfig.isEditing(),
                                fileViewerWindowConfig.isVizTabFirst(),
                                isTabularFile,
                                isPathListFile,
                                columns,
                                delimiter);
        }

        super.show(windowConfig, tag, isMaximizable);
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
                        cmb.hide();
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

    @Override
    public void onEditorDirtyStateChanged(DirtyStateChangedEvent event) {
        if (event.isDirty()) {
            setHeadingHtml(getHeader().getHTML()
                    + "<span style='color:red; vertical-align: super'> * </span>");
        } else {
            setHeadingText(presenter.getTitle());
        }
    }

}
