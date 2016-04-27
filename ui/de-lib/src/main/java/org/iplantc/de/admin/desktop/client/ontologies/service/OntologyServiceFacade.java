package org.iplantc.de.admin.desktop.client.ontologies.service;

import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyHierarchyFilterReq;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyServiceFacade {

    void saveOntologyHierarchy(String version, String root, AsyncCallback<OntologyHierarchy> callback);

    /**
     * Get a list of all ontology versions that are available
     * @param callback
     */
    void getOntologies(AsyncCallback<List<Ontology>> callback);


    /**
     * Get the saved hierarchy at the given root IRI for the specified version of the ontology
     * @param callback
     */
    void getOntologyHierarchies(String version, String root, AsyncCallback<List<OntologyHierarchy>> callback);


    /**
     * Get the list of hierarchies and their subclasses for the active version of an ontology
     * @param callback
     */
    void getActiveOntologyHierarchies(AsyncCallback<List<OntologyHierarchy>> callback);

    /**
     * Get the list of ontology hierarchies for a specific version of an ontology at the specified root IRI
     * with a set of target IDs.
     * @param version
     * @param root
     * @param filter
     * @param callback
     */
    void filterOntologyHierarchies(String version, String root, OntologyHierarchyFilterReq filter, AsyncCallback<List<OntologyHierarchy>> callback);

    /**
     * Get the list of ontology hierarchies for the active ontology version at the specified root IRI
     * @param root
     * @param callback
     */
    void filterActiveOntologyHierarchies(String root, AsyncCallback<List<OntologyHierarchy>> callback);

    /**
     * Filter a list of target-ids from a specific ontology version at the specified root down to
     * the orphaned target-ids
     * @param version
     * @param root
     * @param filter
     * @param callback
     */
    void filterUnclassifiedTargets(String version, String root, OntologyHierarchyFilterReq filter, AsyncCallback<List<String>> callback);

    /**
     * Set the active ontology version used by the app-hierarchies endpoints
     * @param version
     * @param callback
     */
    void setActiveOntologyVersion(String version, AsyncCallback<OntologyVersionDetail> callback);

}
