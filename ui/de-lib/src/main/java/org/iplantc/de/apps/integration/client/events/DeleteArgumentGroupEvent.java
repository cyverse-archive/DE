package org.iplantc.de.apps.integration.client.events;

import org.iplantc.de.apps.integration.client.events.DeleteArgumentGroupEvent.DeleteArgumentGroupEventHandler;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class DeleteArgumentGroupEvent extends GwtEvent<DeleteArgumentGroupEventHandler> {

    public interface DeleteArgumentGroupEventHandler extends EventHandler {
        void doArgumentGroupDelete(DeleteArgumentGroupEvent event);
    }

    public static interface HasDeleteArgumentGroupEventHandlers {
        HandlerRegistration addDeleteArgumentGroupEventHandler(DeleteArgumentGroupEventHandler handler);
    }

    public static final GwtEvent.Type<DeleteArgumentGroupEventHandler> TYPE = new GwtEvent.Type<DeleteArgumentGroupEvent.DeleteArgumentGroupEventHandler>();
    private final ArgumentGroup argGrp;

    public DeleteArgumentGroupEvent(ArgumentGroup argGrp) {
        this.argGrp = argGrp;
    }

    public ArgumentGroup getArgumentGroupToBeDeleted() {
        return argGrp;
    }

    @Override
    public GwtEvent.Type<DeleteArgumentGroupEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DeleteArgumentGroupEventHandler handler) {
        handler.doArgumentGroupDelete(this);
    }
}
