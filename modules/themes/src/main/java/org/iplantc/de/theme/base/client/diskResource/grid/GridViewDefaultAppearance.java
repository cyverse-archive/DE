package org.iplantc.de.theme.base.client.diskResource.grid;

import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceErrorMessages;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 */
public class GridViewDefaultAppearance implements GridView.Appearance {
    private final GridViewDisplayStrings displayStrings;
    private final DiskResourceMessages diskResourceMessages;
    private final DiskResourceErrorMessages errorMessages;
    private final IplantDisplayStrings iplantDisplayStrings;

    public GridViewDefaultAppearance() {
        this(GWT.<GridViewDisplayStrings> create(GridViewDisplayStrings.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<DiskResourceErrorMessages> create(DiskResourceErrorMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    GridViewDefaultAppearance(final GridViewDisplayStrings displayStrings,
                              final DiskResourceMessages diskResourceMessages,
                              final DiskResourceErrorMessages errorMessages,
                              final IplantDisplayStrings iplantDisplayStrings) {
        this.displayStrings = displayStrings;
        this.diskResourceMessages = diskResourceMessages;
        this.errorMessages = errorMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
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
        // FIXME
        return iplantDisplayStrings.dateSubmitted();
    }

    @Override
    public int createdDateColumnWidth() {
        return 130;
    }

    @Override
    public String dataDragDropStatusText(int totalSelectionCount) {
        return diskResourceMessages.dataDragDropStatusText(totalSelectionCount).asString();
    }

    @Override
    public String lastModifiedColumnLabel() {
        return iplantDisplayStrings.lastModified();
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
        return iplantDisplayStrings.name();
    }

    @Override
    public int nameColumnWidth() {
        return 50;
    }

    @Override
    public String pathColumnLabel() {
        return iplantDisplayStrings.path();
    }

    @Override
    public int pathColumnWidth() {
        return 100;
    }

    @Override
    public String pathFieldLabel() {
        return new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(displayStrings.pathFieldLabel()).toSafeHtml().asString();
    }

    @Override
    public int pathFieldLabelWidth() {
        return 50;
    }

    @Override
    public String pathFieldEmptyText() {
        return displayStrings.pathFieldEmptyText();
    }

    @Override
    public String permissionErrorMessage() {
        return errorMessages.permissionErrorMessage();
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
        return diskResourceMessages.size();
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
