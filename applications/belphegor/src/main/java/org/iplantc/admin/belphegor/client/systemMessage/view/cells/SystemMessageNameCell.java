package org.iplantc.admin.belphegor.client.systemMessage.view.cells;

import org.iplantc.admin.belphegor.client.systemMessage.SystemMessageView;
import org.iplantc.de.client.models.systemMessages.SystemMessage;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
import org.iplantc.de.resources.client.FavoriteCellStyle;
import org.iplantc.de.resources.client.IplantResources;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

import com.sencha.gxt.core.client.XTemplates;

public class SystemMessageNameCell extends AbstractCell<SystemMessage> {

    interface Templates extends XTemplates {
        @XTemplate("<span name='smName' class='{style.nameStyle}' >{sysMessage.body}</span>")
        SafeHtml genome(DiskResourceNameCellStyle style, SystemMessage sysMessage);
    }

    private static final DiskResourceNameCellStyle diskResourceNameStyle = IplantResources.RESOURCES.diskResourceNameCss();
    private final FavoriteCellStyle appFavStyle = IplantResources.RESOURCES.favoriteCss();

    private final Templates templates = GWT.create(Templates.class);
    private final SystemMessageView view;

    public SystemMessageNameCell(SystemMessageView view) {
        super(CLICK);
        diskResourceNameStyle.ensureInjected();
        appFavStyle.ensureInjected();
        this.view = view;
    }

    @Override
    public void render(Cell.Context arg0, SystemMessage value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        sb.append(templates.genome(diskResourceNameStyle, value));
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, SystemMessage value, NativeEvent event, ValueUpdater<SystemMessage> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget) && (Event.as(event).getTypeInt() == Event.ONCLICK) && eventTarget.getAttribute("name").equalsIgnoreCase("smName")) {
            view.editSystemMessage(value);
        }
    }

}
