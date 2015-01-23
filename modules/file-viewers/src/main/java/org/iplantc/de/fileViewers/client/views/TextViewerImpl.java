package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.events.LineNumberCheckboxChangeEvent;
import org.iplantc.de.fileViewers.client.events.RefreshSelectedEvent;
import org.iplantc.de.fileViewers.client.events.SaveSelectedEvent;
import org.iplantc.de.fileViewers.client.events.ViewerPagingToolbarUpdatedEvent;
import org.iplantc.de.fileViewers.client.events.WrapTextCheckboxChangeEvent;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class TextViewerImpl extends AbstractFileViewer implements
                                                      ViewerPagingToolbarUpdatedEvent.ViewerPagingToolbarUpdatedEventHandler {

    public interface TextViewerAppearance {
        String markdownPreviewWindowHeader();

        String markdownPreviewWindowWidth();

        String markdownPreviewWindowHeight();

        String unsupportedPreviewAlertTitle();

        String unsupportedPreviewAlertMsg();
    }

    private final class MarkdownPreviewSelectHandlerImpl implements SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            // do not support preview if content cannot be fit in one page.
            if (pagingToolbar.getTotalPages() > 1) {
                AlertMessageBox amb = new AlertMessageBox(appearance.unsupportedPreviewAlertTitle(),
                                                          appearance.unsupportedPreviewAlertMsg());
                amb.show();
                return;
            }
            Window d = new Window();
            File fileObj = TextViewerImpl.this.file;
            if (fileObj != null) {
                d.setHeadingHtml(fileObj.getName());
            } else {
                d.setHeadingHtml(appearance.markdownPreviewWindowHeader());
            }
            d.setSize(appearance.markdownPreviewWindowWidth(),
                      appearance.markdownPreviewWindowHeight());
            MarkDownRendererViewImpl renderer = new MarkDownRendererViewImpl(fileObj,
                                                                             TextViewerImpl.this.infoType,
                                                                             getEditorContent(jso),
                                                                             presenter);
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
            resizeDisplay(jso, center.getElement().getOffsetWidth(), center.getElement()
                                                                           .getOffsetHeight());
        }
    }

    @UiTemplate("TextViewer.ui.xml")
    interface TextViewerUiBinder extends UiBinder<Widget, TextViewerImpl> { }

    @UiField SimpleContainer center;
    @UiField BorderLayoutContainer con;
    @UiField ViewerPagingToolBar pagingToolbar;
    @UiField TextViewToolBar toolbar;

    protected boolean editing;
    protected JavaScriptObject jso;
    static Logger LOG = Logger.getLogger(TextViewerImpl.class.getName());
    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);
    private final String mode;
    private final FileViewer.Presenter presenter;
    private boolean dirty;
    private TextViewerAppearance appearance = GWT.create(TextViewerAppearance.class);

    public TextViewerImpl(final File file,
                          final String infoType,
                          final String mode,
                          final boolean editing,
                          final FileViewer.Presenter presenter) {
        super(file, infoType);
        this.editing = editing;
        this.mode = mode;
        this.presenter = presenter;
        LOG.fine("in viewer-->" + mode);

        initWidget(uiBinder.createAndBindUi(this));

        if (file == null) {
            /*
             * when u start editing a new file, data is empty but the new file is yet to be saved.
             */
            // FIXME Presenter should be performing this initialization
            setData("");
        } else {
            presenter.loadTextData(pagingToolbar.getPageNumber(), pagingToolbar.getPageSize());
        }

        center.addResizeHandler(new ResizeViewHandlerImpl());
        pagingToolbar.addViewerPagingToolbarUpdatedEventHandler(this);
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
		myCodeMirror
				.on(
						"change",
						$entry(function() {
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
        return addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getEditorContent() {
        return getEditorContent(jso);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void refresh() {
        presenter.loadTextData(pagingToolbar.getPageNumber(), pagingToolbar.getPageSize());
    }

    @Override
    public void onViewerPagingToolbarUpdated(ViewerPagingToolbarUpdatedEvent event) {
        presenter.loadTextData(event.getPageNumber(), event.getPageSize());
    }

    @Override
    public void setData(Object data) {
        if (data instanceof StructuredText) {
            return;
        }
        boolean allowEditing = pagingToolbar.getTotalPages() == 1 && editing;
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
            setEditing(jso, allowEditing);
            setDirty(false);
        }
        toolbar.setEditing(allowEditing);

        /**
         * XXX - SS - support editing for files with only one page
         */
        dirty = false;
    }

    protected void clearDisplay() {
        center.getElement().removeChildren();
        center.forceLayout();
    }

    @UiFactory
    ViewerPagingToolBar initPagingToolbar() {
        return new ViewerPagingToolBar(getFileSize());
    }

    @UiFactory
    TextViewToolBar initToolBar() {

        TextViewToolBar textViewPagingToolBar;
        if (mode != null && mode.equals("markdown")) {
            textViewPagingToolBar = new TextViewToolBar(editing, true);
            textViewPagingToolBar.addPreviewHandler(new MarkdownPreviewSelectHandlerImpl());
        } else {
            textViewPagingToolBar = new TextViewToolBar(editing, false);
        }

        return textViewPagingToolBar;
    }

    @UiHandler("toolbar")
    void onLineNumberCheckboxChanged(LineNumberCheckboxChangeEvent event) {
        showLineNumbersInEditor(jso, event.getValue());
    }

    @UiHandler("toolbar")
    void onRefreshClick(RefreshSelectedEvent event) {
        presenter.loadTextData(pagingToolbar.getPageNumber(), pagingToolbar.getPageSize());
    }

    @UiHandler("toolbar")
    void onSaveClick(SaveSelectedEvent event) {
        if(Strings.isNullOrEmpty(getEditorContent())){
            toolbar.setSaveEnabled(false);
            return;
        }
        presenter.saveFile(TextViewerImpl.this);
    }

    @UiHandler("toolbar")
    void onWrapCheckboxChanged(WrapTextCheckboxChangeEvent event) {
        wrapText(jso, event.getValue());
    }

    void setDirty(Boolean dirty) {
        this.dirty = dirty;
        if (presenter.isDirty() != dirty) {
            presenter.setViewDirtyState(dirty, this);
        }
        if(dirty){
            toolbar.setSaveEnabled(true);
        }
    }

}
