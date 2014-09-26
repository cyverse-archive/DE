package org.iplantc.de.client.services;

import org.iplantc.de.client.models.WindowState;

import com.google.gwt.http.client.Request;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public interface UserSessionServiceFacade {

    Request getUserSession(AsyncCallback<List<WindowState>> callback);

    Request saveUserSession(List<WindowState> windowStates, AsyncCallback<Void> callback);

    Request getUserPreferences(AsyncCallback<String> callback);

    void saveUserPreferences(Splittable json, AsyncCallback<Void> callback);

    void postClientNotification(JSONObject notification, AsyncCallback<String> callback);

    Request bootstrap(AsyncCallback<String> callback);

    void logout(AsyncCallback<String> callback);

}