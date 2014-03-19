package org.iplantc.de.client.models.diskResources;



import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface File extends DiskResource {

    @PropertyName("file-size")
    long getSize();

    @PropertyName("file-size")
    void setSize(long size);

}
