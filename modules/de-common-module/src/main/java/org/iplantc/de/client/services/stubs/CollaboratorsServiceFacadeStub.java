package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.services.CollaboratorsServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class CollaboratorsServiceFacadeStub implements CollaboratorsServiceFacade {
    @Override
    public void searchCollaborators(String term, AsyncCallback<String> callback) {

    }

    @Override
    public void getCollaborators(AsyncCallback<String> callback) {

    }

    @Override
    public void addCollaborators(JSONObject users, AsyncCallback<String> callback) {

    }

    @Override
    public void removeCollaborators(JSONObject users, AsyncCallback<String> callback) {

    }

    @Override
    public void getUserInfo(List<String> usernames, AsyncCallback<String> callback) {

    }
}
