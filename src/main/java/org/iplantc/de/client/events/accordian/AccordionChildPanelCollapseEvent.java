package org.iplantc.de.client.events.accordian;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event for accordion panel collapse
 * 
 * @author sriram
 * 
 */
public class AccordionChildPanelCollapseEvent extends GwtEvent<AccordionChildPanelCollapseEventHandler> {
    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.accordian.AccordionChildPanelCollapseEventHandler
     */
    public static final GwtEvent.Type<AccordionChildPanelCollapseEventHandler> TYPE = new GwtEvent.Type<AccordionChildPanelCollapseEventHandler>();

    private String contentPanelID;

    /**
     * Instantiate from panel id.
     * 
     * @param contentPanelID id of the panel that generated the collapse event.
     */
    public AccordionChildPanelCollapseEvent(String contentPanelID) {
        this.setContentPanelID(contentPanelID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatch(AccordionChildPanelCollapseEventHandler handler) {
        handler.onCollapse(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AccordionChildPanelCollapseEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * set the panel id
     * 
     * @param contentPanelID id of the content panel
     */
    public void setContentPanelID(String contentPanelID) {
        this.contentPanelID = contentPanelID;
    }

    /**
     * Get the content panel id
     * 
     * @return panel id.
     */
    public String getContentPanelID() {
        return contentPanelID;
    }
}
