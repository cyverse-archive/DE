package org.iplantc.de.theme.base.client.admin.refGenome;

import org.iplantc.de.admin.desktop.client.refGenome.view.cells.ReferenceGenomeNameCell;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
import org.iplantc.de.resources.client.FavoriteCellStyle;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author jstroot
 */
public class ReferenceGenomeNameCellDefaultAppearance implements ReferenceGenomeNameCell.ReferenceGenomeNameCellAppearance {
    interface Templates extends XTemplates {
        @XTemplate("<span><span class='{appStyle.unavailable}'> </span>&nbsp;<span name='rgName' class='{style.nameStyle}' id='{debugId}' >{refGenome.name}</span></span>")
        SafeHtml genomeDeleted(FavoriteCellStyle appStyle, DiskResourceNameCellStyle style, ReferenceGenome refGenome, String debugId);

        @XTemplates.XTemplate("<span name='rgName' class='{style.nameStyle}' id='{debugId}' >{refGenome.name}</span>")
        SafeHtml genome(DiskResourceNameCellStyle style, ReferenceGenome refGenome, String debugId);
    }

    private final DiskResourceNameCellStyle diskResourceNameCellStyle;
    private final FavoriteCellStyle favoriteCellStyle;
    private final Templates templates;
    private final IplantResources iplantResources;

    public ReferenceGenomeNameCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    ReferenceGenomeNameCellDefaultAppearance(final Templates templates,
                                             final IplantResources iplantResources) {
        this.templates = templates;
        this.iplantResources = iplantResources;
        this.favoriteCellStyle = iplantResources.favoriteCss();
        this.diskResourceNameCellStyle = iplantResources.diskResourceNameCss();

        this.favoriteCellStyle.ensureInjected();
        this.diskResourceNameCellStyle.ensureInjected();
    }

    @Override
    public void renderDeletedReferenceGenomeCell(SafeHtmlBuilder sb, ReferenceGenome value, String debugId) {
        sb.append(templates.genomeDeleted(favoriteCellStyle, diskResourceNameCellStyle, value, debugId));
    }

    @Override
    public void renderReferenceGenomeCell(SafeHtmlBuilder sb, ReferenceGenome value, String debugId) {
        sb.append(templates.genome(diskResourceNameCellStyle, value, debugId));
    }
}
