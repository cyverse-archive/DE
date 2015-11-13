/**
 * 
 * @author sriram
 */
package org.iplantc.de.apps.client.presenter.sharing;

import org.iplantc.de.apps.client.views.sharing.AppSharingView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.SharedResource;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.sharing.SharingPermissionsPanel;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.HasOneWidget;

import com.sencha.gxt.core.shared.FastMap;

import java.util.List;

public class AppSharingPresenter implements SharingPresenter {

    final AppSharingView view;
    private final SharingPermissionsPanel permissionsPanel;
    private final List<App> selectedApps;
    private Appearance appearance;
    private FastMap<List<Sharing>> dataSharingMap;
    private FastMap<List<JSONObject>> sharingList;
    private final JsonUtil jsonUtil;
    private final CollaboratorsUtil collaboratorsUtil;

    public AppSharingPresenter(final AppUserServiceFacade appService,
                               final List<App> selectedApps,
                               final AppSharingView view,
                               final CollaboratorsUtil collaboratorsUtil,
                               final JsonUtil jsonUtil) {

        this.view = view;
        view.setPresenter(this);
        this.jsonUtil = jsonUtil;
        this.collaboratorsUtil = collaboratorsUtil;
        this.selectedApps = selectedApps;
        this.permissionsPanel = new SharingPermissionsPanel(this,
                                                            getSelectedResourcesAsMap(selectedApps));
        view.addShareWidget(permissionsPanel.asWidget());

    }

    private FastMap<SharedResource> getSelectedResourcesAsMap(List<App> selectedResources) {
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
        // TODO: load perm here
    }

    @Override
    public PermissionValue getDefaultPermissions() {
        return PermissionValue.read;
    }

    @Override
    public void processRequest() {
        // TODO Auto-generated method stub

    }

}
