package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.DiskResourceView.Presenter;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Sets;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.ui.IsWidget;

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
import java.util.Set;

/**
 * @author jstroot
 */
class DiskResourceViewDnDHandler implements DndDragStartHandler,
                                            DndDropHandler,
                                            DndDragMoveHandler,
                                            DndDragEnterHandler {

    private final Presenter presenter;
    private final DiskResourceView view;
    private final DiskResourceUtil diskResourceUtil;

    /**
     * Guard against rapid clicks triggering drag+drop events.
     */
    private boolean moved;

    public DiskResourceViewDnDHandler(final DiskResourceView view,
                                      final DiskResourceView.Presenter presenter) {
        this.view = view;
        this.presenter = presenter;
        this.diskResourceUtil = DiskResourceUtil.getInstance();
    }

    private boolean validateDropStatus(Folder targetFolder, Set<DiskResource> dropData,
            StatusProxy status) {
        // Verify we have drag data.
        if (dropData == null) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        if(isSelectAllChecked()) {
            status.update(I18N.DISPLAY.dataDragDropStatusText(getTotalSelectionCount()));
        } else {
            status.update(I18N.DISPLAY.dataDragDropStatusText(dropData.size()));
        }

        // Verify we have a drop target.
        if (targetFolder == null) {
            status.setStatus(false);
            return false;
        }

        // Check for permissions
        if (!(diskResourceUtil.isMovable(targetFolder, dropData))) {
            status.setStatus(false);
            status.update(I18N.ERROR.permissionErrorMessage());
            return false;
        }

        // Check if the drop data contains an ancestor folder of the target folder.
        if (!canDragDataToTargetFolder(targetFolder, dropData)) {
            status.setStatus(false);
            return false;
        }

        return true;
    }

    @Override
    public void onDragStart(DndDragStartEvent event) {
        moved = false;

        Element dragStartEl = event.getDragStartEvent().getStartElement();

        Set<? extends DiskResource> dragData = getDragSources(event.getTarget(), dragStartEl);

        if ((dragData != null) && !dragData.isEmpty() && (!containsFilteredItems(dragData))) {
            event.setData(dragData);
            if(isSelectAllChecked()) {
                event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(getTotalSelectionCount()));
            } else {
                event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(dragData.size()));
            }
            event.getStatusProxy().setStatus(true);
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    private boolean containsFilteredItems(Set<? extends DiskResource> dragData) {
        for (DiskResource dr : dragData) {
            if(dr.isFilter()) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        moved = false;

        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());
        DragMoveEvent dragEnterEvent = event.getDragEnterEvent();
        EventTarget target = dragEnterEvent.getNativeEvent().getEventTarget();
        Folder targetFolder = getDropTargetFolder(dragEnterEvent.getTarget(),
                Element.as(target));

        if (!validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
            return;
        }

        if(isSelectAllChecked()) {
            event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(getTotalSelectionCount()));
        } else {
            event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(dropData.size()));
        }
    }



    @Override
    public void onDragMove(DndDragMoveEvent event) {
        moved = true;

        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        Folder targetFolder = getDropTargetFolder(event.getDropTarget().getWidget(),
                Element.as(target));

        if (!validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDrop(DndDropEvent event) {
        // Guard against rapid clicks triggering drag+drop events.
        if (!moved) {
            return;
        }

        Set<DiskResource> dropData = getDropData(event.getData());
        EventTarget target = event.getDragEndEvent().getNativeEvent().getEventTarget();
        Folder targetFolder = getDropTargetFolder(event.getDropTarget().getWidget(),
                Element.as(target));

        if (validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            doMoveDiskResources(targetFolder, dropData);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<DiskResource> getDropData(Object data) {
        if (!((data instanceof Collection<?>)
                && !((Collection<?>)data).isEmpty()
                && ((Collection<?>)data).iterator().next() instanceof DiskResource)) {
            return null;
        }
        Set<DiskResource> dropData;
        dropData = Sets.newHashSet((Collection<DiskResource>)data);

        return dropData;
    }

    boolean isSelectAllChecked() {
        return view.isSelectAllChecked();
    }

    int getTotalSelectionCount() {
        return view.getTotalSelectionCount();
    }

    boolean canDragDataToTargetFolder(final Folder targetFolder, final Collection<DiskResource> dropData) {
        return presenter.canDragDataToTargetFolder(targetFolder, dropData);
    }

    Set<? extends DiskResource> getDragSources(IsWidget source, Element dragStartEl){
         return presenter.getDragSources(source, dragStartEl);
    }
    Folder getDropTargetFolder(IsWidget target, Element eventTargetElement) {
        return presenter.getDropTargetFolder(target, eventTargetElement);
    }

    void doMoveDiskResources(Folder targetFolder, Set<DiskResource> resources) {
        presenter.doMoveDiskResources(targetFolder, resources);
    }

}
