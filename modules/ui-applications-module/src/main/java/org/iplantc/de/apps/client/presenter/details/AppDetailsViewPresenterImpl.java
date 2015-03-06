package org.iplantc.de.apps.client.presenter.details;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.events.selection.AppDetailsDocSelected;
import org.iplantc.de.apps.client.events.selection.SaveMarkdownSelected;
import org.iplantc.de.apps.client.gin.factory.AppDetailsViewFactory;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

/**
 * @author jstroot
 */
public class AppDetailsViewPresenterImpl implements AppDetailsView.Presenter,
                                                    AppDetailsDocSelected.AppDetailsDocSelectedHandler,
                                                    SaveMarkdownSelected.SaveMarkdownSelectedHandler {

    private class AppDetailsCallback implements AsyncCallback<App> {
        private final HasOneWidget widget;
        private final AppUserServiceFacade appUserService;

        public AppDetailsCallback(final HasOneWidget widget,
                                  final AppUserServiceFacade appUserService) {
            this.widget = widget;
            this.appUserService = appUserService;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(final App result) {

            if(!Strings.isNullOrEmpty(result.getWikiUrl())){
                return;
            }
            // If wikiUrl is null or empty, prefetch App Doc
            appUserService.getAppDoc(result, new AsyncCallback<AppDoc>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }

                @Override
                public void onSuccess(AppDoc result) {
                    appDoc = result;
                }
            });
        }
    }

    @Inject AppUserServiceFacade appUserService;
    @Inject Provider<AppDetailsDialog> appInfoDialogProvider;
    @Inject Provider<AppDetailsViewFactory> viewFactoryProvider;
    private AppDetailsView view;
    private AppDoc appDoc;

    @Inject
    AppDetailsViewPresenterImpl() {
    }

    @Override
    public void go(final HasOneWidget widget,
                   final App app,
                   final String searchRegexPattern,
                   final List<List<String>> appGroupHierarchies) {
        Preconditions.checkState(view == null, "Cannot call go(..) more than once");

        view = viewFactoryProvider.get().create(app, searchRegexPattern, appGroupHierarchies);
        view.addAppDetailsDocSelectedHandler(this);
        view.addSaveMarkdownSelectedHandler(this);
        // Fetch app details, Callback chains with grabbing app doc
        appUserService.getAppDetails(app, new AppDetailsCallback(widget, appUserService));
        widget.setWidget(view);
    }

    @Override
    public void onAppDetailsDocSelected(AppDetailsDocSelected event) {
        if (Strings.isNullOrEmpty(event.getApp().getWikiUrl())) {
            Preconditions.checkNotNull(appDoc, "AppDoc should have been prefetched in go(..) method!");
            view.showDoc(appDoc);
        } else {
            Window.open(event.getApp().getWikiUrl(), "_blank", "");
        }
    }

    @Override
    public void onSaveMarkdownSelected(SaveMarkdownSelected event) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(event.getEditorContent()));

        appUserService.saveAppDoc(event.getApp().getId(), event.getEditorContent(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(String result) {
                // FIXME consider firing global event
            }
        });
    }
}
