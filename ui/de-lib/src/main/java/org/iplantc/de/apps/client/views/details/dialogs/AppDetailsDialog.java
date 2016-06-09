package org.iplantc.de.apps.client.views.details.dialogs;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.List;

/**
 * @author jstroot
 */
public class AppDetailsDialog extends IPlantDialog {

    @Inject AsyncProviderWrapper<AppDetailsView.Presenter> presenterProvider;

    @Inject
    AppDetailsDialog() {
        setModal(true);
        setResizable(false);
        setPixelSize(450, 300);
        getButtonBar().clear();
    }

    public void show(final App app,
                     final String searchRegexPattern,
                     final List<List<String>> appGroupHierarchies,
                     final AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler favoriteSelectedHandler,
                     final AppRatingSelected.AppRatingSelectedHandler ratingSelectedHandler,
                     final AppRatingDeselected.AppRatingDeselectedHandler ratingDeselectedHandler) {
        setHeadingText(app.getName());
        presenterProvider.get(new AsyncCallback<AppDetailsView.Presenter>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(final AppDetailsView.Presenter result) {
                result.go(AppDetailsDialog.this, app, searchRegexPattern, appGroupHierarchies);
                if(favoriteSelectedHandler != null){
                    result.addAppFavoriteSelectedEventHandlers(favoriteSelectedHandler);
                }
                if(ratingSelectedHandler != null){
                    result.addAppRatingSelectedHandler(ratingSelectedHandler);
                }
                if(ratingDeselectedHandler != null){
                    result.addAppRatingDeselectedHandler(ratingDeselectedHandler);
                }
            }
        });

        super.show();
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("This method is not supported. Use show(App) instead.");
    }

}
