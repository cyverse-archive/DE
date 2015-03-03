package org.iplantc.de.apps.client.presenter.details;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.views.details.dialogs.AppInfoDialog;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

/**
 * @author jstroot
 */
public class AppDetailsViewPresenterImpl implements AppDetailsView.Presenter {

    private static class AppDetailsCallback implements AsyncCallback<App> {
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
            appUserService.getAppDoc(result, new AsyncCallback<AppDoc>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                }

                @Override
                public void onSuccess(AppDoc result) {



                }
            });
        }
    }

    @Inject AppUserServiceFacade appUserService;
    @Inject Provider<AppInfoDialog> appInfoDialogProvider;

    @Inject
    AppDetailsViewPresenterImpl() {

    }

    @Override
    public AppDetailsView getView() {
        return null;
    }

    @Override
    public void go(HasOneWidget widget, App app, String searchRegexPattern,
                   List<List<String>> appGroupHierarchies) {

        appUserService.getAppDetails(app, new AppDetailsCallback(widget, appUserService));
    }

}
