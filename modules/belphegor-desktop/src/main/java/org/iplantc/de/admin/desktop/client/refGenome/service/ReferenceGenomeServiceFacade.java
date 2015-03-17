package org.iplantc.de.admin.desktop.client.refGenome.service;

import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author jstroot
 */
public interface ReferenceGenomeServiceFacade {

    /**
     * https://github.com/iPlantCollaborativeOpenSource/Conrad#listing-all-genome-references
     */
    void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback);

    /**
     * https://github.com/iPlantCollaborativeOpenSource/Conrad#modifying-a-genome-reference
     */
    void editReferenceGenomes(ReferenceGenome referenceGenome, AsyncCallback<ReferenceGenome> callback);

    /**
     * https://github.com/iPlantCollaborativeOpenSource/Conrad#creating-a-new-genome-reference
     */
    void
    createReferenceGenomes(ReferenceGenome referenceGenome,
                           AsyncCallback<ReferenceGenome> callback);
}
