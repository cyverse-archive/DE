package org.iplantc.de.client.viewer.events;

import org.iplantc.de.client.viewer.events.EditNewTabFileEvent.EditNewTabFileEventHandeler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class EditNewTabFileEvent extends GwtEvent<EditNewTabFileEventHandeler> {
    
    public interface EditNewTabFileEventHandeler extends EventHandler {
        void onNewTabFile(EditNewTabFileEvent event);
    }

    public static final com.google.gwt.event.shared.GwtEvent.Type<EditNewTabFileEventHandeler> TYPE = new Type<EditNewTabFileEventHandeler>();
    private int columns;
    private String separator;

    public EditNewTabFileEvent(int columns, String separator) {
        this.setColumns(columns);
        this.setSeparator(separator);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditNewTabFileEventHandeler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EditNewTabFileEventHandeler handler) {
        handler.onNewTabFile(this);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

}
