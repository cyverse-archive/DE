package org.iplantc.de.admin.desktop.client.apps.presenter;

import org.iplantc.de.admin.apps.client.AppCategorizeView;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.List;

/**
 * @author jstroot
 */
public class AppCategorizePresenter implements AppCategorizeView.Presenter {

    private final DEProperties properties;
    private final AppCategorizeView view;
    private final App app;

    public AppCategorizePresenter(final AppCategorizeView view,
                           final App app,
                           final DEProperties properties) {
        this.view = view;
        this.app = app;
        this.properties = properties;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void setAppCategories(List<AppCategory> children) {
        view.setAppCategories(children);

        // Remove trash and beta from the store.
        DEProperties props = properties;
        view.removeCategoryWithId(props.getDefaultTrashAppCategoryId());

        List<AppCategory> selectedGroups = findPreSelectedGroups();
        if (selectedGroups != null && !selectedGroups.isEmpty()) {
            view.setSelectedCategories(selectedGroups);
        }
    }

    /**
     * Returns the AppCategories the App is currently listed under. Unless the App is listed in no other
     * groups except Beta, then the App integrator's suggested groups are returned.
     * 
     * @return List of AppCategories to pre-select in the view.
     */
    private List<AppCategory> findPreSelectedGroups() {
        List<AppCategory> appCategories = app.getGroups();
        if (appCategories == null || appCategories.isEmpty()) {
            return app.getSuggestedGroups();
        }

        String betaGroupId = properties
                .getDefaultBetaCategoryId();
        if (appCategories.size() == 1 && appCategories.get(0).getId().equals(betaGroupId)) {
            return app.getSuggestedGroups();
        }

        return appCategories;
    }

    public List<AppCategory> getSelectedCategories() {
        return view.getSelectedCategories();
    }
}
