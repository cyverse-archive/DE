/**
 * 
 */
package org.iplantc.de.client.models.deployedComps;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author sriram
 *
 */
public interface DeployedComponentAutoBeanFactory extends AutoBeanFactory {

    AutoBean<DeployedComponent> getDeployedComponent();

    AutoBean<DeployedComponentList> getDeployedComponentList();
}
