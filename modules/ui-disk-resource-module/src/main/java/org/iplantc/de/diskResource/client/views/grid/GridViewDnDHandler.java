package org.iplantc.de.diskResource.client.views.grid;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.GridView;

import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;

import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent.DndDragEnterHandler;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent.DndDragMoveHandler;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragMoveEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
class GridViewDnDHandler implements DndDragStartHandler,
                                    DndDropHandler,
                                    DndDragMoveHandler,
                                    DndDragEnterHandler {

    private final GridView.Appearance appearance;
    private final DiskResourceUtil diskResourceUtil;
    private final GridView.Presenter presenter;
    /**
     * Guard against rapid clicks triggering drag+drop events.
     */
    private boolean moved;

    public GridViewDnDHandler(final DiskResourceUtil diskResourceUtil,
                              final GridView.Presenter presenter,
                              final GridView.Appearance appearance) {
        this.presenter = presenter;
        this.diskResourceUtil = diskResourceUtil;
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

        if (isSelectAllChecked()) {
            event.getStatusProxy().update(appearance.dataDragDropStatusText(getTotalSelectionCount()));
        } else {
            event.getStatusProxy().update(appearance.dataDragDropStatusText(dropData.size()));
        }
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

        List<? extends DiskResource> dragData = getDragSources(dragStartEl);

        if (dragData.isEmpty()
                || containsFilteredItems(dragData)) {
            // Cancel drag
            event.setCancelled(true);
        } else {
            event.setData(dragData);
            if (isSelectAllChecked()) {
                event.getStatusProxy().update(appearance.dataDragDropStatusText(getTotalSelectionCount()));
            } else {
                event.getStatusProxy().update(appearance.dataDragDropStatusText(dragData.size()));
            }
            event.getStatusProxy().setStatus(true);
            event.setCancelled(false);
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

    private boolean canDragDataToTargetFolder(final Folder targetFolder,
                                              final List<DiskResource> dropData) {
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
                if (diskResourceUtil.isDescendantOfFolder((Folder) dr, targetFolder)) {
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

    private void doMoveDiskResources(Folder targetFolder, List<DiskResource> resources) {
        presenter.doMoveDiskResources(targetFolder, resources);
    }

    private List<? extends DiskResource> getDragSources(Element dragStartEl) {
        final List<DiskResource> selectedDiskResources = presenter.getSelectedDiskResources();

        /*
         * Return an empty list (empty drag data) if
         * -- the target row cannot be found in the grid, OR
         * -- the presenter doesn't have any selections
         */
        Element targetRow = presenter.findGridRow(dragStartEl);
        if(targetRow != presenter.findGridRow(dragStartEl)
            || selectedDiskResources.isEmpty()){
            return Collections.emptyList();
        }

        /*
         * Return an empty list if the drag start element is not included in current selection
         */
        int dropIndex = presenter.findGridRowIndex(targetRow);
        if (dropIndex == -1
                || presenter.getAllDiskResources().get(dropIndex) == null) {
            return Collections.emptyList();
        }

        return Lists.newArrayList(selectedDiskResources);
    }

    @SuppressWarnings("unchecked")
    private List<DiskResource> getDropData(Object data) {
        if (!((data instanceof Collection<?>)
                  && !((Collection<?>) data).isEmpty()
                  && ((Collection<?>) data).iterator().next() instanceof DiskResource)) {
            return null;
        }
        List<DiskResource> dropData;
        dropData = Lists.newArrayList((Collection<DiskResource>) data);

        return dropData;
    }

    private Folder getDropTargetFolder(Element eventTargetElement) {
        Folder ret;
        Element targetRow = presenter.findGridRow(eventTargetElement);

        if(targetRow == null){
            ret = getSelectedUploadFolder();
        } else {
            int dropIndex = presenter.findGridRowIndex(targetRow);
            DiskResource selDiskResource = presenter.getAllDiskResources().get(dropIndex);
            ret = (selDiskResource instanceof Folder) ? (Folder)selDiskResource : null;
        }

        return ret;
    }

    private Folder getSelectedUploadFolder() {
        return presenter.getSelectedUploadFolder();
    }

    private int getTotalSelectionCount() {
        return presenter.getSelectedDiskResources().size();
    }

    private boolean isSelectAllChecked() {
        return presenter.isSelectAllChecked();
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
        if (isSelectAllChecked()) {
            status.update(appearance.dataDragDropStatusText(getTotalSelectionCount()));
        } else {
            status.update(appearance.dataDragDropStatusText(dropData.size()));
        }

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
