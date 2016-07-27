package org.iplantc.de.admin.apps.client;

import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public interface AdminAppsGridView extends IsWidget,
                                           IsMaskable,
                                           AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers,
                                           AppInfoSelectedEvent.HasAppInfoSelectedEventHandlers,
                                           AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                           AppSearchResultLoadEvent.AppSearchResultLoadEventHandler,
                                           BeforeAppSearchEvent.BeforeAppSearchEventHandler,
                                           HierarchySelectedEvent.HierarchySelectedEventHandler {

    interface Appearance extends AppsGridView.AppsGridAppearance {

    }

    interface Presenter extends AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                DeleteAppsSelected.DeleteAppsSelectedHandler,
                                StoreRemoveEvent.HasStoreRemoveHandler<App>,
                                RestoreAppSelected.RestoreAppSelectedHandler,
                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler {

        interface Appearance extends AppsGridView.AppsGridAppearance {

            String confirmDeleteAppTitle();

            String confirmDeleteAppWarning();

            String deleteAppLoadingMask();

            String deleteApplicationError(String name);

            String restoreAppFailureMsg(String name);

            String restoreAppFailureMsgTitle();

            String restoreAppLoadingMask();


            String restoreAppSuccessMsg(String name, String s);

            String restoreAppSuccessMsgTitle();

            String saveAppLoadingMask();

            String updateApplicationError();

            String updateDocumentationSuccess();
        }

        AdminAppsGridView getView();

        App getAppFromElement(Element eventTarget);

        List<App> getSelectedApps();
    }

    Grid<App> getGrid();

    void clearAndAdd(List<App> apps);

    App getAppFromElement(Element as);

    List<App> getSelectedApps();

    void deselectAll();

    void removeApp(App selectedApp);
}
