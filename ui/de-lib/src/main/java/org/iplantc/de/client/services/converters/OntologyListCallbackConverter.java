package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyListCallbackConverter extends AsyncCallbackConverter<String, List<Ontology>> {

    private final OntologyAutoBeanFactory factory;

    public OntologyListCallbackConverter(AsyncCallback<List<Ontology>> callback,
                                         OntologyAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<Ontology> convertFrom(String object) {
        final AutoBean<OntologyList> decode = AutoBeanCodex.decode(factory, OntologyList.class, object);
        return decode.as().getOntologies();
    }
}
