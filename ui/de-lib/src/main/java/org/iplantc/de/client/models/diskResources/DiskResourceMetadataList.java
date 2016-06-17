package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuList;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Convenience autobean for de-serializing lists of metadata.
 * 
 * @author jstroot
 * 
 */
public interface DiskResourceMetadataList extends AvuList {

    @PropertyName("irods-avus")
    void setOtherMetadata(List<Avu> otherMetadata);

    @PropertyName("irods-avus")
    List<Avu> getOtherMetadata();

}
