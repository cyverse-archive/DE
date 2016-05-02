package org.iplantc.de.admin.desktop.client.ontologies.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PUT;

import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.service.callbacks.OntologyHierarchyCallbackConverter;
import org.iplantc.de.admin.desktop.client.ontologies.service.callbacks.OntologyHierarchyListCallbackConverter;
import org.iplantc.de.admin.desktop.client.ontologies.service.callbacks.OntologyListCallbackConverter;
import org.iplantc.de.admin.desktop.client.ontologies.service.callbacks.OntologyVersionCallbackConverter;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyMetadata;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyServiceFacadeImpl implements OntologyServiceFacade {

    private final String ONTOLOGY = "org.iplantc.services.ontologies";
    private final String ONTOLOGY_ADMIN = "org.iplantc.services.admin.ontologies";
    private final String APPS_HIERARCHIES = "org.iplantc.services.apps.hierarchies";
    @Inject OntologyAutoBeanFactory factory;
    @Inject private DiscEnvApiService deService;
    @Inject AppServiceFacade.AppServiceAutoBeanFactory svcFactory;

    @Inject
    OntologyServiceFacadeImpl() {
    }
    
    @Override
    public void saveOntologyHierarchy(String version,
                                      String root,
                                      AsyncCallback<OntologyHierarchy> callback) {
        String address = ONTOLOGY_ADMIN + "/" + URL.encodeQueryString(version) + "/" + URL.encodeQueryString(root);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, "{}");
        deService.getServiceData(wrapper, new OntologyHierarchyCallbackConverter(callback, factory));

    }

    @Override
    public void getOntologies(AsyncCallback<List<Ontology>> callback) {

        String address = ONTOLOGY_ADMIN;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new OntologyListCallbackConverter(callback, factory));

    }

    @Override
    public void setActiveOntologyVersion(String version, AsyncCallback<OntologyVersionDetail> callback) {
        String address = ONTOLOGY_ADMIN + "/" + URL.encodeQueryString(version);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, "{}");
        deService.getServiceData(wrapper, new OntologyVersionCallbackConverter(callback, factory));

    }

    @Override
    public void getAppsByHierarchy(String iri, OntologyMetadata metadata, AsyncCallback<List<App>> callback) {
        String address = APPS_HIERARCHIES + "/" + URL.encodeQueryString(iri) + "/apps" + "?attr=" + metadata.getAttr();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new AsyncCallbackConverter<String, List<App>>(callback) {
            @Override
            protected List<App> convertFrom(String object) {
                List<App> apps = AutoBeanCodex.decode(svcFactory, AppList.class, object).as().getApps();
                return apps;
            }
        });
    }


    @Override
    public void getOntologyHierarchies(String version,
                                       AsyncCallback<List<OntologyHierarchy>> callback) {

        String address = ONTOLOGY_ADMIN + "/" + URL.encodeQueryString(version);

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new OntologyHierarchyListCallbackConverter(callback, factory));

    }

    @Override
    public void getUnclassifiedApps(String version, String root, AsyncCallback<List<App>> callback) {
        String address = ONTOLOGY_ADMIN + "/" + URL.encodeQueryString(version) + "/" + URL.encodeQueryString(root) + "/unclassified";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new AsyncCallbackConverter<String, List<App>>(callback) {
            @Override
            protected List<App> convertFrom(String object) {
                List<App> apps = AutoBeanCodex.decode(svcFactory, AppList.class, object).as().getApps();
                return apps;
            }
        });
    }
}
