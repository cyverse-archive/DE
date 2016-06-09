package org.iplantc.de.apps.client.presenter.grid;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.events.selection.RunAppSelected;
import org.iplantc.de.apps.client.gin.factory.AppsGridViewFactory;
import org.iplantc.de.apps.client.presenter.callbacks.DeleteRatingCallback;
import org.iplantc.de.apps.client.presenter.callbacks.RateAppCallback;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.view.dialogs.CommentsDialog;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.shared.AsyncProviderWrapper;
import org.iplantc.de.shared.exceptions.HttpRedirectException;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import java.util.List;

/**
 * @author jstroot
 */
public class AppsGridPresenterImpl implements AppsGridView.Presenter,
                                              AppNameSelectedEvent.AppNameSelectedEventHandler,
                                              AppRatingSelected.AppRatingSelectedHandler,
                                              AppRatingDeselected.AppRatingDeselectedHandler,
                                              AppCommentSelectedEvent.AppCommentSelectedEventHandler,
                                              AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler, AppUpdatedEvent.AppUpdatedEventHandler {

    final ListStore<App> listStore;
    @Inject IplantAnnouncer announcer;
    @Inject AppServiceFacade appService;
    @Inject AppUserServiceFacade appUserService;
    @Inject AppsGridView.AppsGridAppearance appearance;
    @Inject AsyncProviderWrapper<CommentsDialog> commentsDialogProvider;
    @Inject AppMetadataServiceFacade metadataFacade;
    @Inject UserInfo userInfo;
    private final EventBus eventBus;
    private final AppsGridView view;
    private App desiredSelectedApp;

    @Inject
    AppsGridPresenterImpl(final AppsGridViewFactory viewFactory,
                          final ListStore<App> listStore,
                          final EventBus eventBus) {
        this.listStore = listStore;
        this.eventBus = eventBus;
        this.view = viewFactory.create(listStore);

        this.view.addAppNameSelectedEventHandler(this);
        this.view.addAppRatingDeselectedHandler(this);
        this.view.addAppRatingSelectedHandler(this);
        this.view.addAppCommentSelectedEventHandlers(this);
        this.view.addAppFavoriteSelectedEventHandlers(this);

        eventBus.addHandler(AppUpdatedEvent.TYPE, this);
    }

    @Override
    public HandlerRegistration addAppFavoritedEventHandler(AppFavoritedEvent.AppFavoritedEventHandler eventHandler) {
        return view.addAppFavoritedEventHandler(eventHandler);
    }

    @Override
    public HandlerRegistration addStoreAddHandler(StoreAddEvent.StoreAddHandler<App> handler) {
        return listStore.addStoreAddHandler(handler);
    }

    @Override
    public HandlerRegistration addStoreClearHandler(StoreClearEvent.StoreClearHandler<App> handler) {
        return listStore.addStoreClearHandler(handler);
    }

    @Override
    public HandlerRegistration addStoreRemoveHandler(StoreRemoveEvent.StoreRemoveHandler<App> handler) {
        return listStore.addStoreRemoveHandler(handler);
    }

    @Override
    public HandlerRegistration addStoreUpdateHandler(StoreUpdateEvent.StoreUpdateHandler<App> handler) {
        return listStore.addStoreUpdateHandler(handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        throw new UnsupportedOperationException("Firing events on this presenter is not allowed.");
    }

    public App getDesiredSelectedApp() {
        return desiredSelectedApp;
    }

    @Override
    public App getSelectedApp() {
        return view.getGrid().getSelectionModel().getSelectedItem();
    }

    @Override
    public AppsGridView getView() {
        return view;
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        if (event.getAppCategorySelection().isEmpty()) {
            return;
        }
        Preconditions.checkArgument(event.getAppCategorySelection().size() == 1);
        view.mask(appearance.getAppsLoadingMask());

        final AppCategory appCategory = event.getAppCategorySelection().iterator().next();
        appService.getApps(appCategory, new AsyncCallback<List<App>>() {
            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof HttpRedirectException) {
                    MessageBox messageBox = new MessageBox(appearance.agaveAuthRequiredTitle(), appearance.agaveAuthRequiredMsg());
                    messageBox.setIcon(MessageBox.ICONS.info());
                    messageBox.show();
                } else {
                    ErrorHandler.post(caught);
                }
                view.unmask();
            }

            @Override
            public void onSuccess(final List<App> apps) {
                listStore.clear();
                listStore.addAll(apps);

                if (getDesiredSelectedApp() != null) {

                    view.getGrid().getSelectionModel().select(getDesiredSelectedApp(), false);

                } else if (listStore.size() > 0) {
                    // Select first app
                    view.getGrid().getSelectionModel().select(listStore.get(0), false);
                }
                setDesiredSelectedApp(null);
                view.unmask();
            }
        });
    }

    @Override
    public void onAppCommentSelectedEvent(AppCommentSelectedEvent event) {
        final App app = event.getApp();
        commentsDialogProvider.get(new AsyncCallback<CommentsDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig("Something happened while trying to manage comments. Please try again or contact support for help."));
            }

            @Override
            public void onSuccess(CommentsDialog result) {
                result.show(app,
                            app.getIntegratorEmail().equals(userInfo.getEmail()),
                            metadataFacade);
            }
        });
    }

    @Override
    public void onAppFavoriteSelected(AppFavoriteSelectedEvent event) {
        final App app = event.getApp();
        appUserService.favoriteApp(app, !app.isFavorite(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig(appearance.favServiceFailure()));
            }

            @Override
            public void onSuccess(Void result) {
                app.setFavorite(!app.isFavorite());
                eventBus.fireEvent(new AppFavoritedEvent(app));
                eventBus.fireEvent(new AppUpdatedEvent(app));
            }
        });
    }

    @Override
    public void onAppNameSelected(AppNameSelectedEvent event) {
        doRunApp(event.getSelectedApp());
    }

    @Override
    public void onAppRatingDeselected(final AppRatingDeselected event) {
        final App appToUnRate = event.getApp();
        appUserService.deleteRating(appToUnRate, new DeleteRatingCallback(appToUnRate,
                                                                          eventBus));
    }

    @Override
    public void onAppRatingSelected(final AppRatingSelected event) {
        final App appToRate = event.getApp();
        appUserService.rateApp(appToRate,
                               event.getScore(),
                               new RateAppCallback(appToRate,
                                                   eventBus));
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        view.setSearchPattern(event.getSearchPattern());
        listStore.clear();
        listStore.addAll(event.getResults());
    }

    @Override
    public void onAppUpdated(final AppUpdatedEvent event) {
        listStore.update(event.getApp());
    }

    @Override
    public void onDeleteAppsSelected(final DeleteAppsSelected event) {
        appUserService.deleteAppsFromWorkspace(event.getAppsToBeDeleted(),
                                               new AsyncCallback<Void>() {
                                                   @Override
                                                   public void onFailure(Throwable caught) {
                                                       ErrorHandler.post(appearance.appRemoveFailure(), caught);
                                                   }

                                                   @Override
                                                   public void onSuccess(Void result) {
                                                       for (App app : event.getAppsToBeDeleted()) {
                                                           listStore.remove(app);
                                                       }
                                                   }
                                               });
    }

    @Override
    public void onRunAppSelected(RunAppSelected event) {
        doRunApp(event.getApp());
    }

    public void setDesiredSelectedApp(final App desiredSelectedApp) {
        this.desiredSelectedApp = desiredSelectedApp;
    }

    void doRunApp(final App app) {
        if (app.isRunnable()) {
            if (!app.isDisabled()) {
                eventBus.fireEvent(new RunAppEvent(app));
            }
        } else {
            announcer.schedule(new ErrorAnnouncementConfig(appearance.appLaunchWithoutToolError()));
        }
    }
}
