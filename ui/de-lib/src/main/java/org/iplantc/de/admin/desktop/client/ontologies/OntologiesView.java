package org.iplantc.de.admin.desktop.client.ontologies;

import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteOntologyButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RefreshOntologiesEvent;
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
                                        RefreshOntologiesEvent.HasViewOntologyVersionEventHandlers,
                                        SelectOntologyVersionEvent.HasSelectOntologyVersionEventHandlers,
                                        HierarchySelectedEvent.HasHierarchySelectedEventHandlers,
                                        SaveOntologyHierarchyEvent.HasSaveOntologyHierarchyEventHandlers,
                                        PublishOntologyClickEvent.HasPublishOntologyClickEventHandlers,
                                        CategorizeButtonClickedEvent.HasCategorizeButtonClickedEventHandlers,
                                        AppSelectionChangedEvent.AppSelectionChangedEventHandler,
                                        DeleteOntologyButtonClickedEvent.HasDeleteOntologyButtonClickedEventHandlers,
                                        DeleteHierarchyEvent.HasDeleteHierarchyEventHandlers {

    void showOntologyVersions(List<Ontology> result);

    void showEmptyTreePanel();

    void showTreePanel();

    OntologyHierarchy getHierarchyFromElement(Element el);

    OntologyHierarchy getSelectedHierarchy();

    void clearStore();

    void addToStore(List<OntologyHierarchy> children);

    void addToStore(OntologyHierarchy parent, List<OntologyHierarchy> children);

    void maskHierarchyTree();

    void unMaskHierarchyTree();
    
    void selectHierarchy(OntologyHierarchy hierarchy);

    void selectActiveOntology(Ontology activeOntology);

    void reSortHierarchies();

    void updateButtonStatus();

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

        String loadingMask();

        int publishDialogWidth();

        int publishDialogMinHeight();

        int publishDialogMinWidth();

        int publishDialogHeight();

        String activeOntologyFieldWidth();

        String editedOntologyFieldWidth();

        int rootIriLabelWidth();

        String emptyDEOntologyLabel();

        String deleteOntology();

        String deleteHierarchy();

        String hierarchyDeleted(String hierarchy);

        String confirmDeleteOntology(String version);

        String ontologyDeleted(String ontologyVersion);

        int rootColumnWidth();

        String rootColumnLabel();

        int hierarchyColumnWidth();

        String hierarchyColumnLabel();

        String confirmDeleteHierarchy(String selectedItem);
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
