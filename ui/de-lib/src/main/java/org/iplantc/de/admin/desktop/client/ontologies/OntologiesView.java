package org.iplantc.de.admin.desktop.client.ontologies;

import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.client.models.ontologies.Ontology;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologiesView extends IsWidget,
                                        ViewOntologyVersionEvent.HasViewOntologyVersionEventHandlers {

    void showOntologyVersions(List<Ontology> result);

    interface OntologiesViewAppearance {
        String addOntology();

        ImageResource addIcon();

        String ontologyListDialogName();

        String ontologyList();

        int iriColumnWidth();

        String iriColumnLabel();

        int versionColumnWidth();

        String versionColumnLabel();

        int createdByColumnWidth();

        String createdByColumnLabel();

        int createdOnColumnWidth();

        String createdOnColumnLabel();

        String setActiveVersion();
    }

    interface Presenter {
        void go(HasOneWidget container);
    }

}
