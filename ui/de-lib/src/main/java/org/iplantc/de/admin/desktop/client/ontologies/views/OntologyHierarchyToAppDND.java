package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragMoveEvent;

/**
 * @author aramsey
 */
public class OntologyHierarchyToAppDND implements DndDragStartEvent.DndDragStartHandler,
                                                  DndDropEvent.DndDropHandler,
                                                  DndDragMoveEvent.DndDragMoveHandler,
                                                  DndDragEnterEvent.DndDragEnterHandler {

    OntologiesView.OntologiesViewAppearance appearance;
    AdminAppsGridView.Presenter oldGridPresenter;
    AdminAppsGridView.Presenter newGridPresenter;
    OntologiesView.Presenter presenter;
    boolean moved;

    public OntologyHierarchyToAppDND(OntologiesView.OntologiesViewAppearance appearance,
                                     AdminAppsGridView.Presenter oldGridPresenter,
                                     AdminAppsGridView.Presenter newGridPresenter,
                                     OntologiesView.Presenter presenter) {
        this.appearance = appearance;
        this.oldGridPresenter = oldGridPresenter;
        this.newGridPresenter = newGridPresenter;
        this.presenter = presenter;
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        moved = false;
        OntologyHierarchy dropData = getDragSources();
        DragMoveEvent dragEnterEvent = event.getDragEnterEvent();
        Widget widget = dragEnterEvent.getTarget();
        EventTarget target = dragEnterEvent.getNativeEvent().getEventTarget();
        App targetApp = getDropTargetApp(Element.as(target), widget);

        if (!validateDropStatus(targetApp, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        moved = true;

        OntologyHierarchy hierarchy = getDragSources();
        Widget widget = event.getDropTarget().getWidget();
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        App targetApp = getDropTargetApp(Element.as(target), widget);

        if (!validateDropStatus(targetApp, hierarchy, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }


    @Override
    public void onDragStart(DndDragStartEvent event) {
        moved = false;

        OntologyHierarchy hierarchy = getDragSources();

        if (hierarchy == null) {
            // Cancel drag
            event.setCancelled(true);
        } else {
            event.setData(hierarchy);
            event.getStatusProxy().update(hierarchy.getLabel());
            event.getStatusProxy().setStatus(true);
            event.setCancelled(false);
        }
    }

    @Override
    public void onDrop(DndDropEvent event) {
        if (!moved) return;

        OntologyHierarchy hierarchy = getDragSources();
        Widget widget = event.getDropTarget().getWidget();
        EventTarget target = event.getDragEndEvent().getNativeEvent().getEventTarget();
        App targetApp = getDropTargetApp(Element.as(target), widget);

        if (validateDropStatus(targetApp, hierarchy, event.getStatusProxy())) {
            presenter.hierarchyDNDtoApp(hierarchy, targetApp);
        }

    }

    private OntologyHierarchy getDragSources() {
        return presenter.getSelectedHierarchy();
    }

    private App getDropTargetApp(Element eventTarget, Widget dropTarget) {
        if (dropTarget == oldGridPresenter.getView().asWidget()) {
            return oldGridPresenter.getAppFromElement(eventTarget);
        }

        if (dropTarget == newGridPresenter.getView().asWidget()) {
            return newGridPresenter.getAppFromElement(eventTarget);
        }

        return null;
    }

    private boolean validateDropStatus(final App targetApp,
                                       final OntologyHierarchy hierarchy,
                                       final StatusProxy status) {
        // Verify we have drag data.
        if (hierarchy == null) {
            status.setStatus(false);
            status.update("");
            return false;
        }

        // Verify we have a drop target.
        if (targetApp == null) {
            status.setStatus(false);
            status.update("");
            return false;
        }

        // Verify the target is not an external app
        if (targetApp.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
            status.setStatus(false);
            status.update(appearance.externalAppDND(targetApp.getName()));
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(hierarchy.getLabel() + " > " + targetApp.getName());
        return true;
    }
}
