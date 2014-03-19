/**
 * 
 */
package org.iplantc.de.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 * 
 */
public class DataSearchHistorySelectedEvent extends GwtEvent<DataSearchHistorySelectedEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.DataSearchHistorySelectedEventHandler
     */
    public static final GwtEvent.Type<DataSearchHistorySelectedEventHandler> TYPE = new GwtEvent.Type<DataSearchHistorySelectedEventHandler>();

    private String searchHistoryTerm;

    public DataSearchHistorySelectedEvent(String historyTerm) {
        setSearchHistoryTerm(historyTerm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DataSearchHistorySelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(DataSearchHistorySelectedEventHandler handler) {
        handler.onSelection(this);
    }

    /**
     * @return the searchHistoryTerm
     */
    public String getSearchHistoryTerm() {
        return searchHistoryTerm;
    }

    /**
     * @param searchHistoryTerm the searchHistoryTerm to set
     */
    public void setSearchHistoryTerm(String searchHistoryTerm) {
        this.searchHistoryTerm = searchHistoryTerm;
    }

}
