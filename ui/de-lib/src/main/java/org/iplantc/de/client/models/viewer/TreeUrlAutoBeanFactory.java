/**
 * 
 */
package org.iplantc.de.client.models.viewer;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author sriram
 * 
 */
public interface TreeUrlAutoBeanFactory extends AutoBeanFactory {

    AutoBean<VizUrl> getTreeUrl();

    AutoBean<VizUrlList> getTreeUrlList();
}
