package org.iplantc.de.diskResource.client.views.navigation;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.NavigationView;

import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;

import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Collection;
import java.util.List;

/**
 * @author jstroot
 */
public class NavigationViewDnDHandler implements DndDragStartEvent.DndDragStartHandler,
                                                 DndDropEvent.DndDropHandler,
                                                 DndDragMoveEvent.DndDragMoveHandler,
                                                 DndDragEnterEvent.DndDragEnterHandler {

    private final DiskResourceUtil diskResourceUtil;
    private final NavigationView.Presenter navigationPresenter;
    private final NavigationView.Appearance appearance;
    private boolean moved;

    public NavigationViewDnDHandler(final DiskResourceUtil diskResourceUtil,
                                    final NavigationView.Presenter navigationPresenter,
                                    final NavigationView.Appearance appearance) {
        this.diskResourceUtil = diskResourceUtil;
        this.navigationPresenter = navigationPresenter;
        this.appearance = appearance;
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        moved = false;

        List<DiskResource> dropData = getDropData(event.getDragSource().getData());
        DragMoveEvent dragEnterEvent = event.getDragEnterEvent();
        EventTarget target = dragEnterEvent.getNativeEvent().getEventTarget();
        Folder targetFolder = getDropTargetFolder(Element.as(target));

        if (!validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
            return;
        }

        event.getStatusProxy().update(appearance.dataDragDropStatusText(dropData.size()));
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        moved = true;

        List<DiskResource> dropData = getDropData(event.getDragSource().getData());
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        Folder targetFolder = getDropTargetFolder(Element.as(target));

        if (!validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDragStart(DndDragStartEvent event) {
        moved = false;

        Element dragStartEl = event.getDragStartEvent().getStartElement();

        List<? extends DiskResource> dragData = getDragSources(getDropTargetFolder(dragStartEl));

        if ((dragData != null)
                && !dragData.isEmpty()
                && (!containsFilteredItems(dragData))) {
            event.setData(dragData);
            event.getStatusProxy().update(appearance.dataDragDropStatusText(dragData.size()));
            event.getStatusProxy().setStatus(true);
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDrop(DndDropEvent event) {
        // Guard against rapid clicks triggering drag+drop events.
        if (!moved) {
            return;
        }

        List<DiskResource> dropData = getDropData(event.getData());
        EventTarget target = event.getDragEndEvent().getNativeEvent().getEventTarget();
        Folder targetFolder = getDropTargetFolder(Element.as(target));

        if (validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            doMoveDiskResources(targetFolder, dropData);
        }
    }

    private boolean canDragDataToTargetFolder(Folder targetFolder, List<DiskResource> dropData) {
        if (targetFolder instanceof DiskResourceQueryTemplate) {
            return false;
        }

        if (targetFolder.isFilter()) {
            return false;
        }

        // Assuming that ownership is of no concern.
        for (DiskResource dr : dropData) {
            // if the resource is a direct child of target folder
            if (diskResourceUtil.isChildOfFolder(targetFolder, dr)) {
                return false;
            }

            if (dr instanceof Folder) {
                if (targetFolder.getPath().equals(dr.getPath())) {
                    return false;
                }

                // cannot drag an ancestor (parent, grandparent, etc) onto a
                // child and/or descendant
                if (diskResourceUtil.isDescendantOfFolder((Folder)dr, targetFolder)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean containsFilteredItems(List<? extends DiskResource> dragData) {
        for (DiskResource dr : dragData) {
            if (dr.isFilter()) {
                return true;
            }
        }

        return false;
    }

    private void doMoveDiskResources(Folder targetFolder, List<DiskResource> dropData) {
        navigationPresenter.doMoveDiskResources(targetFolder, dropData);

    }

    private List<? extends DiskResource> getDragSources(Folder srcFolder) {
        return (srcFolder == null) ? null : Lists.newArrayList(srcFolder);
    }

    private List<DiskResource> getDropData(Object data) {
        if (!((data instanceof Collection<?>)
                && !((Collection<?>)data).isEmpty()
                && ((Collection<?>)data).iterator().next() instanceof DiskResource)) {
            return null;
        }

        return Lists.newArrayList((Collection<DiskResource>)data);
    }

    private Folder getDropTargetFolder(Element eventTarget) {
        Folder ret = null;
        final Tree.TreeNode<Folder> treeNode = navigationPresenter.findTreeNode(eventTarget);
        if (treeNode != null) {
            ret = treeNode.getModel();
        }
        return ret;
    }

    private boolean validateDropStatus(final Folder targetFolder,
                                       final List<DiskResource> dropData,
                                       final StatusProxy status) {
        // Verify we have drag data.
        if (dropData == null) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(appearance.dataDragDropStatusText(dropData.size()));

        // Verify we have a drop target.
        if (targetFolder == null) {
            status.setStatus(false);
            return false;
        }

        // Check for permissions
        if (!(diskResourceUtil.isMovable(targetFolder, dropData))) {
            status.setStatus(false);
            status.update(appearance.permissionErrorMessage());
            return false;
        }

        // Check if the drop data contains an ancestor folder of the target folder.
        if (!canDragDataToTargetFolder(targetFolder, dropData)) {
            status.setStatus(false);
            return false;
        }

        return true;
    }
}
