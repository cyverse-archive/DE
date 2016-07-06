package org.iplantc.de.admin.desktop.client.metadata.events;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class DeleteMetadataSelectedEvent
        extends GwtEvent<DeleteMetadataSelectedEvent.DeleteMetadataSelectedEventHandler> {
    public static interface DeleteMetadataSelectedEventHandler extends EventHandler {
        void onDeleteMetadataSelectedHandler(DeleteMetadataSelectedEvent event);
    }

    public interface HasDeleteMetadataSelectedEventHandlers {
        HandlerRegistration addDeleteMetadataSelectedEventHandler(DeleteMetadataSelectedEventHandler handler);
    }

    public static Type<DeleteMetadataSelectedEventHandler> TYPE =
            new Type<DeleteMetadataSelectedEventHandler>();

    private MetadataTemplateInfo templateInfo;

    public DeleteMetadataSelectedEvent(MetadataTemplateInfo templateInfo) {
        this.templateInfo = templateInfo;
    }

    public Type<DeleteMetadataSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteMetadataSelectedEventHandler handler) {
        handler.onDeleteMetadataSelectedHandler(this);
    }

    public MetadataTemplateInfo getTemplateInfo() {
        return templateInfo;
    }
}
