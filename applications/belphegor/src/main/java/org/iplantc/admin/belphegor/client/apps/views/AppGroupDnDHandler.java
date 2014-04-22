package org.iplantc.admin.belphegor.client.apps.views;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;

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

/**
 * A Drag and Drop handler for the AdminAppViewImpl to support moving Apps and AppGroups.
 * 
 * @author psarando
 * 
 */
public class AppGroupDnDHandler implements DndDragStartHandler, DndDragEnterHandler, DndDragMoveHandler,
        DndDropHandler {

    private final AdminAppsView adminAppView;
    private final AdminAppsView.AdminPresenter presenter;

    /**
     * Guard against rapid clicks triggering drag+drop events.
     */
    private boolean moved;

    public AppGroupDnDHandler(AdminAppsView adminAppView, AdminAppsView.AdminPresenter presenter) {
        this.adminAppView = adminAppView;
        this.presenter = presenter;
    }

    private boolean validateMove(AppGroup targetGroup, Object source) {
        if (source instanceof AppGroup) {
            return presenter.canMoveAppGroup(targetGroup, (AppGroup)source);
        } else {
            return presenter.canMoveApp(targetGroup, (App)source);
        }
    }

    @Override
    public void onDragStart(DndDragStartEvent event) {
        moved = false;

        Element dragStartEl = event.getDragStartEvent().getStartElement();

        Object dragData = adminAppView.getAppGroupFromElement(dragStartEl);

        if (dragData == null) {
            // If we don't have an AppGroup, check for an App.
            dragData = adminAppView.getAppFromElement(dragStartEl);
        }

        if (dragData != null) {
            String dragDataLabel;
            if (dragData instanceof AppGroup) {
                dragDataLabel = ((AppGroup)dragData).getName();
            } else {
                dragDataLabel = ((App)dragData).getName();
            }

            event.setData(dragData);
            event.getStatusProxy().update(dragDataLabel);
            event.getStatusProxy().setStatus(true);
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        moved = false;
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        moved = true;

        // Get our destination category.
        EventTarget eventTarget = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        AppGroup targetGroup = adminAppView.getAppGroupFromElement(Element.as(eventTarget));

        // Check if the source may be dropped into the target.
        boolean isValid = validateMove(targetGroup, event.getData());
        event.setCancelled(!isValid);
        event.getStatusProxy().setStatus(isValid);
    }

    @Override
    public void onDrop(DndDropEvent event) {
        // Guard against rapid clicks triggering drag+drop events.
        if (!moved) {
            return;
        }

        // Get our destination category.
        EventTarget eventTarget = event.getDragEndEvent().getNativeEvent().getEventTarget();
        AppGroup targetGroup = adminAppView.getAppGroupFromElement(Element.as(eventTarget));

        if (validateMove(targetGroup, event.getData())) {
            if (event.getData() instanceof AppGroup) {
                AppGroup source = (AppGroup)event.getData();
                presenter.moveAppGroup(targetGroup, source);
            } else {
                App source = (App)event.getData();
                presenter.moveApp(targetGroup, source);
            }
        }
    }
}
