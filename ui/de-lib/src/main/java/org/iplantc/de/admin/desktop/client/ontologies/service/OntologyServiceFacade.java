package org.iplantc.de.admin.desktop.client.ontologies.service;

import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyHierarchyFilterReq;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyServiceFacade {

    void saveOntology(String ontologyXML, AsyncCallback<Ontology> callback);

    void saveOntologyHierarchy(String version, String root, AsyncCallback<OntologyHierarchy> callback);

    void getOntologies(AsyncCallback<List<Ontology>> callback);

    void getOntologyHierarchies(String version, AsyncCallback<List<OntologyHierarchy>> callback);

    void filterOntologyHierarchies(String version, String root, OntologyHierarchyFilterReq filter, AsyncCallback<List<OntologyHierarchy>> callback);

    void filterUnclassifiedTargets(String version, String root, OntologyHierarchyFilterReq filter, AsyncCallback<List<String>> callback);

}
