package org.iplantc.de.admin.desktop.client.apps.views;

import org.iplantc.de.admin.desktop.client.I18N;
import org.iplantc.de.admin.desktop.client.apps.views.cells.AppNameCell;
import org.iplantc.de.admin.desktop.client.apps.views.cells.AvgAppRatingCell;
import org.iplantc.de.apps.client.events.AppNameSelectedEvent;
import org.iplantc.de.apps.client.views.AppProperties;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.AppNameComparator;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

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

public class BelphegorAppColumnModel extends ColumnModel<App> implements AppNameSelectedEvent.HasAppNameSelectedEventHandlers {

    public BelphegorAppColumnModel(AppsView view) {
        super(createColumnConfigList(view, I18N.DISPLAY));

        for(ColumnConfig<App, ?> colConfig : configs){
            final Cell<?> cell = colConfig.getCell();
            if(cell instanceof AppNameCell){
                ((AppNameCell)cell).setHasHandlers(ensureHandlers());
            }
        }
    }

    public static List<ColumnConfig<App, ?>> createColumnConfigList(AppsView view,
                                                                    IplantDisplayStrings displayStrings) {
        AppProperties props = GWT.create(AppProperties.class);
        List<ColumnConfig<App, ?>> list = new ArrayList<>();

        ColumnConfig<App, App> name = new ColumnConfig<>(new IdentityValueProvider<App>("name"), 180,
                displayStrings.name());
        ColumnConfig<App, String> integrator = new ColumnConfig<>(props.integratorName(),
                130, displayStrings.integratedby());
        ColumnConfig<App, AppFeedback> rating = new ColumnConfig<>(props.rating(), 40,
                "Average User Rating"); //$NON-NLS-1$

        name.setComparator(new AppNameComparator());

        name.setSortable(true);
        integrator.setSortable(true);
        rating.setSortable(true);

        name.setResizable(true);
        rating.setResizable(false);

        name.setCell(new AppNameCell(view));
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
        return ensureHandlers().addHandler(AppHyperlinkCell.EVENT_TYPE, handler);
    }
}