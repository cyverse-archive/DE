package org.iplantc.de.apps.client.presenter.grid;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.gin.factory.AppsGridViewFactory;
import org.iplantc.de.apps.client.models.AppModelKeyProvider;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.view.dialogs.CommentsDialog;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;

import java.util.List;

/**
 * @author jstroot
 */
public class AppsGridPresenterImpl implements AppsGridView.Presenter,
                                              AppNameSelectedEvent.AppNameSelectedEventHandler,
                                              AppRatingSelected.AppRatingSelectedHandler,
                                              AppRatingDeselected.AppRatingDeselectedHandler,
                                              AppCommentSelectedEvent.AppCommentSelectedEventHandler,
                                              AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler {

    //<editor-fold desc="Callbacks">
    private static class DeleteRatingCallback implements AsyncCallback<AppFeedback> {
        private final App appToUnRate;
        private final ListStore<App> listStore;

        public DeleteRatingCallback(final App appToUnRate,
                                    final ListStore<App> listStore) {
            this.appToUnRate = appToUnRate;
            this.listStore = listStore;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(AppFeedback result) {
            appToUnRate.setRating(result);

            // Update app in list store, this should update stars
            listStore.update(appToUnRate);
        }
    }

    private static class RateAppCallback implements AsyncCallback<String> {
        private final App appToRate;
        private final AppRatingSelected event;
        private final ListStore<App> listStore;

        public RateAppCallback(final App appToRate,
                               final AppRatingSelected event,
                               final ListStore<App> listStore) {
            this.appToRate = appToRate;
            this.event = event;
            this.listStore = listStore;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(String result) {
            appToRate.getRating().setUserRating(event.getScore());

            // Update app in list store, this should update stars
            listStore.update(appToRate);
        }
    }
    //</editor-fold>

    @Inject IplantAnnouncer announcer;
    @Inject EventBus eventBus;
    @Inject AppUserServiceFacade appUserService;
    @Inject UserInfo userInfo;
    @Inject AsyncProvider<CommentsDialog> commentsDialogProvider;
    @Inject AppMetadataServiceFacade metadataFacade;

    private final ListStore<App> listStore;
    private final AppsGridView view;
    private App desiredSelectedApp;

    @Inject
    AppsGridPresenterImpl(final AppsGridViewFactory viewFactory) {
        this.listStore = new ListStore<>(new AppModelKeyProvider());
        this.view = viewFactory.create(listStore);

        this.view.addAppNameSelectedEventHandler(this);
        this.view.addAppRatingDeselectedHandler(this);
        this.view.addAppRatingSelectedHandler(this);
        this.view.addAppCommentSelectedEventHandlers(this);
        this.view.addAppFavoriteSelectedEventHandlers(this);
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

    @Override
    public App getSelectedApp() {
        return view.getGrid().getSelectionModel().getSelectedItem();
    }

    @Override
    public AppsGridView getView() {
        return view;
    }

    public App getDesiredSelectedApp() {
        return desiredSelectedApp;
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        if(event.getAppCategorySelection().isEmpty()){
            return;
        }
        Preconditions.checkArgument(event.getAppCategorySelection().size() == 1);
        view.mask(I18N.DISPLAY.loadingMask());

        final AppCategory appCategory = event.getAppCategorySelection().iterator().next();
        // FIXME Update service signature
        appUserService.getApps(appCategory.getId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(String result) {
                // FIXME Update the service signature
                AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
                AutoBean<AppList> bean = AutoBeanCodex.decode(factory, AppList.class, result);
                List<App> apps = bean.as().getApps();

                listStore.clear();
                listStore.addAll(apps);

                if (getDesiredSelectedApp() != null) {

                    view.getGrid().getSelectionModel().select(getDesiredSelectedApp(), false);

                } else {
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
        appUserService.favoriteApp(userInfo.getWorkspaceId(), app.getId(), !app.isFavorite(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig(I18N.ERROR.favServiceFailure()));
            }

            @Override
            public void onSuccess(String result) {
                app.setFavorite(!app.isFavorite());
                listStore.update(app);
                view.asWidget().fireEvent(new AppFavoritedEvent(app));
            }
        });
    }

    @Override
    public void onAppNameSelected(AppNameSelectedEvent event) {
        final App app = event.getSelectedApp();
                if (app.isRunnable()) {
            if (!app.isDisabled()) {
                eventBus.fireEvent(new RunAppEvent(app));
            }
        } else {
            announcer.schedule(new ErrorAnnouncementConfig(I18N.ERROR.appLaunchWithoutToolError()));
        }
    }

    @Override
    public void onAppRatingDeselected(final AppRatingDeselected event) {
        final App appToUnRate = event.getApp();
        appUserService.deleteRating(appToUnRate, new DeleteRatingCallback(appToUnRate, listStore));
    }

    @Override
    public void onAppRatingSelected(final AppRatingSelected event) {

        final App appToRate = event.getApp();
        appUserService.rateApp(appToRate,
                               event.getScore(),
                               new RateAppCallback(appToRate, event, listStore));
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {

        // Tell view to update header
    }

    @Override
    public void onDeleteAppsSelected(final DeleteAppsSelected event) {

        List<String> appIds = Lists.newArrayList();
        for (App app : event.getAppsToBeDeleted()) {
            appIds.add(app.getId());
        }
        // FIXME Update service signature
        appUserService.deleteAppFromWorkspace(userInfo.getUsername(),
                                              userInfo.getFullUsername(),
                                              appIds,
                                              new AsyncCallback<String>() {
                                                  @Override
                                                  public void onFailure(Throwable caught) {
                                                      ErrorHandler.post(I18N.ERROR.appRemoveFailure(), caught);

                                                  }

                                                  @Override
                                                  public void onSuccess(String result) {
                                                      for(App app : event.getAppsToBeDeleted()){
                                                          listStore.remove(app);
                                                      }
                                                  }
                                              });
    }

    public void setDesiredSelectedApp(final App desiredSelectedApp) {
        this.desiredSelectedApp = desiredSelectedApp;
    }
}
