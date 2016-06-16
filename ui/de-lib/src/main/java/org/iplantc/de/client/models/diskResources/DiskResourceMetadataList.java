package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.avu.Avu;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Convenience autobean for de-serializing lists of metadata.
 * 
 * @author jstroot
 * 
 */
public interface DiskResourceMetadataList {

    @PropertyName("irods-avus")
    void setOtherMetadata(List<Avu> otherMetadata);

    @PropertyName("avus")
    void setUserMetadata(List<Avu> userMetadata);

    @PropertyName("irods-avus")
    List<Avu> getOtherMetadata();

    @PropertyName("avus")
    List<Avu> getUserMetadata();
}
