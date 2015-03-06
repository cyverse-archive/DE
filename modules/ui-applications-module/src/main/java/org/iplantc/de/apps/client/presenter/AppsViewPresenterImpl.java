package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.gin.factory.AppsViewFactory;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.util.JsonUtil;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.logging.Logger;

/**
 * The presenter for the AppsView.
 *
 * @author jstroot
 */
public class AppsViewPresenterImpl implements AppsView.Presenter {

    protected final AppsView view;
    @Inject JsonUtil jsonUtil;
    @Inject Provider<SubmitAppForPublicUseView.Presenter> submitAppForPublicPresenterProvider;
    private static String FAVORITES;
    private static String USER_APPS_GROUP;
    private static String WORKSPACE;
    private final Logger LOG = Logger.getLogger(AppsViewPresenterImpl.class.getName());
    private final AppCategoriesView.Presenter categoriesPresenter;
    private final AppsGridView.Presenter appsGridPresenter;
    private HasId desiredSelectedAppId;
    private RegExp searchRegex;

    @Inject
    protected AppsViewPresenterImpl(final AppsViewFactory viewFactory,
                                    final AppCategoriesView.Presenter categoriesPresenter,
                                    final AppsGridView.Presenter appsGridPresenter,
                                    final AppsToolbarView.Presenter toolbarPresenter) {
        this.categoriesPresenter = categoriesPresenter;
        this.appsGridPresenter = appsGridPresenter;
        this.view = viewFactory.create(categoriesPresenter,
                                       appsGridPresenter,
                                       toolbarPresenter);

        categoriesPresenter.getView().addAppCategorySelectedEventHandler(appsGridPresenter);
        categoriesPresenter.getView().addAppCategorySelectedEventHandler(appsGridPresenter.getView());

        // Wire up list store handlers
        appsGridPresenter.addStoreAddHandler(categoriesPresenter);
        appsGridPresenter.addStoreRemoveHandler(categoriesPresenter);
        appsGridPresenter.addAppFavoritedEventHandler(categoriesPresenter);

        toolbarPresenter.getView().addDeleteAppsSelectedHandler(appsGridPresenter);
        toolbarPresenter.getView().addCopyAppSelectedHandler(categoriesPresenter);
        toolbarPresenter.getView().addCopyWorkflowSelectedHandler(categoriesPresenter);
    }

    @Override
    public Grid<App> getAppsGrid() {
        // FIXME Too many levels of misdirection
        return appsGridPresenter.getView().getGrid();
    }

    @Override
    public App getSelectedApp() {
        return appsGridPresenter.getSelectedApp();
    }

    @Override
    public AppCategory getSelectedAppCategory() {
        return categoriesPresenter.getSelectedAppCategory();
    }

    @Override
    public void go(final HasOneWidget container,
                   final HasId selectedAppCategory,
                   final HasId selectedApp) {
        categoriesPresenter.go(selectedAppCategory);
        container.setWidget(view);
    }

    @Override
    public void go(final HasOneWidget container) {
        go(container, null, null);
    }

    @Override
    public AppsView.Presenter hideAppMenu() {
        view.hideAppMenu();
        return this;
    }

    @Override
    public AppsView.Presenter hideWorkflowMenu() {
        view.hideWorkflowMenu();
        return this;
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
    }

}
