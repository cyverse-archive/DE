package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.List;

public class AppMetadataServiceFacadeImpl implements AppMetadataServiceFacade {

    @Inject DEProperties deProps;
    @Inject DiscEnvApiService deServiceFacade;

    @Inject
    public AppMetadataServiceFacadeImpl() {
    }

    /**
     * Duplicated in {@link org.iplantc.de.client.services.AppUserServiceFacade#favoriteApp(org.iplantc.de.client.models.HasId, boolean, com.google.gwt.user.client.rpc.AsyncCallback)}
     */
    @Override
    public void addToFavorites(String UUID, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    /**
     * Duplicated in {@link org.iplantc.de.client.services.AppUserServiceFacade#favoriteApp(org.iplantc.de.client.models.HasId, boolean, com.google.gwt.user.client.rpc.AsyncCallback)}
     */
    @Override
    public void removeFromFavorites(String UUID, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getComments(String UUID, AsyncCallback<String> callback) {
        String address = getAppsMetadataAddress(UUID) + "/comments";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, callback);

    }

    /**
     * Duplicated in {@link org.iplantc.de.client.services.AppUserServiceFacade#addAppComment(String, int, String, String, String, com.google.gwt.user.client.rpc.AsyncCallback)}
     */
    @Override
    public void addComment(String UUID, String comment, AsyncCallback<String> callback) {
        String address = getAppsMetadataAddress(UUID) + "/comments";
        JSONObject obj = new JSONObject();
        obj.put("comment", new JSONString(comment));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, obj.toString());
        callService(wrapper, callback);
    }

    @Override
    public void markAsRetracted(String UUID,
                                String commentId,
                                boolean retracted,
                                AsyncCallback<String> callback) {
        String address = getAppsMetadataAddress(UUID) + "/comments/" + commentId + "?retracted="
                + retracted;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, "{}");
        callService(wrapper, callback);

    }

    @Override
    public void attachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void detachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getTags(HasId hasId, AsyncCallback<List<Tag>> callback) {
        // TODO Auto-generated method stub

    }

    String getAppsMetadataAddress(String uuid) {
        String address = deProps.getUnproctedMuleServiceBaseUrl() + "apps/" + uuid;
        return address;
    }

    /**
     * Performs the actual service call.
     * 
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     * @param callback executed when RPC call completes.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        deServiceFacade.getServiceData(wrapper, callback);
    }

}
