package org.iplantc.de.diskResource.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.iplantc.de.diskResource.client.events.TemplateDownloadClickedEvent.TemplateDownloadClickedEventHandler;

/**
 * Created by sriram on 6/27/16.
 */
public class TemplateDownloadClickedEvent extends GwtEvent<TemplateDownloadClickedEventHandler> {



    public static final GwtEvent.Type<TemplateDownloadClickedEventHandler> TYPE = new GwtEvent.Type<>();

    public interface TemplateDownloadClickedEventHandler extends EventHandler {

        void onDownloadClick(TemplateDownloadClickedEvent event);

    }

    private String selectedTemplateId;


    public TemplateDownloadClickedEvent(String id) {
        this.selectedTemplateId = id;
    }


    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<TemplateDownloadClickedEventHandler> getAssociatedType() {
        return TYPE;
    }


    @Override
    protected void dispatch(TemplateDownloadClickedEventHandler handler) {
        handler.onDownloadClick(this);

    }
}
