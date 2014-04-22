package org.iplantc.de.apps.client.views;

import static org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent.AppGroupSelectionChangedEventHandler;
import static org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent.HasAppGroupSelectionChangedEventHandlers;
import static org.iplantc.de.apps.client.events.AppSelectionChangedEvent.AppSelectionChangedEventHandler;
import static org.iplantc.de.apps.client.events.AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers;
import static org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent.AppSearchResultLoadEventHandler;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

public interface AppsView extends IsWidget, AppSearchResultLoadEventHandler {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
            AppHyperlinkCell.AppNameSelectedEventHandler,
            AppSearchResultLoadEventHandler,
            AppGroupSelectionChangedEventHandler {

        void copySelectedApp();

        void createNewAppClicked();

        void createWorkflowClicked();

        void deleteSelectedApps();

        void editSelectedApp();

        App getSelectedApp();

        AppGroup getSelectedAppGroup();

        void go(HasOneWidget container, HasId selectedAppGroup, HasId selectedApp);

        Grid<App> getAppsGrid();

        String highlightSearchText(final String text);
        
        void cleanUp();

        Presenter hideAppMenu();

        Presenter hideWorkflowMenu();

        void onRequestToolClicked();

        void runSelectedApp();

        void submitClicked();
    }

    public interface ViewMenu extends IsWidget, AppSelectionChangedEventHandler, AppGroupSelectionChangedEventHandler {

        void hideAppMenu();

        void hideWorkflowMenu();

        void init(Presenter presenter, AppsView view, HasAppSelectionChangedEventHandlers hasAppSelectionChangedEventHandlers, HasAppGroupSelectionChangedEventHandlers hasAppGroupSelectionChangedEventHandlers);
    }

    List<String> computeGroupHierarchy(AppGroup ag);

    void hideAppMenu();

    void hideWorkflowMenu();

    void setPresenter(final Presenter presenter);

    void setCenterPanelHeading(final String name);

    void maskCenterPanel(final String loadingMask);

    void submitSelectedApp();

    void unMaskCenterPanel();

    void maskWestPanel(String loadingMask);

    void unMaskWestPanel();

    void selectApp(String appId);

    void selectAppGroup(String appGroupId);

    App getSelectedApp();

    AppGroup getSelectedAppGroup();

    void setApps(List<App> apps);

    void selectFirstApp();

    void selectFirstAppGroup();

    void addAppGroup(AppGroup parent, AppGroup child);

    void addAppGroups(AppGroup parent, List<AppGroup> children);

    void removeApp(App app);

    void updateAppGroup(AppGroup appGroup);

    AppGroup findAppGroupByName(String name);

    void updateAppGroupAppCount(AppGroup appGroup, int newCount);

    Grid<App> getAppsGrid();

    void expandAppGroups();

    boolean isTreeStoreEmpty();

    List<App> getAllSelectedApps();

    void clearAppGroups();

    AppGroup getAppGroupFromElement(Element el);

    App getAppFromElement(Element el);

    String highlightSearchText(String text);

    List<AppGroup> getAppGroupRoots();

    AppGroup getParent(AppGroup child);

}
