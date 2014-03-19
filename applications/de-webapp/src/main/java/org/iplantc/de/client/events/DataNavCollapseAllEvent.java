/**
 * 
 */
package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 * 
 */
public class DataNavCollapseAllEvent extends GwtEvent<DataNavCollapseAllEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.DataNavCollapseAllEventHandler
     */
    public static final GwtEvent.Type<DataNavCollapseAllEventHandler> TYPE = new GwtEvent.Type<DataNavCollapseAllEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DataNavCollapseAllEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DataNavCollapseAllEventHandler handler) {
        handler.onCollapse(this);
    }

}
