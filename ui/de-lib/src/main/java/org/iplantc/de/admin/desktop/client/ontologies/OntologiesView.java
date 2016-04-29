package org.iplantc.de.admin.desktop.client.ontologies;

import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
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
                                        ViewOntologyVersionEvent.HasViewOntologyVersionEventHandlers,
                                        SelectOntologyVersionEvent.HasSelectOntologyVersionEventHandlers,
                                        HierarchySelectedEvent.HasHierarchySelectedEventHandlers,
                                        SaveOntologyHierarchyEvent.HasSaveOntologyHierarchyEventHandlers,
                                        PublishOntologyClickEvent.HasPublishOntologyClickEventHandlers {

    void showOntologyVersions(List<Ontology> result);

    void showEmptyTreePanel();

    void showTreePanel();

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

        String upload();

        String fileUploadMaxSizeWarning();

        String reset();

        ImageResource arrowUndoIcon();

        String confirmAction();

        String closeConfirmMessage();

        String fileUploadsFailed(String strings);

        String fileUploadSuccess(String value);

        ImageResource refreshIcon();

        String publishOntology();

        ImageResource publishIcon();

        String activeOntologyLabel();

        String editedOntologyLabel();

        String setActiveOntologySuccess();

        String saveHierarchy();

        ImageResource saveIcon();
    }

    interface Presenter {
        void go(HasOneWidget container);
    }

}
