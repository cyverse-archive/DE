package org.iplantc.admin.belphegor.client.services.impl;

import org.iplantc.admin.belphegor.client.I18N;
import org.iplantc.admin.belphegor.client.models.ToolIntegrationAdminProperties;
import org.iplantc.admin.belphegor.client.services.AppAdminServiceFacade;
import org.iplantc.admin.belphegor.client.services.ToolIntegrationAdminServiceFacade;
import org.iplantc.admin.belphegor.client.services.callbacks.AdminServiceCallback;
import org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.converters.AppGroupListCallbackConverter;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.widget.core.client.Component;

import java.util.List;

public class AppAdminServiceFacadeImpl implements AppAdminServiceFacade {
    private final Component maskingCaller;

    public AppAdminServiceFacadeImpl() {
        this(null);
    }

    public AppAdminServiceFacadeImpl(Component maskingCaller) {
        this.maskingCaller = maskingCaller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getAppGroups(AsyncCallback<List<AppGroup>> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getCategoryListServiceUrl();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, new AppGroupListCallbackConverter(callback, I18N.ERROR));
    }

    @Override
    public void getPublicAppGroups(AsyncCallback<List<AppGroup>> callback) {
        getAppGroups(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getApps(String analysisGroupId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAppsInCategoryServiceUrl()
                + "/" + analysisGroupId; //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getSearchAppServiceUrl()
                + "?search=" + URL.encodeQueryString(search); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#addCategory(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void addCategory(String name, String destCategoryId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAddCategoryServiceUrl();

        JSONObject body = new JSONObject();
        body.put("parentCategoryId", new JSONString(destCategoryId)); //$NON-NLS-1$
        body.put("name", new JSONString(name)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, address,
                body.toString());
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#renameAppGroup(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void renameAppGroup(String categoryId, String name, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getRenameCategoryServiceUrl();

        JSONObject body = new JSONObject();
        body.put("categoryId", new JSONString(categoryId)); //$NON-NLS-1$
        body.put("name", new JSONString(name)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#moveCategory(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void moveCategory(String categoryId, String parentCategoryId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getMoveCategoryServiceUrl();

        JSONObject body = new JSONObject();
        body.put("categoryId", new JSONString(categoryId)); //$NON-NLS-1$
        body.put("parentCategoryId", new JSONString(parentCategoryId)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#deleteAppGroup(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteAppGroup(String categoryId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getDeleteCategoryServiceUrl()
                + "/" + categoryId; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#updateApplication(com.google.gwt.json.client.JSONObject, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void updateApplication(JSONObject application, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getUpdateAppServiceUrl();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                application.toString());
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#moveApplication(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void moveApplication(String applicationId, String groupId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getMoveAppServiceUrl();

        JSONObject body = new JSONObject();
        body.put("id", new JSONString(applicationId)); //$NON-NLS-1$
        body.put("categoryId", new JSONString(groupId)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address,
                body.toString());
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#deleteApplication(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteApplication(String applicationId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getDeleteAppServiceUrl() + "/" //$NON-NLS-1$
                + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#restoreApplication(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void restoreApplication(String applicationId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getRestoreAppServiceUrl() + "/" //$NON-NLS-1$
                + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#categorizeApp(org.iplantc.admin.belphegor.client.services.model.AppCategorizeRequest, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getCategorizeAppServiceUrl();
        String body = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, body);
        callService(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.admin.belphegor.client.services.impl.AppAdminServiceFacade#getAppDetails(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getAppDetails(String appId, AsyncCallback<String> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAppDetailsServiceUrl() + "/" //$NON-NLS-1$
                + appId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        callService(wrapper, callback);
    }

    /**
     * Performs the actual service call, masking any calling component.
     *
     * @param callback executed when RPC call completes.
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        if (callback instanceof AdminServiceCallback) {
            ((AdminServiceCallback)callback).setMaskedCaller(maskingCaller);
        }

        if (maskingCaller != null) {
            maskingCaller.mask(I18N.DISPLAY.loadingMask());
        }

        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    @Override
    public void getPagedApps(String appGroupId, int limit, String sortField, int offset,
            com.sencha.gxt.data.shared.SortDir sortDir, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }
}
