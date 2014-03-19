package org.iplantc.admin.belphegor.client.refGenome.service;

import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface ReferenceGenomeServiceFacade {

    /**
     * https://github.com/iPlantCollaborativeOpenSource/Conrad#listing-all-genome-references
     * 
     * @param callback
     */
    void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback);

    /**
     * https://github.com/iPlantCollaborativeOpenSource/Conrad#modifying-a-genome-reference
     * 
     * @param referenceGenome
     * @param callback
     */
    void editReferenceGenomes(ReferenceGenome referenceGenome, AsyncCallback<List<ReferenceGenome>> callback);

    /**
     * https://github.com/iPlantCollaborativeOpenSource/Conrad#creating-a-new-genome-reference
     * 
     * @param referenceGenome
     * @param callback
     */
    void createReferenceGenomes(ReferenceGenome referenceGenome, AsyncCallback<List<ReferenceGenome>> callback);
}
