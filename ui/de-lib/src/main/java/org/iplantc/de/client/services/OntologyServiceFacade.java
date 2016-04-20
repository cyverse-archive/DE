package org.iplantc.de.client.services;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyServiceFacade {

    void getAppHierarchies(AsyncCallback<List<OntologyHierarchy>> callback);

    void getAppsInCategory(String iri, Avu avu, AsyncCallback<List<App>> callback);

    void getUnclassifiedAppsInCategory(String iri, Avu avu, AsyncCallback<List<App>> callback);
}
