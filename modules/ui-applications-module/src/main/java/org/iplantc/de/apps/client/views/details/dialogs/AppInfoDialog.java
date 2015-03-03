package org.iplantc.de.apps.client.views.details.dialogs;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.inject.Inject;

public class AppInfoDialog extends IPlantDialog implements AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler {

    @Inject AppDetailsView.Presenter presenter;

    @Inject
    AppInfoDialog() {
        setModal(true);
        setResizable(false);
        setPixelSize(450, 300);
    }

    public void show(final App app) {
        setHeadingText(app.getName());
        // FIXME Need to reconcile search text highlighting

        presenter.go(this, app, searchRegexPattern, appGroupHierarchies);

    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("This method is not supported. Use show(App) instead.")
    }

    @Override
    public void onAppFavoriteSelected(AppFavoriteSelectedEvent event) {
        //
    }
}
