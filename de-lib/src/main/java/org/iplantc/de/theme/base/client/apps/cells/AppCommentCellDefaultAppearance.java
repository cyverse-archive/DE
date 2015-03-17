package org.iplantc.de.theme.base.client.apps.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppCommentCell.AppCommentCellAppearance;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * @author jstroot
 */
public class AppCommentCellDefaultAppearance implements AppCommentCellAppearance {

    public interface AppCommentCellStyles extends CssResource {
        @ClassName("comment_icon")
        String commentIcon();
    }

    public interface AppCommentCellResources extends ClientBundle {
        @Source("AppCommentCell.css")
        AppCommentCellStyles css();
    }

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<img name='{0}' title='{1}' class='{2}' src='{3}' />")
        SafeHtml imgCell(String name, String toolTip, String className, SafeUri imgSrc);
    }

    private final IplantDisplayStrings displayStrings = I18N.DISPLAY;
    private final IplantResources iplantResources = IplantResources.RESOURCES;

    private final Templates template;
    private final AppCommentCellResources resources;

    public AppCommentCellDefaultAppearance() {
        this((AppCommentCellResources)GWT.create(AppCommentCellResources.class));
    }

    public AppCommentCellDefaultAppearance(AppCommentCellResources resources) {
        this.resources = resources;
        resources.css().ensureInjected();
        this.template = GWT.create(Templates.class);
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        String comments = displayStrings.comments();
        String className = resources.css().commentIcon();
        SafeUri imgSrc = iplantResources.userComment().getSafeUri();
        sb.append(template.imgCell(comments, comments, className, imgSrc));
    }

}
