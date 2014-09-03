package org.iplantc.admin.belphegor.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.services.AppAdminServiceFacade;
import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.converters.AppGroupListCallbackConverter;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.services.DEServiceAsync;
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
    @Inject private DEServiceAsync deService;
    @Inject private IplantErrorStrings errorStrings;
    @Inject private BelphegorAdminProperties properties;

    @Inject
    public AppAdminServiceFacadeImpl() {
    }

    @Override
    public void addCategory(String name, String destCategoryId, AsyncCallback<String> callback) {
        String address = properties.getAddCategoryServiceUrl();

        JSONObject body = new JSONObject();
        body.put("parentCategoryId", new JSONString(destCategoryId)); //$NON-NLS-1$
        body.put("name", new JSONString(name)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback) {
        String address = properties.getCategorizeAppServiceUrl();
        String body = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteAppGroup(String categoryId, AsyncCallback<String> callback) {
        String address = properties.getDeleteCategoryServiceUrl()
                             + "/" + categoryId; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteApplication(String applicationId, AsyncCallback<String> callback) {
        String address = properties.getDeleteAppServiceUrl() + "/" //$NON-NLS-1$
                             + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppDetails(String appId, AsyncCallback<String> callback) {
        String address = properties.getAppDetailsServiceUrl() + "/" //$NON-NLS-1$
                             + appId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppGroups(AsyncCallback<List<AppGroup>> callback) {
        String address = properties.getCategoryListServiceUrl();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, new AppGroupListCallbackConverter(callback, errorStrings));
    }

    @Override
    public void getApps(String analysisGroupId, AsyncCallback<String> callback) {
        String address = properties.getAppsInCategoryServiceUrl()
                             + "/" + analysisGroupId; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getPagedApps(String appGroupId, int limit, String sortField, int offset,
                             com.sencha.gxt.data.shared.SortDir sortDir,
                             AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPublicAppGroups(AsyncCallback<List<AppGroup>> callback) {
        getAppGroups(callback);
    }

    @Override
    public void moveApplication(String applicationId, String groupId,
                                AsyncCallback<String> callback) {
        String address = properties.getMoveAppServiceUrl();

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
        String address = properties.getMoveCategoryServiceUrl();

        JSONObject body = new JSONObject();
        body.put("categoryId", new JSONString(categoryId)); //$NON-NLS-1$
        body.put("parentCategoryId", new JSONString(parentCategoryId)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void renameAppGroup(String categoryId, String name, AsyncCallback<String> callback) {
        String address = properties.getRenameCategoryServiceUrl();

        JSONObject body = new JSONObject();
        body.put("categoryId", new JSONString(categoryId)); //$NON-NLS-1$
        body.put("name", new JSONString(name)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void restoreApplication(String applicationId, AsyncCallback<String> callback) {
        String address = properties.getRestoreAppServiceUrl() + "/" //$NON-NLS-1$
                             + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {
        String address = properties.getSearchAppServiceUrl()
                             + "?search=" + URL.encodeQueryString(search); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void updateApplication(JSONObject application, AsyncCallback<String> callback) {
        String address = properties.getUpdateAppServiceUrl();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address,
                                                            application.toString());
        deService.getServiceData(wrapper, callback);
    }

}
