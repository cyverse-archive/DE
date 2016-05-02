package org.iplantc.de.theme.base.client.admin.ontologies;

import com.google.gwt.i18n.client.Messages;

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

    String publishOntology();

    String activeOntologyLabel();

    String editedOntologyLabel();

    String setActiveOntologySuccess();

    String saveHierarchy();

    String selectOntologyVersion();

    String successTopicSaved();

    String successOperationSaved();

    String publishOntologyWarning();
}
