package org.iplantc.de.client.events;

import org.iplantc.de.client.events.FileEditorWindowClosedEvent.FileEditorWindowClosedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event representation a FileEditorWindow being closed by the user.
 * 
 * @author amuir
 * 
 */
public class FileEditorWindowClosedEvent extends GwtEvent<FileEditorWindowClosedEventHandler> {
    /**
     * Defines a handler for FileEditorWindowClosedEvents.
     */
    public interface FileEditorWindowClosedEventHandler extends EventHandler {
        /**
         * Handle when a file editor window has been closed.
         * 
         * @param event event to be handled.
         */
        public void onClosed(FileEditorWindowClosedEvent event);
    }

    /**
     * Defines the GWT Event Type.
     */
    public static final GwtEvent.Type<FileEditorWindowClosedEventHandler> TYPE = new GwtEvent.Type<FileEditorWindowClosedEventHandler>();

    private final String id;

    /**
     * Instantiate from id.
     * 
     * @param id id of edited file.
     */
    public FileEditorWindowClosedEvent(String id) {
        this.id = id;
    }

    @Override
    protected void dispatch(FileEditorWindowClosedEventHandler handler) {
        handler.onClosed(this);
    }

    @Override
    public Type<FileEditorWindowClosedEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Retrieve the id of the closed file.
     * 
     * @return file id of closed window.
     */
    public String getId() {
        return id;
    }
}
