package org.iplantc.de.apps.client.views.cells;

import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.shared.AppsModule;
import org.iplantc.de.client.models.apps.App;
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
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;

/**
 * FIXME Create appearance
 */
public class AppInfoCell extends AbstractCell<App> {

    interface MyCss extends CssResource {
        @ClassName("app_info")
        String appRun();
    }

    interface Resources extends ClientBundle {
        @Source("AppInfoCell.css")
        MyCss css();
    }

    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<img class='{0}' qtip='{2}' src='{1}'/>")
        SafeHtml cell(String imgClassName, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("<img id='{3}' class='{0}' qtip='{2}' src='{1}'/>")
        SafeHtml debugCell(String imgClassName, SafeUri img, String toolTip, String debugId);
    }

    private static final Resources resources = GWT.create(Resources.class);
    private static final Templates templates = GWT.create(Templates.class);
    private String baseID;
    private HasHandlers hasHandlers;

    public AppInfoCell() {
        super(CLICK);
        resources.css().ensureInjected();
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        String imgClassName, tooltip;
        imgClassName = resources.css().appRun();
        tooltip = I18N.DISPLAY.clickAppInfo();
        final SafeUri safeUri = IplantResources.RESOURCES.info().getSafeUri();
        if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)){
            String debugId = baseID + "." + value.getId() + AppsModule.Ids.APP_INFO_CELL;
            sb.append(templates.debugCell(imgClassName, safeUri, tooltip, debugId));
        }else {
            sb.append(templates.cell(imgClassName, safeUri, tooltip));
        }
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
                    doOnClick(eventTarget, value);
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

    private void doOnClick(Element eventTarget, App value) {
        if(hasHandlers != null){
            hasHandlers.fireEvent(new AppInfoSelectedEvent(value));
        }
    }

}
