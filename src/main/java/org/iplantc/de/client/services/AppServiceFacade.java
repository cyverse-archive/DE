package org.iplantc.de.client.services;


import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

/**
 * An interface that provides access to remote services related to apps.
 */
public interface AppServiceFacade {

    /**
     * Retrieves list of templates in the given group.
     *
     * @param appGroupId unique identifier for the group to search in for apps.
     * @param callback called when the RPC call is complete.
     */
    void getApps(String appGroupId, AsyncCallback<String> callback);

    /**
     * Retrieves a paged listing of templates in the given group.
     *
     * @param appGroupId unique identifier for the group to search in for apps.
     * @param limit
     * @param sortField
     * @param offset
     * @param sortDir
     * @param callback called when the RPC call is complete.
     */
    void getPagedApps(String appGroupId, int limit, String sortField, int offset,
            SortDir sortDir, AsyncCallback<String> callback);

    /**
     * Retrieves a hierarchy of public App Groups.
     *
     * @param workspaceId
     * @param callback
     */
    void getPublicAppGroups(AsyncCallback<List<AppGroup>> callback);

    /**
     * Retrieves a hierarchy of all <code>AppGroups</code>s via a secured endpoint.
     *
     * @param callback
     */
    void getAppGroups(AsyncCallback<List<AppGroup>> callback);

    /**
     * Searches for all active Apps with a name or description that contains the given search term.
     *
     * @param search
     * @param callback called when the RPC call is complete.
     */
    void searchApp(String search, AsyncCallback<String> callback);
}
