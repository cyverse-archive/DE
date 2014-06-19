package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.tags.IpalntTagAutoBeanFactory;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class FileSystemMetadataServiceFacadeImpl implements MetadataServiceFacade {

    private final DEProperties deProps;
    private final DEServiceFacade deServiceFacade;
    IpalntTagAutoBeanFactory factory = GWT.create(IpalntTagAutoBeanFactory.class);
    private static final DiskResourceAutoBeanFactory drFactory = GWT.create(DiskResourceAutoBeanFactory.class);

    @Inject
    public FileSystemMetadataServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProps) {
        this.deServiceFacade = deServiceFacade;
        this.deProps = deProps;
    }

    @Override
    public void getFavorites(AsyncCallback<Folder> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "favorites/filesystem";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, new AsyncCallbackConverter<String, Folder>(callback) {

            @Override
            protected Folder convertFrom(String object) {
                JSONObject contents = JsonUtil.getObject(object);
                JSONObject todecode = JsonUtil.getObject(contents, "filesystem");
                return decode(Folder.class, todecode.toString());
            }
        });
    }

    private static <T> T decode(Class<T> clazz, String payload) {
        return AutoBeanCodex.decode(drFactory, clazz, payload).as();
    }

    @Override
    public void addToFavorites(String UUID, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "favorites/filesystem/" + UUID;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PUT, address, "{}");
        callService(wrapper, callback);

    }

    @Override
    public void removeFromFavorites(String UUID, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "favorites/filesystem/" + UUID;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.DELETE, address);
        callService(wrapper, callback);

    }

    @Override
    public void getComments(String UUID, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + UUID + "/comments";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, callback);
    }

    @Override
    public void addComment(String UUID, String comment, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + UUID + "/comments";
        JSONObject obj = new JSONObject();
        obj.put("comment", new JSONString(comment));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, obj.toString());
        callService(wrapper, callback);

    }

    @Override
    public void markAsRetracted(String UUID, String commentId, boolean retracted, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + UUID + "/comments/" + commentId + "?retracted=" + retracted;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, "{}");
        callService(wrapper, callback);

    }

    @Override
    public void attachTags(List<String> tagIds, String objectId, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + objectId + "/tags?type=attach";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, arrayToJsonString(tagIds));
        callService(wrapper, callback);

    }

    @Override
    public void detachTags(List<String> tagIds, String objectId, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + objectId + "/tags?type=detach";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, arrayToJsonString(tagIds));
        callService(wrapper, callback);

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

    private String arrayToJsonString(List<String> ids) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        int index = 0;
        for (String id : ids) {
            arr.set(index++, new JSONString(id));
        }
        obj.put("tags", arr);
        return obj.toString();

    }

    @Override
    public void getTags(String UUID, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + UUID + "/tags";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, callback);

    }

}
