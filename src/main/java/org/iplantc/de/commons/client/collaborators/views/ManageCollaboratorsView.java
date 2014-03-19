/**
 * 
 */
package org.iplantc.de.commons.client.collaborators.views;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface ManageCollaboratorsView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void addAsCollaborators(List<Collaborator> models);

        void removeFromCollaborators(List<Collaborator> models);

        void loadCurrentCollaborators();

        void setCurrentMode(MODE mode);

        MODE getCurrentMode();

        List<Collaborator> getSelectedCollaborators();

        void cleanup();
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
