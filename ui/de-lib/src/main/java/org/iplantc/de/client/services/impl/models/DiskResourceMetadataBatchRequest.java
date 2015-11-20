package org.iplantc.de.client.services.impl.models;

import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface DiskResourceMetadataBatchRequest {

    DiskResourceMetadataTemplate getMetadata();

    void setMetadata(DiskResourceMetadataTemplate metadata);

    @PropertyName("irods-avus")
    List<DiskResourceMetadata> getAvus();

    @PropertyName("irods-avus")
    void setAvus(List<DiskResourceMetadata> avus);
}
