package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface DiskResourceMetadataTemplate extends HasId {

    @Override
    @PropertyName("template_id")
    String getId();

    @PropertyName("template_id")
    void setId(String id);

    List<DiskResourceMetadata> getAvus();

    void setAvus(List<DiskResourceMetadata> avus);
}
