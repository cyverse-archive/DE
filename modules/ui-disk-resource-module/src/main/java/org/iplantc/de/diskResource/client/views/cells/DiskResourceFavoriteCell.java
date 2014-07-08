package org.iplantc.de.diskResource.client.views.cells;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.views.cells.events.RequestDiskResourceFavoriteEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.FavoriteCellStyle;
import org.iplantc.de.resources.client.FavoriteTemplates;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

public class DiskResourceFavoriteCell extends AbstractCell<DiskResource> {

    final FavoriteTemplates templates = GWT.create(FavoriteTemplates.class);
    final FavoriteCellStyle css = IplantResources.RESOURCES.favoriteCss();
    private String baseID;
    private HasHandlers hasHandlers;

    public DiskResourceFavoriteCell() {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        css.ensureInjected();
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
                imgClassName = css.favorite();
                imgToolTip = I18N.DISPLAY.remAppFromFav();
            } else {
                imgName = "fav";
                imgClassName = css.favoriteDisabled();
                imgToolTip = I18N.DISPLAY.addAppToFav();
            }

            if (DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)) {
                String debugId = baseID + "." + value.getPath()
                        + DiskResourceModule.Ids.ACTION_CELL_FAVORITE;
                sb.append(templates.debugCell(imgName, imgClassName, imgToolTip, debugId));
            } else {
                sb.append(templates.cell(imgName, imgClassName, imgToolTip));
            }

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
                case Event.ONMOUSEOVER:
                    doOnMouseOver(eventTarget, value);
                    break;
                case Event.ONMOUSEOUT:
                    doOnMouseOut(eventTarget, value);
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

    private void doOnMouseOut(Element eventTarget, DiskResource value) {
        if (value.isFavorite()) {
            eventTarget.setClassName(css.favorite());
            eventTarget.setAttribute("qtip", I18N.DISPLAY.remAppFromFav());
        } else {
            eventTarget.setClassName(css.favoriteDisabled());
            eventTarget.setAttribute("qtip", I18N.DISPLAY.addAppToFav());
        }
    }

    private void doOnMouseOver(Element eventTarget, DiskResource value) {
        if (value.isFavorite()) {
            eventTarget.setClassName(css.favoriteDelete());
        } else {
            eventTarget.setClassName(css.favoriteAdd());
        }
    }

}
