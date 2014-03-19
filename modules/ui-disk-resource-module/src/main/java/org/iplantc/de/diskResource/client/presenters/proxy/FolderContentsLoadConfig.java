package org.iplantc.de.diskResource.client.presenters.proxy;

import org.iplantc.de.client.models.diskResources.Folder;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

public class FolderContentsLoadConfig extends FilterPagingLoadConfigBean {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Folder folder;
    
    public void setFolder(Folder folder) {
        this.folder = folder;
    }
    
    public Folder getFolder() {
        return folder;
    }

}
