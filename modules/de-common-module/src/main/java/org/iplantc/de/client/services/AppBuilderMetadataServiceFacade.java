package org.iplantc.de.client.services;

import org.iplantc.de.client.models.apps.integration.DataSource;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppBuilderMetadataServiceFacade {

    void getDataSources(AsyncCallback<List<DataSource>> callback);

    void getFileInfoTypes(AsyncCallback<List<FileInfoType>> callback);
    
    void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback);

}
