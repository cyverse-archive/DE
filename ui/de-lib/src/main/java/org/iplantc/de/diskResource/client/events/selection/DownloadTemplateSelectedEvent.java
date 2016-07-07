package org.iplantc.de.diskResource.client.events.selection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by sriram on 7/1/16.
 */
public class DownloadTemplateSelectedEvent
        extends com.google.gwt.event.shared.GwtEvent<DownloadTemplateSelectedEvent.DownloadTemplateSelectedEventHandler> {


    public static interface HasDownloadTemplateSelectedEventHandlers {
        HandlerRegistration
        addDownloadTemplateSelectedEventHandler(DownloadTemplateSelectedEventHandler handler);
    }

    public static final Type<DownloadTemplateSelectedEventHandler> TYPE = new Type<>();

    public interface DownloadTemplateSelectedEventHandler extends EventHandler {
        void onDownloadTemplateSelected(DownloadTemplateSelectedEvent event);
    }

    @Override
    public Type<DownloadTemplateSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DownloadTemplateSelectedEventHandler handler) {
        handler.onDownloadTemplateSelected(this);
    }
}
