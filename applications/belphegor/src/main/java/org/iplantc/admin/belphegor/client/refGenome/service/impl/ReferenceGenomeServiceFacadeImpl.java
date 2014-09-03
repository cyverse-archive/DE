package org.iplantc.admin.belphegor.client.refGenome.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PUT;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.refGenome.service.ReferenceGenomeServiceFacade;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenomeAutoBeanFactory;
import org.iplantc.de.shared.services.DEServiceAsync;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;

public class ReferenceGenomeServiceFacadeImpl implements ReferenceGenomeServiceFacade {

    private final ReferenceGenomeAutoBeanFactory factory;
    private final DEServiceAsync deService;
    private final BelphegorAdminProperties properties;

    @Inject
    public ReferenceGenomeServiceFacadeImpl(ReferenceGenomeAutoBeanFactory factory,
                                            final DEServiceAsync deService,
                                            final BelphegorAdminProperties properties) {
        this.factory = factory;
        this.deService = deService;
        this.properties = properties;
    }

    @Override
    public void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback) {
        String address = properties.getListRefGenomeServiceUrl();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new ReferenceGenomeListCallbackConverter(callback, factory));
    }

    @Override
    public void editReferenceGenomes(ReferenceGenome referenceGenome, AsyncCallback<List<ReferenceGenome>> callback) {
        String address = properties.getEditRefGenomeServiceUrl();
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(referenceGenome.getName()).assign(body, ReferenceGenome.NAME);
        StringQuoter.create(referenceGenome.getPath()).assign(body, ReferenceGenome.PATH);
        StringQuoter.create(referenceGenome.isDeleted()).assign(body, ReferenceGenome.DELETED);
        StringQuoter.create(referenceGenome.getUuid()).assign(body, ReferenceGenome.UUID);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, body.getPayload());
        deService.getServiceData(wrapper, new ReferenceGenomeListCallbackConverter(callback, factory));
    }

    @Override
    public void createReferenceGenomes(ReferenceGenome referenceGenome, AsyncCallback<List<ReferenceGenome>> callback) {
        String address = properties.getAddRefGenomeServiceUrl();
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(referenceGenome.getName()).assign(body, ReferenceGenome.NAME);
        StringQuoter.create(referenceGenome.getPath()).assign(body, ReferenceGenome.PATH);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, body.getPayload());
        deService.getServiceData(wrapper, new ReferenceGenomeListCallbackConverter(callback, factory));
    }
}
