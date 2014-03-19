package org.iplantc.de.diskResource.client.views.cells;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectedEvent;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
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
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.Popup;
import com.sencha.gxt.widget.core.client.tips.Tip;

/**
 * A cell for displaying the icons and names for <code>DiskResource</code> list items.
 * 
 * TODO JDS Implement preview tooltip. Tooltip will probably have to be {@link Tip}, since this is a cell
 * and not a widget.
 * 
 * @author jstroot
 * 
 */
public class DiskResourceNameCell extends AbstractCell<DiskResource> {

    private static final DiskResourceNameCellStyle CSS = IplantResources.RESOURCES.diskResourceNameCss();
    public static enum CALLER_TAG {
        DATA, SHARING;
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span><span class=\"{0}\"> </span>&nbsp;<span name=\"drName\" class=\"{1}\" >{2}</span></span>")
        SafeHtml cell(String imgClassName, String diskResourceClassName, SafeHtml diskResourceName);
    }

    final Templates templates = GWT.create(Templates.class);
    private final IsWidget caller;
    private boolean previewEnabled = true;

    final CALLER_TAG tag;
    private Popup linkPopup;

    public DiskResourceNameCell(IsWidget caller, CALLER_TAG tag) {
        super(CLICK, MOUSEOVER, MOUSEOUT);

        this.tag = tag;
        this.caller = caller;
        CSS.ensureInjected();
    }

    @Override
    public void render(Cell.Context context, DiskResource value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        SafeHtml name = SafeHtmlUtils.fromString(value.getName());
        if (value instanceof File) {
            String nameStyle = previewEnabled ? CSS.nameStyle() : CSS.nameStyleNoPointer();
            nameStyle = value.isFilter() ? nameStyle + " " + CSS.nameDisabledStyle() : nameStyle;
            sb.append(templates.cell(CSS.drFile(), nameStyle, name));
        } else if (value instanceof Folder) {
            String nameStyle = CSS.nameStyle() + (value.isFilter() ? " " + CSS.nameDisabledStyle() : "");
            sb.append(templates.cell(CSS.drFolder(), nameStyle, name));
        }

    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, DiskResource value,
            NativeEvent event, ValueUpdater<DiskResource> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

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

    private void doOnMouseOut(Element eventTarget, DiskResource value) {
        if (!isValidClickTarget(eventTarget, value)) {
            return;
        }

        eventTarget.getStyle().setTextDecoration(TextDecoration.NONE);
    }

    private void doOnMouseOver(final Element eventTarget, DiskResource value) {
        if (linkPopup != null) {
            linkPopup.hide();
            linkPopup = null;
        }
        if (!isValidClickTarget(eventTarget, value)) {
            if (value.isFilter()) {
                initPopup();
                linkPopup.add(new HTML(I18N.DISPLAY.diskResourceNotAvailable()));
                linkPopup.setSize("300px", "150px");
                schedulePopupTimer(eventTarget);
            }
            return;
        }

       
        eventTarget.getStyle().setTextDecoration(TextDecoration.UNDERLINE);
    }

    private void schedulePopupTimer(final Element eventTarget) {
        Timer t = new Timer() {

            @Override
            public void run() {
                if (linkPopup != null && (eventTarget.getOffsetHeight() > 0 || eventTarget.getOffsetWidth() > 0)) {
                    linkPopup.showAt(eventTarget.getAbsoluteLeft() + 25,
                            eventTarget.getAbsoluteTop() - 15);
                }

            }
        };
        t.schedule(2500);
    }

   

    private void initPopup() {
        linkPopup = new Popup();
        linkPopup.setBorders(true);
        linkPopup.getElement().getStyle().setBackgroundColor("#F8F8F8");
    }

    private void doOnClick(Element eventTarget, DiskResource value,
            ValueUpdater<DiskResource> valueUpdater) {

        if (!isValidClickTarget(eventTarget, value)) {
            return;
        }

        if (tag.equals(CALLER_TAG.DATA)) {
            EventBus.getInstance().fireEvent(new DiskResourceSelectedEvent(caller, value));
        }
    }

    private boolean isValidClickTarget(Element eventTarget, DiskResource value) {
        return eventTarget.getAttribute("name").equalsIgnoreCase("drName") //$NON-NLS-1$ //$NON-NLS-2$
                && tag != DiskResourceNameCell.CALLER_TAG.SHARING
                && (previewEnabled || !(value instanceof File)) && (!value.isFilter());
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

   

}
