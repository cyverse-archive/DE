package org.iplantc.de.apps.client.views.widgets;


import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class AppsViewToolbarImpl implements AppsViewToolbar {

    private static AppsViewToolbarUiBinder uiBinder = GWT.create(AppsViewToolbarUiBinder.class);

    @UiTemplate("AppsViewToolbar.ui.xml")
    interface AppsViewToolbarUiBinder extends UiBinder<Widget, AppsViewToolbarImpl> { }

    private final AppSearchAutoBeanFactory appSearchFactory;

    private final Widget widget;
    private Presenter presenter;
    private final AppSearchRpcProxy proxy;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;

    @UiField
    TextButton app_menu;
    
    @UiField
    TextButton wf_menu;

    @UiField
    MenuItem editApp;

    @UiField
    MenuItem createNewApp;

    @UiField
    MenuItem createWorkflow;

    @UiField
    MenuItem appRun;

    @UiField
    MenuItem requestTool;

    @UiField
    MenuItem copyApp;

    @UiField
    MenuItem deleteApp;

    @UiField
    MenuItem submitApp;

    @UiField
    MenuItem wfRun;

    @UiField
    MenuItem copyWf;

    @UiField
    MenuItem deleteWf;

    @UiField
    MenuItem editWf;

    @UiField
    MenuItem submitWf;

    @UiField
    AppSearchField appSearch;

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }

    @Inject
    public AppsViewToolbarImpl(final AppServiceFacade appService,
                               final AppSearchAutoBeanFactory appSearchFactory,
                               final AppAutoBeanFactory appFactory) {
        this.appSearchFactory = appSearchFactory;
        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory);
        loader = createPagingLoader();
        widget = uiBinder.createAndBindUi(this);
    }

    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader() {
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>(
                proxy);

        AppLoadConfig appLoadConfig = appSearchFactory.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler({"appRun", "wfRun"})
    public void appInfoClicked(SelectionEvent<Item> event) {
        presenter.onAppRunClick();
    }

    @UiHandler("requestTool")
    public void requestToolClicked(SelectionEvent<Item> event) {
        presenter.onRequestToolClicked();
    }

    @UiHandler({"copyApp", "copyWf"})
    public void copyClicked(SelectionEvent<Item> event) {
        presenter.onCopyClicked();
    }

    @UiHandler({"editApp", "editWf"})
    public void editClicked(SelectionEvent<Item> event) {
        presenter.onEditClicked();
    }

    @UiHandler({"deleteApp", "deleteWf"})
    public void deleteClicked(SelectionEvent<Item> event) {
        presenter.onDeleteClicked();
    }

    @UiHandler({"submitApp", "submitWf"})
    public void submitClicked(SelectionEvent<Item> event) {
        presenter.submitClicked();
    }

    @UiHandler("createNewApp")
    public void createNewAppClicked(SelectionEvent<Item> event) {
        presenter.createNewAppClicked();
    }

    @UiHandler("createWorkflow")
    public void createWorkflowClicked(SelectionEvent<Item> event) {
        presenter.createWorkflowClicked();
    }
    


    @Override
    public AppSearchRpcProxy getAppSearchRpcProxy() {
        return proxy;
    }

    @Override
    public void setEditAppMenuItemEnabled(boolean enabled) {
        editApp.setEnabled(enabled);

    }

    @Override
    public void setSubmitAppMenuItemEnabled(boolean enabled) {
        submitApp.setEnabled(enabled);

    }

    @Override
    public void setDeleteAppMenuItemEnabled(boolean enabled) {
        deleteApp.setEnabled(enabled);

    }

    @Override
    public void setCopyAppMenuItemEnabled(boolean enabled) {
        copyApp.setEnabled(enabled);

    }

    @Override
    public void setAppRunMenuItemEnabled(boolean enabled) {
        appRun.setEnabled(enabled);

    }

    @Override
    public void setAppMenuEnabled(boolean enabled) {
        app_menu.setEnabled(enabled);

    }

    @Override
    public void setWorkflowMenuEnabled(boolean enabled) {
        wf_menu.setEnabled(enabled);

    }

    @Override
    public void setEditWorkflowMenuItemEnabled(boolean enabled) {
        editWf.setEnabled(enabled);

    }

    @Override
    public void setSubmitWorkflowMenuItemEnabled(boolean enabled) {
        submitWf.setEnabled(enabled);

    }

    @Override
    public void setDeleteWorkflowMenuItemEnabled(boolean enabled) {
        deleteWf.setEnabled(enabled);

    }

    @Override
    public void setCopyWorkflowMenuItemEnabled(boolean enabled) {
        copyWf.setEnabled(enabled);

    }

    @Override
    public void setWorkflowRunMenuItemEnabled(boolean enabled) {
        wfRun.setEnabled(enabled);

    }

    @Override
    public void hideAppMenu() {
        app_menu.setVisible(false);

    }

    @Override
    public void hideWorkflowMenu() {
        wf_menu.setVisible(false);
    }
}
