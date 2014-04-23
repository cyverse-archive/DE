package org.iplantc.de.apps.client.views.widgets;


import static org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent.*;
import org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.ArrayList;

public class AppsViewToolbarImpl extends Composite implements AppsView.ViewMenu, HasAppSearchResultLoadEventHandlers {

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
    private final UserInfo userInfo;
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    private final AppSearchRpcProxy proxy;
    private final Widget widget;
    private AppsView.Presenter presenter;
    private AppsView appsView;

    @Inject
    public AppsViewToolbarImpl(final AppServiceFacade appService,
                               final IplantDisplayStrings displayStrings,
                               final AppSearchAutoBeanFactory appSearchFactory,
                               final AppAutoBeanFactory appFactory,
                               final UserInfo userInfo) {
        this.userInfo = userInfo;
        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory, displayStrings);
        loader = createPagingLoader(proxy, appSearchFactory);
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEventHandler handler) {
        return addHandler(handler, TYPE);
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
    public void hideAppMenu() {
        app_menu.setVisible(false);
    }

    @Override
    public void hideWorkflowMenu() {
        wf_menu.setVisible(false);
    }

    @Override
    public void init(final AppsView.Presenter presenter,
                     final AppsView appsView,
                     final AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                     final AppGroupSelectionChangedEvent.HasAppGroupSelectionChangedEventHandlers hasAppGroupSelectionChangedEventHandlers) {
        this.presenter = presenter;
        this.appsView = appsView;
        addAppSearchResultLoadEventHandler(appsView);
        hasAppSelectionChangedEventHandlers.addAppSelectionChangedEventHandler(this);
        hasAppGroupSelectionChangedEventHandlers.addAppGroupSelectedEventHandler(this);
        proxy.setHasHandlers(this);
        proxy.setMaskable(new IsMaskable() {
            @Override
            public void mask(String loadingMask) {
                appsView.maskCenterPanel(loadingMask);
            }

            @Override
            public void unmask() {
                appsView.unMaskCenterPanel();
            }
        });

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

        // Filter out any null items
        final ArrayList<App> appSelection = Lists.newArrayList(Iterables.filter(event.getAppSelection(), Predicates.notNull()));

        boolean deleteAppEnabled, editAppEnabled, submitAppEnabled, copyAppEnabled, appRunEnabled;
        boolean deleteWfEnabled, editWfEnabled, submitWfEnabled, copyWfEnabled, wfRunEnabled;

        switch (appSelection.size()){
            case 0:
                deleteAppEnabled = false;
                editAppEnabled = false;
                submitAppEnabled = false;
                copyAppEnabled = false;
                appRunEnabled = false;

                deleteWfEnabled = false;
                editWfEnabled = false;
                submitWfEnabled = false;
                copyWfEnabled = false;
                wfRunEnabled = false;

                break;
            case 1:
                final App selectedApp = appSelection.get(0);
                final boolean isSingleStep = selectedApp.getStepCount() == 1;
                final boolean isMultiStep = selectedApp.getStepCount() > 1;
                final boolean isAppPublic = !selectedApp.isPublic();
                final boolean isAppDisabled = selectedApp.isDisabled();

                deleteAppEnabled = isSingleStep && !isAppPublic;
                editAppEnabled = isSingleStep && !isAppPublic;
                submitAppEnabled = isSingleStep && !isAppPublic;
                copyAppEnabled = isSingleStep;
                appRunEnabled = isSingleStep && !isAppDisabled;

                deleteWfEnabled = isMultiStep && !isAppPublic;
                editWfEnabled = isMultiStep && !isAppPublic;
                submitWfEnabled = isMultiStep && !isAppPublic;
                copyWfEnabled = isMultiStep;
                wfRunEnabled = isMultiStep && !isAppDisabled;
                break;
            default:
                // How does deleting workflows work?

                deleteAppEnabled = false;
                editAppEnabled = false;
                // TODO JDS Do we want to be able to do this?
                submitAppEnabled = false;
                // TODO JDS Do we want to be able to do this?
                copyAppEnabled = false;
                // TODO JDS Do we want to be able to do this?
                appRunEnabled = false;

                deleteWfEnabled = false;
                editWfEnabled = false;
                submitWfEnabled = false;
                copyWfEnabled = false;
                wfRunEnabled = false;
        }
        deleteApp.setEnabled(deleteAppEnabled);
        editApp.setEnabled(editAppEnabled);
        submitApp.setEnabled(submitAppEnabled);
        copyApp.setEnabled(copyAppEnabled);
        appRun.setEnabled(appRunEnabled);

        deleteWf.setEnabled(deleteWfEnabled);
        editWf.setEnabled(editWfEnabled);
        submitWf.setEnabled(submitWfEnabled);
        copyWf.setEnabled(copyWfEnabled);
        wfRun.setEnabled(wfRunEnabled);
    }

    @UiHandler("requestTool")
    public void requestToolClicked(SelectionEvent<Item> event) {
        presenter.onRequestToolClicked();
    }

    @UiHandler({"submitApp", "submitWf"})
    public void submitClicked(SelectionEvent<Item> event) {
        presenter.submitClicked();
        appsView.submitSelectedApp();
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
