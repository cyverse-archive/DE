package org.iplantc.de.theme.base.client.admin.systemMessage.cells;

import org.iplantc.de.admin.desktop.client.systemMessage.view.cells.SystemMessageNameCell;
import org.iplantc.de.client.models.systemMessages.SystemMessage;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author jstroot
 */
public class SystemMessageNameCellDefaultAppearance implements SystemMessageNameCell.SystemMessageNameCellAppearance {
     interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span name='{elementName}' class='{style.nameStyle}' id='{debugID}' >{sysMessage.body}</span>")
        SafeHtml genome(String elementName, DiskResourceNameCellStyle style, SystemMessage sysMessage, String debugID);
    }

    private final DiskResourceNameCellStyle diskResourceNameCellStyle;

    private final IplantResources iplantResources;
    private final Templates templates;

    public SystemMessageNameCellDefaultAppearance() {
        this(GWT.<IplantResources> create(IplantResources.class),
             GWT.<Templates> create(Templates.class));
    }

    SystemMessageNameCellDefaultAppearance(final IplantResources iplantResources,
                                           final Templates templates) {
        this.iplantResources = iplantResources;
        this.diskResourceNameCellStyle = iplantResources.diskResourceNameCss();
        this.templates = templates;

        this.diskResourceNameCellStyle.ensureInjected();
    }

    @Override
    public void render(SafeHtmlBuilder sb, SystemMessage value, String debugID) {
         if(value == null){
             return;
         }

        sb.append(templates.genome(CLICKABLE_ELEMENT_NAME, diskResourceNameCellStyle, value, debugID));
    }
}
