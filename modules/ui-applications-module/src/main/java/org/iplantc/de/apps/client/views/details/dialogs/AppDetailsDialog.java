package org.iplantc.de.apps.client.views.details.dialogs;

import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.gin.factory.AppDetailsViewPresenterFactory;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * FIXME Fix favorite select wiring
 * @author jstroot
 */
public class AppDetailsDialog extends IPlantDialog implements AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler,
                                                              AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers {

    @Inject AsyncProvider<AppDetailsViewPresenterFactory> presenterFactoryProvider;

    @Inject
    AppDetailsDialog() {
        setModal(true);
        setResizable(false);
        setPixelSize(450, 300);
        getButtonBar().clear();
    }

    @Override
    public HandlerRegistration addAppFavoriteSelectedEventHandlers(AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler handler) {
        // FIXME
        return null;
    }

    public void show(final App app,
                     final String searchRegexPattern,
                     final AppsView.Presenter appsViewPresenter) {
        setHeadingText(app.getName());
        presenterFactoryProvider.get(new AsyncCallback<AppDetailsViewPresenterFactory>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(AppDetailsViewPresenterFactory result) {
                result.create(appsViewPresenter).go(AppDetailsDialog.this, app, searchRegexPattern);
            }
        });
        /*
         * This show() method will be executed BEFORE any of the service calls initiated in the
         * presenter.go(..) method completed. This could be a candidate case for using an event
         * (from the presenter, handled in this class), or a command.
         *
         * An event would be preferable.
         */
        super.show();
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("This method is not supported. Use show(App) instead.");
    }

    @Override
    public void onAppFavoriteSelected(AppFavoriteSelectedEvent event) {
        //
    }
}
