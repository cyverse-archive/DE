package org.iplantc.de.diskResource.client.search.presenter;


import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchClickedEvent;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryClickedEvent;
import org.iplantc.de.diskResource.client.search.events.SavedSearchDeletedEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;

/**
 * An interface definition for the "search" sub-system.
 * 
 * <h2><u>Terms and Concepts</u></h2>
 * <dl>
 * <dt>Query Template</dt>
 * <dd>a template which is used to generate a query to be submitted to the search endpoints.</dd>
 * <dd>acts as a "smart folder" which is accessed from the data navigation window.</dd>
 * <dt>Active Query</dt>
 * <dd>the current query template whose generated search query results are displayed in the view's center
 * panel.</dd>
 * <dd>
 * </dl>
 * 
 * <h2>Presenter Responsibilities</h2>
 * <ul>
 * <li>Managing the <em>active query</em> state. This includes:
 * <ul>
 * <li>Ensuring that the view communicates to the user what the current <em>active query</em> is.</li>
 * <li>Ensuring that the view communicates to the user if there is no <em>active query</em>.</li>
 * </ul>
 * </li>
 * 
 * <li>Maintaining a list of saved query templates.</li>
 * <li>Saving query templates when the user requests.<br/>
 * This includes ensuring that the user has full permissions to the query template.</li>
 * <li>Retrieving saved query templates
 * <ul>
 * <li>Displaying the saved filters as selectable root items in the Navigation panel</li>
 * </ul>
 * </li>
 * <li>Deleting saved queries</li>
 * 
 * </ul>
 * 
 * 
 * @author jstroot
 * 
 */
public interface DataSearchPresenter extends SaveDiskResourceQueryClickedEvent.SaveDiskResourceQueryClickedEventHandler,
                                             SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler,
                                             FolderSelectionEvent.HasFolderSelectionEventHandlers,
                                             DeleteSavedSearchClickedEvent.DeleteSavedSearchEventHandler,
                                             SavedSearchDeletedEvent.HasSavedSearchDeletedEventHandlers,
                                             SavedSearchesRetrievedEvent.SavedSearchesRetrievedEventHandler,
                                             UpdateSavedSearchesEvent.HasUpdateSavedSearchesEventHandlers {
}
