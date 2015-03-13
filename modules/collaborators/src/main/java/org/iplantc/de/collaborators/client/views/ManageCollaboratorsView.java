package org.iplantc.de.collaborators.client.views;

import org.iplantc.de.client.models.collaborators.Collaborator;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author sriram, jstroot
 * 
 */
public interface ManageCollaboratorsView extends IsWidget {

    interface Appearance {

        SafeHtml renderCheckBoxColumnHeader(String debugId);
    }

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void addAsCollaborators(List<Collaborator> models);

        void removeFromCollaborators(List<Collaborator> models);

        void loadCurrentCollaborators();

        void setCurrentMode(MODE mode);

        MODE getCurrentMode();

        List<Collaborator> getSelectedCollaborators();

        void cleanup();
    }

    enum MODE {
        MANAGE, SELECT
    }

    void setPresenter(Presenter p);

    void loadData(List<Collaborator> models);

    void removeCollaborators(List<Collaborator> models);

    void mask(String maskText);

    void unmask();

    void setMode(MODE mode);

    List<Collaborator> getSelectedCollaborators();

    MODE getMode();

    void addCollaborators(List<Collaborator> models);
}
