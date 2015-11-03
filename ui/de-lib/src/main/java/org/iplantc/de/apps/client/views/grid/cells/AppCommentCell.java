package org.iplantc.de.apps.client.views.grid.cells;

import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

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
public class AppCommentCell extends AbstractCell<App> {

    public interface AppCommentCellAppearance {
        void render(Context context, App value, SafeHtmlBuilder sb);

    }

    private final AppCommentCellAppearance appearance;
    private HasHandlers hasHandlers;
    private final AppsMessages appMsgs;

    public AppCommentCell() {
        this(GWT.<AppCommentCellAppearance> create(AppCommentCellAppearance.class),
             GWT.<AppsMessages> create(AppsMessages.class));
    }

    public AppCommentCell(AppCommentCellAppearance appearance, AppsMessages appMsgs) {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        this.appearance = appearance;
        this.appMsgs = appMsgs;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, App value, SafeHtmlBuilder sb) {
        appearance.render(context, value, sb);
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, App value, NativeEvent event,
            ValueUpdater<App> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    if (hasHandlers != null && !value.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
                        hasHandlers.fireEvent(new AppCommentSelectedEvent(value));
                    }
                    break;
                case Event.ONMOUSEOVER:
                    if (value.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
                        eventTarget.setTitle(appMsgs.featureNotSupported());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setHasHandlers(HasHandlers handlerManager) {
        hasHandlers = handlerManager;
    }

}
