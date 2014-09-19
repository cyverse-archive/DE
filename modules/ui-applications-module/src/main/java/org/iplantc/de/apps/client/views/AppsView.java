package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.events.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppCommentSelectedEvent.AppCommentSelectedEventHandler;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent.AppSelectionChangedEventHandler;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers;
import org.iplantc.de.apps.client.views.cells.AppFavoriteCell;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent.AppSearchResultLoadEventHandler;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

public interface AppsView extends IsWidget,
                                  AppSearchResultLoadEventHandler,
                                  AppFavoritedEvent.AppFavoritedEventHandler {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
                                       AppNameSelectedEvent.AppNameSelectedEventHandler,
                                       AppSearchResultLoadEventHandler,
                                       AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler,
                                       AppFavoriteCell.RequestAppFavoriteEventHandler,
                                       AppCommentSelectedEventHandler {

        void copySelectedApp();

        void createNewAppClicked();

        void createWorkflowClicked();

        void deleteSelectedApps();

        void editSelectedApp();

        App getSelectedApp();

        AppCategory getSelectedAppCategory();

        void go(HasOneWidget container, HasId selectedAppCategory, HasId selectedApp);

        Grid<App> getAppsGrid();

        String highlightSearchText(final String text);
        
        void cleanUp();

        Presenter hideAppMenu();

        Presenter hideWorkflowMenu();

        void onRequestToolClicked();

        void runSelectedApp();

        void submitClicked();

        void setViewDebugId(String baseId);
    }

    public interface ViewMenu extends IsWidget,
                                      AppSelectionChangedEventHandler,
                                      AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler {

        void hideAppMenu();

        void hideWorkflowMenu();

        void init(Presenter presenter, AppsView view, HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers, AppCategorySelectionChangedEvent.HasAppCategorySelectionChangedEventHandlers hasAppCategorySelectionChangedEventHandlers);
    }

    List<String> computeGroupHierarchy(AppCategory ag);

    void hideAppMenu();

    void hideWorkflowMenu();

    void setPresenter(final Presenter presenter);

    void maskCenterPanel(final String loadingMask);

    void unMaskCenterPanel();

    void maskWestPanel(String loadingMask);

    void unMaskWestPanel();

    void selectApp(String appId);

    void selectAppCategory(String appGroupId);

    App getSelectedApp();

    AppCategory getSelectedAppCategory();

    void setApps(List<App> apps);

    void selectFirstApp();

    void selectFirstAppCategory();

    void addAppCategory(AppCategory parent, AppCategory child);

    void addAppCategories(AppCategory parent, List<AppCategory> children);

    void removeApp(App app);

    void updateAppCategory(AppCategory appCategory);

    AppCategory findAppCategoryByName(String name);

    void updateAppCategoryAppCount(AppCategory appCategory, int newCount);

    Grid<App> getAppsGrid();

    void expandAppCategories();

    boolean isTreeStoreEmpty();

    List<App> getAllSelectedApps();

    void clearAppCategories();

    AppCategory getAppCategoryFromElement(Element el);

    App getAppFromElement(Element el);

    String highlightSearchText(String text);

    List<AppCategory> getAppCategoryRoots();

    AppCategory getParent(AppCategory child);

}
