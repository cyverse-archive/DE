/**
 * @author sriram
 */
package org.iplantc.de.apps.client.presenter.sharing;

import org.iplantc.de.apps.client.views.sharing.AppSharingView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.sharing.AppPermission;
import org.iplantc.de.client.models.apps.sharing.AppSharingAutoBeanFactory;
import org.iplantc.de.client.models.apps.sharing.AppSharingRequest;
import org.iplantc.de.client.models.apps.sharing.AppSharingRequestList;
import org.iplantc.de.client.models.apps.sharing.AppUnSharingRequestList;
import org.iplantc.de.client.models.apps.sharing.AppUnsharingRequest;
import org.iplantc.de.client.models.apps.sharing.AppUserPermissions;
import org.iplantc.de.client.models.apps.sharing.AppUserPermissionsList;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.SharedResource;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.models.sharing.UserPermission;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.sharing.SharingPermissionsPanel;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.shared.FastMap;

import java.util.ArrayList;
import java.util.List;

public class AppSharingPresenter implements SharingPresenter {

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
                sharingMap = new FastMap<>();
                for (String userName : usernames) {
                    Collaborator user = results.get(userName);
                    if (user == null) {
                        user = collaboratorsUtil.getDummyCollaborator(userName);
                    }

                    List<Sharing> shares = new ArrayList<>();

                    sharingMap.put(userName, shares);

                    for (JSONObject share : sharingList.get(userName)) {
                        String id = jsonUtil.getString(share, "id"); //$NON-NLS-1$
                        String name = jsonUtil.getString(share, "name");
                        Sharing sharing = new Sharing(user, buildPermissionFromJson(share), id, name);
                        shares.add(sharing);
                    }
                }

                permissionsPanel.loadSharingData(sharingMap);
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
            AutoBean<AppUserPermissionsList> abrp =
                    AutoBeanCodex.decode(shareFactory, AppUserPermissionsList.class, result);
            AppUserPermissionsList appPermsList = abrp.as();
            sharingList = new FastMap<>();
            for (AppUserPermissions rup : appPermsList.getResourceUserPermissionsList()) {
                String id = rup.getId();
                String appName = rup.getName();
                List<UserPermission> upList = rup.getPermissions();
                loadPermissions(id, appName, upList);
            }
            final List<String> usernames = new ArrayList<>();
            usernames.addAll(sharingList.keySet());
            collaboratorsUtil.getUserInfo(usernames, new GetUserInfoCallback(usernames));
        }

    }

    final AppSharingView view;
    private final SharingPermissionsPanel permissionsPanel;
    private final List<App> selectedApps;
    private final AppUserServiceFacade appService;
    private Appearance appearance;
    private FastMap<List<Sharing>> sharingMap;
    private FastMap<List<JSONObject>> sharingList;
    private final JsonUtil jsonUtil;
    private final CollaboratorsUtil collaboratorsUtil;
    private AppAutoBeanFactory appFactory = GWT.create(AppAutoBeanFactory.class);
    private AppSharingAutoBeanFactory shareFactory = GWT.create(AppSharingAutoBeanFactory.class);


    public AppSharingPresenter(final AppUserServiceFacade appService,
                               final List<App> selectedApps,
                               final AppSharingView view,
                               final CollaboratorsUtil collaboratorsUtil,
                               final JsonUtil jsonUtil) {
        this(appService,
             selectedApps,
             view,
             collaboratorsUtil,
             jsonUtil,
             GWT.<SharingPresenter.Appearance>create(SharingPresenter.Appearance.class));
    }

    public AppSharingPresenter(final AppUserServiceFacade appService,
                               final List<App> selectedApps,
                               final AppSharingView view,
                               final CollaboratorsUtil collaboratorsUtil,
                               final JsonUtil jsonUtil,
                               Appearance appearance) {

        this.view = view;
        this.appearance = appearance;
        this.appService = appService;
        view.setPresenter(this);
        this.jsonUtil = jsonUtil;
        this.collaboratorsUtil = collaboratorsUtil;
        this.selectedApps = selectedApps;
        this.permissionsPanel = new SharingPermissionsPanel(this, getSelectedApps(selectedApps));
        view.addShareWidget(permissionsPanel.asWidget());
        loadResources();
        loadPermissions();

    }

    private FastMap<SharedResource> getSelectedApps(List<App> selectedResources) {
        FastMap<SharedResource> resourcesMap = new FastMap<>();
        for (App sr : selectedResources) {
            resourcesMap.put(sr.getId(), new SharedResource(sr.getId(), sr.getName()));
        }
        return resourcesMap;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());

    }

    @Override
    public void loadResources() {
        view.setSelectedApps(selectedApps);

    }

    @Override
    public void loadPermissions() {
        permissionsPanel.mask();
        appService.getPermissions(selectedApps, new LoadPermissionsCallback());
    }

    @Override
    public PermissionValue getDefaultPermissions() {
        return PermissionValue.read;
    }

    private PermissionValue buildPermissionFromJson(JSONObject perm) {
        return PermissionValue.valueOf(jsonUtil.getString(perm, "permission"));
    }

    private void loadPermissions(String id, String appName, List<UserPermission> userPerms) {
        for (UserPermission up : userPerms) {
            String permVal = up.getPermission();
            String userName = up.getUser();
            JSONObject perm = new JSONObject();
            List<JSONObject> shareList = sharingList.get(userName);
            if (shareList == null) {
                shareList = new ArrayList<>();
                sharingList.put(userName, shareList);
            }
            perm.put("permission", new JSONString(permVal));
            perm.put("id", new JSONString(id)); //$NON-NLS-1$
            perm.put("name", new JSONString(appName));
            shareList.add(perm);
        }

    }

    @Override
    public void processRequest() {
        AppSharingRequestList request = buildSharingRequest();
        AppUnSharingRequestList unshareRequest = buildUnSharingRequest();
        if (request != null) {
            callSharingService(request);
        }

        if (unshareRequest != null) {
            callUnshareService(unshareRequest);
        }

        if (request != null || unshareRequest != null) {
            IplantAnnouncer.getInstance().schedule(appearance.sharingCompleteMsg());
        }

    }

    private AppSharingRequestList buildSharingRequest() {
        AutoBean<AppSharingRequestList> sharingAbList =
                AutoBeanCodex.decode(appFactory, AppSharingRequestList.class, "{}");
        AppSharingRequestList sharingRequestList = sharingAbList.as();

        FastMap<List<Sharing>> sharingMap = permissionsPanel.getSharingMap();

        List<AppSharingRequest> requests = new ArrayList<>();
        if (sharingMap != null && sharingMap.size() > 0) {
            for (String userName : sharingMap.keySet()) {
                AutoBean<AppSharingRequest> sharingAb =
                        AutoBeanCodex.decode(appFactory, AppSharingRequest.class, "{}");
                AppSharingRequest sharingRequest = sharingAb.as();
                List<Sharing> shareList = sharingMap.get(userName);
                sharingRequest.setUser(userName);
                sharingRequest.setAppPermissions(buildAppPermissions(shareList));
                requests.add(sharingRequest);
            }

            sharingRequestList.setAppSharingRequestList(requests);
            return sharingRequestList;

        } else {
            return null;
        }

    }

    private AppUnSharingRequestList buildUnSharingRequest() {
        AutoBean<AppUnSharingRequestList> unsharingAbList =
                AutoBeanCodex.decode(appFactory, AppUnSharingRequestList.class, "{}");

        AppUnSharingRequestList unsharingRequestList = unsharingAbList.as();

        FastMap<List<Sharing>> unSharingMap = permissionsPanel.getUnshareList();

        List<AppUnsharingRequest> requests = new ArrayList<>();

        if (unSharingMap != null && unSharingMap.size() > 0) {
            for (String userName : unSharingMap.keySet()) {
                List<Sharing> shareList = unSharingMap.get(userName);
                AutoBean<AppUnsharingRequest> unsharingAb =
                        AutoBeanCodex.decode(appFactory, AppUnsharingRequest.class, "{}");

                AppUnsharingRequest unsharingRequest = unsharingAb.as();
                unsharingRequest.setUser(userName);
                unsharingRequest.setApps(buildAppsList(shareList));
                requests.add(unsharingRequest);
            }
            unsharingRequestList.setAppUnSharingRequestList(requests);
            return unsharingRequestList;
        } else {
            return null;
        }

    }

    private List<String> buildAppsList(List<Sharing> shareList) {
        List<String> appIds = new ArrayList<>();
        for (Sharing s : shareList) {
            appIds.add(s.getId());
        }

        return appIds;
    }

    private List<AppPermission> buildAppPermissions(List<Sharing> shareList) {
        List<AppPermission> appPermList = new ArrayList<>();
        for (Sharing s : shareList) {
            AutoBean<AppPermission> appPermAb =
                    AutoBeanCodex.decode(appFactory, AppPermission.class, "{}");
            AppPermission appPerm = appPermAb.as();
            appPerm.setId(s.getId());
            appPerm.setPermission(s.getPermission().toString());
            appPermList.add(appPerm);
        }
        return appPermList;
    }

    private void callSharingService(AppSharingRequestList obj) {
        appService.shareApp(obj, new AsyncCallback<String>() {

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

    private void callUnshareService(AppUnSharingRequestList obj) {
        appService.unshareApp(obj, new AsyncCallback<String>() {

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

}
