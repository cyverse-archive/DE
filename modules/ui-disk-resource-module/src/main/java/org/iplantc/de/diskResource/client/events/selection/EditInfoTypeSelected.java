package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class EditInfoTypeSelected extends GwtEvent<EditInfoTypeSelected.EditInfoTypeSelectedEventHandler> {
    public static interface EditInfoTypeSelectedEventHandler extends EventHandler {
        void onEditInfoTypeSelected(EditInfoTypeSelected event);
    }

    public static interface HasEditInfoTypeSelectedEventHandlers {
        HandlerRegistration addEditInfoTypeSelectedEventHandler(EditInfoTypeSelectedEventHandler handler);
    }

    public static final Type<EditInfoTypeSelectedEventHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;

    public EditInfoTypeSelected(final List<DiskResource> selectedDiskResources) {
        this.selectedDiskResources = selectedDiskResources;
    }

    public Type<EditInfoTypeSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelectedDiskResources() {
        return selectedDiskResources;
    }

    protected void dispatch(EditInfoTypeSelectedEventHandler handler) {
        handler.onEditInfoTypeSelected(this);
    }
}
