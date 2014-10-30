package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.fileViewers.client.callbacks.FileSaveCallback;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sriram
 */
public class TextViewerImpl extends AbstractFileViewer implements FileViewer.EditingSupport {

    private final class GetDataCallbackImpl implements AsyncCallback<String> {
        private final Component maskable;
        private final String fileName;
        private final IplantErrorStrings errorStrings;

        public GetDataCallbackImpl(final Component maskable,
                                   final String fileName,
                                   final IplantErrorStrings errorStrings) {
            this.maskable = maskable;
            this.fileName = fileName;
            this.errorStrings = errorStrings;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(errorStrings.unableToRetrieveFileData(fileName),
                              caught);
            maskable.unmask();
        }

        @Override
        public void onSuccess(String result) {
            String data = JsonUtil.getString(JsonUtil.getObject(result),
                                             "chunk");
            setData(data);
            maskable.unmask();
        }
    }

    private final class PreviewSelectHandlerImpl implements SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            // do not support preview if content cannot be fit in one page.
            if (pagingToolbar.getTotalPages() > 1) {
                AlertMessageBox amb = new AlertMessageBox("Preview",
                                                          "Unable to generate preview. Please adjust page size to fit  file contents in 1 page and try again!");
                amb.show();
                return;
            }
            Window d = new Window();
            File fileObj = TextViewerImpl.this.file;
            if (fileObj != null) {
                d.setHeadingHtml(fileObj.getName());
            } else {
                d.setHeadingHtml("Preview");
            }
            d.setSize("600", "500");
            MarkDownRendererViewImpl renderer = new MarkDownRendererViewImpl(fileObj,
                                                                             TextViewerImpl.this.infoType,
                                                                             getEditorContent(jso));
            d.add(renderer.asWidget());
            d.show();
        }
    }

    private final class ResizeViewHandlerImpl implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            if (jso == null) {
                return;
            }
            resizeDisplay(jso,
                          center.getElement().getOffsetWidth(),
                          center.getElement().getOffsetHeight());
        }
    }

    @UiTemplate("TextViewer.ui.xml")
    interface TextViewerUiBinder extends UiBinder<Widget, TextViewerImpl> { }

    static Logger LOG = Logger.getLogger(TextViewerImpl.class.getName());
    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);

    @UiField
    SimpleContainer center;
    @UiField
    BorderLayoutContainer con;
    @UiField
    ViewerPagingToolBar pagingToolbar;
    @UiField
    TextViewToolBar toolbar;

    protected boolean editing;
    protected JavaScriptObject jso;
    private final IplantDisplayStrings displayStrings;
    private final FileEditorServiceFacade fileEditorService;
    private final String mode;
    private final Folder parentFolder;
    private final FileViewer.Presenter presenter;
    private final Widget widget;
    private IplantErrorStrings errorStrings;

    public TextViewerImpl(final File file,
                          final String infoType,
                          final String mode,
                          final boolean editing,
                          final Folder parentFolder,
                          final FileViewer.Presenter presenter,
                          final IplantDisplayStrings displayStrings,
                          final IplantErrorStrings errorStrings,
                          final FileEditorServiceFacade fileEditorService) {
        super(file, infoType);
        this.editing = editing;
        this.mode = mode;
        this.parentFolder = parentFolder;
        this.presenter = presenter;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.fileEditorService = fileEditorService;
        LOG.log(Level.INFO, "in viewer-->" + mode);

        widget = uiBinder.createAndBindUi(this);

        if (mode != null && mode.equals("markdown")) {
            toolbar.addPreviewHandler(new PreviewSelectHandlerImpl());
        }

        addWrapHandler();
        addLineNumberHandler();

        if (file != null) {
            loadData();
        } else {
            /* when u start editing a new file, data is empty but the new file
             * is yet to be saved. */
            setData("");
        }

        center.addResizeHandler(new ResizeViewHandlerImpl());
    }

    public static native JavaScriptObject displayData(final TextViewerImpl instance,
                                                      XElement textArea,
                                                      String editorMode,
                                                      String val,
                                                      int width,
                                                      int height,
                                                      boolean wrap,
                                                      boolean editing) /*-{

        if (editorMode == "python") {
            editorMode = {
                name: "python",
                version: 3,
                singleLineStringErrors: false
            }
        }
        var myCodeMirror = $wnd.CodeMirror(textArea, {
            value: val,
            mode: editorMode,
            matchBrackets: true,
            autoCloseBrackets: true

        });
        myCodeMirror.setSize(width, height);
        myCodeMirror.setOption("readOnly", !editing);
        myCodeMirror
            .on(
            "change",
            $entry(function () {
                instance.@org.iplantc.de.fileViewers.client.views.TextViewerImpl::setDirty(Ljava/lang/Boolean;)(@java.lang.Boolean::TRUE);
            }));
        return myCodeMirror;
    }-*/;

    public static native String getEditorContent(JavaScriptObject jso) /*-{
        return jso.getValue();
    }-*/;

    public static native boolean isClean(JavaScriptObject jso) /*-{
        return jso.isClean();
    }-*/;

    public static native void resizeDisplay(JavaScriptObject jso, int width, int height) /*-{
        jso.setSize(width, height);
    }-*/;

    public static native void setEditing(JavaScriptObject jso, boolean editing) /*-{
        jso.setOption("readOnly", !editing);
    }-*/;

    public static native void showLineNumbersInEditor(JavaScriptObject jso, boolean show) /*-{
        jso.setOption("lineNumbers", show);
    }-*/;

    public static native void updateData(JavaScriptObject jso, String val) /*-{
        jso.setValue(val);
    }-*/;

    public static native void wrapText(JavaScriptObject jso, boolean wrap) /*-{
        jso.setOption("lineWrapping", wrap);
    }-*/;

    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEvent.FileSavedEventHandler handler) {
        return con.addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public boolean isDirty() {
        return isClean(jso);
    }

    @Override
    public void setDirty(Boolean dirty) {
        if (presenter.isDirty() != dirty) {
            presenter.setViewDirtyState(dirty);
        }
    }

    @Override
    public void loadData() {
        if (file == null) {
            return;
        }
        con.mask(displayStrings.loadingMask());
        long chunkSize = pagingToolbar.getPageSize();
        long chunkPosition = chunkSize * (pagingToolbar.getPageNumber() - 1);
        fileEditorService.readChunk(file, chunkPosition, chunkSize, new GetDataCallbackImpl(con, file.getName(), errorStrings));
    }

    @Override
    public void refresh() {
        loadData();
    }

    @Override
    public void save() {
        if (file == null) {
            final SaveAsDialog saveDialog = new SaveAsDialog(parentFolder);
            SaveAsDialogOkSelectHandler okSelectHandler = new SaveAsDialogOkSelectHandler(con,
                                                                                          saveDialog,
                                                                                          displayStrings.savingMask(),
                                                                                          getEditorContent(jso),
                                                                                          fileEditorService);
            SaveAsDialogCancelSelectHandler cancelSelectHandler = new SaveAsDialogCancelSelectHandler(con,
                                                                                                      saveDialog);
            saveDialog.addOkButtonSelectHandler(okSelectHandler);
            saveDialog.addCancelButtonSelectHandler(cancelSelectHandler);
            saveDialog.show();
            saveDialog.toFront();
        } else {
            con.mask(displayStrings.savingMask());
            fileEditorService.uploadTextAsFile(file.getPath(),
                                               getEditorContent(jso),
                                               false,
                                               new FileSaveCallback(file.getPath(),
                                                                    false,
                                                                    con));
        }
    }

    @Override
    public void setData(Object data) {
        boolean allowEditing = pagingToolbar.getTotalPages() == 1 && editing;
        if (jso == null) {
            clearDisplay();
            jso = displayData(this,
                              center.getElement(),
                              mode,
                              (String) data,
                              center.getElement().getOffsetWidth(),
                              center.getElement().getOffsetHeight(),
                              toolbar.isWrapText(),
                              allowEditing);
        } else {
            updateData(jso, (String) data);
            setEditing(jso, allowEditing);
            setDirty(false);
        }
        toolbar.setEditing(allowEditing);

        /**
         * XXX - SS - support editing for files with only one page
         */
    }

    protected void clearDisplay() {
        center.getElement().removeChildren();
        center.forceLayout();
    }

    @UiFactory
    ViewerPagingToolBar initPagingToolbar() {
        return new ViewerPagingToolBar(this, getFileSize());
    }

    @UiFactory
    TextViewToolBar initToolBar() {

        TextViewToolBar textViewPagingToolBar;
        if (mode != null && mode.equals("markdown")) {
            textViewPagingToolBar = new TextViewToolBar(this, editing, true);
        } else {
            textViewPagingToolBar = new TextViewToolBar(this, editing, false);
        }
        return textViewPagingToolBar;
    }

    private void addLineNumberHandler() {
        toolbar.addLineNumberCbxChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                showLineNumbersInEditor(jso, event.getValue());
            }

        });
    }

    private void addWrapHandler() {
        toolbar.addWrapCbxChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                wrapText(jso, event.getValue());
            }
        });
    }
}
