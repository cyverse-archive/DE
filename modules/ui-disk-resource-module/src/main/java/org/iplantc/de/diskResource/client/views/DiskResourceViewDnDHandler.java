package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.views.DiskResourceView.Presenter;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Sets;
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
import java.util.Set;

class DiskResourceViewDnDHandler implements DndDragStartHandler, DndDropHandler, DndDragMoveHandler,
        DndDragEnterHandler {

    private final Presenter presenter;
    private final DiskResourceView view;

    /**
     * Guard against rapid clicks triggering drag+drop events.
     */
    private boolean moved;

    public DiskResourceViewDnDHandler(final DiskResourceView view, final DiskResourceView.Presenter presenter) {
        this.view = view;
        this.presenter = presenter;
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
        if(view.isSelectAll()) {
            status.update(I18N.DISPLAY.dataDragDropStatusText(view.getTotalSelectionCount()));
        } else {
            status.update(I18N.DISPLAY.dataDragDropStatusText(dropData.size()));
        }

        // Verify we have a drop target.
        if (targetFolder == null) {
            status.setStatus(false);
            return false;
        }

        // Check for permissions
        if (!(DiskResourceUtil.isMovable(targetFolder, dropData))) {
            status.setStatus(false);
            status.update(I18N.ERROR.permissionErrorMessage());
            return false;
        }

        // Check if the drop data contains an ancestor folder of the target folder.
        if (!presenter.canDragDataToTargetFolder(targetFolder, dropData)) {
            status.setStatus(false);
            return false;
        }

        return true;
    }

    @Override
    public void onDragStart(DndDragStartEvent event) {
        moved = false;

        Element dragStartEl = event.getDragStartEvent().getStartElement();

        Set<? extends DiskResource> dragData = presenter.getDragSources(event.getTarget(), dragStartEl);

        if ((dragData != null) && !dragData.isEmpty() && (!containsFilteredItems(dragData))) {
            event.setData(dragData);
            if(view.isSelectAll()) {
                event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(view.getTotalSelectionCount()));
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
        Folder targetFolder = presenter.getDropTargetFolder(dragEnterEvent.getTarget(),
                Element.as(target));

        if (!validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
            return;
        }

        if(view.isSelectAll()) {
            event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(view.getTotalSelectionCount()));
        } else {
            event.getStatusProxy().update(I18N.DISPLAY.dataDragDropStatusText(dropData.size()));
        }
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        moved = true;

        Set<DiskResource> dropData = getDropData(event.getDragSource().getData());
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        Folder targetFolder = presenter.getDropTargetFolder(event.getDropTarget().getWidget(),
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
        Folder targetFolder = presenter.getDropTargetFolder(event.getDropTarget().getWidget(),
                Element.as(target));

        if (validateDropStatus(targetFolder, dropData, event.getStatusProxy())) {
            presenter.doMoveDiskResources(targetFolder, dropData);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<DiskResource> getDropData(Object data) {
        if (!((data instanceof Collection<?>)
                && !((Collection<?>)data).isEmpty()
                && ((Collection<?>)data).iterator().next() instanceof DiskResource)) {
            return null;
        }
        Set<DiskResource> dropData = null;
        dropData = Sets.newHashSet((Collection<DiskResource>)data);

        return dropData;
    }
}
