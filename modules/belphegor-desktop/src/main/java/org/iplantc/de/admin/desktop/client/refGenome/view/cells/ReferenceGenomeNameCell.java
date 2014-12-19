package org.iplantc.de.admin.desktop.client.refGenome.view.cells;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
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

public class ReferenceGenomeNameCell extends AbstractCell<ReferenceGenome> {

    interface Templates extends XTemplates {
        @XTemplate("<span><span class='{appStyle.unavailable}'> </span>&nbsp;<span name='rgName' class='{style.nameStyle}' >{refGenome.name}</span></span>")
        SafeHtml genomeDeleted(FavoriteCellStyle appStyle, DiskResourceNameCellStyle style, ReferenceGenome refGenome);

        @XTemplate("<span name='rgName' class='{style.nameStyle}' >{refGenome.name}</span>")
        SafeHtml genome(DiskResourceNameCellStyle style, ReferenceGenome refGenome);
    }

    private static final DiskResourceNameCellStyle diskResourceNameStyle = IplantResources.RESOURCES.diskResourceNameCss();
    private final FavoriteCellStyle appFavStyle = IplantResources.RESOURCES.favoriteCss();

    private final Templates templates = GWT.create(Templates.class);
    private final RefGenomeView view;

    public ReferenceGenomeNameCell(RefGenomeView view) {
        super(CLICK);
        diskResourceNameStyle.ensureInjected();
        appFavStyle.ensureInjected();
        this.view = view;
    }

    @Override
    public void render(Cell.Context arg0, ReferenceGenome value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        if (value.isDeleted()) {
            sb.append(templates.genomeDeleted(appFavStyle, diskResourceNameStyle, value));
        } else {
            sb.append(templates.genome(diskResourceNameStyle, value));
        }
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, ReferenceGenome value, NativeEvent event, ValueUpdater<ReferenceGenome> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget) 
                && (Event.as(event).getTypeInt() == Event.ONCLICK)
                && eventTarget.getAttribute("name").equalsIgnoreCase("rgName")) {
            view.editReferenceGenome(value);
        }
    }

}