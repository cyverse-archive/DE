package org.iplantc.de.admin.desktop.client.refGenome.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PATCH;

import org.iplantc.de.admin.desktop.client.refGenome.service.ReferenceGenomeServiceFacade;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenomeAutoBeanFactory;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import java.util.List;

/**
 * @author jstroot
 */
public class ReferenceGenomeServiceFacadeImpl implements ReferenceGenomeServiceFacade {

    private final String REFERENCE_GENOMES = "org.iplantc.services.referenceGenomes";
    private final String REFERENCE_GENOMES_ADMIN = "org.iplantc.services.admin.referenceGenomes";
    @Inject private ReferenceGenomeAutoBeanFactory factory;
    @Inject private DiscEnvApiService deService;

    @Inject
    public ReferenceGenomeServiceFacadeImpl() { }

    @Override
    public void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback) {
        String address = REFERENCE_GENOMES + "?deleted=true";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new ReferenceGenomeListCallbackConverter(callback, factory));
    }

    @Override
    public void editReferenceGenomes(ReferenceGenome referenceGenome,
                                     AsyncCallback<ReferenceGenome> callback) {
        String address = REFERENCE_GENOMES_ADMIN + "/" + referenceGenome.getId();
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(referenceGenome.getName()).assign(body, ReferenceGenome.NAME);
        StringQuoter.create(referenceGenome.getPath()).assign(body, ReferenceGenome.PATH);
        StringQuoter.create(referenceGenome.isDeleted()).assign(body, ReferenceGenome.DELETED);
        StringQuoter.create(referenceGenome.getId()).assign(body, "id");
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address, body.getPayload());
        deService.getServiceData(wrapper, new ReferenceGenomeCallbackConverter(callback, factory));
    }

    @Override
    public void createReferenceGenomes(ReferenceGenome referenceGenome,
                                       AsyncCallback<ReferenceGenome> callback) {
        String address = REFERENCE_GENOMES_ADMIN;
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(referenceGenome.getName()).assign(body, ReferenceGenome.NAME);
        StringQuoter.create(referenceGenome.getPath()).assign(body, ReferenceGenome.PATH);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, body.getPayload());
        deService.getServiceData(wrapper, new ReferenceGenomeCallbackConverter(callback, factory));
    }
}
