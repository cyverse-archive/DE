package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.converters.AppCategoryListCallbackConverter;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.exceptions.ConfluenceException;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ConfluenceServiceAsync;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.EmailServiceAsync;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

/**
 * Provides access to remote services for operations related to analysis submission templates.
 * 
 * @author Dennis Roberts
 */
public class AppUserServiceFacadeImpl implements AppUserServiceFacade {

    private final String APPS = "org.iplantc.services.apps";
    private final String CATEGORIES = "org.iplantc.services.apps.categories";
    private final String PIPELINES = "org.iplantc.services.apps.pipelines";
    private final DiscEnvApiService deServiceFacade;
    private final DEProperties deProperties;
    private final ConfluenceServiceAsync confluenceService;
    private final UserInfo userInfo;
    private final EmailServiceAsync emailService;
    @Inject
    IplantErrorStrings errorStrings;
    @Inject
    IplantDisplayStrings displayStrings;

    @Inject
    public AppUserServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                    final DEProperties deProperties,
                                    final ConfluenceServiceAsync confluenceService,
                                    final UserInfo userInfo,
                                    final EmailServiceAsync emailService) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.confluenceService = confluenceService;
        this.userInfo = userInfo;
        this.emailService = emailService;
    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback, boolean loadHpc) {
        String address = CATEGORIES + "?public=true&hpc=" + loadHpc;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppCategoryListCallbackConverter(callback,
                                                                                     errorStrings));
    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {
        String address = CATEGORIES;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, new AppCategoryListCallbackConverter(callback,
                                                                                     errorStrings));
    }

    @Override
    public void getApps(String appCategoryId, AsyncCallback<String> callback) {
        String address = CATEGORIES + "/" + appCategoryId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, callback);
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
    public void publishToWorld(JSONObject application, String appId, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/publish"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, application.toString());

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getAppDetails(String appId, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/details";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void addAppComment(final String appId,
                              final int rating,
                              final String appWikiPageUrl,
                              final String comment,
                              final String authorEmail,
                              final AsyncCallback<String> callback) {
        // add comment to wiki page, then call rating service, then update avg
        // on wiki page
        String username = userInfo.getUsername();
        String appName = parsePageName(appWikiPageUrl);
        confluenceService.addComment(appName, rating, username, comment, new AsyncCallback<String>() {
            @Override
            public void onSuccess(final String commentIdString) {

                try {
                    long commentId = Long.valueOf(commentIdString);
                    // wrap the callback so it returns the comment id on success
                    rateApp(appWikiPageUrl,
                            appId,
                            rating,
                            commentId,
                            authorEmail,
                            new AsyncCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    callback.onSuccess(commentIdString);
                                }

                                @Override
                                public void onFailure(Throwable caught) {
                                    // TODO post user friendly error message.
                                    callback.onFailure(caught);
                                }
                            });
                } catch (NumberFormatException e) {
                    // no comment id, do nothing
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * calls /rate-analysis and if that is successful, calls updateDocumentationPage()
     */
    @Override
    public void rateApp(final String appWikiPageUrl,
                        String appId,
                        int rating,
                        final long commentId,
                        final String authorEmail,
                        final AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/rating";

        JSONObject body = new JSONObject();
        body.put("rating", new JSONNumber(rating)); //$NON-NLS-1$
        body.put("comment_id", new JSONNumber(commentId)); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());
        deServiceFacade.getServiceData(wrapper, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final String appName = parsePageName(appWikiPageUrl);
                if (!Strings.isNullOrEmpty(appName)) {
                    sendRatingEmail(appName, authorEmail);
                    updateDocumentationPage(appName, result, commentId, this);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
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

    private void updateDocumentationPage(final String appName,
                                         String avgJson,
                                         final Long commentId,
                                         final AsyncCallback<?> callback) {
        JSONObject json = JSONParser.parseStrict(avgJson).isObject();
        if (json != null) {
            Number avg = JsonUtil.getNumber(json, "average"); //$NON-NLS-1$
            int avgRounded = (int)Math.round(avg.doubleValue());
            confluenceService.updatePage(appName, avgRounded, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(new ConfluenceException(caught));
                }

                @Override
                public void onSuccess(Void result) {
                    // Do nothing intentionally
                    if (commentId != null && commentId > 0) {
                        try {
                            removeComment(appName, commentId, this);
                        } catch (Exception e) {
                            onFailure(e);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void editAppComment(final String appId,
                               final int rating,
                               final String appWikiPageUrl,
                               final Long commentId,
                               final String comment,
                               final String authorEmail,
                               final AsyncCallback<String> callback) {
        // update comment on wiki page, then call rating service, then update avg on wiki page
        String appName = parsePageName(appWikiPageUrl);
        if (!Strings.isNullOrEmpty(appName)) {
            confluenceService.editComment(appName,
                                          rating,
                                          userInfo.getUsername(),
                                          commentId,
                                          comment,
                                          new AsyncCallback<Void>() {
                                              @Override
                                              public void onSuccess(Void result) {
                                                  callback.onSuccess(commentId.toString());
                                              }

                                              @Override
                                              public void onFailure(Throwable caught) {
                                                  callback.onFailure(caught);
                                              }
                                          });
        }
    }

    @Override
    public void deleteRating(final String appId,
                             final String appWikiPageUrl,
                             final Long commentId,
                             final AsyncCallback<String> callback) {
        // call rating service, then delete comment from wiki page
        String address = APPS + "/" + appId + "/rating";

        // KLUDGE Have to send empty JSON body with POST request
        Splittable body = StringQuoter.createSplittable();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address, body.toString());
        deServiceFacade.getServiceData(wrapper, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String appName = parsePageName(appWikiPageUrl);
                if (!Strings.isNullOrEmpty(appName)) {
                    updateDocumentationPage(appName, result, commentId, this);
                }
                callback.onSuccess(result);

            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    private void removeComment(String toolName, long commentId, final AsyncCallback<?> callback) {
        confluenceService.removeComment(toolName, commentId, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(new ConfluenceException(caught));
            }

            @Override
            public void onSuccess(Void result) {
                // Do nothing intentionally
            }
        });
    }

    private String parsePageName(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return url;
        }
        return URL.decode(DiskResourceUtil.parseNameFromPath(url));
    }

    @Override
    public void
            favoriteApp(String workspaceId, String appId, boolean fav, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/favorite";

        JSONObject body = new JSONObject();
        ServiceCallWrapper wrapper = null;

        if (fav) {
            wrapper = new ServiceCallWrapper(Type.PUT, address, body.toString());
        } else {
            wrapper = new ServiceCallWrapper(DELETE, address, body.toString());
        }
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void copyApp(String appId, AsyncCallback<String> callback) {
        String address = APPS + "/" + appId + "/copy";

        // KLUDGE Have to send empty JSON body with POST request
        Splittable split = StringQuoter.createSplittable();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, split.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void deleteAppFromWorkspace(String user,
                                       String fullUsername,
                                       List<String> appIds,
                                       AsyncCallback<String> callback) {
        String address = APPS + "/" + "shredder"; //$NON-NLS-1$

        JSONObject body = new JSONObject();
        body.put("app_ids", JsonUtil.buildArrayFromStrings(appIds)); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.toString());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {
        String address = APPS + "?search=" + URL.encodeQueryString(search);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, callback);
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
    public void editWorkflow(String workflowId, AsyncCallback<String> callback) {
        String address = PIPELINES + "/" + workflowId + "/ui";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void copyWorkflow(String workflowId, AsyncCallback<String> callback) {
        String address = PIPELINES + "/" + workflowId + "/copy";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, "{}");
        deServiceFacade.getServiceData(wrapper, callback);
    }
}
