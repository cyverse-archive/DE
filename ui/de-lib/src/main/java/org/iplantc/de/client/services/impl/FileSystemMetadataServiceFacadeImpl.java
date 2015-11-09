package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.tags.IplantTagAutoBeanFactory;
import org.iplantc.de.client.models.tags.IplantTagList;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;

/**
 * @author jstroot
 */
public class FileSystemMetadataServiceFacadeImpl implements FileSystemMetadataServiceFacade {

    static class FavoritesCallbackConverter extends AsyncCallbackConverter<String, Folder> {
        private final DiskResourceAutoBeanFactory drFactory;

        public FavoritesCallbackConverter(final AsyncCallback<Folder> callback,
                                          final DiskResourceAutoBeanFactory drFactory) {
            super(callback);
            this.drFactory = drFactory;
        }

        @Override
        protected Folder convertFrom(String object) {
            Splittable splitContents = StringQuoter.split(object);
            Splittable filesystem = splitContents.get("filesystem");

            return AutoBeanCodex.decode(drFactory, Folder.class, filesystem).as();
        }
    }

    @Inject DEProperties deProps;
    @Inject DiscEnvApiService deServiceFacade;
    @Inject DiskResourceAutoBeanFactory drFactory;
    @Inject IplantTagAutoBeanFactory factory;
    @Inject DiskResourceUtil diskResourceUtil;

    @Inject
    public FileSystemMetadataServiceFacadeImpl() { }

    @Override
    public void addComment(String UUID, String comment, AsyncCallback<String> callback) {
        String address = getFileSystemEntryAddress(UUID) + "/comments";
        JSONObject obj = new JSONObject();
        obj.put("comment", new JSONString(comment));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, obj.toString());
        callService(wrapper, callback);
    }

    @Override
    public void addToFavorites(String UUID, AsyncCallback<String> callback) {
        String address = getFavoritesFilesystemAddress() + "/" + UUID;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PUT, address, "{}");
        callService(wrapper, callback);
    }

    @Override
    public void attachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {
        String address = getFileSystemEntryAddress(hasId.getId()) + "/tags?type=attach";
        final List<String> stringIdList = diskResourceUtil.asStringIdList(tags);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, arrayToJsonString(stringIdList));
        callService(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void detachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {
        String address = getFileSystemEntryAddress(hasId.getId()) + "/tags?type=detach";
        final List<String> stringIdList = diskResourceUtil.asStringIdList(tags);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, arrayToJsonString(stringIdList));
        callService(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getComments(String UUID, AsyncCallback<String> callback) {
        String address = getFileSystemEntryAddress(UUID) + "/comments";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, callback);
    }

    @Override
    public void getFavorites(final List<InfoType> infoTypeFilters,
                             final TYPE entityType,
                             final FilterPagingLoadConfigBean configBean,
                             final AsyncCallback<Folder> callback) {
        String address = getFavoritesFilesystemAddress() + "?";

        SortInfoBean sortInfo = Iterables.getFirst(configBean.getSortInfo(),
                                                   new SortInfoBean("NAME", SortDir.ASC));
        address += "sort-col=" + sortInfo.getSortField()
                       + "&limit=" + configBean.getLimit()
                       + "&offset=" + configBean.getOffset()
                       + "&sort-dir=" + sortInfo.getSortDir().toString();

        // Apply entity type query parameter if applicable
        if(entityType != null){
            address += "&entity-type=" + entityType.toString();
        }

        // Apply InfoType filters if applicable
        if (infoTypeFilters != null) {
            String infoTypeUrlParameters = "";
            for (InfoType infoType : infoTypeFilters) {
                infoTypeUrlParameters += "&info-type=" + infoType.toString();
            }
            if (!Strings.isNullOrEmpty(infoTypeUrlParameters)) {
                address += infoTypeUrlParameters;
            }
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, new FavoritesCallbackConverter(callback, drFactory));
    }

    @Override
    public void getTags(HasId hasId, AsyncCallback<List<Tag>> callback) {
        String address = getFileSystemEntryAddress(hasId.getId()) + "/tags";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        callService(wrapper, new AsyncCallbackConverter<String, List<Tag>>(callback) {
            @Override
            protected List<Tag> convertFrom(String object) {
                AutoBean<IplantTagList> tagListAutoBean = AutoBeanCodex.decode(factory, IplantTagList.class, object);
                return tagListAutoBean.as().getTagList();
            }
        });
    }

    @Override
    public void markAsRetracted(String UUID, String commentId, boolean retracted,
                                AsyncCallback<String> callback) {
        String address = getFileSystemEntryAddress(UUID) + "/comments/" + commentId + "?retracted=" + retracted;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PATCH, address, "{}");
        callService(wrapper, callback);
    }

    @Override
    public void removeFromFavorites(String UUID, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "favorites/filesystem/"
                             + UUID;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.DELETE, address);
        callService(wrapper, callback);
    }

    String getFavoritesFilesystemAddress() {
        String address = deProps.getMuleServiceBaseUrl() + "favorites/filesystem";
        return address;
    }

    String getFileSystemEntryAddress(String uuid) {
        String address = deProps.getMuleServiceBaseUrl() + "filesystem/entry/" + uuid;
        return address;
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

    /**
     * Performs the actual service call.
     *
     * @param wrapper  the wrapper used to get to the actual service via the service proxy.
     * @param callback executed when RPC call completes.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        deServiceFacade.getServiceData(wrapper, callback);
    }

}
