package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author aramsey
 */
public class CategorizeButtonClickedEvent extends GwtEvent<CategorizeButtonClickedEvent.CategorizeButtonClickedEventHandler> {

    public static interface CategorizeButtonClickedEventHandler extends EventHandler {
        void onCategorizeButtonClicked(CategorizeButtonClickedEvent event);
    }

    public interface HasCategorizeButtonClickedEventHandlers {
        HandlerRegistration addCategorizeButtonClickedEventHandler(CategorizeButtonClickedEventHandler handler);
    }

    public static Type<CategorizeButtonClickedEventHandler> TYPE =
            new Type<CategorizeButtonClickedEventHandler>();

    private App selectedApp;
    private List<OntologyHierarchy> hierarchyRoots;

    public CategorizeButtonClickedEvent(App selectedApp, List<OntologyHierarchy> hierarchyRoots) {
        this.selectedApp = selectedApp;
        this.hierarchyRoots = hierarchyRoots;
    }

    public App getSelectedApp() {
        return selectedApp;
    }

    public List<OntologyHierarchy> getHierarchyRoots() {
        return hierarchyRoots;
    }

    public Type<CategorizeButtonClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CategorizeButtonClickedEventHandler handler) {
        handler.onCategorizeButtonClicked(this);
    }

}
