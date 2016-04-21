package org.iplantc.de.admin.desktop.client.ontologies.service.callbacks;

import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author aramsey
 */
public class OntologyCallbackConverter extends AsyncCallbackConverter<String,Ontology> {

    private final OntologyAutoBeanFactory factory;

    public OntologyCallbackConverter(AsyncCallback<Ontology> callback, OntologyAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected Ontology convertFrom(String object) {
        final AutoBean<Ontology> decode = AutoBeanCodex.decode(factory, Ontology.class, object);
        return decode.as();
    }
}
