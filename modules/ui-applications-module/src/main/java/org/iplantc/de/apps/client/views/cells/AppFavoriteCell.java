package org.iplantc.de.apps.client.views.cells;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.AppFavoriteCellStyle;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.*;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * FIXME Create appearance
 */
public class AppFavoriteCell extends AbstractCell<App> {

    public static final GwtEvent.Type<RequestAppFavoriteEventHandler> REQUEST_APP_FAV_EVNT_TYPE = new GwtEvent.Type<RequestAppFavoriteEventHandler>();
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

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span name=\"{0}\" class=\"{1}\" qtip=\"{2}\"> </span>")
        SafeHtml cell(String imgName, String imgClassName, String imgToolTip);
    }

    final Templates templates = GWT.create(Templates.class);
    final AppFavoriteCellStyle css = IplantResources.RESOURCES.appFavoriteCss();
    private HasHandlers hasHandlers;

    public AppFavoriteCell() {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        css.ensureInjected();
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        if (!value.isDisabled() && value.isFavorite()) {
            sb.append(templates.cell("fav", css.appFavorite(), I18N.DISPLAY.remAppFromFav()));
        } else if (!value.isDisabled() && !value.isFavorite()) {
            sb.append(templates.cell("fav", css.appFavoriteDisabled(), I18N.DISPLAY.addAppToFav()));
        } else{
            sb.append(templates.cell("disabled", css.appFavoriteDisabled(), I18N.DISPLAY.appUnavailable()));
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

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    private void doOnMouseOut(Element eventTarget, App value) {
        if (value.isFavorite()) {
            eventTarget.setClassName(css.appFavorite());
            eventTarget.setAttribute("qtip", I18N.DISPLAY.remAppFromFav());
            eventTarget.setTitle(I18N.DISPLAY.remAppFromFav());
        } else {
            eventTarget.setClassName(css.appFavoriteDisabled());
            eventTarget.setAttribute("qtip", I18N.DISPLAY.addAppToFav());
            eventTarget.setTitle(I18N.DISPLAY.addAppToFav());
        }
    }

    private void doOnMouseOver(Element eventTarget, App value) {
        if (value.isFavorite()) {
            eventTarget.setClassName(css.appFavoriteDelete());
        } else {
            eventTarget.setClassName(css.appFavoriteAdd());
        }
    }

}
