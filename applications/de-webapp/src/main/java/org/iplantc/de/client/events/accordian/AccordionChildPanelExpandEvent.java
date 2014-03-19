package org.iplantc.de.client.events.accordian;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event for accordion panel expand
 * 
 * @author sriram
 * 
 */
public class AccordionChildPanelExpandEvent extends GwtEvent<AccordionChildPanelExpandEventHandler> {
    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.accordian.AccordionChildPanelExpandEventHandler
     */
    public static final GwtEvent.Type<AccordionChildPanelExpandEventHandler> TYPE = new GwtEvent.Type<AccordionChildPanelExpandEventHandler>();

    private String contentPanelID;

    /**
     * Instantiate from a panel id.
     * 
     * @param contentPanelID id of panel that generated the expand event.
     */
    public AccordionChildPanelExpandEvent(String contentPanelID) {
        this.setContentPanelID(contentPanelID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatch(AccordionChildPanelExpandEventHandler handler) {
        handler.onExpand(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AccordionChildPanelExpandEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Set the panel id.
     * 
     * @param contentPanelID id of the content panel that generated the expand event.
     */
    public void setContentPanelID(String contentPanelID) {
        this.contentPanelID = contentPanelID;
    }

    /**
     * Get the content panel id.
     * 
     * @return panel id.
     */
    public String getContentPanelID() {
        return contentPanelID;
    }
}
