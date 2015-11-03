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
    
    @PropertyName("totalBad")
    void setTotalFiltered(int total_filtered);

    @PropertyName("totalBad")
    int getTotalFiltered();

    @PropertyName("dir-count")
    int getDirCount();

    @PropertyName("dir-count")
    void setDirCount(int count);

    @PropertyName("file-count")
    void setFileCount(int count);

    @PropertyName("file-count")
    int getFileCount();
}
