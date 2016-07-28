package org.iplantc.de.apps.client.presenter.details;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.selection.AppDetailsDocSelected;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.DetailsHierarchyClicked;
import org.iplantc.de.apps.client.events.selection.SaveMarkdownSelected;
import org.iplantc.de.apps.client.gin.factory.AppDetailsViewFactory;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author jstroot
 */
public class AppDetailsViewPresenterImpl implements AppDetailsView.Presenter,
                                                    AppDetailsDocSelected.AppDetailsDocSelectedHandler,
                                                    SaveMarkdownSelected.SaveMarkdownSelectedHandler {

    @Inject AppUserServiceFacade appUserService;
    @Inject Provider<AppDetailsViewFactory> viewFactoryProvider;
    @Inject IplantAnnouncer announcer;
    @Inject AppDetailsView.AppDetailsAppearance appearance;
    @Inject EventBus eventBus;

    private AppDetailsView view;
    private AppDoc appDoc;

    @Inject
    AppDetailsViewPresenterImpl() {
    }

    @Override
    public HandlerRegistration addAppFavoriteSelectedEventHandlers(final AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler handler) {
        if(view == null){
            throw new IllegalStateException("You must call 'go(..)' before calling this method");
        }
        return view.addAppFavoriteSelectedEventHandlers(handler);
    }

    @Override
    public HandlerRegistration addAppRatingDeselectedHandler(AppRatingDeselected.AppRatingDeselectedHandler handler) {
        if(view == null){
            throw new IllegalStateException("You must call 'go(..)' before calling this method");
        }
        return view.addAppRatingDeselectedHandler(handler);
    }

    @Override
    public HandlerRegistration addAppRatingSelectedHandler(AppRatingSelected.AppRatingSelectedHandler handler) {
        if(view == null){
            throw new IllegalStateException("You must call 'go(..)' before calling this method");
        }
        return view.addAppRatingSelectedHandler(handler);
    }

    @Override
    public HandlerRegistration addDetailsHierarchyClickedHandler(DetailsHierarchyClicked.DetailsHierarchyClickedHandler handler) {
        if(view == null){
            throw new IllegalStateException("You must call 'go(..)' before calling this method");
        }
        return view.addDetailsHierarchyClickedHandler(handler);
    }

    @Override
    public void go(final HasOneWidget widget,
                   final App app,
                   final String searchRegexPattern,
                   TreeStore<OntologyHierarchy> hierarchyTreeStore) {
        Preconditions.checkState(view == null, "Cannot call go(..) more than once");

        view = viewFactoryProvider.get().create(app, searchRegexPattern, hierarchyTreeStore);
        view.addAppDetailsDocSelectedHandler(AppDetailsViewPresenterImpl.this);
        view.addSaveMarkdownSelectedHandler(AppDetailsViewPresenterImpl.this);
        widget.setWidget(view);

        eventBus.addHandler(AppUpdatedEvent.TYPE, view);

        // If the App has a wiki url, return before fetching app doc.
        if (!Strings.isNullOrEmpty(app.getWikiUrl())){
            return;
        }
        // Prefetch Docs
        appUserService.getAppDoc(app, new AsyncCallback<AppDoc>() {
            @Override
            public void onFailure(Throwable caught) {
                // warn only for public app
                if (app.isPublic()) {
                    announcer.schedule(new ErrorAnnouncementConfig(appearance.getAppDocError(caught)));
                }
            }

            @Override
            public void onSuccess(final AppDoc result) {
                appDoc = result;
            }
        });
    }

    @Override
    public void onAppDetailsDocSelected(AppDetailsDocSelected event) {
        if (Strings.isNullOrEmpty(event.getApp().getWikiUrl())) {
            Preconditions.checkNotNull(appDoc, "AppDoc should have been pre-fetched in go(..) method!");
            view.showDoc(appDoc);
        } else {
            Window.open(event.getApp().getWikiUrl(), "_blank", "");
        }
    }

    @Override
    public void onSaveMarkdownSelected(final SaveMarkdownSelected event) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(event.getEditorContent()));

        appUserService.saveAppDoc(event.getApp(),
                                  event.getEditorContent(), new AsyncCallback<AppDoc>() {
            @Override
            public void onFailure(Throwable caught) {
                event.getMaskable().unmask();
                announcer.schedule(new ErrorAnnouncementConfig(appearance.saveAppDocError(caught)));
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(final AppDoc result) {
                event.getMaskable().unmask();
                appDoc = result;
            }
        });
    }
}
