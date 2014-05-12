package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.admin.belphegor.client.apps.views.cells.AppNameCell;
import org.iplantc.admin.belphegor.client.apps.views.cells.AvgAnalysisUserRatingCell;
import org.iplantc.de.apps.client.views.AppProperties;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.events.EventBus;
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

public class BelphegorAppColumnModel extends ColumnModel<App> implements AppHyperlinkCell.HasAppNameSelectedEventHandlers {

    public BelphegorAppColumnModel(AppsView view) {
        super(createColumnConfigList(EventBus.getInstance(), view));

        for(ColumnConfig<App, ?> colConfig : configs){
            final Cell<?> cell = colConfig.getCell();
            if(cell instanceof AppNameCell){
                ((AppNameCell)cell).setHasHandlers(ensureHandlers());
            }
        }
    }

    public static List<ColumnConfig<App, ?>> createColumnConfigList(final EventBus eventBus,
            AppsView view) {
        AppProperties props = GWT.create(AppProperties.class);
        List<ColumnConfig<App, ?>> list = new ArrayList<ColumnConfig<App, ?>>();

        ColumnConfig<App, App> name = new ColumnConfig<App, App>(new IdentityValueProvider<App>("name"), 180,
                I18N.DISPLAY.name());
        ColumnConfig<App, String> integrator = new ColumnConfig<App, String>(props.integratorName(),
                130, I18N.DISPLAY.integratedby());
        ColumnConfig<App, AppFeedback> rating = new ColumnConfig<App, AppFeedback>(props.rating(), 40,
                "Average User Rating"); //$NON-NLS-1$

        name.setComparator(new AppNameComparator());

        name.setSortable(true);
        integrator.setSortable(true);
        rating.setSortable(true);

        name.setResizable(true);
        rating.setResizable(false);

        name.setCell(new AppNameCell(view));
        rating.setCell(new AvgAnalysisUserRatingCell());
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

        rating.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        list.add(name);
        list.add(integrator);
        list.add(rating);
        return list;
    }

    @Override
    public HandlerRegistration addAppNameSelectedEventHandler(AppHyperlinkCell.AppNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AppHyperlinkCell.EVENT_TYPE, handler);
    }
}