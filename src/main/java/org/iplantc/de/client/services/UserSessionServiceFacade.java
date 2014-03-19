package org.iplantc.de.client.services;

import org.iplantc.de.client.models.UserSession;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;

public interface UserSessionServiceFacade {

    void getUserSession(AsyncCallback<String> callback);

    void saveUserSession(UserSession userSession, AsyncCallback<String> callback);

    void clearUserSession(AsyncCallback<String> callback);

    void getUserPreferences(AsyncCallback<String> callback);

    void saveUserPreferences(Splittable json, AsyncCallback<String> callback);

    void postClientNotification(JSONObject notification, AsyncCallback<String> callback);

    void getSearchHistory(AsyncCallback<String> callback);

    void saveSearchHistory(JSONObject body, AsyncCallback<String> callback);

}