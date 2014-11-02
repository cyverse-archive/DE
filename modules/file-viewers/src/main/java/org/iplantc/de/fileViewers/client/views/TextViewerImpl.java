package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.fileViewers.client.events.LineNumberCheckboxChangeEvent;
import org.iplantc.de.fileViewers.client.events.ViewerPagingToolbarUpdatedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
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
 * @author sriram, jstroot
 */
public class TextViewerImpl extends AbstractFileViewer implements ViewerPagingToolbarUpdatedEvent.ViewerPagingToolbarUpdatedEventHandler {

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
            resizeDisplay(jso,
                          center.getElement().getOffsetWidth(),
                          center.getElement().getOffsetHeight());
        }
    }

    @UiTemplate("TextViewer.ui.xml")
    interface TextViewerUiBinder extends UiBinder<Widget, TextViewerImpl> { }

    protected boolean editing;
    protected JavaScriptObject jso;
    static Logger LOG = Logger.getLogger(TextViewerImpl.class.getName());
    @UiField
    SimpleContainer center;
    @UiField
    BorderLayoutContainer con;
    @UiField
    ViewerPagingToolBar pagingToolbar;
    @UiField
    TextViewToolBar toolbar;
    private static TextViewerUiBinder uiBinder = GWT.create(TextViewerUiBinder.class);
    private final String mode;
    private final FileViewer.Presenter presenter;
    private final Widget widget;
    private boolean dirty;

    public TextViewerImpl(final File file,
                          final String infoType,
                          final String mode,
                          final boolean editing,
                          final FileViewer.Presenter presenter) {
        super(file, infoType);
        this.editing = editing;
        this.mode = mode;
        this.presenter = presenter;
        LOG.log(Level.INFO, "in viewer-->" + mode);

        widget = uiBinder.createAndBindUi(this);

        if (file == null) {
            /* when u start editing a new file, data is empty but the new file
             * is yet to be saved. */
            setData("");
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
    public void fireEvent(GwtEvent<?> event) {
        con.fireEvent(event);
    }

    @Override
    public String getEditorContent() {
        return getEditorContent(jso);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    void setDirty(Boolean dirty) {
        this.dirty = dirty;
        if (presenter.isDirty() != dirty) {
            presenter.setViewDirtyState(dirty, this);
        }
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);
    }

    @Override
    public void onViewerPagingToolbarUpdated(ViewerPagingToolbarUpdatedEvent event) {
        // TODO Update presenter to load data instead
        presenter.loadTextData(event.getPageNumber(), event.getPageSize());
    }

    @Override
    public void refresh() {
        presenter.loadTextData(pagingToolbar.getPageNumber(), (int) pagingToolbar.getPageSize());
    }

    @Override
    public void setData(Object data) {
        if(data instanceof StructuredText){
            return;
        }
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
        dirty = false;
    }

    @Override
    public void unmask() {
        con.unmask();
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
            textViewPagingToolBar = new TextViewToolBar(this, editing, true);
            textViewPagingToolBar.addPreviewHandler(new PreviewSelectHandlerImpl());
        } else {
            textViewPagingToolBar = new TextViewToolBar(this, editing, false);
        }
        textViewPagingToolBar.addRefreshHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                presenter.loadTextData(pagingToolbar.getPageNumber(), (int) pagingToolbar.getPageSize());
            }
        });
        textViewPagingToolBar.addSaveHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                presenter.saveFile(TextViewerImpl.this);
            }
        });

        textViewPagingToolBar.addWrapCbxChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                wrapText(jso, event.getValue());
            }
        });
        textViewPagingToolBar.addLineNumberCheckboxChangeHandler(new LineNumberCheckboxChangeEvent.LineNumberCheckboxChangeEventHandler() {
            @Override
            public void onLineNumberCheckboxChange(LineNumberCheckboxChangeEvent event) {
                showLineNumbersInEditor(jso, event.getValue());
            }
        });

        return textViewPagingToolBar;
    }

}
