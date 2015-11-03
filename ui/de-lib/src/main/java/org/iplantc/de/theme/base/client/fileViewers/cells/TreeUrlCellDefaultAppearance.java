package org.iplantc.de.theme.base.client.fileViewers.cells;

import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.fileViewers.client.views.cells.TreeUrlCell;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class TreeUrlCellDefaultAppearance implements TreeUrlCell.TreeUrlCellAppearance{
    @Override
    public void render(SafeHtmlBuilder sb, VizUrl model) {
        // TODO JDS We should use CssResource here
        sb.appendHtmlConstant("<div style=\"cursor:pointer;text-decoration:underline;white-space:pre-wrap;\">"
                                  + model.getUrl() + "</div>");
    }

    @Override
    public String treeUrlExternalWindowWidthHeight() {
        return "width=800,height=600";
    }
}
