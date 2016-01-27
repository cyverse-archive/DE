package org.iplantc.de.theme.base.client.admin.toolAdmin.cells;

import org.iplantc.de.admin.desktop.client.toolAdmin.view.cells.ToolAdminNameCell;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author aramsey
 */
public class ToolAdminViewCellDefaultAppearance
        implements ToolAdminNameCell.ToolAdminNameCellAppearance {

    interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span name='{elementName}' class='{style.nameStyle}' >{tool.name}</span>")
        SafeHtml tool(String elementName, DiskResourceNameCellStyle style, Tool tool);
    }

    private final Templates templates;
    private final DiskResourceNameCellStyle defaultStyle;

    public ToolAdminViewCellDefaultAppearance(IplantResources iplantResources, Templates templates) {
        this.defaultStyle = iplantResources.diskResourceNameCss();
        this.templates = templates;
    }

    public ToolAdminViewCellDefaultAppearance() {
        this(GWT.<IplantResources>create(IplantResources.class),
             GWT.<Templates>create(Templates.class));
    }

    @Override
    public void render(SafeHtmlBuilder safeHtmlBuilder, Tool tool) {
        if (tool == null) {
            return;
        }

        safeHtmlBuilder.append(templates.tool(CLICKABLE_ELEMENT_NAME, defaultStyle, tool));
    }
}
