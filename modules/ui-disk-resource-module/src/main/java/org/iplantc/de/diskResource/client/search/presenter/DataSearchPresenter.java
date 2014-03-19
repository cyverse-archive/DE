package org.iplantc.de.diskResource.client.search.presenter;


import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.List;

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
public interface DataSearchPresenter extends SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler, SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler, FolderSelectedEvent.HasFolderSelectedEventHandlers, FolderSelectedEvent.FolderSelectedEventHandler,
        DeleteSavedSearchEvent.DeleteSavedSearchEventHandler {

    /**
     * Initializes this presenter's contract with the given input parameters.
     * 
     * Adds itself as a listener for {@code SubmitDiskResourceQueryEvent} and
     * {@code SaveDiskResourceQueryEvent}s on the given view's toolbar, and
     * {@code SubmitDiskResourceQueryEvent}s on the view itself.
     * 
     * @param hasFolderSelectedHandlers
     * @param hasDeleteSavedSearchEventHandlers
     * @param folderSelectedHandler
     * @param treeStore
     * @param searchField
     */
    void searchInit(FolderSelectedEvent.HasFolderSelectedEventHandlers hasFolderSelectedHandlers, DeleteSavedSearchEvent.HasDeleteSavedSearchEventHandlers hasDeleteSavedSearchEventHandlers, FolderSelectedEvent.FolderSelectedEventHandler folderSelectedHandler,
            TreeStore<Folder> treeStore, DiskResourceSearchField searchField);

    /**
     * @return the current active query, or null if there is not active query.
     */
    DiskResourceQueryTemplate getActiveQuery();

    /**
     * Loads the list of given queries.
     * 
     * Checks that the given query templates have unique name.
     * 
     * This method should only be called from the proxy which loads the root folders into the data window
     * navigation pane.
     * 
     * @param savedQueries
     */
    void loadSavedQueries(List<DiskResourceQueryTemplate> savedQueries);

    void refreshQuery();
}
