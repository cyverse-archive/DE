package org.iplantc.de.client.services.impl.models;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceExistMap;
import org.iplantc.de.client.models.diskResources.DiskResourcePermissionCategory;
import org.iplantc.de.client.models.diskResources.DiskResourceStatMap;
import org.iplantc.de.client.models.diskResources.FilePermissionCategory;
import org.iplantc.de.client.models.diskResources.FolderPermissionCategory;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

@Category({DiskResourcePermissionCategory.class, FilePermissionCategory.class, FolderPermissionCategory.class, DiskResourceExistMap.Category.class, DiskResourceStatMap.Category.class})
public interface DiskResourceServiceAutoBeanFactory extends DiskResourceAutoBeanFactory {

    AutoBean<DiskResourceMetadataBatchRequest> metadataBatchRequest();

}
