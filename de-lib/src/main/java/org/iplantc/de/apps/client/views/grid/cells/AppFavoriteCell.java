package org.iplantc.de.apps.client.views.grid.cells;

import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * @author jstroot
 */
public class AppFavoriteCell extends AbstractCell<App> {

    public interface AppFavoriteCellAppearance {
        String addAppToFav();

        String appUnavailable();

        String favoriteClass();

        String favoriteDisabledClass();

        String remAppFromFav();

        String favoriteAddClass();

        void render(SafeHtmlBuilder sb, String imgName, String imgClassName, String imgToolTip,
                    String debugId);
    }

    private final AppFavoriteCellAppearance appearance;
    private String baseID;
    private HasHandlers hasHandlers;
    private final AppsMessages appMsgs;

    public AppFavoriteCell() {
        this(GWT.<AppFavoriteCellAppearance> create(AppFavoriteCellAppearance.class),  GWT.<AppsMessages> create(AppsMessages.class));
    }

    public AppFavoriteCell(final AppFavoriteCellAppearance appearance, AppsMessages appMsgs) {
        super(CLICK);
        this.appearance = appearance;
        this.appMsgs = appMsgs;
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        String imgName, imgClassName, imgToolTip;
        
        if (value.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
            imgName = "disabled";
            imgClassName = appearance.favoriteDisabledClass();
            imgToolTip = appMsgs.featureNotSupported();
        } else if (!value.isDisabled() && value.isFavorite()) {
            imgName = "fav";
            imgClassName = appearance.favoriteClass();
            imgToolTip = appearance.remAppFromFav();
        } else if (!value.isDisabled() && !value.isFavorite()) {
            imgName = "fav";
            imgClassName = appearance.favoriteAddClass();
            imgToolTip = appearance.addAppToFav();
        } else{
            imgName = "disabled";
            imgClassName = appearance.favoriteDisabledClass();
            imgToolTip = appearance.appUnavailable();
        }

        String debugId = baseID + "." + value.getId() + AppsModule.Ids.APP_FAVORITE_CELL;
        appearance.render(sb, imgName, imgClassName, imgToolTip, debugId);
    }

    @Override
    public void onBrowserEvent(final Cell.Context context,
                               final Element parent,
                               final App value,
                               final NativeEvent event,
                               final ValueUpdater<App> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget) && eventTarget.getAttribute("name").equalsIgnoreCase("fav")
                && !value.isDisabled()) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    if (hasHandlers != null) {
                        hasHandlers.fireEvent(new AppFavoriteSelectedEvent(value));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setBaseDebugId(String baseID) {
        this.baseID = baseID;
    }

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

}
