package org.iplantc.de.admin.apps.client.presenter.grid;

import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;

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
 * A Drag and Drop handler for the AdminAppViewImpl to support moving Apps and AppCategories.
 * 
 * @author psarando
 * FIXME Split this into two separate DND handlers. Refer to disk resource module for guidance
 * 
 */
public class AppGridDnDHandler implements DndDragStartHandler,
                                              DndDragEnterHandler,
                                              DndDragMoveHandler,
                                              DndDropHandler {

    private final AdminAppsView adminAppView;
    private final AdminAppsView.AdminPresenter presenter;

    /**
     * Guard against rapid clicks triggering drag+drop events.
     */
    private boolean moved;

    public AppGridDnDHandler(AdminAppsView adminAppView, AdminAppsView.AdminPresenter presenter) {
        this.adminAppView = adminAppView;
        this.presenter = presenter;
    }

    private boolean validateMove(AppCategory targetGroup, Object source) {
        if (source instanceof AppCategory) {
            // FIXME
            return false;
//            return presenter.canMoveAppCategory(targetGroup, (AppCategory) source);
        } else {
            // FIXME
            return false;
//            return presenter.canMoveApp(targetGroup, (App)source);
        }
    }

    @Override
    public void onDragStart(DndDragStartEvent event) {
        moved = false;

        Element dragStartEl = event.getDragStartEvent().getStartElement();

//        Object dragData = adminAppView.getAppCategoryFromElement(dragStartEl);
        Object dragData = null;

        if (dragData == null) {
            // If we don't have an AppCategory, check for an App.
//            dragData = adminAppView.getAppFromElement(dragStartEl);
        }

        if (dragData != null) {
            String dragDataLabel;
            if (dragData instanceof AppCategory) {
                dragDataLabel = ((AppCategory)dragData).getName();
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
//        AppCategory targetGroup = adminAppView.getAppCategoryFromElement(Element.as(eventTarget));
        AppCategory targetGroup = null;

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
//        AppCategory targetGroup = adminAppView.getAppCategoryFromElement(Element.as(eventTarget));
        AppCategory targetGroup = null;

        if (validateMove(targetGroup, event.getData())) {
            if (event.getData() instanceof AppCategory) {
                AppCategory source = (AppCategory)event.getData();
//                presenter.moveAppCategory(targetGroup, source);
            } else {
                App source = (App)event.getData();
//                presenter.moveApp(targetGroup, source);
            }
        }
    }
}
