/**
 *
 */
package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.callbacks.FileSaveCallback;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.events.SaveFileEvent;
import org.iplantc.de.client.viewer.events.SaveFileEvent.SaveFileEventHandler;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.resources.client.messages.I18N;

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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sriram
 * 
 */
public class TextViewerImpl extends AbstractFileViewer implements EditingSupport {

    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);

    private final class SaveAsDialogHandlerImpl implements SelectHandler {
        private final SaveAsDialog saveDialog;

        private SaveAsDialogHandlerImpl(SaveAsDialog saveDialog) {
            this.saveDialog = saveDialog;
        }

        @Override
        public void onSelect(SelectEvent event) {
            if (saveDialog.isVaild()) {
                con.mask(I18N.DISPLAY.savingMask());
                String destination = saveDialog.getSelectedFolder().getPath() + "/"
                        + saveDialog.getFileName();
                ServicesInjector.INSTANCE.getFileEditorServiceFacade()
                                         .uploadTextAsFile(destination,
                                                           getEditorContent(jso),
                                                           true,
                                                           new FileSaveCallback(destination,
                                                                                true,
                                                                                con));
                saveDialog.hide();
            }
        }
    }

    private final class GetDataCallbackImpl implements AsyncCallback<String> {
        @Override
         public void onSuccess(String result) {
             data = JsonUtil.getString(JsonUtil.getObject(result),
                                       "chunk");
             setData(data);
             con.unmask();
         }

        @Override
         public void onFailure(Throwable caught) {
             ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.unableToRetrieveFileData(file.getName()),
                               caught);
             con.unmask();
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

    private final class SaveFileHandlerImpl implements SaveFileEventHandler {
        @Override
        public void onSave(SaveFileEvent event) {
            save();

        }
    }

    private final class PreviewSelectHandlerImpl implements SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            // do not support preview if content cannot be fit in one page.
            if(pagingToolbar.getToltalPages() > 1) {
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

    @UiTemplate("TextViewer.ui.xml")
    interface TextViewerUiBinder extends UiBinder<Widget, TextViewerImpl> {
    }

    private final Widget widget;

    @UiField
    SimpleContainer center;

    @UiField
    BorderLayoutContainer con;

    @UiField(provided = true)
    TextViewToolBar toolbar;

    @UiField(provided = true)
    ViewerPagingToolBar pagingToolbar;

    private long file_size;

    private int totalPages;

    private String data;

    protected boolean editing;

    private final Folder parentFolder;

    private Presenter presenter;

    protected JavaScriptObject jso;

    private final String mode;

    private final List<HandlerRegistration> eventHandlers = new ArrayList<HandlerRegistration>();

    static Logger LOG = Logger.getLogger("viewer");

    public TextViewerImpl(File file, String infoType, String mode, boolean editing, Folder parentFolder) {
        super(file, infoType);
        this.editing = editing;
        this.mode = mode;
        LOG.log(Level.SEVERE, "in viewer-->" + mode);
        this.parentFolder = parentFolder;
        toolbar = initToolBar();
        pagingToolbar = initPagingToolbar();

        if (mode != null && mode.equals("markdown")) {
            toolbar.addPreviewHandler(new PreviewSelectHandlerImpl());
        }
        widget = uiBinder.createAndBindUi(this);
        widget.addHandler(new SaveFileHandlerImpl(), SaveFileEvent.TYPE);

        addWrapHandler();
        addLineHumberHandler();

        if (file != null) {
            loadData();
        } else {
            // when u start editing a new file, data is empty but the new file
            // is yet to be saved.
            setData("");
        }

        center.addResizeHandler(new ResizeViewHandlerImpl());

    }

    TextViewToolBar initToolBar() {
        TextViewToolBar textViewPagingToolBar;
        if (mode != null && mode == "markdown") {
             textViewPagingToolBar = new TextViewToolBar(this, editing, true);
        } else {
             textViewPagingToolBar = new TextViewToolBar(this, editing, false);
        }
        return textViewPagingToolBar;
    }

    ViewerPagingToolBar initPagingToolbar() {
        return new ViewerPagingToolBar(this, getFileSize());
    }

    @Override
    public void cleanUp() {
        EventBus eventBus = EventBus.getInstance();
        for (HandlerRegistration hr : eventHandlers) {
            eventBus.removeHandler(hr);
        }
        file = null;
        jso = null;
        toolbar.cleanup();
        toolbar = null;
        pagingToolbar = null;
        data = null;
    }

    private void addWrapHandler() {
        toolbar.addWrapCbxChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                wrapText(jso, event.getValue());
            }
        });
    }

    private void addLineHumberHandler() {
        toolbar.addLineNumberCbxChangeHandleer(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                showLineNumbersInEditor(jso, event.getValue());
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

    @Override
    public void loadData() {
        String url = "read-chunk";
        JSONObject requestBody = getRequestBody();
        if (requestBody != null) {
            con.mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
            ServicesInjector.INSTANCE.getFileEditorServiceFacade()
                                     .getDataChunk(url, requestBody, new GetDataCallbackImpl());
        }

    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @Override
    public void setData(Object data) {
        boolean allowEditing = pagingToolbar.getToltalPages() == 1 && editing;
        if (jso == null) {
            clearDisplay();
            jso = displayData(this,
                              center.getElement(),
                              mode,
                              (String)data,
                              center.getElement().getOffsetWidth(),
                              center.getElement().getOffsetHeight(),
                              toolbar.isWrapText(),
                              allowEditing);
        } else {
            updateData(jso, (String)data);
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

    @Override
    public void setDirty(Boolean dirty) {
        if (presenter.isDirty() != dirty) {
            presenter.setVeiwDirtyState(dirty);
        }
    }

    public static native void updateData(JavaScriptObject jso, String val) /*-{
		jso.setValue(val);
    }-*/;

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
				name : "python",
				version : 3,
				singleLineStringErrors : false
			}
		}
		var myCodeMirror = $wnd.CodeMirror(textArea, {
			value : val,
			mode : editorMode,
			matchBrackets : true,
			autoCloseBrackets : true

		});
		myCodeMirror.setSize(width, height);
		myCodeMirror.setOption("readOnly", !editing);
		if (editing) {
			myCodeMirror
					.on(
							"change",
							$entry(function() {
								instance.@org.iplantc.de.client.viewer.views.TextViewerImpl::setDirty(Ljava/lang/Boolean;)(@java.lang.Boolean::TRUE);
							}));
		}
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

    public static native void showLineNumbersInEditor(JavaScriptObject jso, boolean show) /*-{
		jso.setOption("lineNumbers", show);
    }-*/;

    public static native void wrapText(JavaScriptObject jso, boolean wrap) /*-{
		jso.setOption("lineWrapping", wrap);
    }-*/;

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
            con.mask(I18N.DISPLAY.savingMask());
            ServicesInjector.INSTANCE.getFileEditorServiceFacade()
                                     .uploadTextAsFile(file.getPath(),
                                                       getEditorContent(jso),
                                                       false,
                                                       new FileSaveCallback(file.getPath(), false, con));
        }
    }

    @Override
    public boolean isDirty() {
        return isClean(jso);
    }

    @Override
    public void refresh() {
        loadData();

    }
}
