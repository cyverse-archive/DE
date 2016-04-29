package org.iplantc.de.admin.desktop.client.ontologies.service.callbacks;

import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author aramsey
 */
public class OntologyHierarchyCallbackConverter
        extends AsyncCallbackConverter<String, OntologyHierarchy> {

    private final OntologyAutoBeanFactory factory;

    public OntologyHierarchyCallbackConverter(AsyncCallback<OntologyHierarchy> callback,
                                              OntologyAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected OntologyHierarchy convertFrom(String object) {
        final AutoBean<OntologyHierarchy> decode =
                AutoBeanCodex.decode(factory, OntologyHierarchy.class, object);
        return decode.as().getHierarchy();
    }
}
