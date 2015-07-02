package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.selection.ResetInfoTypeSelected.ResetInfoTypeSelectedHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class Md5ValueClicked extends GwtEvent<Md5ValueClicked.Md5ValueClickedHandler> {

    public static interface Md5ValueClickedHandler extends EventHandler {
        void onMd5Clicked(Md5ValueClicked event);
    }

    public static interface HasMd5ValueClickedHandlers {
        HandlerRegistration addMd5ValueClickedHandler(Md5ValueClickedHandler handler);
    }

    public static final Type<Md5ValueClickedHandler> TYPE = new Type<>();
    private final DiskResource diskResource;

    public Md5ValueClicked(final DiskResource diskResource) {
        this.diskResource = diskResource;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<Md5ValueClickedHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    @Override
    protected void dispatch(Md5ValueClickedHandler handler) {
        handler.onMd5Clicked(this);

    }

}
