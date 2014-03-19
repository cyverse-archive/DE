package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.admin.belphegor.client.apps.views.cells.AppNameCell;
import org.iplantc.admin.belphegor.client.apps.views.cells.AvgAnalysisUserRatingCell;
import org.iplantc.de.apps.client.views.AppProperties;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

public class BelphegorAnalysisColumnModel extends ColumnModel<App> {

    public BelphegorAnalysisColumnModel(AppsView view) {
        super(createColumnConfigList(EventBus.getInstance(), view));
    }

    public static List<ColumnConfig<App, ?>> createColumnConfigList(final EventBus eventBus,
            AppsView view) {
        AppProperties props = GWT.create(AppProperties.class);
        List<ColumnConfig<App, ?>> list = new ArrayList<ColumnConfig<App, ?>>();

        ColumnConfig<App, App> name = new ColumnConfig<App, App>(new IdentityValueProvider<App>(), 180,
                I18N.DISPLAY.name());
        ColumnConfig<App, String> integrator = new ColumnConfig<App, String>(props.integratorName(),
                130, I18N.DISPLAY.integratedby());
        ColumnConfig<App, AppFeedback> rating = new ColumnConfig<App, AppFeedback>(props.rating(), 40,
                "Average User Rating"); //$NON-NLS-1$

        name.setResizable(true);
        rating.setResizable(false);

        name.setCell(new AppNameCell(view));
        rating.setCell(new AvgAnalysisUserRatingCell());

        rating.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        list.add(name);
        list.add(integrator);
        list.add(rating);
        return list;
    }

}