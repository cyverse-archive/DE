package org.iplantc.de.admin.apps.client;

import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public interface AdminAppsGridView extends IsWidget,
                                           IsMaskable,
                                           AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers,
                                           AppNameSelectedEvent.HasAppNameSelectedEventHandlers,
                                           AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler {

    interface Presenter extends AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                DeleteAppsSelected.DeleteAppsSelectedHandler,
                                StoreRemoveEvent.HasStoreRemoveHandler<App>,
                                RestoreAppSelected.RestoreAppSelectedHandler {

        interface Appearance {

            String confirmDeleteAppTitle();

            String confirmDeleteAppWarning();

            String deleteAppLoadingMask();

            String deleteApplicationError(String name);

            String restoreAppFailureMsg(String name);

            String restoreAppFailureMsgTitle();


            String restoreAppSuccessMsg(String name, String s);

            String restoreAppSuccessMsgTitle();
        }

        AdminAppsGridView getView();
    }

    Grid<App> getGrid();
}
