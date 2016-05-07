package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;

import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragMoveEvent;

/**
 * @author aramsey
 */
public class OntologyViewDnDHandler implements DndDragStartEvent.DndDragStartHandler,
                                               DndDropEvent.DndDropHandler,
                                               DndDragMoveEvent.DndDragMoveHandler,
                                               DndDragEnterEvent.DndDragEnterHandler {

    OntologiesView.OntologiesViewAppearance appearance;
    AdminAppsGridView.Presenter gridPresenter;
    OntologiesView.Presenter presenter;
    boolean moved;

    public OntologyViewDnDHandler(OntologiesView.OntologiesViewAppearance appearance,
                                  AdminAppsGridView.Presenter gridPresenter,
                                  OntologiesView.Presenter presenter) {
        this.appearance = appearance;
        this.gridPresenter = gridPresenter;
        this.presenter = presenter;
    }

    @Override
    public void onDragEnter(DndDragEnterEvent event) {
        moved = false;
        OntologyHierarchy dropData = getDragSources();
        DragMoveEvent dragEnterEvent = event.getDragEnterEvent();
        EventTarget target = dragEnterEvent.getNativeEvent().getEventTarget();
        App targetApp = getDropTargetApp(Element.as(target));

        if (!validateDropStatus(targetApp, dropData, event.getStatusProxy())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDragMove(DndDragMoveEvent event) {
        moved = true;

        OntologyHierarchy hierarchy = getDragSources();
        EventTarget target = event.getDragMoveEvent().getNativeEvent().getEventTarget();
        App targetApp = getDropTargetApp(Element.as(target));

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
        EventTarget target = event.getDragEndEvent().getNativeEvent().getEventTarget();
        App targetApp = getDropTargetApp(Element.as(target));

        if (validateDropStatus(targetApp, hierarchy, event.getStatusProxy())) {
            presenter.hierarchyDNDtoApp(hierarchy, targetApp);
        }

    }

    private OntologyHierarchy getDragSources() {
        return presenter.getView().getSelectionItems().get(0);
    }

    private App getDropTargetApp(Element eventTarget) {
        Element row = gridPresenter.getView().getGrid().getView().findRow(Element.as(eventTarget));
        int dropIndex = gridPresenter.getView().getGrid().getView().findRowIndex(row);
        return gridPresenter.getView().getGrid().getStore().get(dropIndex);
    }

    private boolean validateDropStatus(final App targetApp,
                                       final OntologyHierarchy hierarchy,
                                       final StatusProxy status) {
        // Verify we have drag data.
        if (hierarchy == null) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(hierarchy.getLabel() + " > " + targetApp.getName());

        // Verify we have a drop target.
        if (targetApp == null) {
            status.setStatus(false);
            return false;
        }

        return true;
    }
}
