package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.events.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.AppCommentSelectedEvent.AppCommentSelectedEventHandler;
import org.iplantc.de.apps.client.events.AppCommentSelectedEvent.HasAppCommentSelectedEventHandlers;
import org.iplantc.de.apps.client.views.cells.AppCommentCell;
import org.iplantc.de.apps.client.views.cells.AppFavoriteCell;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.apps.client.views.cells.AppInfoCell;
import org.iplantc.de.apps.client.views.cells.AppRatingCell;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppNameComparator;
import org.iplantc.de.client.models.apps.AppRatingComparator;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

public class AppColumnModel extends ColumnModel<App> implements
        AppInfoCell.HasAppInfoClickedEventHandlers, AppHyperlinkCell.HasAppNameSelectedEventHandlers,
        AppFavoriteCell.HasRequestAppFavoriteEventHandlers, HasAppCommentSelectedEventHandlers {

    public AppColumnModel(AppsView view, final IplantDisplayStrings displayStrings) {
        super(createColumnConfigList(view, displayStrings));

        // Set handler managers on appropriate cells so they can fire events.
        for (ColumnConfig<App, ?> colConfig : configs) {
            Cell<?> cell = colConfig.getCell();
            if (cell instanceof AppInfoCell) {
                ((AppInfoCell)cell).setHasHandlers(ensureHandlers());
            } else if (cell instanceof AppHyperlinkCell) {
                ((AppHyperlinkCell)cell).setHasHandlers(ensureHandlers());
            } else if (cell instanceof AppFavoriteCell) {
                ((AppFavoriteCell)cell).setHasHandlers(ensureHandlers());
            } else if (cell instanceof AppCommentCell) {
                ((AppCommentCell)cell).setHasHandlers(ensureHandlers());
            }
        }
    }

    public static List<ColumnConfig<App, ?>> createColumnConfigList(final AppsView view, final IplantDisplayStrings displayStrings) {
        AppProperties props = GWT.create(AppProperties.class);
        List<ColumnConfig<App, ?>> list = new ArrayList<ColumnConfig<App, ?>>();

        ColumnConfig<App, App> info = new ColumnConfig<App, App>(
                new IdentityValueProvider<App>(), 20);

        ColumnConfig<App, App> name = new ColumnConfig<App, App>(new IdentityValueProvider<App>("name"), //$NON-NLS-1$
                180, displayStrings.name());

        ColumnConfig<App, String> integrator = new ColumnConfig<App, String>(
                props.integratorName(), 130, displayStrings.integratedby());

        ColumnConfig<App, App> rating = new ColumnConfig<App, App>(new IdentityValueProvider<App>(
                "rating"), 105, displayStrings.rating()); //$NON-NLS-1$

        ColumnConfig<App, App> comment = new ColumnConfig<App, App>(new IdentityValueProvider<App>(
                "comment"), 30); //$NON-NLS-1$

        name.setComparator(new AppNameComparator());
        rating.setComparator(new AppRatingComparator());
        info.setSortable(false);
        comment.setSortable(false);

        info.setResizable(false);
        name.setResizable(true);
        rating.setResizable(false);
        comment.setResizable(false);

        info.setFixed(true);
        rating.setFixed(true);
        comment.setFixed(true);

        info.setCell(new AppInfoCell());
        name.setCell(new AppHyperlinkCell(view));
        integrator.setCell(new AbstractCell<String>() {

            @Override
            public void render(Context context, String value, SafeHtmlBuilder sb) {
                sb.append(SafeHtmlUtils.fromTrustedString(view.highlightSearchText(value)));
            }

        });
        rating.setCell(new AppRatingCell());
        comment.setCell(new AppCommentCell());

        rating.setAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        list.add(info);
        list.add(name);
        list.add(integrator);
        list.add(rating);
        list.add(comment);

        return list;
    }

    @Override
    public HandlerRegistration addAppInfoClickedEventHandler(AppInfoCell.AppInfoClickedEventHandler handler) {
        return ensureHandlers().addHandler(AppInfoCell.APP_INFO_CLICKED_EVENT_HANDLER_TYPE, handler);
    }

    @Override
    public HandlerRegistration addAppNameSelectedEventHandler(AppHyperlinkCell.AppNameSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AppHyperlinkCell.EVENT_TYPE, handler);
    }

    @Override
    public HandlerRegistration addRequestAppFavoriteEventHandlers(AppFavoriteCell.RequestAppFavoriteEventHandler handler) {
        return ensureHandlers().addHandler(AppFavoriteCell.REQUEST_APP_FAV_EVNT_TYPE, handler);
    }

    @Override
    public HandlerRegistration addAppCommentSelectedEventHandlers(AppCommentSelectedEventHandler handler) {
        return ensureHandlers().addHandler(AppCommentSelectedEvent.TYPE, handler);
    }

    public void ensureDebugId(String baseID) {
        for(ColumnConfig<App, ?> cc : configs){
            if(cc.getCell() instanceof AppInfoCell){
                ((AppInfoCell)cc.getCell()).setBaseDebugId(baseID + AppsModule.Ids.APP_GRID);
            }else if(cc.getCell() instanceof AppHyperlinkCell){
                ((AppHyperlinkCell)cc.getCell()).setBaseDebugId(baseID + AppsModule.Ids.APP_GRID);
            }
        }

    }
}


