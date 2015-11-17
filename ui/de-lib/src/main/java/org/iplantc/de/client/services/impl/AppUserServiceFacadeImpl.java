package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.converters.AppCategoryListCallbackConverter;
import org.iplantc.de.client.services.converters.AppTemplateCallbackConverter;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.EmailServiceAsync;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

/**
 * Provides access to remote services for operations related to analysis submission templates.
 * 
 * @author Dennis Roberts, jstroot
 */
public class AppUserServiceFacadeImpl implements AppUserServiceFacade {

    private class AppDocCallbackConverter extends AsyncCallbackConverter<String, AppDoc> {
        public AppDocCallbackConverter(AsyncCallback<AppDoc> callback) {
            super(callback);
        }

        @Override
        protected AppDoc convertFrom(String object) {
            AutoBean<AppDoc> appDocAutoBean = AutoBeanCodex.decode(factory, AppDoc.class, object);
            return appDocAutoBean.as();
        }
    }

    interface AppUserServiceBeanFactory extends AutoBeanFactory {
        AutoBean<App> app();
        AutoBean<AppDoc> appDoc();
    }

    private final String APPS = "org.iplantc.services.apps";
    private final String CATEGORIES = "org.iplantc.services.apps.categories";
    private final String PIPELINES = "org.iplantc.services.apps.pipelines";
    private final DiscEnvApiService deServiceFacade;
    private final EmailServiceAsync emailService;
    @Inject IplantDisplayStrings displayStrings;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject JsonUtil jsonUtil;
    @Inject AppUserServiceBeanFactory factory;
    @Inject AppServiceAutoBeanFactory svcFactory;
    @Inject AppTemplateAutoBeanFactory templateAutoBeanFactory;

    @Inject
    public AppUserServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                    final EmailServiceAsync emailService) {
        this.deServiceFacade = deServiceFacade;
        this.emailService = emailService;
    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback, boolean loadHpc) {
        String address = CATEGORIES + "?public=true&hpc=" + loadHpc;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppCategoryListCallbackConverter(callback));
    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {
        String address = CATEGORIES;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppCategoryListCallbackConverter(callback));
    }

    @Override
    public void getApps(HasId appCategory, AsyncCallback<List<App>> callback) {
        String address = CATEGORIES + "/" + appCategory.getId();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, List<App>>(callback) {
            @Override
            protected List<App> convertFrom(String object) {
                List<App> apps = AutoBeanCodex.decode(svcFactory, AppList.class, object).as().getApps();
                return apps;
            }
        });
    }

    @Override
    public void getPagedApps(String appCategoryId,
                             int limit,
                             String sortField,
                             int offset,
                             SortDir sortDir,
                             AsyncCallback<String> asyncCallback) {
        String address = CATEGORIES + "/" + appCategoryId + "?limit=" + limit + "&sort-field="
                + sortField + "&sort-dir=" + sortDir.toString() + "&offset=" + offset;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, asyncCallback);
    }

    @Override
    public void getDataObjectsForApp(String appId, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/tasks";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void publishToWorld(JSONObject application, String appId, AsyncCallback<Void> callback) {
        String address = APPS + "/" + appId + "/publish"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, application.toString());

        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getAppDetails(final App app, AsyncCallback<App> callback) {
        String address = APPS + "/" + app.getId() + "/details";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, App>(callback) {
            @Override
            protected App convertFrom(String object) {
                Splittable split = StringQuoter.split(object);
                AutoBean<App> appAutoBean = AutoBeanUtils.getAutoBean(app);
                AutoBeanCodex.decodeInto(split, appAutoBean);
                return appAutoBean.as();
            }
        });
    }



    /**
     * calls /rate-analysis and if that is successful, calls updateDocumentationPage()
     */
    @Override
    public void rateApp(final App app,
                        final int rating,
                        final AsyncCallback<AppFeedback> callback) {
        String address = APPS + "/" + app.getId() + "/rating";

        Splittable payload = StringQuoter.createSplittable();
        StringQuoter.create(rating).assign(payload, "rating");
        StringQuoter.create(app.getRating().getCommentId()).assign(payload, "comment_id");

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload.getPayload());
        deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, AppFeedback>(callback) {
            @Override
            protected AppFeedback convertFrom(String object) {
                // Send email
                final String appName = parsePageName(app.getWikiUrl());
                if (!Strings.isNullOrEmpty(appName)) {
                    sendRatingEmail(appName, app.getIntegratorEmail());
                }

                final AppFeedback appFeedback = app.getRating();
                appFeedback.setUserRating(0);
                appFeedback.setCommentId(0);
                appFeedback.setUserRating(rating);
                if(Strings.isNullOrEmpty(object)){
                    appFeedback.setAverageRating(0);
                } else {
                    final Splittable split = StringQuoter.split(object);
                    appFeedback.setAverageRating(split.get("average").asNumber());
                    appFeedback.setTotal((int)split.get("total").asNumber());
                }
                return appFeedback;
            }
        });
    }

    private void sendRatingEmail(final String appName, final String emailAddress) {
        emailService.sendEmail(displayStrings.ratingEmailSubject(appName),
                               displayStrings.ratingEmailText(appName),
                               "noreply@iplantcollaborative.org", emailAddress, //$NON-NLS-1$
                               new AsyncCallback<Void>() {
                                   @Override
                                   public void onSuccess(Void arg0) {
                                   }

                                   @Override
                                   public void onFailure(Throwable arg0) {
                                       // don't bother the user if email sending fails
                                   }
                               });
    }


    @Override
    public void deleteRating(final App app, final AsyncCallback<AppFeedback> callback) {
        // call rating service, then delete comment from wiki page
        String address = APPS + "/" + app.getId() + "/rating";

        // KLUDGE Have to send empty JSON body with POST request
        Splittable body = StringQuoter.createSplittable();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address, body.toString());
        deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, AppFeedback>(callback) {
            @Override
            protected AppFeedback convertFrom(String object) {
                final AppFeedback appFeedback = app.getRating();
                appFeedback.setUserRating(0);
                appFeedback.setCommentId(0);
                if(Strings.isNullOrEmpty(object)){
                    appFeedback.setAverageRating(0);
                } else {
                    final Splittable split = StringQuoter.split(object);
                    appFeedback.setAverageRating(split.get("average").asNumber());
                    appFeedback.setTotal((int)split.get("total").asNumber());
                }
                return appFeedback;
            }
        });
    }


    private String parsePageName(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return url;
        }
        return URL.decode(diskResourceUtil.parseNameFromPath(url));
    }

    @Override
    public void favoriteApp(final HasId appId,
                            final boolean fav,
                            final AsyncCallback<Void> callback) {
        String address = APPS + "/" + appId.getId() + "/favorite";

        JSONObject body = new JSONObject();
        ServiceCallWrapper wrapper;

        if (fav) {
            wrapper = new ServiceCallWrapper(Type.PUT, address, body.toString());
        } else {
            wrapper = new ServiceCallWrapper(DELETE, address, body.toString());
        }
        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void copyApp(final HasId app,
                        final AsyncCallback<AppTemplate> callback) {
        String address = APPS + "/" + app.getId() + "/copy";

        // KLUDGE Have to send empty JSON body with POST request
        Splittable split = StringQuoter.createSplittable();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, split.getPayload());
        deServiceFacade.getServiceData(wrapper, new AppTemplateCallbackConverter(templateAutoBeanFactory, callback));
    }

    @Override
    public void deleteAppsFromWorkspace(final List<App> apps,
                                        final AsyncCallback<Void> callback) {
        String address = APPS + "/" + "shredder"; //$NON-NLS-1$
        List<String> appIds = Lists.newArrayList();
        for (App app : apps) {
            appIds.add(app.getId());
        }
        JSONObject body = new JSONObject();
        body.put("app_ids", jsonUtil.buildArrayFromStrings(appIds)); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());
        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void searchApp(String search, AsyncCallback<AppListLoadResult> callback) {
        String address = APPS + "?search=" + URL.encodeQueryString(search);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper,  new AsyncCallbackConverter<String, AppListLoadResult>(callback) {
            @Override
            protected AppListLoadResult convertFrom(String object) {
                List<App> apps = AutoBeanCodex.decode(svcFactory, AppList.class, object).as().getApps();
                AutoBean<AppListLoadResult> loadResultAutoBean = svcFactory.loadResult();

                final AppListLoadResult loadResult = loadResultAutoBean.as();
                loadResult.setData(apps);
                return loadResult;
            }
        });
    }

    @Override
    public void publishWorkflow(String workflowId, String body, AsyncCallback<String> callback) {
        String address = PIPELINES + "/" + workflowId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PUT, address, body);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void createWorkflows(String body, AsyncCallback<String> callback) {
        String address = PIPELINES;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, body);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void editWorkflow(HasId workflowId, AsyncCallback<String> callback) {
        String address = PIPELINES + "/" + workflowId.getId() + "/ui";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void copyWorkflow(String workflowId, AsyncCallback<String> callback) {
        String address = PIPELINES + "/" + workflowId + "/copy";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, "{}");
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppDoc(HasId app, AsyncCallback<AppDoc> callback) {
        String address = APPS + "/" + app.getId() + "/documentation";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new AppDocCallbackConverter(callback));
    }

    @Override
    public void saveAppDoc(final HasId app,
                           final String doc,
                           final AsyncCallback<AppDoc> callback) {
        String address = APPS + "/" + app.getId() + "/documentation";
        Splittable payload = StringQuoter.createSplittable();
        StringQuoter.create(doc).assign(payload, "documentation");
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address, payload.getPayload());
        deServiceFacade.getServiceData(wrapper, new AppDocCallbackConverter(callback));

    }

    @Override
    public void getPermissions(List<App> apps, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void shareApp(List<App> apps,
                         List<String> users,
                         String permission,
                         AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unshareApp(List<App> apps, List<String> users, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }
}
