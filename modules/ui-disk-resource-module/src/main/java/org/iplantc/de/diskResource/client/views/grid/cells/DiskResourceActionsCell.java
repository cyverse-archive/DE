package org.iplantc.de.diskResource.client.views.grid.cells;

import static org.iplantc.de.client.models.diskResources.PermissionValue.own;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelected;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Event;

/**
 * @author jstroot
 */
public class DiskResourceActionsCell extends AbstractCell<DiskResource> {

    public interface Appearance {

        String actionClass();

        String commentsTooltip();

        ImageResource dataLinkIcon();

        ImageResource linkAddIcon();

        ImageResource metadataIcon();

        String metadataTooltip();

        void render(SafeHtmlBuilder sb, String name, String toolTip, String className,
                    SafeUri imgSrc, String baseID, String debugId);

        String shareByDeTooltip();

        String shareFileByLinkTooltip();

        String shareFolderByLinkTooltip();

        ImageResource shareIcon();

        ImageResource userCommentIcon();
    }

    private final String COMMENTS_ACTION;
    private final String MANAGE_METADATA_ACTION;
    private final String SHARE_BY_DE_ACTION;
    private final String SHARE_FILE_BY_LINK_ACTION;
    private final String SHARE_FOLDER_BY_LINK_ACTION;
    private final Appearance appearance;
    private final DiskResourceUtil diskResourceUtil;
    private String baseID;
    private HasHandlers hasHandlers;

    public DiskResourceActionsCell(final DiskResourceUtil diskResourceUtil) {
        this(diskResourceUtil,
             GWT.<Appearance>create(Appearance.class));
    }

    public DiskResourceActionsCell(final DiskResourceUtil diskResourceUtil,
                                   final Appearance appearance) {
        super(CLICK);
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;

        SHARE_FOLDER_BY_LINK_ACTION = appearance.shareFolderByLinkTooltip();
        SHARE_FILE_BY_LINK_ACTION = appearance.shareFileByLinkTooltip();
        SHARE_BY_DE_ACTION = appearance.shareByDeTooltip();
        MANAGE_METADATA_ACTION = appearance.metadataTooltip();
        COMMENTS_ACTION = appearance.commentsTooltip();

    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, DiskResource value,
                               NativeEvent event, ValueUpdater<DiskResource> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (eventTarget.getNodeName().equalsIgnoreCase("img")
                && parent.isOrHasChild(eventTarget)) {
            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void render(Context context, DiskResource value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        if (value.isFilter() || diskResourceUtil.inTrash(value)) {
            return;
        }

        String name = null;
        String toolTip = null;
        String className = null;
        SafeUri imgSrc = null;

        String debugId = null;
        if (value instanceof Folder) {
            name = SHARE_FOLDER_BY_LINK_ACTION;
            toolTip = SHARE_FOLDER_BY_LINK_ACTION;
            className = appearance.actionClass();
            imgSrc = appearance.dataLinkIcon().getSafeUri();
            debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.ACTION_CELL_DATA_LINK;
        }
        if ((value instanceof File)
                && own.equals(value.getPermission())) {
            name = SHARE_FILE_BY_LINK_ACTION;
            toolTip = SHARE_FILE_BY_LINK_ACTION;
            className = appearance.actionClass();
            imgSrc = appearance.linkAddIcon().getSafeUri();
            debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.ACTION_CELL_DATA_LINK_ADD;
        }


        // Append link action
        if (name != null && className != null && imgSrc != null) {
            appearance.render(sb, name, toolTip, className, imgSrc, baseID, debugId);
        }

        debugId = null;
        if (own.equals(value.getPermission())) {
            name = SHARE_BY_DE_ACTION;
            toolTip = SHARE_BY_DE_ACTION;
            className = appearance.actionClass();
            imgSrc = appearance.shareIcon().getSafeUri();
            debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.ACTION_CELL_SHARE;
        } else {
            name = null;
            toolTip = null;
            className = null;
            imgSrc = null;
        }

        // Append Share action
        if ((name != null) && (className != null) && (imgSrc != null)) {
            appearance.render(sb, name, toolTip, className, imgSrc, baseID, debugId);
        }

        name = MANAGE_METADATA_ACTION;
        toolTip = MANAGE_METADATA_ACTION;
        className = appearance.actionClass();
        imgSrc = appearance.metadataIcon().getSafeUri();
        debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.ACTION_CELL_METADATA;

        // Append metadata action
        if (name != null && className != null && imgSrc != null) {
            appearance.render(sb, name, toolTip, className, imgSrc, baseID, debugId);
        }

        name = COMMENTS_ACTION;
        toolTip = COMMENTS_ACTION;
        className = appearance.actionClass();
        imgSrc = appearance.userCommentIcon().getSafeUri();
        debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.ACTION_CELL_COMMENTS;

        // append comments action
        if (name != null && className != null && imgSrc != null) {
            appearance.render(sb, name, toolTip, className, imgSrc, baseID, debugId);
        }

    }

    public void setBaseDebugId(String baseID) {
        this.baseID = baseID;
    }

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    private void doOnClick(Element eventTarget, DiskResource value) {
        if (hasHandlers == null) {
            return;
        }

        String action = eventTarget.getAttribute("name");
        if (action.equals(SHARE_FOLDER_BY_LINK_ACTION) || action.equals(SHARE_FILE_BY_LINK_ACTION)) {
            hasHandlers.fireEvent(new ShareByDataLinkSelected(value));
        } else if (action.equalsIgnoreCase(SHARE_BY_DE_ACTION)) {
            hasHandlers.fireEvent(new ManageSharingSelected(value));
        } else if (action.equalsIgnoreCase(MANAGE_METADATA_ACTION)) {
            hasHandlers.fireEvent(new ManageMetadataSelected(value));
        } else if (action.equalsIgnoreCase(COMMENTS_ACTION)) {
            hasHandlers.fireEvent(new ManageCommentsSelected(value));
        }

    }
}
