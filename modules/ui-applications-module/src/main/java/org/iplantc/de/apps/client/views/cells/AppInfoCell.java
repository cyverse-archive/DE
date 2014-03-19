package org.iplantc.de.apps.client.views.cells;

import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.client.models.apps.App;
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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;

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

        @SafeHtmlTemplates.Template("<img class=\"{0}\" qtip=\"{2}\" src=\"{1}\"/>")
        SafeHtml cell(String imgClassName, SafeUri img, String toolTip);
    }

    private static final Resources resources = GWT.create(Resources.class);
    private static final Templates templates = GWT.create(Templates.class);
    private AppsView view;

    public AppInfoCell(AppsView view) {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        this.view = view;
        resources.css().ensureInjected();

    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        sb.append(templates.cell(resources.css().appRun(),
                IplantResources.RESOURCES.info().getSafeUri(), I18N.DISPLAY.clickAppInfo()));
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
        // XXX JDS Place holder for switching images on hover
    }

    private void doOnMouseOver(Element eventTarget, App value) {
        // XXX JDS Place holder for switching images on hover
    }

    private void doOnClick(Element eventTarget, App value) {
        view.onAppInfoClick(value);
    }

}
