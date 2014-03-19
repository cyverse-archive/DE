/**
 * 
 */
package org.iplantc.de.commons.client.collaborators.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.collaborators.events.UserSearchResultSelected;
import org.iplantc.de.commons.client.collaborators.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.collaborators.views.ManageCollaboratorsView;
import org.iplantc.de.commons.client.collaborators.views.ManageCollaboratorsView.Presenter;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.Arrays;
import java.util.List;

/**
 * @author sriram
 * 
 */
public class ManageCollaboratorsPresenter implements Presenter {

    private final ManageCollaboratorsView view;
    private HandlerRegistration addCollabHandlerRegistration;

    public static enum MODE {
        MANAGE, SELECT
    };

    public ManageCollaboratorsPresenter(ManageCollaboratorsView view) {
        this.view = view;
        view.setPresenter(this);
        loadCurrentCollaborators();
        addEventHandlers();
    }

    private void addEventHandlers() {
        addCollabHandlerRegistration = EventBus.getInstance().addHandler(UserSearchResultSelected.TYPE,
                new UserSearchResultSelected.UserSearchResultSelectedEventHandler() {

                    @Override
                    public void onUserSearchResultSelected(
                            UserSearchResultSelected userSearchResultSelected) {
                        if (userSearchResultSelected.getTag().equalsIgnoreCase(
                                UserSearchResultSelected.USER_SEARCH_EVENT_TAG.MANAGE.toString())) {
                            Collaborator collaborator = userSearchResultSelected.getCollaborator();
                            if (!UserInfo.getInstance().getUsername().equals(collaborator.getUserName())) {
                                addAsCollaborators(Arrays.asList(collaborator));
                            } else {
                                IplantAnnouncer.getInstance().schedule(
                                        new ErrorAnnouncementConfig(I18N.DISPLAY.collaboratorSelfAdd()));
                            }
                        }
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.commons.client.presenter.Presenter#go(com.google.gwt.user.client.ui.HasOneWidget
     * )
     */
    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.collaborators.views.ManageCollaboratorsView.Presenter#addAsCollaborators
     * (java.util.List)
     */
    @Override
    public void addAsCollaborators(final List<Collaborator> models) {
        CollaboratorsUtil.addCollaborators(models, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                // remove added models from search results
                view.addCollaborators(models);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.collaborators.views.ManageCollaboratorsView.Presenter#removeFromCollaborators
     * (java.util.List)
     */
    @Override
    public void removeFromCollaborators(final List<Collaborator> models) {
        CollaboratorsUtil.removeCollaborators(models, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                view.removeCollaborators(models);

            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.collaborators.views.ManageCollaboratorsView.Presenter#loadCurrentCollaborators
     * ()
     */
    @Override
    public void loadCurrentCollaborators() {
        view.mask(null);
        CollaboratorsUtil.getCollaborators(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
            }

            @Override
            public void onSuccess(Void result) {
                view.unmask();
                view.loadData(CollaboratorsUtil.getCurrentCollaborators());
            }

        });

    }


    @Override
    public void setCurrentMode(MODE m) {
        view.setMode(m);
    }

    @Override
    public MODE getCurrentMode() {
        return view.getMode();
    }

    @Override
    public List<Collaborator> getSelectedCollaborators() {
        return view.getSelectedCollaborators();
    }

    @Override
    public void cleanup() {
        if (addCollabHandlerRegistration != null) {
            addCollabHandlerRegistration.removeHandler();
        }
    }

}
