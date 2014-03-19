package org.iplantc.de.client.services.impl.models;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceExistMap;
import org.iplantc.de.client.models.diskResources.DiskResourceStatMap;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;

@Category({DiskResourceExistMap.Category.class, DiskResourceStatMap.Category.class})
public interface DiskResourceServiceAutoBeanFactory extends DiskResourceAutoBeanFactory {

    AutoBean<DiskResourceMetadataBatchRequest> metadataBatchRequest();

}
