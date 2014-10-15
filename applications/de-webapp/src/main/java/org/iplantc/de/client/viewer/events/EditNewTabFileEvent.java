package org.iplantc.de.client.viewer.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class EditNewTabFileEvent extends GwtEvent<EditNewTabFileEvent.EditNewTabFileEventHandler> {
    
    public interface EditNewTabFileEventHandler extends EventHandler {
        void onNewTabFile(EditNewTabFileEvent event);
    }

    public static final GwtEvent.Type<EditNewTabFileEventHandler> TYPE = new Type<>();
    private int columns;

    public EditNewTabFileEvent(final int columns) {
        this.columns = columns;
    }

    @Override
    public GwtEvent.Type<EditNewTabFileEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EditNewTabFileEventHandler handler) {
        handler.onNewTabFile(this);
    }

    public int getColumns() {
        return columns;
    }

}
