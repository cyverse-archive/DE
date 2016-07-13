package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyHierarchyList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyHierarchyListCallbackConverter
        extends AsyncCallbackConverter<String, List<OntologyHierarchy>> {

    private final OntologyAutoBeanFactory factory;

    public OntologyHierarchyListCallbackConverter(AsyncCallback<List<OntologyHierarchy>> callback,
                                                  OntologyAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<OntologyHierarchy> convertFrom(String object) {
        final AutoBean<OntologyHierarchyList> decode =
                AutoBeanCodex.decode(factory, OntologyHierarchyList.class, object);
        return decode.as().getHierarchies();
    }
}
