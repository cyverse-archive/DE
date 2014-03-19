/**
 * 
 */
package org.iplantc.de.apps.client.views.widgets;

import org.iplantc.de.apps.client.views.cells.AppRatingCell;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.view.client.ProvidesKey;

/**
 * @author sriram
 *
 */
public class AppRatingCellWidget extends CellWidget<App> {

    private final AppRatingCell cell;

    public AppRatingCellWidget() {
        this(new AppRatingCell(), null, null);
    }

    public AppRatingCellWidget(AppRatingCell cell) {
        this(cell, null, null);
    }

    public AppRatingCellWidget(AppRatingCell cell, ProvidesKey<App> keyProvider) {
        this(cell, null, keyProvider);
    }

    public AppRatingCellWidget(App app) {
        this(new AppRatingCell(), app, null);
    }

    public AppRatingCellWidget(AppRatingCell cell, App app, ProvidesKey<App> keyProvider) {
        super(cell, app, keyProvider);
        this.cell = cell;
    }

    @Override
    public Cell<App> getCell() {
        return cell;
    }

}
