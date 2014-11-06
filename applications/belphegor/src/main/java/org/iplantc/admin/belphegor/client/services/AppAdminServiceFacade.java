package org.iplantc.admin.belphegor.client.services;

import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AppAdminServiceFacade extends AppServiceFacade {

    /**
     * Adds a new Category with the given category name.
     *
     * @param name
     * @param destCategoryId
     * @param callback
     */
    public abstract void addCategory(String name, String destCategoryId, AsyncCallback<String> callback);

    /**
     * Renames a Category with the given category ID to the given name.
     *
     * @param categoryId
     * @param name
     * @param callback
     */
    public abstract void renameAppCategory(String categoryId, String name,
                                           AsyncCallback<String> callback);

    /**
     * Moves a Category with the given category ID to a parent Category with the given parentCategoryId.
     *
     * @param categoryId
     * @param parentCategoryId
     * @param callback
     */
    public abstract void moveCategory(String categoryId, String parentCategoryId, AsyncCallback<String> callback);

    /**
     * Deletes the Category with the given category ID.
     *
     * @param categoryId
     * @param callback
     */
    public abstract void deleteAppCategory(String categoryId, AsyncCallback<String> callback);

    /**
     * Updates an app with the given values in application.
     * 
     * @param application id
     * @param application
     * @param callback
     */
    public abstract void updateApplication(String appId,
                                           JSONObject application,
                                           AsyncCallback<String> callback);

    /**
     * Moves an App with the given applicationId to the category with the given groupId.
     *
     * @param applicationId
     * @param groupId
     * @param callback
     */
    public abstract void moveApplication(String applicationId, String groupId, AsyncCallback<String> callback);

    /**
     * Deletes an App with the given applicationId.
     *
     * @param applicationId
     * @param callback
     */
    public abstract void deleteApplication(String applicationId, AsyncCallback<String> callback);

    /**
     * Deletes an App with the given applicationId.
     *
     * @param applicationId
     * @param callback
     */
    public abstract void restoreApplication(String applicationId, AsyncCallback<String> callback);

    public abstract void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback);

    public abstract void getAppDetails(String appId, AsyncCallback<String> callback);

}