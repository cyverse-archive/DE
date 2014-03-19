/**
 *
 */
package org.iplantc.de.diskResource.client.sharing.presenter;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.Permissions;
import org.iplantc.de.client.models.sharing.DataSharing;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.collaborators.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.sharing.views.DataSharingPermissionsPanel;
import org.iplantc.de.diskResource.client.sharing.views.DataSharingView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.shared.FastMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram
 *
 */
public class DataSharingPresenter implements DataSharingView.Presenter {

    DataSharingView view;
    private final List<DiskResource> selectedResources;


	private FastMap<List<JSONObject>> sharingList;
    private FastMap<List<DataSharing>> dataSharingMap;
    private final DiskResourceServiceFacade facade;
    private final DataSharingPermissionsPanel permissionsPanel;
    private static DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);

    public DataSharingPresenter(List<DiskResource> selectedResources, DataSharingView view) {
        facade = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
        this.view = view;
        this.selectedResources = selectedResources;
        view.setPresenter(this);
        permissionsPanel = new DataSharingPermissionsPanel(this, getSelectedResourcesAsMap(selectedResources));
        view.addShareWidget(permissionsPanel.asWidget());
        loadDiskResources();
        loadPermissions();
    }


    private FastMap<DiskResource> getSelectedResourcesAsMap(List<DiskResource> selectedResources) {
    	FastMap<DiskResource> resourcesMap = new FastMap<DiskResource>();
    	for (DiskResource sr : selectedResources) {
    		resourcesMap.put(sr.getId(), sr);
    	}
    	return resourcesMap;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
    }

    @Override
    public List<DiskResource> getSelectedResources() {
  		return selectedResources;
  	}


    @Override
    public void loadDiskResources() {
        view.setSelectedDiskResources(selectedResources);
    }

    @Override
    public void loadPermissions() {
        permissionsPanel.mask(I18N.DISPLAY.loadingMask());
        facade.getPermissions(buildPermissionsRequestBody(), new LoadPermissionsCallback());
    }


    private JSONObject buildPermissionsRequestBody() {
        JSONObject obj = new JSONObject();
        JSONArray ids = new JSONArray();
        for (int i = 0; i < selectedResources.size(); i++) {
            ids.set(i, new JSONString(selectedResources.get(i).getId()));
        }
        obj.put("paths", ids);
        return obj;
    }

    private void loadPermissions(String path, JSONArray user_arr) {
        for (int i = 0; i < user_arr.size(); i++) {
            JSONObject userPermission = JsonUtil.getObjectAt(user_arr, i);
            JSONObject perm = JsonUtil.getObject(userPermission, "permissions"); //$NON-NLS-1$
            String userName = JsonUtil.getString(userPermission, "user"); //$NON-NLS-1$

            List<JSONObject> shareList = sharingList.get(userName);
            if (shareList == null) {
                shareList = new ArrayList<JSONObject>();
                sharingList.put(userName, shareList);
            }
            perm.put("path", new JSONString(path)); //$NON-NLS-1$
            shareList.add(perm);
        }

    }

    private final class LoadPermissionsCallback implements AsyncCallback<String> {
        private final class GetUserInfoCallback implements AsyncCallback<FastMap<Collaborator>> {
            private final List<String> usernames;

            private GetUserInfoCallback(List<String> usernames) {
                this.usernames = usernames;
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(FastMap<Collaborator> results) {
                dataSharingMap = new FastMap<List<DataSharing>>();
                for (String userName : usernames) {
                    Collaborator user = results.get(userName);
                    if (user == null) {
                        user = CollaboratorsUtil.getDummyCollaborator(userName);
                    }

                    List<DataSharing> dataShares = new ArrayList<DataSharing>();

                    dataSharingMap.put(userName, dataShares);

                    for (JSONObject share : sharingList.get(userName)) {
                        String path = JsonUtil.getString(share, "path"); //$NON-NLS-1$
                        DataSharing dataSharing = new DataSharing(user,
                                buildPermissionFromJson(share),
                                path);
                        dataShares.add(dataSharing);
                    }
                }

                permissionsPanel.loadSharingData(dataSharingMap);
                permissionsPanel.unmask();
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            permissionsPanel.unmask();
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(String result) {
            JSONArray permissionsArray = JsonUtil.getArray(JsonUtil.getObject(result), "paths"); //$NON-NLS-1$
            if (permissionsArray != null) {
                sharingList = new FastMap<List<JSONObject>>();
                for (int i = 0; i < permissionsArray.size(); i++) {
                    JSONObject user_perm_obj = permissionsArray.get(i).isObject();
                    String path = JsonUtil.getString(user_perm_obj, "path"); //$NON-NLS-1$
                    JSONArray user_arr = JsonUtil.getArray(user_perm_obj, "user-permissions"); //$NON-NLS-1$
                    loadPermissions(path, user_arr);
                }

                final List<String> usernames = new ArrayList<String>();
                usernames.addAll(sharingList.keySet());
                CollaboratorsUtil.getUserInfo(usernames, new GetUserInfoCallback(usernames));
            }
    }
    }

    @Override
    public void processRequest() {
        JSONObject requestBody = buildSharingJson();
        JSONObject unshareRequestBody = buildUnSharingJson();
        if (requestBody != null) {
            share(requestBody);
        }

        if (unshareRequestBody != null) {
            unshare(unshareRequestBody);
        }

        if (requestBody != null || unshareRequestBody != null) {
            IplantAnnouncer.getInstance().schedule(I18N.DISPLAY.sharingCompleteMsg());
        }

    }

    private void share(JSONObject requestBody) {

        if (requestBody != null) {
            callSharingService(requestBody);
        }

    }

    private void unshare(JSONObject unshareRequestBody) {

        if (unshareRequestBody != null) {
            callUnshareService(unshareRequestBody);
        }

    }

    private void callSharingService(JSONObject obj) {
        facade.shareDiskResource(obj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // do nothing intentionally
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);

            }
        });
    }

    private void callUnshareService(JSONObject obj) {
        facade.unshareDiskResource(obj, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);

            }
        });
    }

    private JSONObject buildSharingJson() {
        JSONObject sharingObj = new JSONObject();
        FastMap<List<DataSharing>> sharingMap = permissionsPanel.getSharingMap();

        if (sharingMap != null && sharingMap.size() > 0) {
            JSONArray sharingArr = new JSONArray();
            int index = 0;
            for (String userName : sharingMap.keySet()) {
                List<DataSharing> shareList = sharingMap.get(userName);
                JSONObject userObj = new JSONObject();
                userObj.put("user", new JSONString(userName));
                userObj.put("paths", buildPathArrWithPermissions(shareList));
                sharingArr.set(index++, userObj);
            }

            sharingObj.put("sharing", sharingArr);
            return sharingObj;
        } else {
            return null;
        }
    }

    private JSONArray buildPathArrWithPermissions(List<DataSharing> shareList) {
        JSONArray pathArr = new JSONArray();
        int index = 0;
        JSONObject obj;
        for (Sharing s : shareList) {
            DataSharing ds = (DataSharing)s;
            obj = new JSONObject();
            obj.put("path", new JSONString(ds.getPath()));
            obj.put("permissions", buildSharingPermissionsAsJson(ds));
            pathArr.set(index++, obj);
        }

        return pathArr;
    }

    private JSONArray buildPathArr(List<DataSharing> shareList) {
        JSONArray pathArr = new JSONArray();
        int index = 0;
        for (Sharing s : shareList) {
            DataSharing ds = (DataSharing)s;
            pathArr.set(index++, new JSONString(ds.getPath()));
        }
        return pathArr;
    }

    private JSONObject buildSharingPermissionsAsJson(DataSharing sh) {
        JSONObject permission = new JSONObject();
        permission.put("read", JSONBoolean.getInstance(sh.isReadable()));
        permission.put("write", JSONBoolean.getInstance(sh.isWritable()));
        permission.put("own", JSONBoolean.getInstance(sh.isOwner()));
        return permission;
    }

    private Permissions buildPermissionFromJson(JSONObject perm) {
        AutoBean<Permissions> bean = AutoBeanCodex.decode(factory, Permissions.class, perm.toString());
        return bean.as();
    }

    private JSONObject buildUnSharingJson() {
        JSONObject unsharingObj = new JSONObject();
        FastMap<List<DataSharing>> unSharingMap = permissionsPanel.getUnshareList();

        if (unSharingMap != null && unSharingMap.size() > 0) {
            JSONArray unsharingArr = new JSONArray();
            int index = 0;
            for (String userName : unSharingMap.keySet()) {
                List<DataSharing> shareList = unSharingMap.get(userName);
                JSONObject userObj = new JSONObject();
                userObj.put("user", new JSONString(userName));
                userObj.put("paths", buildPathArr(shareList));
                unsharingArr.set(index++, userObj);
            }
            unsharingObj.put("unshare", unsharingArr);
            return unsharingObj;
        } else {
            return null;
        }

    }

    @Override
    public DataSharing.TYPE getSharingResourceType(String path) {
        for (DiskResource dr : selectedResources) {
            if (dr.getId().equalsIgnoreCase(path)) {
                if (dr instanceof Folder) {
                    return DataSharing.TYPE.FOLDER;
                } else {
                    return DataSharing.TYPE.FILE;
                }
            }
        }

        return null;
    }


    @Override
    public Permissions getDefaultPermissions() {
        JSONObject obj = new JSONObject();
        obj.put(DataSharing.READ, JSONBoolean.getInstance(true));
        obj.put(DataSharing.WRITE, JSONBoolean.getInstance(false));
        obj.put(DataSharing.OWN, JSONBoolean.getInstance(false));
        AutoBean<Permissions> bean = AutoBeanCodex.decode(factory, Permissions.class, obj.toString());
        return bean.as();
    }


}
