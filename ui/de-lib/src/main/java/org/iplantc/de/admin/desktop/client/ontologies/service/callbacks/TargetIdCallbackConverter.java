package org.iplantc.de.admin.desktop.client.ontologies.service.callbacks;

import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.TargetIDList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author aramsey
 */
public class TargetIdCallbackConverter extends AsyncCallbackConverter<String, List<String>>{

    private OntologyAutoBeanFactory factory;

    public TargetIdCallbackConverter(AsyncCallback<List<String>> callback, OntologyAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<String> convertFrom(String object) {
        final AutoBean<TargetIDList> decode = AutoBeanCodex.decode(factory, TargetIDList.class, object);
        return decode.as().getTargetIds();
    }
}
