package org.iplantc.de.apps.client.views;

import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AppDocEditView extends Composite {

    private final class SaveButtonSelectHandlerImpl implements SelectHandler {
        private final String appId;
        private final AppUserServiceFacade appUserService;
        private final Command saveCallbck;

        private SaveButtonSelectHandlerImpl(String appId,
                                            AppUserServiceFacade appUserService,
                                            Command saveCallbck) {
            this.appId = appId;
            this.appUserService = appUserService;
            this.saveCallbck = saveCallbck;
        }

        @Override
        public void onSelect(SelectEvent event) {
            String doc = getEditorContent(jso);

            if (!Strings.isNullOrEmpty(doc)) {
                con.mask();

                appUserService.saveAppDoc(appId, getJsonFormat(doc), new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        if (saveCallbck != null) {
                            saveCallbck.execute();
                        }
                        con.unmask();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        con.unmask();
                        ErrorHandler.post("Unable to save app documentation! Please try again.",
                                          caught);
                    }
                });
            }
            
        }
    }


    @UiTemplate("AppDocEditView.ui.xml")
    interface AppDocEditViewUiBinder extends UiBinder<Widget, AppDocEditView> {
    }

    private static AppDocEditViewUiBinder uiBinder = GWT.create(AppDocEditViewUiBinder.class);

    @UiField
    TextButton saveBtn;

    @UiField
    SimpleContainer panel;

    @UiField
    VerticalLayoutContainer con;
    
    protected JavaScriptObject jso;
    
    private boolean dirty;

    private final AppUserServiceFacade appUserService;

    private final Command saveCallbck;

    public AppDocEditView(final String appId,
                          String appDoc,
                          final AppUserServiceFacade appUserService,
                          final Command saveCallbck) {
        initWidget(uiBinder.createAndBindUi(this));
        setData(appDoc);
        this.appUserService = appUserService;
        this.saveCallbck = saveCallbck;
        panel.addResizeHandler(new ResizeViewHandlerImpl());
        saveBtn.addSelectHandler(new SaveButtonSelectHandlerImpl(appId, appUserService, saveCallbck));
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
							instance.@org.iplantc.de.apps.client.views.AppDocEditView::setDirty(Ljava/lang/Boolean;)(@java.lang.Boolean::TRUE);
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
