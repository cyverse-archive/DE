package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.apps.client.views.cells.AppInfoCell;
import org.iplantc.de.apps.client.views.cells.AppRatingCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppColumnModel extends ColumnModel<App> {


    public AppColumnModel(AppsView view) {
        super(createColumnConfigList(view));

    }

    public static List<ColumnConfig<App, ?>> createColumnConfigList(final AppsView view) {
        AppProperties props = GWT.create(AppProperties.class);
        List<ColumnConfig<App, ?>> list = new ArrayList<ColumnConfig<App, ?>>();

        ColumnConfig<App, App> info = new ColumnConfig<App, App>(
                new IdentityValueProvider<App>(), 20);

        ColumnConfig<App, App> name = new ColumnConfig<App, App>(
                new IdentityValueProvider<App>("name"), 180, I18N.DISPLAY.name()) {

        };
        ColumnConfig<App, String> integrator = new ColumnConfig<App, String>(
                props.integratorName(), 130, I18N.DISPLAY.integratedby());
        ColumnConfig<App, App> rating = new ColumnConfig<App, App>(new IdentityValueProvider<App>(),
                105, "Rating"); //$NON-NLS-1$

        name.setComparator(new Comparator<App>() {
            @Override
            public int compare(App arg0, App arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        });
        info.setSortable(false);

        info.setResizable(false);
        name.setResizable(true);
        rating.setResizable(false);

        info.setFixed(true);
        rating.setFixed(true);

        info.setCell(new AppInfoCell(view));
        name.setCell(new AppHyperlinkCell(view));
        integrator.setCell(new AbstractCell<String>() {

            @Override
            public void render(Context context, String value, SafeHtmlBuilder sb) {
                sb.append(SafeHtmlUtils.fromTrustedString(view.highlightSearchText(value)));
            }

        });
        rating.setCell(new AppRatingCell());

        rating.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        list.add(info);
        list.add(name);
        list.add(integrator);
        list.add(rating);
        return list;
    }

}


