package org.iplantc.de.diskResource.client.views.cells;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.views.cells.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
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
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.widget.core.client.Popup;

/**
 * A cell for displaying the icons and names for <code>DiskResource</code> list items.
 * 
 *
 * FIXME REFACTOR This cell needs an appearance
 * @author jstroot
 * 
 */
public class DiskResourceNameCell extends AbstractCell<DiskResource> {

    private static final DiskResourceNameCellStyle CSS = IplantResources.RESOURCES.diskResourceNameCss();
    private String baseID;

    public void setBaseDebugId(String baseID) {
        this.baseID = baseID;
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span></span><span class='{0}'> </span>&nbsp;<span name=\"drName\" title='{2}' class='{1}' >{2}</span></span>")
                SafeHtml
                cell(String imgClassName, String diskResourceClassName, String diskResourceName);

        @SafeHtmlTemplates.Template("<span><span class='{0}'> </span>&nbsp;<span id='{3}' name=\"drName\" title='{2}' class='{1}' >{2}</span></span>")
                SafeHtml
                debugCell(String imgClassName,
                          String diskResourceClassName,
                          String diskResourceName,
                          String id);
    }

    final Templates templates = GWT.create(Templates.class);
    private final boolean previewEnabled;
    private HasHandlers hasHandlers;

    private Popup linkPopup;

    private final DiskResourceFavoriteCell favCell;

    public DiskResourceNameCell() {
        this(true);
    }

    public DiskResourceNameCell(boolean previewEnabled) {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        favCell = new DiskResourceFavoriteCell();
        this.previewEnabled = previewEnabled;
        CSS.ensureInjected();
    }

    @Override
    public void render(Cell.Context context, DiskResource value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        favCell.render(context, value, sb);

        boolean inTrash = value.getPath().startsWith(UserInfo.getInstance().getBaseTrashPath());

        SafeHtml name;
        if (Strings.isNullOrEmpty(value.getName())) {
            name = SafeHtmlUtils.EMPTY_SAFE_HTML;
        } else {
            name = SafeHtmlUtils.fromString(value.getName());
        }
        String nameStyle = CSS.nameStyle();
        String imgClassName = ""; //$NON-NLS-1$
        String infoType1 = value.getInfoType();
        InfoType infoType = InfoType.fromTypeString(infoType1);
        if(InfoType.PATH_LIST.equals(infoType)){
           imgClassName = CSS.pathList();
        } else if (value instanceof File) {
            if (!previewEnabled) {
                nameStyle = CSS.nameStyleNoPointer();
            }

            imgClassName = inTrash ? CSS.drFileTrash() : CSS.drFile();
        } else if (value instanceof Folder) {
            imgClassName = inTrash ? CSS.drFolderTrash() : CSS.drFolder();
        }

        if (value.isFilter()) {
            nameStyle += " " + CSS.nameDisabledStyle(); //$NON-NLS-1$
        } else {
            if (value.isFavorite()) {

            } else {

            }
        }

        if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)) {
            final String debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.NAME_CELL;
            sb.append(templates.debugCell(imgClassName, nameStyle, name.asString(), debugId));
        }else {
            sb.append(templates.cell(imgClassName, nameStyle, name.asString()));
        }
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, DiskResource value,
            NativeEvent event, ValueUpdater<DiskResource> valueUpdater) {
        if (value == null) {
            return;
        }
        favCell.onBrowserEvent(context, parent, value, event, valueUpdater);
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

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
        // Side effect ?
        favCell.setHasHandlers(hasHandlers);
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
                linkPopup.getElement().getStyle().setFontSize(11, Unit.PX);
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
        if(hasHandlers != null){
            hasHandlers.fireEvent(new DiskResourceNameSelectedEvent(value));
        }
    }

    private boolean isValidClickTarget(Element eventTarget, DiskResource value) {
        return eventTarget.getAttribute("name").equalsIgnoreCase("drName") //$NON-NLS-1$ //$NON-NLS-2$
                && !value.isFilter();
    }

}
