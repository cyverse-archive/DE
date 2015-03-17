package org.iplantc.de.diskResource.client.views.grid.cells;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.RequestDiskResourceFavoriteEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import static com.google.gwt.dom.client.BrowserEvents.*;
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
public class DiskResourceFavoriteCell extends AbstractCell<DiskResource> {

    public interface Appearance {

        String addToFavoriteTooltip();

        String favoriteClass();

        String favoriteDisabledClass();

        String removeFromFavoriteTooltip();

        void render(SafeHtmlBuilder sb, String imgName, String imgClassName, String imgToolTip,
                    String baseID, String debugId);
    }

    private final Appearance appearance;

    private String baseID;
    private HasHandlers hasHandlers;

    public DiskResourceFavoriteCell() {
        this(GWT.<Appearance> create(Appearance.class));
    }

    public DiskResourceFavoriteCell(final Appearance appearance) {
        super(CLICK);
        this.appearance = appearance;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, DiskResource value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        String imgName, imgClassName, imgToolTip;
        if (!value.isFilter()) {
            if (value.isFavorite()) {
                imgName = "fav";
                imgClassName = appearance.favoriteClass();
                imgToolTip = appearance.removeFromFavoriteTooltip();
            } else {
                imgName = "fav";
                imgClassName = appearance.favoriteDisabledClass();
                imgToolTip = appearance.addToFavoriteTooltip();
            }


            String debugId = baseID + "." + value.getPath()
                                 + DiskResourceModule.Ids.ACTION_CELL_FAVORITE;
            appearance.render(sb, imgName, imgClassName, imgToolTip, baseID, debugId);
        }
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, DiskResource value, NativeEvent event, ValueUpdater<DiskResource> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget) && eventTarget.getAttribute("name").equalsIgnoreCase("fav") && !value.isFilter()) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    if (hasHandlers != null) {
                        hasHandlers.fireEvent(new RequestDiskResourceFavoriteEvent(value));
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
