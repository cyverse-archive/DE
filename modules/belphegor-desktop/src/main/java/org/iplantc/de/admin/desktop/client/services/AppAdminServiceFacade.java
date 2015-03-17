package org.iplantc.de.admin.desktop.client.services;

import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppAdminServiceFacade {

    interface AdminServiceAutoBeanFactory extends AutoBeanFactory {
        AutoBean<AppCategory> appCategory();
        AutoBean<App> app();
        AutoBean<AppDoc> appDoc();
    }

    /**
     * Adds a new Category with the given category name.
     */
    void addCategory(String newCategoryName, HasId parentCategory, AsyncCallback<AppCategory> callback);

    void getPublicAppCategories(AsyncCallback<List<AppCategory>> asyncCallback, boolean loadHpc);

    /**
     * Renames a Category with the given category ID to the given name.
     */
    void renameAppCategory(HasId categoryId, String newCategoryName,
                                           AsyncCallback<AppCategory> callback);

    /**
     * Moves a Category with the given category ID to a parent Category with the given parentCategoryId.
     */
    void moveCategory(String categoryId, String parentCategoryId, AsyncCallback<String> callback);

    /**
     * Deletes the Category with the given category ID.
     */
    void deleteAppCategory(HasId category, AsyncCallback<Void> callback);

    /**
     * Updates an app with the given values in application.
     *
     */
    void restoreApp(HasId app,
                    AsyncCallback<App> callback);

    void updateApp(App app, AsyncCallback<App> callback);

    /**
     * Deletes an App with the given applicationId.
     */
    void deleteApp(HasId app, AsyncCallback<Void> callback);

    void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback);

    void getAppDetails(HasId app, AsyncCallback<App> callback);

    void getAppDoc(HasId app, AsyncCallback<AppDoc> callback);

    void saveAppDoc(HasId app, AppDoc doc, AsyncCallback<AppDoc> callback);

    void updateAppDoc(HasId app, AppDoc doc, AsyncCallback<AppDoc> callback);
}