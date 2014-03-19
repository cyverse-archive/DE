package org.iplantc.de.apps.widgets.client.events;

import org.iplantc.de.apps.widgets.client.events.ArgumentGroupAddedEvent.ArgumentGroupAddedEventHandler;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class ArgumentGroupAddedEvent extends GwtEvent<ArgumentGroupAddedEventHandler> {

    public interface ArgumentGroupAddedEventHandler extends EventHandler {
        void onArgumentGroupAdded(ArgumentGroupAddedEvent event);
    }

    public static interface HasArgumentGroupAddedEventHandlers {
        HandlerRegistration addArgumentGroupAddedEventHandler(ArgumentGroupAddedEventHandler handler);
    }

    public static final GwtEvent.Type<ArgumentGroupAddedEventHandler> TYPE = new GwtEvent.Type<ArgumentGroupAddedEvent.ArgumentGroupAddedEventHandler>();
    private final ArgumentGroupEditor argGrpEditor;
    private final ArgumentGroup argumentGroup;

    public ArgumentGroupAddedEvent(ArgumentGroup argumentGroup, ArgumentGroupEditor argGrpEditor) {
        this.argumentGroup = argumentGroup;
        this.argGrpEditor = argGrpEditor;
    }

    public ArgumentGroup getArgumentGroup() {
        return argumentGroup;
    }

    public ArgumentGroupEditor getArgumentGroupEditor() {
        return argGrpEditor;
    }

    @Override
    public GwtEvent.Type<ArgumentGroupAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ArgumentGroupAddedEventHandler handler) {
        handler.onArgumentGroupAdded(this);
    }
}
