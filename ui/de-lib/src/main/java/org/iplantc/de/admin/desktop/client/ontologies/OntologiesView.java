package org.iplantc.de.admin.desktop.client.ontologies;

import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
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
                                        PublishOntologyClickEvent.HasPublishOntologyClickEventHandlers,
                                        CategorizeButtonClickedEvent.HasCategorizeButtonClickedEventHandlers,
                                        AppSelectionChangedEvent.AppSelectionChangedEventHandler{

    void showOntologyVersions(List<Ontology> result);

    void showEmptyTreePanel();

    void showTreePanel();

    OntologyHierarchy getHierarchyFromElement(Element el);

    OntologyHierarchy getSelectedHierarchy();

    interface OntologiesViewAppearance {
        String addOntology();

        ImageResource addIcon();

        String ontologyList();

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

        String selectOntologyVersion();

        SafeHtml activeOntologyField(String version);

        SafeHtml editedOntologyField(String version);

        String successTopicSaved();

        String successOperationSaved();

        SafeHtml publishOntologyWarning();

        String rootIriLabel();

        String enterIriEmptyText();

        String add();

        ImageResource deleteIcon();

        String delete();

        String invalidHierarchySubmitted(String iri);

        String clearHierarchySelection();

        ImageResource blueFolder();

        ImageResource blueFolderOpen();

        ImageResource blueFolderLeaf();

        String categorize();

        String categorizeDialogWidth();

        String categorizeDialogHeight();

        String categorizeApp(App targetApp);

        String appAvusCleared(App targetApp);

        String appClassified(String name, List<Avu> result);

        String appClassified(String name, String label);
    }

    interface Presenter {
        void go(HasOneWidget container);

        OntologiesView getView();

        void hierarchyDNDtoApp(OntologyHierarchy hierarchy, App targetApp);

        OntologyHierarchy getHierarchyFromElement(Element el);

        void appsDNDtoHierarchy(List<App> apps, OntologyHierarchy hierarchy);

        OntologyHierarchy getSelectedHierarchy();
    }

}
