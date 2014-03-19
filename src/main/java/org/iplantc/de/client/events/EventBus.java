package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a simple manner for components to communicate via events. Components that wish to handle
 * particular events get added as handlers for that event type.
 * 
 * Implements a publish/subscribe pattern.
 * 
 * @author amuir
 */
public class EventBus {
    class HandlerWrapper {
        private GwtEvent.Type<EventHandler> type;
        private HandlerRegistration handler;

        public HandlerWrapper(Type<EventHandler> type, HandlerRegistration handler) {
            this.type = type;
            this.handler = handler;
        }

        public GwtEvent.Type<EventHandler> getType() {
            return type;
        }

        public HandlerRegistration getHandler() {
            return handler;
        }

        public void removeHandler() {
            handler.removeHandler();
        }
    }

    private static EventBus instance;
    private SimpleEventBus eventbus;
    private List<HandlerWrapper> wrappers = new ArrayList<HandlerWrapper>();

    private EventBus() {
        eventbus = new SimpleEventBus();
    }

    /**
     * Retrieve singleton instance.
     * 
     * @return the singleton instance.
     */
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }

        return instance;
    }

    /**
     * Add an event handler by type.
     * 
     * @param <H> the event.
     * @param type the event type.
     * @param handler the handler to be added.
     * @return a new handler registration object.
     */
    @SuppressWarnings("unchecked")
    public <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, final H handler) {
        HandlerRegistration reg = eventbus.addHandler(type, handler);
        wrappers.add(new HandlerWrapper((Type<EventHandler>)type, reg));

        return reg;
    }

    /**
     * Remove all handlers of a specific type.
     * 
     * @param type the type of event handlers to remove.
     */
    public void removeHandlers(Type<? extends EventHandler> type) {
        List<HandlerWrapper> deleted = new ArrayList<HandlerWrapper>();

        // build our delete list
        for (HandlerWrapper wrapper : wrappers) {
            if (wrapper.getType().equals(type)) {
                deleted.add(wrapper);
            }
        }

        // perform our delete
        for (HandlerWrapper wrapper : deleted) {
            wrapper.removeHandler();
            wrappers.remove(wrapper);
        }
    }

    /**
     * Remove a single event handler.
     * 
     * @param in a handler registration object for the handler to be removed.
     */
    public void removeHandler(HandlerRegistration in) {
        if (in != null) {
            for (HandlerWrapper wrapper : wrappers) {
                if (wrapper.getHandler() == in) {
                    wrapper.removeHandler();
                    wrappers.remove(wrapper);
                    break;
                }
            }
        }
    }

    /**
     * Fire an event.
     * 
     * @param event the event to fire.
     */
    public void fireEvent(GwtEvent<?> event) {
        if (event != null) {
            eventbus.fireEvent(event);
        }
    }

    /**
     * Clear all event handlers.
     */
    public void clearHandlers() {
        for (HandlerWrapper wrapper : wrappers) {
            wrapper.removeHandler();
        }

        wrappers.clear();
    }
}
