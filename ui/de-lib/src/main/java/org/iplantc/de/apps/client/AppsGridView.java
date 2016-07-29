package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.events.selection.OntologyHierarchySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.RunAppSelected;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * This view is responsible for displaying lists of {@link App}s resulting for {@link AppCategory}
 * selection or App searches.
 *
 * It is also responsible for relaying user-related events from the listed Apps, primarily selection
 * events.
 *
 * Created by jstroot on 3/5/15.
 * @author jstroot
 */
public interface AppsGridView extends IsWidget,
                                      IsMaskable,
                                      AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers,
                                      AppInfoSelectedEvent.HasAppInfoSelectedEventHandlers,
                                      AppNameSelectedEvent.HasAppNameSelectedEventHandlers,
                                      AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers,
                                      AppCommentSelectedEvent.HasAppCommentSelectedEventHandlers,
                                      AppRatingSelected.HasAppRatingSelectedEventHandlers,
                                      AppRatingDeselected.HasAppRatingDeselectedHandlers,
                                      AppSearchResultLoadEvent.AppSearchResultLoadEventHandler,
                                      AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                      AppFavoritedEvent.HasAppFavoritedEventHandlers,
                                      BeforeAppSearchEvent.BeforeAppSearchEventHandler,
                                      OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler{
    interface AppsGridAppearance {

        String appLaunchWithoutToolError();

        String appRemoveFailure();

        String beforeAppSearchLoadingMask();

        String favServiceFailure();

        String getAppsLoadingMask();

        String integratedByColumnLabel();

        String nameColumnLabel();

        String ratingColumnLabel();

        String searchAppResultsHeader(String searchText, int total);

        String agaveAuthRequiredTitle();

        String agaveAuthRequiredMsg();
    }

    /**
     * This presenter is responsible for updating/maintaining the {@code ListStore} associated with
     * the view. It fires store related events for other presenters. \
     *
     * To update the {@code ListStore}, it listens for {@link AppCategory}
     * selection and search result load events.
     */
    interface Presenter extends AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler,
                                StoreAddEvent.HasStoreAddHandlers<App>,
                                StoreRemoveEvent.HasStoreRemoveHandler<App>,
                                StoreUpdateEvent.HasStoreUpdateHandlers<App>,
                                StoreClearEvent.HasStoreClearHandler<App>,
                                AppFavoritedEvent.HasAppFavoritedEventHandlers,
                                DeleteAppsSelected.DeleteAppsSelectedHandler,
                                RunAppSelected.RunAppSelectedHandler,
                                OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler {
        App getSelectedApp();

        AppsGridView getView();
    }

    Grid<App> getGrid();

    void setSearchPattern(String searchPattern);

    void setHeadingText(String text);
}
