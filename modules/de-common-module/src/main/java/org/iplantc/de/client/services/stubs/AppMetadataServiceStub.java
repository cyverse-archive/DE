package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.apps.integration.DataSource;
import org.iplantc.de.client.models.apps.integration.FileInfoType;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.client.services.AppBuilderMetadataServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class AppMetadataServiceStub implements AppBuilderMetadataServiceFacade {
    @Override
    public void getDataSources(AsyncCallback<List<DataSource>> callback) {

    }

    @Override
    public void getFileInfoTypes(AsyncCallback<List<FileInfoType>> callback) {

    }

    @Override
    public void getReferenceGenomes(AsyncCallback<List<ReferenceGenome>> callback) {

    }
}
