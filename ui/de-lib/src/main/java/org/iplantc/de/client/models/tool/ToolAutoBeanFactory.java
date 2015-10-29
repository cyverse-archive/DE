/**
 *
 */
package org.iplantc.de.client.models.tool;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author sriram
 */
public interface ToolAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Tool> getTool();

    AutoBean<ToolList> getToolList();

    AutoBean<ToolContainer> getContainer();

    AutoBean<ToolDevice> getDevice();

    AutoBean<ToolVolume> getVolume();

    AutoBean<ToolVolumesFrom> getVolumesFrom();

    AutoBean<ToolImage> getImage();

    AutoBean<ToolImportUpdateRequest> getRequest();

    AutoBean<ToolImplementation> getImplementation();

    AutoBean<ToolTestData> getTest();

    AutoBean<ToolImportUpdateRequestList> update();

}
