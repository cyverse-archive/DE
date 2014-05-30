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

import com.google.common.base.Strings;
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
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram
 * 
 */
public class TextViewerImpl extends AbstractFileViewer implements EditingSupport {

    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);

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

    private final List<HandlerRegistration> eventHandlers = new ArrayList<HandlerRegistration>();

    public TextViewerImpl(File file, String infoType, boolean editing, Folder parentFolder) {
        super(file, infoType);
        this.editing = editing;
        this.parentFolder = parentFolder;
        toolbar = initToolBar();
        pagingToolbar = initPagingToolbar();
        widget = uiBinder.createAndBindUi(this);
        widget.addHandler(new SaveFileEventHandler() {

            @Override
            public void onSave(SaveFileEvent event) {
                save();

            }
        }, SaveFileEvent.TYPE);

        addWrapHandler();

        if (file != null) {
            loadData();
        } else {
            // when u start editing a new file, data is empty but the new file
            // is yet to be saved.
            setData("");
        }

        center.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                if (jso != null) {
                    resizeDisplay(jso, center.getElement().getOffsetWidth(), center.getElement().getOffsetHeight());
                }
            }
        });

    }

    TextViewToolBar initToolBar() {
        TextViewToolBar textViewPagingToolBar = new TextViewToolBar(this, editing);
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
                if (isDirty() || Strings.isNullOrEmpty(data)) {
                    setData(getEditorContent(jso));
                } else {
                    setData(data);
                }
            }
        });
    }

    private JSONObject getRequestBody() {
        if (file == null) {
            return null;
        }
        JSONObject obj = new JSONObject();
        obj.put("path", new JSONString(file.getId()));
        // position starts at 0
        obj.put("position", new JSONString("" + pagingToolbar.getPageSize() * (pagingToolbar.getPageNumber() - 1)));
        obj.put("chunk-size", new JSONString("" + pagingToolbar.getPageSize()));
        return obj;
    }

    @Override
    public void loadData() {
        String url = "read-chunk";
        JSONObject requestBody = getRequestBody();
        if (requestBody != null) {
            con.mask(org.iplantc.de.resources.client.messages.I18N.DISPLAY.loadingMask());
            ServicesInjector.INSTANCE.getFileEditorServiceFacade().getDataChunk(url, requestBody, new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    data = JsonUtil.getString(JsonUtil.getObject(result), "chunk");
                    setData(data);
                    con.unmask();
                }

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(org.iplantc.de.resources.client.messages.I18N.ERROR.unableToRetrieveFileData(file.getName()), caught);
                    con.unmask();
                }
            });
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
            jso = displayData(this, center.getElement(), infoType, (String)data, center.getElement().getOffsetWidth(), center.getElement().getOffsetHeight(), toolbar.isWrapText(), allowEditing);
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
        presenter.setVeiwDirtyState(dirty);
    }
    
    public static native void updateData(JavaScriptObject jso, String val) /*-{
		jso.setValue(val);
    }-*/;

    public static native JavaScriptObject displayData(final TextViewerImpl instance, XElement textArea, String mode, String val, int width, int height, boolean wrap, boolean editing) /*-{
		var myCodeMirror = $wnd.CodeMirror(textArea, {
			value : val,
			mode : mode
		});
		myCodeMirror.setOption("lineWrapping", wrap);
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

    @Override
    public void save() {
        if (file == null) {
            final SaveAsDialog saveDialog = new SaveAsDialog(parentFolder);
            saveDialog.addOkButtonSelectHandler(new SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    if (saveDialog.isVaild()) {
                        con.mask(I18N.DISPLAY.savingMask());
                        String destination = saveDialog.getSelectedFolder().getPath() + "/" + saveDialog.getFileName();
                        ServicesInjector.INSTANCE.getFileEditorServiceFacade().uploadTextAsFile(destination, getEditorContent(jso), true, new FileSaveCallback(destination, true, con));
                        saveDialog.hide();
                    }
                }
            });
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
            ServicesInjector.INSTANCE.getFileEditorServiceFacade().uploadTextAsFile(file.getPath(), getEditorContent(jso), false, new FileSaveCallback(file.getPath(), false, con));
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
