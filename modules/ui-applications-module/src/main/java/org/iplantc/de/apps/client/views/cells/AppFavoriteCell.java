package org.iplantc.de.apps.client.views.cells;

import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.FavoriteCellStyle;
import org.iplantc.de.resources.client.FavoriteTemplates;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * FIXME Create appearance
 * @author jstroot
 * 
 */
public class AppFavoriteCell extends AbstractCell<App> {

    public static final GwtEvent.Type<RequestAppFavoriteEventHandler> REQUEST_APP_FAV_EVNT_TYPE = new GwtEvent.Type<>();
    public class RequestAppFavoriteEvent extends GwtEvent<RequestAppFavoriteEventHandler> {

        private final App app;

        public RequestAppFavoriteEvent(App app) {
            this.app = app;
        }

        public App getApp() {
            return app;
        }

        @Override
        public Type<RequestAppFavoriteEventHandler> getAssociatedType() {
            return REQUEST_APP_FAV_EVNT_TYPE;
        }

        @Override
        protected void dispatch(RequestAppFavoriteEventHandler handler) {
            handler.onAppFavoriteRequest(this);
        }
    }

    public interface RequestAppFavoriteEventHandler extends EventHandler {
        void onAppFavoriteRequest(RequestAppFavoriteEvent event);
    }
    public static interface HasRequestAppFavoriteEventHandlers {
        HandlerRegistration addRequestAppFavoriteEventHandlers(RequestAppFavoriteEventHandler handler);
    }

    final FavoriteTemplates templates = GWT.create(FavoriteTemplates.class);
    final FavoriteCellStyle css = IplantResources.RESOURCES.favoriteCss();
    private String baseID;
    private HasHandlers hasHandlers;

    public AppFavoriteCell() {
        super(CLICK);
        css.ensureInjected();
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        String imgName, imgClassName, imgToolTip;

        if (!value.isDisabled() && value.isFavorite()) {
            imgName = "fav";
            imgClassName = css.favorite();
            imgToolTip = I18N.DISPLAY.remAppFromFav();
        } else if (!value.isDisabled() && !value.isFavorite()) {
            imgName = "fav";
            imgClassName = css.favoriteDisabled();
            imgToolTip = I18N.DISPLAY.addAppToFav();
        } else{
            imgName = "disabled";
            imgClassName = css.favoriteDisabled();
            imgToolTip = I18N.DISPLAY.appUnavailable();
        }

        if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)){
            String debugId = baseID + "." + value.getId() + AppsModule.Ids.APP_FAVORITE_CELL;
            sb.append(templates.debugCell(imgName, imgClassName, imgToolTip, debugId));
        } else {
            sb.append(templates.cell(imgName, imgClassName, imgToolTip));
        }
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, App value, NativeEvent event,
            ValueUpdater<App> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget) && eventTarget.getAttribute("name").equalsIgnoreCase("fav")
                && !value.isDisabled()) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    if (hasHandlers != null) {
                        hasHandlers.fireEvent(new RequestAppFavoriteEvent(value));
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
