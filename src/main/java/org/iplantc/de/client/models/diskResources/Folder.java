package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface Folder extends DiskResource {

    @PropertyName("hasSubDirs")
    boolean hasSubDirs();

    List<Folder> getFolders();

    void setFolders(List<Folder> folders);

    List<File> getFiles();
    
    void setFiles(List<File> files);
    
    @PropertyName("total")
    void setTotal(int total);
    
    int getTotal();
    
    @PropertyName("total_filtered")
    void setTotalFiltered(int total_filtered);

    @PropertyName("total_filtered")
    int getTotalFiltered();
}
