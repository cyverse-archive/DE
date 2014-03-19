package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.views.widgets.AppsViewToolbar;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

public interface AppsView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter,
            AppsViewToolbar.Presenter {
        void onAppSelected(final App app);

        void onAppGroupSelected(final AppGroup ag);

        App getSelectedApp();

        List<App> getAllSelectedApps();

        AppGroup getSelectedAppGroup();

        void onAppInfoClick(App app);
        
        void onAppNameSelected(App app);

        void go(HasOneWidget container, HasId selectedAppGroup, HasId selectedApp);

        Grid<App> getAppsGrid();

        AppGroup getAppGroupFromElement(Element el);

        App getAppFromElement(Element el);

        String highlightSearchText(final String text);
        
        void cleanUp();
    }

    void setPresenter(final Presenter presenter);

    ListStore<App> getListStore();

    void setListLoader(final ListLoader<ListLoadConfig, ListLoadResult<App>> listLoader);

    void setTreeLoader(final TreeLoader<AppGroup> treeLoader);

    void setCenterPanelHeading(final String name);

    void maskCenterPanel(final String loadingMask);

    void unMaskCenterPanel();

    void maskWestPanel(String loadingMask);

    void unMaskWestPanel();

    void selectApp(String appId);

    void selectAppGroup(String appGroupId);

    App getSelectedApp();

    AppGroup getSelectedAppGroup();

    void setApps(List<App> apps);

    void setNorthWidget(IsWidget widget);

    void setEastWidget(IsWidget widget);

    void selectFirstApp();

    void selectFirstAppGroup();

    void addAppGroup(AppGroup parent, AppGroup child);

    void addAppGroups(AppGroup parent, List<AppGroup> children);

    void removeApp(App app);

    void deSelectAllAppGroups();

    void updateAppGroup(AppGroup appGroup);

    AppGroup findAppGroup(String id);

    AppGroup findAppGroupByName(String name);

    void updateAppGroupAppCount(AppGroup appGroup, int newCount);

    App findApp(String appId);

    void onAppInfoClick(App app);

    void onAppNameSelected(final App app);

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
