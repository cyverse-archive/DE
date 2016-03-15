/**
 * @author sriram
 */

package org.iplantc.de.analysis.client.presenter.sharing;

import org.iplantc.de.analysis.client.views.sharing.AnalysisSharingView;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.sharing.AnalysisPermission;
import org.iplantc.de.client.models.analysis.sharing.AnalysisSharingAutoBeanFactory;
import org.iplantc.de.client.models.analysis.sharing.AnalysisSharingRequest;
import org.iplantc.de.client.models.analysis.sharing.AnalysisSharingRequestList;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUnsharingRequest;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUnsharingRequestList;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUserPermissions;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUserPermissionsList;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.SharedResource;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.models.sharing.UserPermission;
import org.iplantc.de.client.services.AnalysisServiceFacade;
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

public class AnalysisSharingPresenter implements SharingPresenter {


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
            AutoBean<AnalysisUserPermissionsList> abrp =
                    AutoBeanCodex.decode(shareFactory, AnalysisUserPermissionsList.class, result);
            AnalysisUserPermissionsList aPermsList = abrp.as();
            sharingList = new FastMap<>();
            for (AnalysisUserPermissions rup : aPermsList.getResourceUserPermissionsList()) {
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

    final AnalysisSharingView sharingView;
    private final SharingPermissionsPanel permissionsPanel;
    private final List<Analysis> selectedAnalysis;
    private Appearance appearance;
    private FastMap<List<Sharing>> sharingMap;
    private FastMap<List<JSONObject>> sharingList;
    private final JsonUtil jsonUtil;
    private final CollaboratorsUtil collaboratorsUtil;
    private final AnalysisServiceFacade aService;
    private AnalysisSharingAutoBeanFactory shareFactory = GWT.create(AnalysisSharingAutoBeanFactory.class);

    public AnalysisSharingPresenter(final AnalysisServiceFacade aService,
                                    final List<Analysis> selectedAnalysis,
                                    final AnalysisSharingView view,
                                    final CollaboratorsUtil collaboratorsUtil,
                                    final JsonUtil jsonUtil) {

        this.sharingView = view;
        this.aService = aService;
        this.jsonUtil = jsonUtil;
        this.collaboratorsUtil = collaboratorsUtil;
        this.selectedAnalysis = selectedAnalysis;
        this.appearance = GWT.create(Appearance.class);
        this.permissionsPanel =
                new SharingPermissionsPanel(this, getSelectedResourcesAsMap(this.selectedAnalysis));
        permissionsPanel.hidePermissionColumn();
        permissionsPanel.setExplainPanelVisibility(false);
        view.setPresenter(this);
        view.addShareWidget(permissionsPanel.asWidget());
        loadResources();
        loadPermissions();
    }

    private FastMap<SharedResource> getSelectedResourcesAsMap(List<Analysis> selectedAnalysis) {
        FastMap<SharedResource> resourcesMap = new FastMap<>();
        for (Analysis sr : selectedAnalysis) {
            resourcesMap.put(sr.getId(), new SharedResource(sr.getId(), sr.getName()));
        }
        return resourcesMap;
    }

    private List<String> buildAppsList(List<Sharing> shareList) {
        List<String> anaIds = new ArrayList<>();
        for (Sharing s : shareList) {
            anaIds.add(s.getId());
        }

        return anaIds;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(sharingView.asWidget());
   }

    @Override
    public void loadResources() {
        sharingView.setSelectedAnalysis(selectedAnalysis);
   }

    @Override
    public void loadPermissions() {
       permissionsPanel.mask();
       aService.getPermissions(selectedAnalysis,new LoadPermissionsCallback());

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
       AnalysisSharingRequestList request = buildSharingRequest();
       AnalysisUnsharingRequestList  unshareRequest = buildUnsharingRequest();

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

    private AnalysisSharingRequestList buildSharingRequest() {
        AutoBean<AnalysisSharingRequestList> sharingAbList =
                AutoBeanCodex.decode(shareFactory, AnalysisSharingRequestList.class, "{}");
        AnalysisSharingRequestList sharingRequestList = sharingAbList.as();

        FastMap<List<Sharing>> sharingMap = permissionsPanel.getSharingMap();

        List<AnalysisSharingRequest> requests = new ArrayList<>();
        if (sharingMap != null && sharingMap.size() > 0) {
            for (String userName : sharingMap.keySet()) {
                AutoBean<AnalysisSharingRequest> sharingAb =
                        AutoBeanCodex.decode(shareFactory, AnalysisSharingRequest.class, "{}");
                AnalysisSharingRequest sharingRequest = sharingAb.as();
                List<Sharing> shareList = sharingMap.get(userName);
                sharingRequest.setUser(userName);
                sharingRequest.setAnalysisPermissions(buildAnalysisPermissions(shareList));
                requests.add(sharingRequest);
            }

            sharingRequestList.setAnalysisSharingRequestList(requests);
            return sharingRequestList;

        } else {
            return null;
        }

    }

    private List<AnalysisPermission> buildAnalysisPermissions(List<Sharing> shareList) {
        List<AnalysisPermission> aPermList = new ArrayList<>();
        for (Sharing s : shareList) {
            AutoBean<AnalysisPermission>aPermAb =
                    AutoBeanCodex.decode(shareFactory, AnalysisPermission.class, "{}");
            AnalysisPermission aPerm = aPermAb.as();
            aPerm.setId(s.getId());
            aPerm.setPermission(getDefaultPermissions().toString());
            aPermList.add(aPerm);
        }
        return aPermList;
    }

    private AnalysisUnsharingRequestList buildUnsharingRequest() {
        AutoBean<AnalysisUnsharingRequestList> unsharingAbList =
                AutoBeanCodex.decode(shareFactory, AnalysisUnsharingRequestList.class, "{}");

        AnalysisUnsharingRequestList unsharingRequestList = unsharingAbList.as();

        FastMap<List<Sharing>> unSharingMap = permissionsPanel.getUnshareList();

        List<AnalysisUnsharingRequest> requests = new ArrayList<>();

        if (unSharingMap != null && unSharingMap.size() > 0) {
            for (String userName : unSharingMap.keySet()) {
                List<Sharing> shareList = unSharingMap.get(userName);
                AutoBean<AnalysisUnsharingRequest> unsharingAb =
                        AutoBeanCodex.decode(shareFactory, AnalysisUnsharingRequest.class, "{}");

                AnalysisUnsharingRequest unsharingRequest = unsharingAb.as();
                unsharingRequest.setUser(userName);
                unsharingRequest.setAnalyses(buildAnalysisList(shareList));
                requests.add(unsharingRequest);
            }
            unsharingRequestList.setAnalysisUnSharingRequestList(requests);
            return unsharingRequestList;
        } else {
            return null;
        }

    }

    private List<String> buildAnalysisList(List<Sharing> shareList) {
        List<String> anaList = new ArrayList<>();
        for (Sharing s : shareList) {
            anaList.add(s.getId());
        }

        return anaList;
    }


    private void callSharingService(AnalysisSharingRequestList obj) {
        aService.shareAnalyses(obj, new AsyncCallback<String>() {

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

    private void callUnshareService(AnalysisUnsharingRequestList obj) {
        aService.unshareAnalyses(obj, new AsyncCallback<String>() {

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
