package org.iplantc.de.admin.desktop.client.refGenome.service.impl;

import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenomeAutoBeanFactory;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class ReferenceGenomeCallbackConverter extends AsyncCallbackConverter<String, ReferenceGenome> {

    private final ReferenceGenomeAutoBeanFactory factory;

    public ReferenceGenomeCallbackConverter(AsyncCallback<ReferenceGenome> callback,
                                            ReferenceGenomeAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected ReferenceGenome convertFrom(String object) {
        final AutoBean<ReferenceGenome> decode = AutoBeanCodex.decode(factory,
                                                                      ReferenceGenome.class,
                                                                      object);
        return decode.as();
    }

}
