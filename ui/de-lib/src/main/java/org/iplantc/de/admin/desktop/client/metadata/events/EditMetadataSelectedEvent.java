package org.iplantc.de.admin.desktop.client.metadata.events;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class EditMetadataSelectedEvent
        extends GwtEvent<EditMetadataSelectedEvent.EditMetadataSelectedEventHandler> {
    public static interface EditMetadataSelectedEventHandler extends EventHandler {
        void onEditMetadataSelected(EditMetadataSelectedEvent event);
    }

    public interface HasEditMetadataSelectedEventHandlers {
        HandlerRegistration addEditMetadataSelectedEventHandler(EditMetadataSelectedEventHandler handler);
    }

    public static Type<EditMetadataSelectedEventHandler> TYPE =
            new Type<EditMetadataSelectedEventHandler>();

    private MetadataTemplateInfo templateInfo;

    public EditMetadataSelectedEvent(MetadataTemplateInfo templateInfo) {
        this.templateInfo = templateInfo;
    }

    public Type<EditMetadataSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EditMetadataSelectedEventHandler handler) {
        handler.onEditMetadataSelected(this);
    }

    public MetadataTemplateInfo getTemplateInfo() {
        return templateInfo;
    }
}
