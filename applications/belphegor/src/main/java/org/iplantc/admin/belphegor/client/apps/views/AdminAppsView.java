package org.iplantc.admin.belphegor.client.apps.views;

import static org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent.HasAppGroupSelectionChangedEventHandlers;
import static org.iplantc.de.apps.client.events.AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by jstroot on 4/21/14.
 */
public interface AdminAppsView extends AppsView {
    public interface AdminPresenter extends Presenter {

        boolean canMoveAppGroup(AppGroup parentGroup, AppGroup childGroup);

        boolean canMoveApp(final AppGroup parentGroup, final App app);

        void moveAppGroup(final AppGroup parentGroup, final AppGroup childGroup);

        void moveApp(final AppGroup parentGroup, final App app);

        void onAddAppGroupClicked();

        void onRenameAppGroupClicked();

        void onDeleteClicked();

        void onRestoreAppClicked();

        void onCategorizeAppClicked();

    }

    interface Toolbar extends IsWidget {
        void init(AdminPresenter presenter,
                  final HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                  final HasAppGroupSelectionChangedEventHandlers hasAppGroupSelectionChangedEventHandlers);
    }
}
