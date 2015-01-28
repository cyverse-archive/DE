package org.iplantc.de.theme.base.client.diskResource.grid;

import org.iplantc.de.diskResource.client.GridView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 */
public class GridViewDefaultAppearance implements GridView.Appearance {
    private final GridViewDisplayStrings displayStrings;

    public GridViewDefaultAppearance() {
        this(GWT.<GridViewDisplayStrings> create(GridViewDisplayStrings.class));
    }

    GridViewDefaultAppearance(final GridViewDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String actionsColumnLabel() {
        return "";
    }

    @Override
    public int actionsColumnWidth() {
        return 90;
    }

    @Override
    public String createdDateColumnLabel() {
        return null;
    }

    @Override
    public int createdDateColumnWidth() {
        return 130;
    }

    @Override
    public String lastModifiedColumnLabel() {
        return null;
    }

    @Override
    public int lastModifiedColumnWidth() {
        return 130;
    }

    @Override
    public int liveGridViewRowHeight() {
        return 25;
    }

    @Override
    public int liveToolItemWidth() {
        return 150;
    }

    @Override
    public String nameColumnLabel() {
        return null;
    }

    @Override
    public int nameColumnWidth() {
        return 50;
    }

    @Override
    public String pathColumnLabel() {
        return null;
    }

    @Override
    public int pathColumnWidth() {
        return 100;
    }

    @Override
    public SafeHtmlBuilder pathFieldLabel() {
        return new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(displayStrings.pathFieldLabel());
    }

    @Override
    public String pathFieldLabelWidth() {
        return "50";
    }

    @Override
    public String pathFieldEmptyText() {
        return displayStrings.pathFieldEmptyText();
    }

    @Override
    public int selectionStatusItemWidth() {
        return 100;
    }

    @Override
    public void setPagingToolBarStyle(ToolBar pagingToolBar) {
         pagingToolBar.addStyleName(ThemeStyles.get().style().borderTop());
        pagingToolBar.getElement().getStyle().setProperty("borderBottom", "none");
    }

    @Override
    public String sizeColumnLabel() {
        return null;
    }

    @Override
    public int sizeColumnWidth() {
        return 70;
    }

    @Override
    public String gridViewEmptyText() {
        return displayStrings.noItemsToDisplay();
    }
}
