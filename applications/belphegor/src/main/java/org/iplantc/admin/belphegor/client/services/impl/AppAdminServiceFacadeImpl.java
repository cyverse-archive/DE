package org.iplantc.admin.belphegor.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.services.AppAdminServiceFacade;
import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.converters.AppCategoryListCallbackConverter;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.List;

public class AppAdminServiceFacadeImpl implements AppAdminServiceFacade {
    @Inject private DiscEnvApiService deService;
    @Inject private IplantErrorStrings errorStrings;
    @Inject private BelphegorAdminProperties properties;

    @Inject
    public AppAdminServiceFacadeImpl() {
    }

    @Override
    public void addCategory(String name, String destCategoryId, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.add-category";

        JSONObject body = new JSONObject();
        body.put("parentCategoryId", new JSONString(destCategoryId)); //$NON-NLS-1$
        body.put("name", new JSONString(name)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.categorize-app";
        String body = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteAppCategory(String categoryId, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.delete-category/" + categoryId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteApplication(String applicationId, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.delete-app/" + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppDetails(String appId, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.app-details/" + appId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {
        String address = "org.iplantc.belphegor.get-app-groups";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, new AppCategoryListCallbackConverter(callback, errorStrings));
    }

    @Override
    public void getApps(String appCategoryId, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.get-apps-in-group/" + appCategoryId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getPagedApps(String appCategoryId, int limit, String sortField, int offset,
                             com.sencha.gxt.data.shared.SortDir sortDir,
                             AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback) {
        getAppCategories(callback);
    }

    @Override
    public void moveApplication(String applicationId, String groupId,
                                AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.move-app";

        JSONObject body = new JSONObject();
        body.put("id", new JSONString(applicationId)); //$NON-NLS-1$
        body.put("categoryId", new JSONString(groupId)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void moveCategory(String categoryId, String parentCategoryId,
                             AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.move-category";

        JSONObject body = new JSONObject();
        body.put("categoryId", new JSONString(categoryId)); //$NON-NLS-1$
        body.put("parentCategoryId", new JSONString(parentCategoryId)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void renameAppCategory(String categoryId, String name, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.rename-category";

        JSONObject body = new JSONObject();
        body.put("categoryId", new JSONString(categoryId)); //$NON-NLS-1$
        body.put("name", new JSONString(name)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void restoreApplication(String applicationId, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.restore-app/" + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.search-apps?search=" + URL.encodeQueryString(search);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void updateApplication(JSONObject application, AsyncCallback<String> callback) {
        String address = "org.iplantc.belphegor.update-app";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            application.toString());
        deService.getServiceData(wrapper, callback);
    }

}
