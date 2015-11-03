package org.iplantc.de.client.models.errors.diskResources;

import org.iplantc.de.client.models.errors.diskResources.categories.ErrorCreateFolderCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorDiskResourceCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorDiskResourceDeleteCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorDiskResourceMoveCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorDiskResourceRenameCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorDuplicateDiskResourceCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorGetManifestCategory;
import org.iplantc.de.client.models.errors.diskResources.categories.ErrorUpdateMetadataCategory;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

@Category({ErrorDiskResourceCategory.class, 
    ErrorDiskResourceDeleteCategory.class, 
    ErrorCreateFolderCategory.class,
    ErrorDiskResourceMoveCategory.class, 
    ErrorUpdateMetadataCategory.class,
    ErrorDuplicateDiskResourceCategory.class,
    ErrorGetManifestCategory.class,
    ErrorDiskResourceRenameCategory.class})
public interface DiskResourceErrorAutoBeanFactory extends AutoBeanFactory {

    AutoBean<ErrorDiskResource> errorDiskResource();

    AutoBean<ErrorDiskResourceDelete> diskResourceDeleteError();

    AutoBean<ErrorCreateFolder> createFolderError();
    
    AutoBean<ErrorDiskResourceMove> moveDiskResourceError();
    
    AutoBean<ErrorDiskResourceRename> renameDiskResourceError();

    AutoBean<ErrorUpdateMetadata> errorUpdateMetadata();

    AutoBean<ErrorDuplicateDiskResource> errorDuplicateDiskResource();

    AutoBean<ErrorGetManifest> errorGetManifest();
}
