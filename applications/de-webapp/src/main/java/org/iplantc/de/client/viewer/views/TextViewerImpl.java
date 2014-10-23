package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.callbacks.FileSaveCallback;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.XElement;
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
public class TextViewerImpl extends AbstractFileViewer implements EditingSupport {

    private final class GetDataCallbackImpl implements AsyncCallback<String> {
        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.unableToRetrieveFileData(file.getName()),
                              caught);
            con.unmask();
        }

        @Override
        public void onSuccess(String result) {
            data = JsonUtil.getString(JsonUtil.getObject(result),
                                      "chunk");
            setData(data);
            con.unmask();
        }
    }

    private final class PreviewSelectHandlerImpl implements SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            // do not support preview if content cannot be fit in one page.
            if (pagingToolbar.getToltalPages() > 1) {
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
            if (jso != null) {
                resizeDisplay(jso, center.getElement().getOffsetWidth(), center.getElement()
                                                                               .getOffsetHeight());
            }
        }
    }

    private final class SaveAsDialogHandlerImpl implements SelectHandler {
        private final SaveAsDialog saveDialog;

        private SaveAsDialogHandlerImpl(SaveAsDialog saveDialog) {
            this.saveDialog = saveDialog;
        }

        @Override
        public void onSelect(SelectEvent event) {
            if (saveDialog.isVaild()) {
                con.mask(displayStrings.savingMask());
                String destination = saveDialog.getSelectedFolder().getPath() + "/"
                                         + saveDialog.getFileName();
                fileEditorService.uploadTextAsFile(destination,
                                                   getEditorContent(jso),
                                                    true,
                                                    new FileSaveCallback(destination,
                                                                         true,
                                                                         con));
                saveDialog.hide();
            }
        }
    }

    @UiTemplate("TextViewer.ui.xml")
    interface TextViewerUiBinder extends UiBinder<Widget, TextViewerImpl> { }

    static Logger LOG = Logger.getLogger(TextViewerImpl.class.getName());

    @UiField
    SimpleContainer center;
    @UiField
    BorderLayoutContainer con;
    @UiField(provided = true)
    ViewerPagingToolBar pagingToolbar;
    @UiField(provided = true)
    TextViewToolBar toolbar;

    protected boolean editing;
    protected JavaScriptObject jso;
    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);
    private final IplantDisplayStrings displayStrings;
    private final FileEditorServiceFacade fileEditorService;
    private final String mode;
    private final Folder parentFolder;
    private final FileViewer.Presenter presenter;
    private final Widget widget;
    private String data;
    private long file_size;
    private int totalPages;

    public TextViewerImpl(File file,
                          final String infoType,
                          final String mode,
                          final boolean editing,
                          final Folder parentFolder,
                          final FileViewer.Presenter presenter) {
        super(file, infoType);
        this.editing = editing;
        this.mode = mode;
        this.parentFolder = parentFolder;
        this.presenter = presenter;
        fileEditorService = ServicesInjector.INSTANCE.getFileEditorServiceFacade();
        displayStrings = I18N.DISPLAY;
        LOG.log(Level.INFO, "in viewer-->" + mode);
        toolbar = initToolBar();
        pagingToolbar = initPagingToolbar();

        if (mode != null && mode.equals("markdown")) {
            toolbar.addPreviewHandler(new PreviewSelectHandlerImpl());
        }
        widget = uiBinder.createAndBindUi(this);

        addWrapHandler();
        addLineNumberHandler();

        if (file != null) {
            loadData();
        } else {
            // when u start editing a new file, data is empty but the new file
            // is yet to be saved.
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
                instance.@org.iplantc.de.client.viewer.views.TextViewerImpl::setDirty(Ljava/lang/Boolean;)(@java.lang.Boolean::TRUE);
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
        String url = "read-chunk";
        JSONObject requestBody = getRequestBody();
        if (requestBody != null) {
            con.mask(displayStrings.loadingMask());
            fileEditorService.getDataChunk(url, requestBody, new GetDataCallbackImpl());
        }

    }

    @Override
    public void refresh() {
        loadData();
    }

    @Override
    public void save() {
        if (file == null) {
            final SaveAsDialog saveDialog = new SaveAsDialog(parentFolder);
            saveDialog.addOkButtonSelectHandler(new SaveAsDialogHandlerImpl(saveDialog));
            saveDialog.addCancelButtonSelectHandler(new SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    saveDialog.hide();
                    con.unmask();
                }
            });
            saveDialog.show();
            saveDialog.toFront();
        } else {
            con.mask(displayStrings.savingMask());
            fileEditorService.uploadTextAsFile(file.getPath(),
                                               getEditorContent(jso),
                                                false,
                                                new FileSaveCallback(file.getPath(), false, con));
        }
    }

    @Override
    public void setData(Object data) {
        boolean allowEditing = pagingToolbar.getToltalPages() == 1 && editing;
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

    ViewerPagingToolBar initPagingToolbar() {
        return new ViewerPagingToolBar(this, getFileSize());
    }

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

    private JSONObject getRequestBody() {
        if (file == null) {
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(file.getPath()));
        // position starts at 0
        obj.put("position",
                new JSONString("" + pagingToolbar.getPageSize() * (pagingToolbar.getPageNumber() - 1)));
        obj.put("chunk-size", new JSONString("" + pagingToolbar.getPageSize()));
        return obj;
    }
}
