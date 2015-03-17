package org.iplantc.de.theme.base.client.diskResource.grid.cells;

import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceActionsCell;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.grid.GridViewDisplayStrings;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * @author jstroot
 */
public class DiskResourceActionsCellDefaultAppearance implements DiskResourceActionsCell.Appearance {
    interface MyCss extends CssResource {
        @ClassName("actions_icon")
        String actionIcon();
    }

    interface Resources extends ClientBundle {

        @ClientBundle.Source("DiskResourceActionsCell.css")
        MyCss css();

        @Source("../../link.png")
        ImageResource dataLinkIcon();

        @Source("../../link_add.png")
        ImageResource linkAdd();

        @Source("../../comments.png")
        ImageResource metadataIcon();
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<img name='{0}' title='{1}' class='{2}' src='{3}'></img>")
        SafeHtml imgCell(String name, String toolTip, String className, SafeUri imgSrc);

        @SafeHtmlTemplates.Template("<img id='{4}' name='{0}' title='{1}' class='{2}' src='{3}'></img>")
        SafeHtml debugImgCell(String name, String toolTip, String className, SafeUri imgSrc, String id);
    }

    private final GridViewDisplayStrings displayStrings;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final Resources resources;
    private final IplantResources iplantResources;
    private final Templates templates;

    public DiskResourceActionsCellDefaultAppearance() {
        this(GWT.<GridViewDisplayStrings> create(GridViewDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<Resources> create(Resources.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<Templates> create(Templates.class));
    }

    DiskResourceActionsCellDefaultAppearance(final GridViewDisplayStrings displayStrings,
                                             final IplantDisplayStrings iplantDisplayStrings,
                                             final Resources resources,
                                             final IplantResources iplantResources,
                                             final Templates templates) {
        this.displayStrings = displayStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.resources = resources;
        this.iplantResources = iplantResources;
        this.templates = templates;

        this.resources.css().ensureInjected();
    }

    @Override
    public String actionClass() {
        return resources.css().actionIcon();
    }

    @Override
    public String commentsTooltip() {
        return iplantDisplayStrings.comments();
    }

    @Override
    public ImageResource dataLinkIcon() {
        return resources.dataLinkIcon();
    }

    @Override
    public ImageResource linkAddIcon() {
        return resources.linkAdd();
    }

    @Override
    public ImageResource metadataIcon() {
        return resources.metadataIcon();
    }

    @Override
    public String metadataTooltip() {
        return displayStrings.metadata();
    }

    @Override
    public void render(SafeHtmlBuilder sb, String name, String toolTip, String className,
                       SafeUri imgSrc, String baseID, String debugId) {

        if(DebugInfo.isDebugIdEnabled() && !Strings.isNullOrEmpty(baseID)){
            sb.append(templates.debugImgCell(name, toolTip, className, imgSrc, debugId));
        } else {
            sb.append(templates.imgCell(name, toolTip, className, imgSrc));
        }
    }

    @Override
    public String shareByDeTooltip() {
        return displayStrings.share() + " " + displayStrings.viaDiscoveryEnvironment();
    }

    @Override
    public String shareFileByLinkTooltip() {
        return displayStrings.share() + " " + displayStrings.viaPublicLink();
    }

    @Override
    public String shareFolderByLinkTooltip() {
        return displayStrings.share() + " " + iplantDisplayStrings.path();
    }

    @Override
    public ImageResource shareIcon() {
        return iplantResources.share();
    }

    @Override
    public ImageResource userCommentIcon() {
        return iplantResources.userComment();
    }
}
