/**
 * @author sriram
 */

package org.iplantc.de.analysis.client.presenter.sharing;

import org.iplantc.de.analysis.client.views.sharing.AnalysisSharingView;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.SharedResource;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.sharing.SharingPermissionsPanel;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.HasOneWidget;

import com.sencha.gxt.core.shared.FastMap;

import java.util.List;

public class AnalysisSharingPresenter implements SharingPresenter {

    final AnalysisSharingView view;
    private final SharingPermissionsPanel permissionsPanel;
    private final List<Analysis> selectedAnalysis;
    private Appearance appearance;
    private FastMap<List<Sharing>> dataSharingMap;
    private FastMap<List<JSONObject>> sharingList;
    private final JsonUtil jsonUtil;
    private final CollaboratorsUtil collaboratorsUtil;

    public AnalysisSharingPresenter(final AnalysisServiceFacade aService,
                                    final List<Analysis> selectedAnalysis,
                                    final AnalysisSharingView view,
                                    final CollaboratorsUtil collaboratorsUtil,
                                    final JsonUtil jsonUtil) {

        this.view = view;
        this.jsonUtil = jsonUtil;
        this.collaboratorsUtil = collaboratorsUtil;
        this.selectedAnalysis = selectedAnalysis;
        this.permissionsPanel =
                new SharingPermissionsPanel(this, getSelectedResourcesAsMap(this.selectedAnalysis));
        permissionsPanel.hidePermissionColumn();
        permissionsPanel.setExplainPanelVisibility(false);
        view.setPresenter(this);
        view.addShareWidget(permissionsPanel.asWidget());
    }

    private FastMap<SharedResource> getSelectedResourcesAsMap(List<Analysis> selectedAnalysis) {
        FastMap<SharedResource> resourcesMap = new FastMap<>();
        for (Analysis sr : selectedAnalysis) {
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
        view.setSelectedAnalysis(selectedAnalysis);

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
