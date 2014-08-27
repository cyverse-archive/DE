package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSession;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.shared.services.DEServiceAsync;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.Request;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

/**
 * A service facade to save and retrieve user session
 * 
 * @author sriram
 * 
 */
public class UserSessionServiceFacadeImpl implements UserSessionServiceFacade {

    private final DEProperties deProperties;
    private final UserInfo userInfo;
    private final CommonModelAutoBeanFactory factory;
    private final DEServiceAsync deServiceFacade;

    @Inject
    public UserSessionServiceFacadeImpl(final DEServiceAsync deServiceFacade,
                                        final DEProperties deProperties,
                                        final UserInfo userInfo,
                                        final CommonModelAutoBeanFactory factory) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.userInfo = userInfo;
        this.factory = factory;
    }

    @Override
    public Request getUserSession(AsyncCallback<List<WindowState>> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        return deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, List<WindowState>>(callback) {

            @Override
            protected List<WindowState> convertFrom(String object) {
                final AutoBean<UserSession> decode = AutoBeanCodex.decode(factory, UserSession.class, object);
                return decode.as().getWindowStates();
            }
        });
    }

    @Override
    public Request saveUserSession(final List<WindowState> windowStates, AsyncCallback<Void> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        final AutoBean<UserSession> userSessionAutoBean = factory.userSession();
        userSessionAutoBean.as().setWindowStates(windowStates);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, AutoBeanCodex.encode(userSessionAutoBean).getPayload());
        return deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void clearUserSession(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "sessions"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public Request getUserPreferences(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "preferences"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        return deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void saveUserPreferences(Splittable json, AsyncCallback<Void> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "preferences"; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, json.getPayload());
        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void postClientNotification(JSONObject notification, AsyncCallback<String> callback) {
        String address = deProperties.getUnproctedMuleServiceBaseUrl() + "send-notification";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, notification.toString());

        deServiceFacade.getServiceData(wrapper, callback);

    }

    @Override
    public void getSearchHistory(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "search-history";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void saveSearchHistory(JSONObject body, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "search-history";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());
        deServiceFacade.getServiceData(wrapper, callback);

    }

    @Override
    public Request bootstrap(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "bootstrap";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        return deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void logout(AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "logout?=login-time=" + userInfo.getLoginTime();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

}
