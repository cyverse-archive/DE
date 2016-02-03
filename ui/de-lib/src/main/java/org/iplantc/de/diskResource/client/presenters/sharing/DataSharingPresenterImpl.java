package org.iplantc.de.diskResource.client.presenters.sharing;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.SharedResource;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.sharing.SharingPermissionsPanel;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.DataSharingView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;

import com.sencha.gxt.core.shared.FastMap;

import java.util.ArrayList;
import java.util.List;

/**
 * FIXME Refactor to use injection
 * FIXME Tighten contract with data sharing service. Should not have to manually construct the json here.
 * @author sriram, jstroot
 */
public class DataSharingPresenterImpl implements SharingPresenter {

    private final class LoadPermissionsCallback implements AsyncCallback<String> {
        private final class GetUserInfoCallback implements AsyncCallback<FastMap<Collaborator>> {
            private final List<String> usernames;

            private GetUserInfoCallback(List<String> usernames) {
                this.usernames = usernames;
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(FastMap<Collaborator> results) {
                dataSharingMap = new FastMap<>();
                for (String userName : usernames) {
                    Collaborator user = results.get(userName);
                    if (user == null) {
                        user = collaboratorsUtil.getDummyCollaborator(userName);
                    }

                    List<Sharing> dataShares = new ArrayList<>();

                    dataSharingMap.put(userName, dataShares);

                    for (JSONObject share : sharingList.get(userName)) {
                        String path = jsonUtil.getString(share, "path"); //$NON-NLS-1$
                        Sharing dataSharing = new Sharing(user,
                                                                  buildPermissionFromJson(share),
                                                                  path,
                                                                  DiskResourceUtil.getInstance()
                                                                                  .parseNameFromPath(path));
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
            JSONArray permissionsArray = jsonUtil.getArray(jsonUtil.getObject(result), "paths"); //$NON-NLS-1$
            if (permissionsArray != null) {
                sharingList = new FastMap<>();
                for (int i = 0; i < permissionsArray.size(); i++) {
                    JSONObject user_perm_obj = permissionsArray.get(i).isObject();
                    String path = jsonUtil.getString(user_perm_obj, "path"); //$NON-NLS-1$
                    JSONArray user_arr = jsonUtil.getArray(user_perm_obj, "user-permissions"); //$NON-NLS-1$
                    loadPermissions(path, user_arr);
                }

                final List<String> usernames = new ArrayList<>();
                usernames.addAll(sharingList.keySet());
                collaboratorsUtil.getUserInfo(usernames, new GetUserInfoCallback(usernames));
            }
        }
    }

    final DataSharingView view;
    private final DiskResourceServiceFacade diskResourceService;
    private final SharingPermissionsPanel permissionsPanel;
    private final List<DiskResource> selectedResources;
    private final Appearance appearance;
    private FastMap<List<Sharing>> dataSharingMap;
    private FastMap<List<JSONObject>> sharingList;
    private final JsonUtil jsonUtil;
    private final CollaboratorsUtil collaboratorsUtil;


    public DataSharingPresenterImpl(final DiskResourceServiceFacade diskResourceService,
                                    final List<DiskResource> selectedResources,
                                    final DataSharingView view,
                                    final CollaboratorsUtil collaboratorsUtil,
                                    final JsonUtil jsonUtil) {
        this(diskResourceService,
             selectedResources,
             view,
             collaboratorsUtil,
             jsonUtil,
             GWT.<SharingPresenter.Appearance> create(SharingPresenter.Appearance.class));
    }

    DataSharingPresenterImpl(final DiskResourceServiceFacade diskResourceService,
                             final List<DiskResource> selectedResources,
                             final DataSharingView view,
                             final CollaboratorsUtil collaboratorsUtil,
                             final JsonUtil jsonUtil,
                             final SharingPresenter.Appearance appearance) {
        this.diskResourceService = diskResourceService;
        this.view = view;
        this.selectedResources = selectedResources;
        this.collaboratorsUtil = collaboratorsUtil;
        this.jsonUtil = jsonUtil;
        this.appearance = appearance;
        view.setPresenter(this);
        permissionsPanel = new SharingPermissionsPanel(this, getSelectedResourcesAsMap(selectedResources));
        view.addShareWidget(permissionsPanel.asWidget());
        loadResources();
        loadPermissions();
    }

    @Override
    public PermissionValue getDefaultPermissions() {
        return PermissionValue.read;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
    }

    @Override
    public void loadResources() {
        view.setSelectedDiskResources(selectedResources);
    }

    @Override
    public void loadPermissions() {
        permissionsPanel.mask();
        diskResourceService.getPermissions(buildPermissionsRequestBody(), new LoadPermissionsCallback());
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
            IplantAnnouncer.getInstance().schedule(appearance.sharingCompleteMsg());
        }

    }

    private JSONArray buildPathArr(List<Sharing> shareList) {
        JSONArray pathArr = new JSONArray();
        int index = 0;
        for (Sharing s : shareList) {
            pathArr.set(index++, new JSONString(s.getId()));
        }
        return pathArr;
    }

    private JSONArray buildPathArrWithPermissions(List<Sharing> shareList) {
        JSONArray pathArr = new JSONArray();
        int index = 0;
        JSONObject obj;
        for (Sharing s : shareList) {
            obj = new JSONObject();
            obj.put("path", new JSONString(s.getId()));
            obj.put("permission", buildSharingPermissionsAsJson(s));
            pathArr.set(index++, obj);
        }

        return pathArr;
    }

    private PermissionValue buildPermissionFromJson(JSONObject perm) {
        return PermissionValue.valueOf(jsonUtil.getString(perm, "permission"));
    }

    private JSONObject buildPermissionsRequestBody() {
        JSONObject obj = new JSONObject();
        JSONArray ids = new JSONArray();
        for (int i = 0; i < selectedResources.size(); i++) {
            ids.set(i, new JSONString(selectedResources.get(i).getPath()));
        }
        obj.put("paths", ids);
        return obj;
    }

    private JSONObject buildSharingJson() {
        JSONObject sharingObj = new JSONObject();
        FastMap<List<Sharing>> sharingMap = permissionsPanel.getSharingMap();

        if (sharingMap != null && sharingMap.size() > 0) {
            JSONArray sharingArr = new JSONArray();
            int index = 0;
            for (String userName : sharingMap.keySet()) {
                List<Sharing> shareList = sharingMap.get(userName);
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

    private JSONValue buildSharingPermissionsAsJson(Sharing sh) {
        return new JSONString(sh.getPermission().toString());
    }

    private JSONObject buildUnSharingJson() {
        JSONObject unsharingObj = new JSONObject();
        FastMap<List<Sharing>> unSharingMap = permissionsPanel.getUnshareList();

        if (unSharingMap != null && unSharingMap.size() > 0) {
            JSONArray unsharingArr = new JSONArray();
            int index = 0;
            for (String userName : unSharingMap.keySet()) {
                List<Sharing> shareList = unSharingMap.get(userName);
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

    private void callSharingService(JSONObject obj) {
        diskResourceService.shareDiskResource(obj, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);

            }

            @Override
            public void onSuccess(String result) {
                // do nothing intentionally
            }
        });
    }

    private void callUnshareService(JSONObject obj) {
        diskResourceService.unshareDiskResource(obj, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);

            }

            @Override
            public void onSuccess(String result) {
                // do nothing
            }
        });
    }

    private FastMap<SharedResource> getSelectedResourcesAsMap(List<DiskResource> selectedResources) {
        FastMap<SharedResource> resourcesMap = new FastMap<>();
        for (DiskResource sr : selectedResources) {
            resourcesMap.put(sr.getPath(),
                             new SharedResource(sr.getPath(),
                                                  DiskResourceUtil.getInstance()
                                                                  .parseNameFromPath(sr.getPath())));
        }
        return resourcesMap;
    }

    private void loadPermissions(String path, JSONArray user_arr) {
        for (int i = 0; i < user_arr.size(); i++) {
            JSONObject userPermission = jsonUtil.getObjectAt(user_arr, i);
            JSONObject perm = new JSONObject();
            String permVal = jsonUtil.getString(userPermission, "permission"); //$NON-NLS-1$
            String userName = jsonUtil.getString(userPermission, "user"); //$NON-NLS-1$

            List<JSONObject> shareList = sharingList.get(userName);
            if (shareList == null) {
                shareList = new ArrayList<>();
                sharingList.put(userName, shareList);
            }
            perm.put("permission", new JSONString(permVal));
            perm.put("path", new JSONString(path)); //$NON-NLS-1$
            shareList.add(perm);
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


}
