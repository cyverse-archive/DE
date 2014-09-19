package org.iplantc.admin.belphegor.client.apps.views;

import static org.iplantc.de.apps.client.events.AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers;
import org.iplantc.de.apps.client.events.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by jstroot on 4/21/14.
 */
public interface AdminAppsView extends AppsView {
    public interface AdminPresenter extends Presenter {

        boolean canMoveAppCategory(AppCategory parentCategory, AppCategory childCategory);

        boolean canMoveApp(final AppCategory parentGroup, final App app);

        void moveAppCategory(final AppCategory parentCategory, final AppCategory childCategory);

        void moveApp(final AppCategory parentCategory, final App app);

        void onAddAppCategoryClicked();

        void onRenameAppCategoryClicked();

        void onDeleteClicked();

        void onRestoreAppClicked();

        void onCategorizeAppClicked();

    }

    interface Toolbar extends IsWidget {
        void init(AdminPresenter presenter,
                  AdminAppsView appView, final HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers,
                  final AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers hasAppCategorySelectionChangedEventHandlers);
    }
}
