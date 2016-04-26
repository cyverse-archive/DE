package org.iplantc.de.admin.desktop.client.ontologies.service.callbacks;

import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author aramsey
 */
public class OntologyVersionCallbackConverter extends AsyncCallbackConverter<String,OntologyVersionDetail> {

    private final OntologyAutoBeanFactory factory;

    public OntologyVersionCallbackConverter(AsyncCallback<OntologyVersionDetail> callback, OntologyAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected OntologyVersionDetail convertFrom(String object) {
        final AutoBean<OntologyVersionDetail> decode = AutoBeanCodex.decode(factory, OntologyVersionDetail.class, object);
        return decode.as();
    }
}
