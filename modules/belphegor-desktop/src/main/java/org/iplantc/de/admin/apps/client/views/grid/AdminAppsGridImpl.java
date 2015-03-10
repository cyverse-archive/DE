package org.iplantc.de.admin.apps.client.views.grid;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

/**
 * Created by jstroot on 3/9/15.
 * @author jstroot
 */
public class AdminAppsGridImpl extends Composite implements AdminAppsGridView,
                                                            SelectionChangedEvent.SelectionChangedHandler<App> {

    interface AdminAppsGridImplUiBinder extends UiBinder<Widget, AdminAppsGridImpl> {
    }

    private static AdminAppsGridImplUiBinder ourUiBinder = GWT.create(AdminAppsGridImplUiBinder.class);
    @UiField(provided = true) ListStore<App> listStore;
    @UiField ColumnModel<App> cm;
    @UiField GridView<App> gridView;
    @UiField Grid<App> grid;

    @Inject
    AdminAppsGridImpl(@Assisted final ListStore<App> listStore) {
        this.listStore = listStore;

        initWidget(ourUiBinder.createAndBindUi(this));
        grid.getSelectionModel().addSelectionChangedHandler(this);
    }

    @Override
    public HandlerRegistration addAppNameSelectedEventHandler(AppNameSelectedEvent.AppNameSelectedEventHandler handler) {
        return ((BelphegorAppColumnModel)cm).addAppNameSelectedEventHandler(handler);
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
        // TODO
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<App> event) {
        fireEvent(new AppSelectionChangedEvent(event.getSelection()));
    }

    @UiFactory
    ColumnModel<App> createColumnModel() {
        return new BelphegorAppColumnModel();
    }
}