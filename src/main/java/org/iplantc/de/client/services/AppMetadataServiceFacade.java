package org.iplantc.de.client.services;

import org.iplantc.de.client.models.apps.integration.DataSource;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.models.apps.integration.ReferenceGenome;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface AppMetadataServiceFacade {

    // DataSourceProperties getDataSourceProperties();

    void getDataSources(AsyncCallback<List<DataSource>> callback);

    // FileInfoTypeProperties getFileInfoTypeProperties();

    void getFileInfoTypes(AsyncCallback<List<FileInfoType>> callback);
    
    // ReferenceGenomeProperties getReferenceGenomeProperties();

    void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback);

}
