package org.iplantc.de.client.services;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;

public interface FileSystemMetadataServiceFacade extends MetadataServiceFacade {
    public void getFavorites(final List<InfoType> infoTypeFilters,
                             final TYPE entityType,
                             final FilterPagingLoadConfigBean configBean,
                             AsyncCallback<Folder> asyncCallback);
}
