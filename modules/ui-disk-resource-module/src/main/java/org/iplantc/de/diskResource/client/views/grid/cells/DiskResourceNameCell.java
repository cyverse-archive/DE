package org.iplantc.de.diskResource.client.views.grid.cells;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import static com.google.gwt.dom.client.BrowserEvents.*;
import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

import com.sencha.gxt.widget.core.client.Popup;

/**
 * A cell for displaying the icons and names for <code>DiskResource</code> list items.
 * 
 * @author jstroot
 */
public class DiskResourceNameCell extends AbstractCell<DiskResource> {

    public interface Appearance {

        String diskResourceNotAvailable();

        String drFileClass();

        String drFileTrashClass();

        String drFolderClass();

        String drFolderTrashClass();

        Popup getFilteredDiskResourcePopup();

        String nameDisabledStyle();

        String nameStyle();

        String nameStyleNoPointer();

        String pathListClass();

        void render(SafeHtmlBuilder sb, String imgClassName, String nameStyle, String name,
                    String baseID, String debugId);
    }

    private final DiskResourceUtil diskResourceUtil;
    private final Appearance appearance;

    private String baseID;

    public void setBaseDebugId(String baseID) {
        this.baseID = baseID;
    }

    private final boolean previewEnabled;
    private HasHandlers hasHandlers;

    private Popup linkPopup;

    private final DiskResourceFavoriteCell favCell;

    public DiskResourceNameCell(final DiskResourceUtil diskResourceUtil) {
        this(true, diskResourceUtil);
    }

    public DiskResourceNameCell(final boolean previewEnabled,
                                final DiskResourceUtil diskResourceUtil) {
        this(previewEnabled,
             diskResourceUtil,
             GWT.<Appearance> create(Appearance.class));
    }

    public DiskResourceNameCell(final boolean previewEnabled,
                                final DiskResourceUtil diskResourceUtil,
                                final Appearance appearance) {
        super(CLICK, MOUSEOVER);
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;
        favCell = new DiskResourceFavoriteCell();
        this.previewEnabled = previewEnabled;
    }

    @Override
    public void render(Cell.Context context, DiskResource value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        favCell.setBaseDebugId(baseID);
        favCell.render(context, value, sb);

        boolean inTrash = diskResourceUtil.inTrash(value);

        String name;
        if (Strings.isNullOrEmpty(value.getName())) {
            name = "";
        } else {
            name = value.getName();
        }
        String nameStyle = value.isFilter() ? appearance.nameDisabledStyle() : appearance.nameStyle();
        String imgClassName = ""; //$NON-NLS-1$
        String infoType1 = value.getInfoType();
        InfoType infoType = InfoType.fromTypeString(infoType1);
        if(InfoType.HT_ANALYSIS_PATH_LIST.equals(infoType)){
           imgClassName = appearance.pathListClass();
        } else if (value instanceof File) {
            if (!previewEnabled) {
                nameStyle = appearance.nameStyleNoPointer();
            }

            imgClassName = inTrash ? appearance.drFileTrashClass() : appearance.drFileClass();
        } else if (value instanceof Folder) {
            imgClassName = inTrash ? appearance.drFolderTrashClass() : appearance.drFolderClass();
        }

        if (value.isFilter()) {
            nameStyle += " " + appearance.nameDisabledStyle();
        }

        final String debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.NAME_CELL;
        appearance.render(sb, imgClassName, nameStyle, name, baseID, debugId);
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
                    doOnClick(eventTarget, value);
                    break;
                case Event.ONMOUSEOVER:
                    doOnMouseOver(eventTarget, value);
                    break;
                default:
                    break;
            }
        }
    }

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
        favCell.setHasHandlers(hasHandlers);
    }

    private void doOnMouseOver(final Element eventTarget, DiskResource value) {
        if (linkPopup != null) {
            linkPopup.hide();
            linkPopup = null;
        }
        if (isValidClickTarget(eventTarget)
            && value.isFilter()) {
            linkPopup = appearance.getFilteredDiskResourcePopup();
            schedulePopupTimer(eventTarget);
        }
    }

    private void schedulePopupTimer(final Element eventTarget) {
        Timer t = new Timer() {

            @Override
            public void run() {
                if (linkPopup != null
                        && (eventTarget.getOffsetHeight() > 0 || eventTarget.getOffsetWidth() > 0)) {
                    linkPopup.showAt(eventTarget.getAbsoluteLeft() + 25,
                            eventTarget.getAbsoluteTop() - 15);
                }

            }
        };
        t.schedule(2500);
    }

    private void doOnClick(Element eventTarget, DiskResource value) {
        if (!isValidClickTarget(eventTarget)
            || value.isFilter()) {
            return;
        }
        if(hasHandlers != null){
            hasHandlers.fireEvent(new DiskResourceNameSelectedEvent(value));
        }
    }

    private boolean isValidClickTarget(Element eventTarget) {
        return eventTarget.getAttribute("name").equalsIgnoreCase("drName");
    }

}
