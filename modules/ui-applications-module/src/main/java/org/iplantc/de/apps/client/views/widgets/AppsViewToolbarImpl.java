package org.iplantc.de.apps.client.views.widgets;


import org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
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
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.List;

public class AppsViewToolbarImpl extends Composite implements AppsView.ViewMenu, AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers {

    @UiTemplate("AppsViewToolbar.ui.xml")
    interface AppsViewToolbarUiBinder extends UiBinder<Widget, AppsViewToolbarImpl> { }
    @UiField
    MenuItem appRun;
    @UiField
    AppSearchField appSearch;
    @UiField
    TextButton app_menu;
    @UiField
    MenuItem copyApp;
    @UiField
    MenuItem copyWf;
    @UiField
    MenuItem createNewApp;
    @UiField
    MenuItem createWorkflow;
    @UiField
    MenuItem deleteApp;
    @UiField
    MenuItem deleteWf;
    @UiField
    MenuItem editApp;
    @UiField
    MenuItem editWf;
    @UiField
    MenuItem requestTool;
    @UiField
    MenuItem submitApp;
    @UiField
    MenuItem submitWf;
    @UiField
    MenuItem wfRun;
    @UiField
    TextButton wf_menu;

    private static AppsViewToolbarUiBinder uiBinder = GWT.create(AppsViewToolbarUiBinder.class);
    private final AppSearchAutoBeanFactory appSearchFactory;
    private final UserInfo userInfo;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    private final AppSearchRpcProxy proxy;
    private final Widget widget;
    private AppsView.Presenter presenter;

    @Inject
    public AppsViewToolbarImpl(final AppServiceFacade appService,
                               final AppSearchAutoBeanFactory appSearchFactory,
                               final AppAutoBeanFactory appFactory,
                               final UserInfo userInfo) {
        this.appSearchFactory = appSearchFactory;
        this.userInfo = userInfo;
        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory);
        loader = createPagingLoader(proxy, appSearchFactory);
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEvent.AppSearchResultLoadEventHandler handler) {
        return addHandler(handler, AppSearchResultLoadEvent.TYPE);
    }

    @UiHandler({"appRun", "wfRun"})
    public void appRunClicked(SelectionEvent<Item> event) {
        presenter.runSelectedApp();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiHandler({"copyApp", "copyWf"})
    public void copyClicked(SelectionEvent<Item> event) {
        presenter.copySelectedApp();
    }

    @UiHandler("createNewApp")
    public void createNewAppClicked(SelectionEvent<Item> event) {
        presenter.createNewAppClicked();
    }

    @UiHandler("createWorkflow")
    public void createWorkflowClicked(SelectionEvent<Item> event) {
        presenter.createWorkflowClicked();
    }

    @UiHandler({"deleteApp", "deleteWf"})
    public void deleteClicked(SelectionEvent<Item> event) {
        presenter.deleteSelectedApps();
    }

    @UiHandler({"editApp", "editWf"})
    public void editClicked(SelectionEvent<Item> event) {
        presenter.editSelectedApp();
    }

    @Override
    public void init(final AppsView.Presenter presenter,
                     final AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                     final AppGroupSelectionChangedEvent.HasAppGroupSelectionChangedEventHandlers hasAppGroupSelectionChangedEventHandlers) {
        this.presenter = presenter;
        hasAppSelectionChangedEventHandlers.addAppSelectedEventHandler(this);
        hasAppGroupSelectionChangedEventHandlers.addAppGroupSelectedEventHandler(this);
        proxy.setHasHandlers(this);
    }

    @Override
    public void onAppGroupSelectionChanged(AppGroupSelectionChangedEvent event) {
        app_menu.setEnabled(false);
        wf_menu.setEnabled(false);
    }

    @Override
    public void onAppSelectionChanged(AppSelectionChangedEvent event) {
        app_menu.setEnabled(true);
        wf_menu.setEnabled(true);
        final List<App> appSelection = event.getAppSelection();
        switch (appSelection.size()){
            case 0:
                deleteApp.setEnabled(false);
                editApp.setEnabled(false);
                submitApp.setEnabled(false);
                copyApp.setEnabled(false);
                appRun.setEnabled(false);

                deleteWf.setEnabled(false);
                editWf.setEnabled(false);
                submitWf.setEnabled(false);
                copyWf.setEnabled(false);
                wfRun.setEnabled(false);
                break;
            case 1:
                final App selectedApp = appSelection.get(0);
                final boolean isSingleStep = selectedApp.getStepCount() == 1;
                final boolean isMultiStep = selectedApp.getStepCount() > 1;
                final boolean isAppPublic = !selectedApp.isPublic();
                final boolean isAppDisabled = selectedApp.isDisabled();
                final boolean isCurrentUserAppIntegrator = userInfo.getEmail().equals(selectedApp.getIntegratorEmail());


                deleteApp.setEnabled(isSingleStep && !isAppPublic);
//                editApp.setEnabled(isSingleStep && ((isAppPublic && isCurrentUserAppIntegrator) || !isAppPublic));
                editApp.setEnabled(isSingleStep && !isAppPublic);
                submitApp.setEnabled(isSingleStep && !isAppPublic);
                copyApp.setEnabled(isSingleStep);
                appRun.setEnabled(isSingleStep && !isAppDisabled);

                deleteWf.setEnabled(isMultiStep && !isAppPublic);
                editWf.setEnabled(isMultiStep && !isAppPublic);
                submitWf.setEnabled(isMultiStep && !isAppPublic);
                copyWf.setEnabled(isMultiStep);
                wfRun.setEnabled(isMultiStep && !isAppDisabled);
                break;
            default:
                // How does deleting workflows work?
                deleteApp.setEnabled(false);
                editApp.setEnabled(false);
                // TODO JDS Do we want to be able to do this?
                submitApp.setEnabled(false);
                // TODO JDS Do we want to be able to do this?
                copyApp.setEnabled(false);
                // TODO JDS Do we want to be able to do this?
                appRun.setEnabled(false);

                deleteWf.setEnabled(false);
                editWf.setEnabled(false);
                submitWf.setEnabled(false);
                copyWf.setEnabled(false);
                wfRun.setEnabled(false);
        }

    }

    @UiHandler("requestTool")
    public void requestToolClicked(SelectionEvent<Item> event) {
        presenter.onRequestToolClicked();
    }

    @UiHandler({"submitApp", "submitWf"})
    public void submitClicked(SelectionEvent<Item> event) {
        presenter.submitClicked();
    }

    @UiFactory
    AppSearchField createAppSearchField() {
        return new AppSearchField(loader);
    }
    
    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> createPagingLoader(final AppSearchRpcProxy proxy,
                                                                                           final AppSearchAutoBeanFactory appSearchFactory) {
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>(proxy);

        AppLoadConfig appLoadConfig = appSearchFactory.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

}
