package org.iplantc.de.apps.client.views.widgets;

import org.iplantc.de.apps.client.views.cells.AppFavoriteCell;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.view.client.ProvidesKey;

public class AppFavoriteCellWidget extends CellWidget<App> implements AppFavoriteCell.HasRequestAppFavoriteEventHandlers {

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
    }

    @Override
    public HandlerRegistration addRequestAppFavoriteEventHandlers(AppFavoriteCell.RequestAppFavoriteEventHandler handler) {
        return addHandler(handler, AppFavoriteCell.REQUEST_APP_FAV_EVNT_TYPE);
    }

    @Override
    public Cell<App> getCell() {
        return cell;
    }

}
