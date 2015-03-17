/**
 * 
 */
package org.iplantc.de.client.models.viewer;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface VizUrlList {

    @PropertyName("urls")
    List<VizUrl> getUrls();

}
