package org.iplantc.admin.belphegor.client.apps.presenter;

import org.iplantc.admin.belphegor.client.apps.views.AppCategorizeView;
import org.iplantc.admin.belphegor.client.models.ToolIntegrationAdminProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.List;

public class AppCategorizePresenter implements AppCategorizeView.Presenter {

    private final AppCategorizeView view;
    private final App app;

    AppCategorizePresenter(AppCategorizeView view, final App app) {
        this.view = view;
        this.app = app;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void setAppGroups(List<AppGroup> children) {
        view.setAppGroups(children);

        // Remove trash and beta from the store.
        ToolIntegrationAdminProperties props = ToolIntegrationAdminProperties.getInstance();
        view.removeGroupWithId(props.getDefaultTrashAnalysisGroupId());

        List<AppGroup> selectedGroups = findPreSelectedGroups();
        if (selectedGroups != null && !selectedGroups.isEmpty()) {
            view.setSelectedGroups(selectedGroups);
        }
    }

    /**
     * Returns the AppGroups the App is currently listed under. Unless the App is listed in no other
     * groups except Beta, then the App integrator's suggested groups are returned.
     * 
     * @return List of AppGroups to pre-select in the view.
     */
    private List<AppGroup> findPreSelectedGroups() {
        List<AppGroup> appGroups = app.getGroups();
        if (appGroups == null || appGroups.isEmpty()) {
            return app.getSuggestedGroups();
        }

        String betaGroupId = ToolIntegrationAdminProperties.getInstance()
                .getDefaultBetaAnalysisGroupId();
        if (appGroups.size() == 1 && appGroups.get(0).getId().equals(betaGroupId)) {
            return app.getSuggestedGroups();
        }

        return appGroups;
    }

    public List<AppGroup> getSelectedGroups() {
        return view.getSelectedGroups();
    }
}
