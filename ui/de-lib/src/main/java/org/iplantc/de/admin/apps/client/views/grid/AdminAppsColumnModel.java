package org.iplantc.de.admin.apps.client.views.grid;

import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.admin.apps.client.views.grid.cells.AppNameCell;
import org.iplantc.de.admin.apps.client.views.grid.cells.AvgAppRatingCell;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.models.AppProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.AppNameComparator;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author jstroot
 */
public class AdminAppsColumnModel extends ColumnModel<App> implements AppNameSelectedEvent.HasAppNameSelectedEventHandlers {

    public AdminAppsColumnModel() {
        super(createColumnConfigList(GWT.<AdminAppsView.AdminAppsViewAppearance> create(AdminAppsView.AdminAppsViewAppearance.class)));

        for(ColumnConfig<App, ?> colConfig : configs){
            final Cell<?> cell = colConfig.getCell();
            if(cell instanceof AppNameCell){
                ((AppNameCell)cell).setHasHandlers(ensureHandlers());
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
        ColumnConfig<App, AppFeedback> rating = new ColumnConfig<>(props.rating(),
                                                                   appearance.avgUserRatingColumnWidth(),
                                                                   appearance.avgUserRatingColumnLabel());
        name.setComparator(new AppNameComparator());

        name.setSortable(true);
        integrator.setSortable(true);
        rating.setSortable(true);

        name.setResizable(true);
        rating.setResizable(false);

        name.setCell(new AppNameCell());
        rating.setCell(new AvgAppRatingCell());
        rating.setComparator(new Comparator<AppFeedback>() {
            @Override
            public int compare(AppFeedback o1, AppFeedback o2) {
                if(o1.getAverageRating() > o2.getAverageRating())
                    return 1;
                else if(o1.getAverageRating() < o2.getAverageRating())
                    return -1;

               return 0;
            }
        });

        rating.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        list.add(name);
        list.add(integrator);
        list.add(rating);
        return list;
    }

    @Override
    public HandlerRegistration addAppNameSelectedEventHandler(AppNameSelectedEvent.AppNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AppNameSelectedEvent.TYPE, handler);
    }
}