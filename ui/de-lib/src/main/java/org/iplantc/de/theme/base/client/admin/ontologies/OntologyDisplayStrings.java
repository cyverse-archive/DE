package org.iplantc.de.theme.base.client.admin.ontologies;

import com.google.gwt.i18n.client.Messages;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyDisplayStrings extends Messages{

    String addOntology();

    String ontologyList();

    String setActiveVersion();

    String fileUploadMaxSizeWarning();

    String reset();

    String fileUploadFailed(String file);

    String fileUploadSuccess(String file);

    String activeOntologyLabel();

    String editedOntologyLabel();

    String setActiveOntologySuccess();

    String saveHierarchy();

    String selectOntologyVersion();

    String successTopicSaved();

    String successOperationSaved();

    String publishOntologyWarning();

    String rootIriLabel();

    String enterIriEmptyText();

    String invalidHierarchySubmitted(String iri);

    String clearHierarchySelection();

    String categorize();

    String categorizeApp(String targetApp);

    String appAvusCleared(String name);

    String appClassifiedList(String name, @PluralCount List<String> tags);

    String appClassified(String name, String label);

    String emptyDEOntologyLabel();

    String deleteOntology();

    String deleteHierarchy();

    String hierarchyDeleted(String hierarchy);

    String confirmDeleteOntology(String version);

    String ontologyDeleted(String ontologyVersion);

    String rootColumnLabel();

    String hierarchyColumnLabel();

    String confirmDeleteHierarchy(String selectedItem);

    String confirmDeleteAppTitle();

    String confirmDeleteAppWarning(String name);

    String externalAppDND(String appLabels);

    String ontologyAttrMatchingError();

    String emptySearchFieldText();
}
