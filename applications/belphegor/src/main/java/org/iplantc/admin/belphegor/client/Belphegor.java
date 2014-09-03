package org.iplantc.admin.belphegor.client;

import org.iplantc.admin.belphegor.client.gin.BelphegorAppInjector;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.views.BelphegorView;
import org.iplantc.admin.belphegor.shared.services.BelphegorPropertyService;
import org.iplantc.admin.belphegor.shared.services.BelphegorPropertyServiceAsync;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.services.DEService;
import org.iplantc.de.shared.services.DEServiceAsync;
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
    private final IplantDisplayStrings displayStrings = I18N.DISPLAY;
    private final IplantErrorStrings errorStrings = I18N.ERROR;
    private final BelphegorAppInjector injector = GWT.create(BelphegorAppInjector.class);
    private final KeepaliveTimer keepaliveTimer = KeepaliveTimer.getInstance();
    private final BelphegorPropertyServiceAsync propertyService = GWT.create(BelphegorPropertyService.class);
    private final DEServiceAsync deService = GWT.create(DEService.class);
    private final BelphegorAdminProperties adminProperties = BelphegorAdminProperties.getInstance();
    private final UserInfo userInfo = UserInfo.getInstance();

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        setBrowserContextMenuEnabled(adminProperties.isContextClickEnabled());
        setEntryPointTitle();
        initProperties();
    }

    private void setEntryPointTitle() {
        Window.setTitle(displayStrings.adminApp());
    }

    private void initProperties() {
        propertyService.getProperties(new AsyncCallback<Map<String, String>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(displayStrings.cantLoadUserInfo(), caught);
            }

            @Override
            public void onSuccess(Map<String, String> result) {
                adminProperties.initialize(result);
                initUserInfo();
            }
        });
    }

    private void initUserInfo() {
        String address = adminProperties.getBootStrapUrl();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(errorStrings.retrieveUserInfoFailed(), caught);
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

        String keepaliveTarget = adminProperties.getKeepaliveTarget();
        int keepaliveInterval = adminProperties.getKeepaliveInterval();
        keepaliveTimer.start(keepaliveTarget, keepaliveInterval);
    }

    private void parseWorkspaceInfo(String json) {
        // Bootstrap the user-info object with workspace info provided in JSON format.
        userInfo.init(json);
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
