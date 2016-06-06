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
public class CategorizeHierarchiesToAppEvent extends GwtEvent<CategorizeHierarchiesToAppEvent.CategorizeHierarchiesToAppEventHandler> {

    public static interface CategorizeHierarchiesToAppEventHandler extends EventHandler {
        void onCategorizeHierarchiesToApp(CategorizeHierarchiesToAppEvent event);
    }

    public interface HasCategorizeHierarchiesToAppEventHandlers {
        HandlerRegistration addCategorizeHierarchiesToAppEventHandler(CategorizeHierarchiesToAppEventHandler handler);
    }

    public static Type<CategorizeHierarchiesToAppEventHandler> TYPE =
            new Type<CategorizeHierarchiesToAppEventHandler>();

    private App targetApp;
    private List<OntologyHierarchy> selectedHierarchies;

    public CategorizeHierarchiesToAppEvent(App targetApp, List<OntologyHierarchy> selectedHierarchies) {

        this.targetApp = targetApp;
        this.selectedHierarchies = selectedHierarchies;
    }

    public App getTargetApp() {
        return targetApp;
    }

    public List<OntologyHierarchy> getSelectedHierarchies() {
        return selectedHierarchies;
    }

    public Type<CategorizeHierarchiesToAppEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CategorizeHierarchiesToAppEventHandler handler) {
        handler.onCategorizeHierarchiesToApp(this);
    }


}
