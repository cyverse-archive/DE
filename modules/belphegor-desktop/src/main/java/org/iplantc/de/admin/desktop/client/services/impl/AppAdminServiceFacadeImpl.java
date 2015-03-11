package org.iplantc.de.admin.desktop.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.services.model.AppCategorizeRequest;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;
import org.iplantc.de.client.services.converters.AppCategoryListCallbackConverter;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.List;

/**
 * @author jstroot
 */
public class AppAdminServiceFacadeImpl implements AppAdminServiceFacade {


    private final String APPS = "org.iplantc.services.apps";
    private final String APPS_ADMIN = "org.iplantc.services.admin.apps";
    private final String CATEGORIES_ADMIN = "org.iplantc.services.admin.apps.categories";

    private final String CATEGORIES = "org.iplantc.services.apps.categories";

    @Inject private DiscEnvApiService deService;
    @Inject AppServiceAutoBeanFactory factory;

    @Inject
    AppAdminServiceFacadeImpl() { }

    @Override
    public void addCategory(String name, String destCategoryId, AsyncCallback<String> callback) {
        String address = CATEGORIES_ADMIN;

        JSONObject body = new JSONObject();
        body.put("parent_id", new JSONString(destCategoryId));
        body.put("name", new JSONString(name));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void categorizeApp(AppCategorizeRequest request, AsyncCallback<String> callback) {
        String address = APPS_ADMIN;
        String body = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteAppCategory(String categoryId, AsyncCallback<String> callback) {
        String address = CATEGORIES_ADMIN + "/" + categoryId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteApplication(String applicationId, AsyncCallback<String> callback) {
        String address = APPS_ADMIN + "/" + applicationId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppDetails(String appId, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/details";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {
        String address = CATEGORIES_ADMIN + "?public=true&hpc=false";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new AppCategoryListCallbackConverter(callback));
    }

    @Override
    public void getApps(HasId appCategory, AsyncCallback<List<App>> callback) {
        String address = CATEGORIES + "/" + appCategory.getId();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, new AsyncCallbackConverter<String, List<App>>(callback) {
            @Override
            protected List<App> convertFrom(String object) {
                List<App> apps = AutoBeanCodex.decode(factory, AppList.class, object).as().getApps();
                return apps;
            }
        });
    }

    @Override
    public void getPagedApps(String appCategoryId, int limit, String sortField, int offset,
                             com.sencha.gxt.data.shared.SortDir sortDir,
                             AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback, boolean loadHpc) {
        getAppCategories(callback);
    }

    @Override
    public void moveCategory(String categoryId, String parentCategoryId,
                             AsyncCallback<String> callback) {
        String address = CATEGORIES_ADMIN + "/" + categoryId;

        JSONObject body = new JSONObject();
        body.put("parent_id", new JSONString(parentCategoryId));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void renameAppCategory(String categoryId, String name, AsyncCallback<String> callback) {
        String address = CATEGORIES_ADMIN + "/" + categoryId;

        JSONObject body = new JSONObject();
        body.put("name", new JSONString(name));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address,
                                                            body.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void searchApp(String search, AsyncCallback<AppListLoadResult> callback) {
        String address = APPS + "?search=" + URL.encodeQueryString(search);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deService.getServiceData(wrapper, new AsyncCallbackConverter<String, AppListLoadResult>(callback) {
            @Override
            protected AppListLoadResult convertFrom(String object) {
                List<App> apps = AutoBeanCodex.decode(factory, AppList.class, object).as().getApps();
                AutoBean<AppListLoadResult> loadResultAutoBean = factory.loadResult();

                final AppListLoadResult loadResult = loadResultAutoBean.as();
                loadResult.setData(apps);
                return loadResult;
            }
        });
    }

    @Override
    public void updateApplication(String appId, JSONObject application, AsyncCallback<String> callback) {
        String address = APPS_ADMIN + "/" + appId;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address,
                                                            application.toString());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppDoc(String appId, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/documentation";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void saveAppDoc(String appId, String doc, AsyncCallback<String> callback) {
        String address = APPS_ADMIN + "/" + appId + "/documentation";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, doc);
        deService.getServiceData(wrapper, callback);

    }

    @Override
    public void updateAppDoc(String appId, String doc, AsyncCallback<String> callback) {
        String address = APPS_ADMIN + "/" + appId + "/documentation";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address, doc);
        deService.getServiceData(wrapper, callback);

    }

}
