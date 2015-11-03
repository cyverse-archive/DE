package org.iplantc.de.apps.client.views.grid.cells;

import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.view.client.ProvidesKey;

/**
 * @author jstroot
 */
public class AppFavoriteCellWidget extends CellWidget<App> implements AppFavoriteSelectedEvent.HasAppFavoriteSelectedEventHandlers {

    private final AppFavoriteCell cell;

    public AppFavoriteCellWidget() {
        this(new AppFavoriteCell(), null, null);
    }

    public AppFavoriteCellWidget(AppFavoriteCell cell) {
        this(cell, null, null);
    }

    public AppFavoriteCellWidget(AppFavoriteCell cell, ProvidesKey<App> keyProvider) {
        this(cell, null, keyProvider);
    }

    public AppFavoriteCellWidget(App app) {
        this(new AppFavoriteCell(), app, null);
    }


    public AppFavoriteCellWidget(AppFavoriteCell cell, App app, ProvidesKey<App> keyProvider) {
        super(cell, app, keyProvider);
        this.cell = cell;
        this.cell.setHasHandlers(this);
        setSize("16px", "16px");
    }

    @Override
    public HandlerRegistration addAppFavoriteSelectedEventHandlers(AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler handler) {
        return addHandler(handler, AppFavoriteSelectedEvent.TYPE);
    }

    @Override
    public Cell<App> getCell() {
        return cell;
    }

    public void setBaseDebugId(final String baseID){
        cell.setBaseDebugId(baseID);
    }
}
