package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.diskResources.RestoreResponse.RestoredResource;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.models.services.DiskResourceMove;
import org.iplantc.de.client.models.services.DiskResourceRename;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

@Category({DiskResourceExistMap.Category.class, DiskResourceStatMap.Category.class})
public interface DiskResourceAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Folder> folder();

    AutoBean<Folder> folder(Folder toWrap);

    AutoBean<DiskResource> diskResource();

    AutoBean<File> file();

    AutoBean<Permissions> permissions();

    AutoBean<RootFolders> rootFolders();

    AutoBean<DiskResourceMetadata> metadata();

    AutoBean<DiskResourceMetadataList> metadataList();

    AutoBean<DiskResourceInfo> info();

    AutoBean<HasPaths> pathsList();

    AutoBean<RestoreResponse> restoreResponse();

    AutoBean<RestoredResource> partialRestoreResponse();

    AutoBean<DiskResourceExistMap> diskResourceExistMap();

    AutoBean<DiskResourceStatMap> diskResourceStatMap();

    AutoBean<SimpleServiceError> simpleServiceError();

    AutoBean<DiskResourceMove> diskResourceMove();

    AutoBean<DiskResourceRename> diskResourceRename();

    AutoBean<Folder> folderContents();

    AutoBean<MetadataTemplateInfoList> getTemplateListing();

    AutoBean<MetadataTemplate> getTemplate();

}
