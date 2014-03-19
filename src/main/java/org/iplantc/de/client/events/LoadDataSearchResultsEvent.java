/**
 * 
 */
package org.iplantc.de.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class LoadDataSearchResultsEvent extends GwtEvent<LoadDataSearchResultsEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.LoadDataSearchResultsEventHandler
     */
    public static final GwtEvent.Type<LoadDataSearchResultsEventHandler> TYPE = new GwtEvent.Type<LoadDataSearchResultsEventHandler>();

    private List<DiskResource> results;
    private String searchTerm;

    public LoadDataSearchResultsEvent(final String searchTerm, final List<DiskResource> results) {
        this.setResults(results);
        this.setSearchTerm(searchTerm);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LoadDataSearchResultsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoadDataSearchResultsEventHandler handler) {
        handler.onLoad(this);

    }

    /**
     * @return the results
     */
    public List<DiskResource> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<DiskResource> results) {
        this.results = results;
    }

    /**
     * @return the searchTerm
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * @param searchTerm the searchTerm to set
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
