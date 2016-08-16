package org.iplantc.de.theme.base.client.admin.workshopAdmin.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import org.iplantc.de.admin.desktop.client.workshopAdmin.view.cells.MemberNameCell;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.resources.client.DiskResourceNameCellStyle;
import org.iplantc.de.resources.client.IplantResources;

/**
 * @author dennis
 */
public class MemberNameCellDefaultAppearance implements MemberNameCell.MemberNameCellAppearance {

    interface Templates extends XTemplates {
        @XTemplates.XTemplate("<span name='{elementName}' class='{style.nameStyle}' >{member.name}</span>")
        SafeHtml member(String elementName, DiskResourceNameCellStyle style, Member member);
    }

    private final Templates templates;
    private final DiskResourceNameCellStyle defaultStyle;

    public MemberNameCellDefaultAppearance() {
        IplantResources iplantResources = GWT.create(IplantResources.class);
        templates = GWT.create(Templates.class);
        defaultStyle = iplantResources.diskResourceNameCss();
    }

    @Override
    public void render(SafeHtmlBuilder builder, Member member) {
        if (member != null) {
            builder.append(templates.member(CLICKABLE_ELEMENT_NAME, defaultStyle, member));
        }
    }
}
