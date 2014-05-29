package org.iplantc.admin.belphegor.client;

import org.iplantc.admin.belphegor.client.gin.BelphegorAppInjector;
import org.iplantc.admin.belphegor.client.models.ToolIntegrationAdminProperties;
import org.iplantc.admin.belphegor.client.services.ToolIntegrationAdminServiceFacade;
import org.iplantc.admin.belphegor.client.views.BelphegorView;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.shared.services.PropertyServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Belphegor implements EntryPoint {
    private final BelphegorAppInjector injector = GWT.create(BelphegorAppInjector.class);
    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        setBrowserContextMenuEnabled(ToolIntegrationAdminProperties.getInstance().isContextClickEnabled());
        setEntryPointTitle();
        initProperties();
    }

    private void setEntryPointTitle() {
        Window.setTitle(I18N.DISPLAY.adminApp());
    }

    private void initProperties() {
        PropertyServiceFacade.getInstance().getProperties(new AsyncCallback<Map<String, String>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.DISPLAY.cantLoadUserInfo(), caught);
            }

            @Override
            public void onSuccess(Map<String, String> result) {
                ToolIntegrationAdminProperties.getInstance().initialize(result);
                initUserInfo();
            }
        });
    }

    private void initUserInfo() {
        String address = ToolIntegrationAdminProperties.getInstance().getBootStrapUrl();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.retrieveUserInfoFailed(), caught);
            }

            @Override
            public void onSuccess(String result) {
                parseWorkspaceInfo(result);
                initApp();
            }
        });
    }

    private void initApp() {
        BelphegorView.Presenter belphegorPresenter = injector.getBelphegorPresenter();
        belphegorPresenter.go(RootPanel.get());

        String keepaliveTarget = ToolIntegrationAdminProperties.getInstance().getKeepaliveTarget();
        int keepaliveInterval = ToolIntegrationAdminProperties.getInstance().getKeepaliveInterval();
        KeepaliveTimer.getInstance().start(keepaliveTarget, keepaliveInterval);
    }

    private void parseWorkspaceInfo(String json) {
        // Bootstrap the user-info object with workspace info provided in JSON format.
        UserInfo.getInstance().init(json);
    }

    /**
     * Disable the context menu of the browser using native JavaScript.
     * 
     * This disables the user's ability to right-click on this widget and get the browser's context menu
     */
    private native void setBrowserContextMenuEnabled(boolean enabled)
    /*-{
		$doc.oncontextmenu = function() {
			return enabled;
		};
    }-*/;

}
