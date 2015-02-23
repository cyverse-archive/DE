package org.iplantc.de.apps.client.views.widgets;


import static org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent.TYPE;

import org.iplantc.de.apps.client.events.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent.AppSearchResultLoadEventHandler;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent.HasAppSearchResultLoadEventHandlers;
import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.apps.shared.AppsModule.Ids;
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
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.ArrayList;
import java.util.List;

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
    @UiField
    BoxLayoutData boxData;

    private static AppsViewToolbarUiBinder uiBinder = GWT.create(AppsViewToolbarUiBinder.class);
    private final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    private final AppSearchRpcProxy proxy;
    private AppsView.Presenter presenter;

    @Inject
    public AppsViewToolbarImpl(final AppServiceFacade appService,
                               final IplantDisplayStrings displayStrings,
                               final AppSearchAutoBeanFactory appSearchFactory,
                               final AppAutoBeanFactory appFactory) {
        proxy = new AppSearchRpcProxy(appService, appSearchFactory, appFactory, displayStrings);
        loader = createPagingLoader(proxy, appSearchFactory);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        app_menu.ensureDebugId(baseID + Ids.MENU_ITEM_APPS);
        appRun.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_USE_APP);
        createNewApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_CREATE_APP);
        requestTool.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_REQUEST_TOOL);
        copyApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_COPY_APP);
        editApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_EDIT_APP);
        deleteApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_DELETE_APP);
        submitApp.ensureDebugId(baseID + Ids.MENU_ITEM_APPS + Ids.MENU_ITEM_SHARE_APP);

        wf_menu.ensureDebugId(baseID + Ids.MENU_ITEM_WF);
        wfRun.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_USE_WF);
        createWorkflow.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_CREATE_WF);
        copyWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_COPY_WF);
        editWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_EDIT_WF);
        deleteWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_DELETE_WF);
        submitWf.ensureDebugId(baseID + Ids.MENU_ITEM_WF + Ids.MENU_ITEM_SHARE_WF);

        appSearch.ensureDebugId(baseID + Ids.MENU_ITEM_SEARCH);

    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEventHandler handler) {
        return addHandler(handler, TYPE);
    }

    @UiHandler({"appRun", "wfRun"})
    public void appRunClicked(SelectionEvent<Item> event) {
        presenter.runSelectedApp();
    }

    @UiHandler({"copyApp", "copyWf"})
    public void copyClicked(SelectionEvent<Item> event) {
        // FIXME Split this up into separate calls.
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
        // KLUDGE:for CORE-5761 set flex to 0 so that search box shows up
        boxData.setFlex(0);
    }

    @Override
    public void hideWorkflowMenu() {
        wf_menu.setVisible(false);
        // KLUDGE:for CORE-5761 set flex to 0 so that search box shows up
        boxData.setFlex(0);
    }

    @Override
    public void init(final AppsView.Presenter presenter,
                     final AppsView appsView,
                     final AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                     final AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers hasAppCategorySelectionChangedEventHandlers) {
        this.presenter = presenter;
        addAppSearchResultLoadEventHandler(appsView);
        hasAppSelectionChangedEventHandlers.addAppSelectionChangedEventHandler(this);
        hasAppCategorySelectionChangedEventHandlers.addAppCategorySelectedEventHandler(this);
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
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
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
                final boolean isAppPublic = selectedApp.isPublic();
                final boolean isAppDisabled = selectedApp.isDisabled();
                final boolean isRunnable = selectedApp.isRunnable();
                final boolean isOwner = selectedApp.getIntegratorEmail() == UserInfo.getInstance()
                                                                                    .getEmail();

                deleteAppEnabled = isSingleStep && !isAppPublic;
                // allow owners to edit their app
                editAppEnabled = isSingleStep && isOwner;
                submitAppEnabled = isSingleStep && isRunnable && !isAppPublic;
                copyAppEnabled = isSingleStep;
                appRunEnabled = isSingleStep && !isAppDisabled;

                deleteWfEnabled = isMultiStep && !isAppPublic;
                editWfEnabled = isMultiStep && !isAppPublic;
                submitWfEnabled = isMultiStep && !isAppPublic;
                copyWfEnabled = isMultiStep;
                wfRunEnabled = isMultiStep && !isAppDisabled;
                break;
            default:
                final boolean containsSingleStepApp = containsSingleStepApp(appSelection);
                final boolean containsMultiStepApp = containsMultiStepApp(appSelection);
                final boolean allSelectedAppsPrivate = allAppsPrivate(appSelection);

                deleteAppEnabled = containsSingleStepApp && allSelectedAppsPrivate;
                editAppEnabled = false;
                submitAppEnabled = false;
                copyAppEnabled = false;
                appRunEnabled = false;

                deleteWfEnabled = containsMultiStepApp && allSelectedAppsPrivate;
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

    private boolean containsSingleStepApp(List<App> apps) {
        for (App app : apps) {
            if (app.getStepCount() == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean containsMultiStepApp(List<App> apps) {
        for (App app : apps) {
            if (app.getStepCount() > 1) {
                return true;
            }
        }
        return false;
    }

    private boolean allAppsPrivate(List<App> apps) {
        for (App app : apps) {
            if (app.isPublic()) {
                return false;
            }
        }
        return true;
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
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader = new PagingLoader<>(proxy);

        AppLoadConfig appLoadConfig = appSearchFactory.loadConfig().as();
        loader.useLoadConfig(appLoadConfig);

        return loader;
    }

}
