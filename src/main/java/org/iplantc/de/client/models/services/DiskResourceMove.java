package org.iplantc.de.client.models.services;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * An AutoBean interface for DiskResource Move requests and responses.
 * 
 * @author psarando
 * 
 */
public interface DiskResourceMove {

    String getDest();

    void setDest(String dest);

    Folder getDestination();

    void setDestination(Folder destination);

    List<String> getSources();

    void setSources(List<String> sources);
    
    /**
     * In case when contents of the folder needs to moved (i.e select all)
     * 
     * @param id id of the parent folder
     */
    @PropertyName("source")
    void setSelectedFolderId(String id);
    
    @PropertyName("source")
    String getSelectedFolderId();
}
