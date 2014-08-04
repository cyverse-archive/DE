package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.services.UserSessionServiceFacade;

import com.google.gwt.http.client.Request;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public class UserSessionServiceFacadeStub implements UserSessionServiceFacade {
    @Override
    public Request getUserSession(AsyncCallback<List<WindowState>> callback) {
        return null;
    }

    @Override
    public Request saveUserSession(List<WindowState> windowStates, AsyncCallback<Void> callback) {
        return null;
    }

    @Override
    public void clearUserSession(AsyncCallback<String> callback) {

    }

    @Override
    public Request getUserPreferences(AsyncCallback<String> callback) {
        return null;
    }

    @Override
    public void saveUserPreferences(Splittable json, AsyncCallback<Void> callback) {

    }

    @Override
    public void postClientNotification(JSONObject notification, AsyncCallback<String> callback) {

    }

    @Override
    public void getSearchHistory(AsyncCallback<String> callback) {

    }

    @Override
    public void saveSearchHistory(JSONObject body, AsyncCallback<String> callback) {

    }

    @Override
    public Request bootstrap(AsyncCallback<String> callback) {
        return null;
    }

    @Override
    public void logout(AsyncCallback<String> callback) {

    }
}
