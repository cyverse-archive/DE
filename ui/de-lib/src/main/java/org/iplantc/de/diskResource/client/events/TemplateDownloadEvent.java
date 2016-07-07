package org.iplantc.de.diskResource.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import org.iplantc.de.diskResource.client.events.TemplateDownloadEvent.TemplateDownloadEventHandler;

/**
 * Created by sriram on 6/27/16.
 */
public class TemplateDownloadEvent extends GwtEvent<TemplateDownloadEventHandler> {

    public static interface HasTemplateDownloadEventtHandlers {
        HandlerRegistration
        addTemplateDownloadHandler(TemplateDownloadEventHandler handler);
    }

    public static final GwtEvent.Type<TemplateDownloadEventHandler> TYPE = new GwtEvent.Type<>();

    public String getSelectedTemplateId() {
        return selectedTemplateId;
    }

    public interface TemplateDownloadEventHandler extends EventHandler {

        void onDownloadClick(TemplateDownloadEvent event);

    }

    private String selectedTemplateId;


    public TemplateDownloadEvent(String id) {
        this.selectedTemplateId = id;
    }


    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<TemplateDownloadEventHandler> getAssociatedType() {
        return TYPE;
    }


    @Override
    protected void dispatch(TemplateDownloadEventHandler handler) {
        handler.onDownloadClick(this);

    }
}
