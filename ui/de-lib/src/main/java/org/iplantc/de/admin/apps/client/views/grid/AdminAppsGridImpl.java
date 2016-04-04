package org.iplantc.de.admin.apps.client.views.grid;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.BeforeAppSearchEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public class AdminAppsGridImpl extends ContentPanel implements AdminAppsGridView,
                                                               SelectionChangedEvent.SelectionChangedHandler<App> {

    interface AdminAppsGridImplUiBinder extends UiBinder<Widget, AdminAppsGridImpl> { }

    private static AdminAppsGridImplUiBinder ourUiBinder = GWT.create(AdminAppsGridImplUiBinder.class);
    @UiField(provided = true) ListStore<App> listStore;
    @UiField ColumnModel<App> cm;
    @UiField GridView<App> gridView;
    @UiField Grid<App> grid;

    private final AdminAppsColumnModel acm; // Convenience class

    @Inject AdminAppsGridView.Appearance appearance;

    @Inject
    AdminAppsGridImpl(@Assisted final ListStore<App> listStore) {
        this.listStore = listStore;

        setWidget(ourUiBinder.createAndBindUi(this));
        this.acm = (AdminAppsColumnModel) cm;
        grid.getSelectionModel().addSelectionChangedHandler(this);
    }

    @Override
    public HandlerRegistration addAppNameSelectedEventHandler(AppNameSelectedEvent.AppNameSelectedEventHandler handler) {
        return acm.addAppNameSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addAppSelectionChangedEventHandler(AppSelectionChangedEvent.AppSelectionChangedEventHandler handler) {
        return addHandler(handler, AppSelectionChangedEvent.TYPE);
    }

    @Override
    public Grid<App> getGrid() {
        return grid;
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        // FIXME Move to appearance
        setHeadingText(Joiner.on(" >> ").join(event.getGroupHierarchy()));
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        unmask();
//        searchRegexPattern = event.getSearchPattern();
//        acm.setSearchRegexPattern(searchRegexPattern);

        int total = event.getResults() == null ? 0 : event.getResults().size();
        setHeadingText(appearance.searchAppResultsHeader(event.getSearchText(), total));
    }

    @Override
    public void onBeforeAppSearch(BeforeAppSearchEvent event) {
        mask(appearance.beforeAppSearchLoadingMask());
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<App> event) {
        fireEvent(new AppSelectionChangedEvent(event.getSelection()));
    }

    @UiFactory
    ColumnModel<App> createColumnModel() {
        return new AdminAppsColumnModel();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        grid.asWidget().ensureDebugId(baseID + Belphegor.AppIds.GRID);
    }
}
