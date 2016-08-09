package org.iplantc.de.client.services;


import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

/**
 * An interface that provides access to remote services related to apps.
 */
public interface AppServiceFacade {

    interface AppServiceAutoBeanFactory extends AutoBeanFactory {
        AutoBean<AppList> appList();

        AutoBean<AppListLoadResult> loadResult();

        AutoBean<HasId> hasId();
    }

    /**
     * Retrieves list of templates in the given group.
     *  @param appCategory unique identifier for the group to search in for apps.
     * @param callback called when the RPC call is complete.*/
    void getApps(HasId appCategory, AsyncCallback<List<App>> callback);

    void getApps(String id, AsyncCallback<List<App>> callback);

    /**
     * Retrieves a paged listing of templates in the given group.
     *
     * @param appCategoryId unique identifier for the group to search in for apps.
     * @param limit
     * @param sortField
     * @param offset
     * @param sortDir
     * @param callback called when the RPC call is complete.
     */
    void getPagedApps(String appCategoryId, int limit, String sortField, int offset,
            SortDir sortDir, AsyncCallback<String> callback);

    /**
     * Retrieves a hierarchy of public App Groups.
     *
     * @param callback
     * @param loadHpc TODO
     */
    void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback, boolean loadHpc);

    /**
     * Retrieves a hierarchy of all <code>AppCategory</code>s via a secured endpoint.
     *
     * @param callback
     */
    void getAppCategories(boolean privateOnly, AsyncCallback<List<AppCategory>> callback);

    /**
     * Searches for all active Apps with a name or description that contains the given search term.
     *
     * @param search the search query
     * @param callback called when the RPC call is complete.
     */
    void searchApp(String search, AsyncCallback<AppListLoadResult> callback);

}
