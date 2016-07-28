package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * Created by jstroot on 3/4/15.
 * @author jstroot
 */
public interface AppDetailsViewFactory {
    AppDetailsView create(App app,
                          String searchRegex,
                          TreeStore<OntologyHierarchy> hierarchyTreeStore);
}
