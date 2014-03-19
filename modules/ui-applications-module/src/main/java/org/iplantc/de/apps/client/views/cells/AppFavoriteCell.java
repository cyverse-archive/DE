package org.iplantc.de.apps.client.views.cells;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.AppFavoriteCellStyle;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppFavoriteCell extends AbstractCell<App> {


    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span name=\"{0}\" class=\"{1}\" qtip=\"{2}\"> </span>")
        SafeHtml cell(String imgName, String imgClassName, String imgToolTip);
    }

    final Templates templates = GWT.create(Templates.class);
    final AppFavoriteCellStyle css = IplantResources.RESOURCES.appFavoriteCss();

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
                    doOnClick(eventTarget, value, valueUpdater);
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

    private void doOnClick(final Element eventTarget, final App value, final ValueUpdater<App> valueUpdater) {

        ServicesInjector.INSTANCE.getAppUserServiceFacade().favoriteApp(UserInfo.getInstance().getWorkspaceId(), value.getId(),
                !value.isFavorite(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                value.setFavorite(!value.isFavorite());
                valueUpdater.update(value);

                // Reset favorite icon
                doOnMouseOut(eventTarget, value);
                EventBus.getInstance().fireEvent(
                        new AppFavoritedEvent(value.getId(), value.isFavorite()));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.favServiceFailure(), caught);
            }
        });
    }

}
