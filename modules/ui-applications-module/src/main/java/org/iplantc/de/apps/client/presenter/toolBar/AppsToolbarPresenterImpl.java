package org.iplantc.de.apps.client.presenter.toolBar;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewWorkflowEvent;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.selection.CreateNewAppSelected;
import org.iplantc.de.apps.client.events.selection.CreateNewWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.EditAppSelected;
import org.iplantc.de.apps.client.events.selection.EditWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.RequestToolSelected;
import org.iplantc.de.apps.client.gin.factory.AppsToolbarViewFactory;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.tools.requests.client.views.dialogs.NewToolRequestDialog;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * TODO Search will stay here until it is necessary to fold it out
 *
 * @author jstroot
 */
public class AppsToolbarPresenterImpl implements AppsToolbarView.Presenter,
                                                 CreateNewAppSelected.CreateNewAppSelectedHandler,
                                                 CreateNewWorkflowSelected.CreateNewWorkflowSelectedHandler,
                                                 EditAppSelected.EditAppSelectedHandler,
                                                 RequestToolSelected.RequestToolSelectedHandler,
                                                 EditWorkflowSelected.EditWorkflowSelectedHandler {

    protected PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    @Inject IplantAnnouncer announcer;
    @Inject AppsToolbarView.AppsToolbarAppearance appearance;
    @Inject EventBus eventBus;
    @Inject Provider<NewToolRequestDialog> newToolRequestDialogProvider;
    @Inject UserInfo userInfo;
    private final AppUserServiceFacade appService;
    private final AppSearchRpcProxy proxy;
    private final AppsToolbarView view;

    @Inject
    AppsToolbarPresenterImpl(final AppUserServiceFacade appService,
                             final AppsToolbarViewFactory viewFactory) {
        this.appService = appService;
        proxy = new AppSearchRpcProxy(appService);
        loader = new PagingLoader<>(proxy);
        view = viewFactory.create(loader);
        proxy.setHasHandlers(view);

        view.addCreateNewAppSelectedHandler(this);
        view.addCreateNewWorkflowSelectedHandler(this);
        view.addEditAppSelectedHandler(this);
        view.addRequestToolSelectedHandler(this);
        view.addEditWorkflowSelectedHandler(this);
    }

    @Override
    public HandlerRegistration addAppSearchResultLoadEventHandler(AppSearchResultLoadEvent.AppSearchResultLoadEventHandler handler) {
        return view.addAppSearchResultLoadEventHandler(handler);
    }

    @Override
    public HandlerRegistration addBeforeLoadHandler(BeforeLoadEvent.BeforeLoadHandler<FilterPagingLoadConfig> handler) {
        return loader.addBeforeLoadHandler(handler);
    }

    @Override
    public AppsToolbarView getView() {
        return view;
    }

    @Override
    public void onCreateNewAppSelected(CreateNewAppSelected event) {
        eventBus.fireEvent(new CreateNewAppEvent());
    }

    @Override
    public void onCreateNewWorkflowSelected(CreateNewWorkflowSelected event) {
        eventBus.fireEvent(new CreateNewWorkflowEvent());
    }

    @Override
    public void onEditAppSelected(EditAppSelected event) {
        final App app = event.getApp();

        boolean isAppPublished = app.isPublic();
        boolean isCurrentUserAppIntegrator = userInfo.getEmail().equals(app.getIntegratorEmail());

        eventBus.fireEvent(new EditAppEvent(app, isAppPublished && isCurrentUserAppIntegrator));
    }

    @Override
    public void onEditWorkflowSelected(final EditWorkflowSelected event) {
        final App workFlow = event.getWorkFlow();
        appService.editWorkflow(workFlow, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.failToRetrieveApp(), caught);
                announcer.schedule(new ErrorAnnouncementConfig(appearance.failToRetrieveApp()));
            }

            @Override
            public void onSuccess(String result) {
                Splittable serviceWorkflowJson = StringQuoter.split(result);
                eventBus.fireEvent(new EditWorkflowEvent(workFlow, serviceWorkflowJson));
            }
        });
    }

    @Override
    public void onRequestToolSelected(RequestToolSelected event) {
        newToolRequestDialogProvider.get().show();
    }
}
