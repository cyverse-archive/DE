package org.iplantc.de.apps.client;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent.AppCommentSelectedEventHandler;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppsView extends IsWidget,
                                  IsMaskable,
                                  AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers,
                                  AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers,
                                  AppFavoritedEvent.HasAppFavoritedEventHandlers,
                                  AppFavoritedEvent.AppFavoritedEventHandler,
                                  AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers,
                                  AppInfoSelectedEvent.HasAppInfoSelectedEventHandlers,
                                  AppNameSelectedEvent.HasAppNameSelectedEventHandlers,
                                  AppCommentSelectedEvent.HasAppCommentSelectedEventHandlers,
                                  AppRatingDeselected.HasAppRatingDeselectedHandlers,
                                  AppRatingSelected.HasAppRatingSelectedEventHandlers {

    interface AppsViewAppearance {

    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
                                       AppNameSelectedEvent.AppNameSelectedEventHandler,
                                       AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                       AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler,
                                       AppCommentSelectedEventHandler,
                                       AppRatingSelected.AppRatingSelectedHandler,
                                       AppRatingDeselected.AppRatingDeselectedHandler {

        List<List<String>> getGroupHierarchies(App app);

        void onCreateNewAppSelected();

        void onCreateNewWorkflowClicked();

        void onDeleteAppsSelected(List<App> currentSelection);

        void onEditAppSelected(App app);

        App getSelectedApp();

        AppCategory getSelectedAppCategory();

        void go(HasOneWidget container, HasId selectedAppCategory, HasId selectedApp);

        Grid<App> getAppsGrid();

        void cleanUp();

        Presenter hideAppMenu();

        Presenter hideWorkflowMenu();

        void onCopyAppSelected(List<App> currentSelection);

        void onCopyWorkFlowSelected(List<App> currentSelection);

        void onRequestToolClicked();

        void onRunAppSelected(App next);

        void setViewDebugId(String baseId);
    }

    AppsToolbarView getToolBar();

    void hideAppMenu();

    void hideWorkflowMenu();

    void setPresenter(final Presenter presenter);

    void maskCenterPanel(final String loadingMask);

    void unMaskCenterPanel();

    void maskWestPanel(String loadingMask);

    void unMaskWestPanel();

    void selectApp(String appId);

    void selectAppCategory(HasId appCategory);

    App getSelectedApp();

    AppCategory getSelectedAppCategory();

    void selectFirstApp();

    void selectFirstAppCategory();

    Grid<App> getAppsGrid();

    void expandAppCategories();

    List<App> getAllSelectedApps();

    AppCategory getAppCategoryFromElement(Element el);

    App getAppFromElement(Element el);

    void updateAppListHeading(String join);
}
