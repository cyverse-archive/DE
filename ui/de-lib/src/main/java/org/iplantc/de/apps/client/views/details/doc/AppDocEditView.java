package org.iplantc.de.apps.client.views.details.doc;

import org.iplantc.de.apps.client.events.selection.SaveMarkdownSelected;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * FIXME This class could use it's own presenter
 * FIXME Extract appearance
 *         That might alleviate the chaining of save selected events back to the details presenter
 * @author jstroot
 */
public class AppDocEditView extends Composite implements IsMaskable,
                                                         SaveMarkdownSelected.HasSaveMarkdownSelectedHandlers {

    @UiTemplate("AppDocEditView.ui.xml")
    interface AppDocEditViewUiBinder extends UiBinder<Widget, AppDocEditView> { }

    private static final AppDocEditViewUiBinder uiBinder = GWT.create(AppDocEditViewUiBinder.class);

    @UiField TextButton saveBtn;
    @UiField SimpleContainer panel;
    @UiField VerticalLayoutContainer con;
    @UiField ToolBar toolbar;

    protected JavaScriptObject jso;
    private final App app;

    private boolean dirty;

    public AppDocEditView(final App app,
                          final AppDoc appDoc) {
        this.app = app;
        initWidget(uiBinder.createAndBindUi(this));
        String safeDoc = SafeHtmlUtils.fromString(appDoc.getDocumentation()).asString();

        setData(safeDoc);
    }

    @Override
    public void mask(String message) {
        panel.mask(message);
    }

    @Override
    public void unmask() {
        panel.unmask();
    }

    @Override
    public HandlerRegistration addSaveMarkdownSelectedHandler(SaveMarkdownSelected.SaveMarkdownSelectedHandler handler) {
        return addHandler(handler, SaveMarkdownSelected.TYPE);
    }

    @UiHandler("panel")
    void onResize(ResizeEvent event) {
             if (jso == null) {
                return;
            }
            resizeDisplay(jso,
                          panel.getElement().getOffsetWidth(),
                          panel.getElement().getOffsetHeight());
    }

    @UiHandler("saveBtn")
    void onSaveSelected(SelectEvent event) {
        String editorContent = getEditorContent(jso);
        fireEvent(new SaveMarkdownSelected(app, editorContent, this));
        mask();
    }

    private String getJsonFormat(String doc) {
        JSONObject json = new JSONObject();
        json.put("documentation", new JSONString(doc));
        return json.toString();
    }

    public void setData(String data) {
        jso = displayData(this,
                              panel.getElement(),
                              "markdown",
                              data,
                              panel.getElement().getOffsetWidth(),
                              panel.getElement().getOffsetHeight());

        dirty = false;
    }
    
    private final class ResizeViewHandlerImpl implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            if (jso == null) {
                return;
            }
            resizeDisplay(jso, panel.getElement().getOffsetWidth(), panel.getElement()
                                                                           .getOffsetHeight());
        }
    }

    public static native void resizeDisplay(JavaScriptObject jso, int width, int height) /*-{
		jso.setSize(width, height);
    }-*/;

    public String getDoc() {
        return getEditorContent(jso);
    }

    public static native JavaScriptObject displayData(final AppDocEditView instance,
                                                      XElement textArea,
                                                      String editorMode,
                                                      String val,
                                                      int width,
                                                      int height) /*-{

		var myCodeMirror = $wnd.CodeMirror(textArea, {
			value : val,
			mode : editorMode,
			matchBrackets : true,
			autoCloseBrackets : true
		});
		myCodeMirror.setSize(width, height);
		myCodeMirror
				.on(
						"change",
						$entry(function() {
							instance.@org.iplantc.de.apps.client.views.details.doc.AppDocEditView::setDirty(Ljava/lang/Boolean;)(@java.lang.Boolean::TRUE);
						}));
		return myCodeMirror;
    }-*/;

    public static native String getEditorContent(JavaScriptObject jso) /*-{
		return jso.getValue();
    }-*/;


    void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

}
