package org.iplantc.de.apps.widgets.client.events;

import org.iplantc.de.apps.widgets.client.events.ArgumentGroupSelectedEvent.ArgumentGroupSelectedEventHandler;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class ArgumentGroupSelectedEvent extends GwtEvent<ArgumentGroupSelectedEventHandler> {

    public interface ArgumentGroupSelectedEventHandler extends EventHandler {
        void onArgumentGroupSelected(ArgumentGroupSelectedEvent event);
    }

    public static interface HasArgumentGroupSelectedHandlers {
        HandlerRegistration addArgumentGroupSelectedHandler(ArgumentGroupSelectedEventHandler handler);
    }

    public static final GwtEvent.Type<ArgumentGroupSelectedEventHandler> TYPE = new GwtEvent.Type<ArgumentGroupSelectedEventHandler>();
    private String absoluteEditorPath;
    private final ArgumentGroup argGroup;

    public ArgumentGroupSelectedEvent(ArgumentGroup argGroup) {
        this.argGroup = argGroup;
    }

    public ArgumentGroupSelectedEvent(ArgumentGroup argGroup, String absoluteEditorPath) {
        this(argGroup);
        this.absoluteEditorPath = absoluteEditorPath;
    }

    public String getAbsoluteEditorPath() {
        return absoluteEditorPath;
    }

    public ArgumentGroup getArgumentGroup() {
        return argGroup;
    }

    @Override
    public GwtEvent.Type<ArgumentGroupSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ArgumentGroupSelectedEventHandler handler) {
        handler.onArgumentGroupSelected(this);
    }

}
