package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;

import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.OntologyHierarchyListCallbackConverter;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.OntologyServiceFacade;
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

    private final String APPS_HIERARCHIES = "org.iplantc.services.apps.hierarchies";
    @Inject OntologyAutoBeanFactory factory;
    @Inject AvuAutoBeanFactory avuFactory;
    @Inject private DiscEnvApiService deService;
    @Inject AppServiceFacade.AppServiceAutoBeanFactory svcFactory;

    @Override
    public void getAppHierarchies(AsyncCallback<List<OntologyHierarchy>> callback) {
        String address = APPS_HIERARCHIES;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new OntologyHierarchyListCallbackConverter(callback, factory));
    }

    @Override
    public void getAppsInCategory(String iri, Avu avu, AsyncCallback<List<App>> callback) {
        String address = APPS_HIERARCHIES + "/" + URL.encodeQueryString(iri) + "/apps?attr=" + URL.encodeQueryString(avu.getAttr());

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
    public void getUnclassifiedAppsInCategory(String iri, Avu avu, AsyncCallback<List<App>> callback) {
        String address = APPS_HIERARCHIES + "/" + URL.encodeQueryString(iri) + "/unclassified?attr=" + URL.encodeQueryString(avu.getAttr());

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
