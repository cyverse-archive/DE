/**
 *
 */
package org.iplantc.de.collaborators.client.util;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.collaborators.CollaboratorAutoBeanFactory;
import org.iplantc.de.client.models.collaborators.CollaboratorsList;
import org.iplantc.de.client.services.CollaboratorsServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.collaborators.client.events.CollaboratorsLoadedEvent;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.shared.FastMap;

import java.util.List;

/**
 * FIXME Utility classes shouldn't be calling service facades. Factor this class out.
 * @author sriram
 *
 */
public class CollaboratorsUtil {

    private List<Collaborator> currentCollaborators;
    private List<Collaborator> searchResults;
    private final CollaboratorAutoBeanFactory factory = GWT.create(CollaboratorAutoBeanFactory.class);
    private final CollaboratorsServiceFacade facade = ServicesInjector.INSTANCE.getCollaboratorsServiceFacade();
    private final JsonUtil jsonUtil = JsonUtil.getInstance();

    private static CollaboratorsUtil INSTANCE;

    CollaboratorsUtil() {

        currentCollaborators = null;
        searchResults = null;
    }

    public static CollaboratorsUtil getInstance(){
        if(INSTANCE == null) {
            INSTANCE = new CollaboratorsUtil();
        }

        return INSTANCE;
    }

    private List<Collaborator> parseResults(String result) {
        AutoBean<CollaboratorsList> bean = AutoBeanCodex
                .decode(factory, CollaboratorsList.class, result);
        JSONObject obj = jsonUtil.getObject(result);
        return bean.as().getCollaborators();
    }

    /**
     * @return the currentCollaborators
     */
    public List<Collaborator> getCurrentCollaborators() {
        return currentCollaborators;
    }

    /**
     * @param currentCollaborators the currentCollaborators to set
     */
    public void setCurrentCollaborators(List<Collaborator> currentCollaborators) {
        this.currentCollaborators = currentCollaborators;
    }

    public void getCollaborators(final AsyncCallback<Void> superCallback) {
        if (getCurrentCollaborators() == null) {
            facade.getCollaborators(new GetCollaboratorsCallback(superCallback));
        } else {
            superCallback.onSuccess(null);
        }
    }

    public void search(final String term, final AsyncCallback<Void> superCallback) {
        facade.searchCollaborators(term, new SearchCallback(superCallback));
    }

    public boolean checkCurrentUser(Collaborator model) {
        return model.getUserName().equalsIgnoreCase(UserInfo.getInstance().getUsername());

    }

    /**
     * @return the searchResults
     */
    public List<Collaborator> getSearchResults() {
        return searchResults;
    }

    public Collaborator findCollaboratorByUserName(String userName) {
        List<Collaborator> collabs = getCurrentCollaborators();
        for (Collaborator c : collabs) {
            if (c.getUserName().equals(userName)) {
                return c;
            }
        }
        return getDummyCollaborator(userName);
    }

    public Collaborator getDummyCollaborator(String userName) {
        JSONObject obj = new JSONObject();
        obj.put("username", new JSONString(userName));
        AutoBean<Collaborator> bean = AutoBeanCodex.decode(factory, Collaborator.class,
                obj.toString());
        return bean.as();
    }

    public boolean isCollaborator(Collaborator c) {
        return getCurrentCollaborators().contains(c);
    }

    public void getUserInfo(List<String> usernames,
            final AsyncCallback<FastMap<Collaborator>> superCallback) {
        facade.getUserInfo(usernames, new GetUserInfoCallback(superCallback));
    }

    /**
     * @param searchResults the searchResults to set
     */
    public void setSearchResults(List<Collaborator> searchResults) {
        this.searchResults = searchResults;
    }

    private final class GetCollaboratorsCallback implements AsyncCallback<String> {
        private final AsyncCallback<Void> callback;

        public GetCollaboratorsCallback(final AsyncCallback<Void> callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            if (callback != null) {
                callback.onFailure(caught);
            }
        }

        @Override
        public void onSuccess(String result) {
            setCurrentCollaborators(parseResults(result));
            CollaboratorsLoadedEvent event = new CollaboratorsLoadedEvent();
            EventBus.getInstance().fireEvent(event);
            if (callback != null) {
                callback.onSuccess(null);
            }
        }
    }

    public boolean isCurrentCollaborator(Collaborator c) {
        for (Collaborator current : getCurrentCollaborators()) {
            if (current.getUserName().equals(c.getUserName())) {
                return true;
            }
        }

        return false;
    }

    public void addCollaborators(final List<Collaborator> models,
            final AsyncCallback<Void> supercallback) {
        JSONObject obj = buildJSONModel(models);
        facade.addCollaborators(obj, new AddCollaboratorCallback(models, supercallback));
    }

    public void removeCollaborators(final List<Collaborator> models,
            final AsyncCallback<Void> supercallback) {
        JSONObject obj = buildJSONModel(models);
        facade.removeCollaborators(obj, new RemoveCollaboratorCallback(models, supercallback));
    }

    private JSONObject buildJSONModel(final List<Collaborator> models) {
        JSONArray arr = new JSONArray();
        int count = 0;
        for (Collaborator model : models) {
            JSONObject user = new JSONObject();
            user.put("username", new JSONString(model.getUserName()));
            arr.set(count++, user);
        }

        JSONObject obj = new JSONObject();
        obj.put("users", arr);
        return obj;
    }

    private final class SearchCallback implements AsyncCallback<String> {

        private final AsyncCallback<Void> callback;

        public SearchCallback(final AsyncCallback<Void> callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            if (callback != null) {
                callback.onFailure(caught);
            }
        }

        @Override
        public void onSuccess(String result) {
            setSearchResults(parseResults(result));
            if (callback != null) {
                callback.onSuccess(null);
            }
        }
    }

    private final class AddCollaboratorCallback implements AsyncCallback<String> {
        private final AsyncCallback<Void> callback;
        private final List<Collaborator> models;

        public AddCollaboratorCallback(final List<Collaborator> models,
                final AsyncCallback<Void> callback) {
            this.callback = callback;
            this.models = models;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.addCollabErrorMsg(), caught);
            if (callback != null) {
                callback.onFailure(caught);
            }

        }

        @Override
        public void onSuccess(String result) {
            getCurrentCollaborators().addAll(models);
            StringBuilder builder = new StringBuilder();
            for (Collaborator c : models) {
                builder.append(c.getUserName() + ",");
            }

            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }

            IplantAnnouncer.getInstance()
                           .schedule(new SuccessAnnouncementConfig(
                    I18N.DISPLAY.collaboratorAddConfirm(builder.toString())));
            if (callback != null) {
                callback.onSuccess(null);
            }
        }
    }

    private class GetUserInfoCallback implements AsyncCallback<String> {

        private final AsyncCallback<FastMap<Collaborator>> superCallback;

        public GetUserInfoCallback(AsyncCallback<FastMap<Collaborator>> superCallback) {
            this.superCallback = superCallback;
        }

        @Override
        public void onFailure(Throwable caught) {
            if (superCallback != null) {
                superCallback.onFailure(caught);
            }
        }

        @Override
        public void onSuccess(String result) {
            if (superCallback != null) {
                FastMap<Collaborator> userResults = new FastMap<>();

                JSONObject users = jsonUtil.getObject(result);
                if (result != null) {

                    for (String username : users.keySet()) {
                        JSONObject userJson = jsonUtil.getObject(users, username);
                        AutoBean<Collaborator> bean = AutoBeanCodex.decode(factory, Collaborator.class,
                                userJson.toString());
                        userResults.put(username, bean.as());
                    }

                }

                superCallback.onSuccess(userResults);
            }
        }

    }

    private final class RemoveCollaboratorCallback implements AsyncCallback<String> {
        private final AsyncCallback<Void> callback;
        private final List<Collaborator> models;

        public RemoveCollaboratorCallback(final List<Collaborator> models,
                final AsyncCallback<Void> callback) {
            this.callback = callback;
            this.models = models;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.removeCollabErrorMsg(), caught);
            if (callback != null) {
                callback.onFailure(caught);
            }

        }

        @Override
        public void onSuccess(String result) {
            StringBuilder builder = new StringBuilder();
            for (Collaborator c : models) {
                builder.append(c.getUserName() + ",");
                getCurrentCollaborators().remove(c);

            }

            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }

            IplantAnnouncer.getInstance()
                           .schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.collaboratorRemoveConfirm(builder.toString())));
            if (callback != null) {
                callback.onSuccess(null);
            }

        }
    }

}
