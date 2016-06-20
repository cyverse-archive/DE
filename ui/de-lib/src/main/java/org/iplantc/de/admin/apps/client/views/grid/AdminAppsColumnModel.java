package org.iplantc.de.admin.apps.client.views.grid;

import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.apps.client.views.grid.cells.AdminAppNameCell;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.models.AppProperties;
import org.iplantc.de.apps.client.views.grid.cells.AppInfoCell;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author jstroot
 */
public class AdminAppsColumnModel extends ColumnModel<App> implements AppNameSelectedEvent.HasAppNameSelectedEventHandlers,
                                                                      AppInfoSelectedEvent.HasAppInfoSelectedEventHandlers {

    public static class AppStringNameComparator implements Comparator<App> {

        @Override
        public int compare(App o1, App o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    public AdminAppsColumnModel() {
        super(createColumnConfigList(GWT.<AdminAppsView.AdminAppsViewAppearance> create(AdminAppsView.AdminAppsViewAppearance.class)));

        for(ColumnConfig<App, ?> colConfig : configs){
            final Cell<?> cell = colConfig.getCell();
            if(cell instanceof AppInfoCell){
                ((AppInfoCell)cell).setHasHandlers(ensureHandlers());
            }
        }
    }

    public static List<ColumnConfig<App, ?>> createColumnConfigList(final AdminAppsView.AdminAppsViewAppearance appearance) {
        AppProperties props = GWT.create(AppProperties.class);
        List<ColumnConfig<App, ?>> list = new ArrayList<>();

        ColumnConfig<App, App> name = new ColumnConfig<>(new IdentityValueProvider<App>("name"),
                                                         appearance.nameColumnWidth(),
                                                         appearance.nameColumnLabel());
        ColumnConfig<App, String> integrator = new ColumnConfig<>(props.integratorName(),
                                                                  appearance.integratedByColumnWidth(),
                                                                  appearance.integratedBy());

        ColumnConfig<App, App> info = new ColumnConfig<>(new IdentityValueProvider<App>(""), 20);
        info.setHeader("");

        name.setComparator(new AppStringNameComparator());
        name.setCell(new AdminAppNameCell());

        name.setSortable(true);
        integrator.setSortable(true);
        info.setSortable(false);

        name.setResizable(true);
        info.setResizable(false);

        info.setCell(new AppInfoCell());

        list.add(name);
        list.add(integrator);
        list.add(info);
        return list;
    }

    @Override
    public HandlerRegistration addAppNameSelectedEventHandler(AppNameSelectedEvent.AppNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AppNameSelectedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addAppInfoSelectedEventHandler(AppInfoSelectedEvent.AppInfoSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AppInfoSelectedEvent.TYPE, handler);
    }
}
