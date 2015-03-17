/**
 * 
 */
package org.iplantc.de.client.models.viewer;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author sriram
 * 
 */
public interface VizUrl {

    @PropertyName("label")
    void setLabel(String label);

    @PropertyName("label")
    String getLabel();

    @PropertyName("url")
    String getUrl();

    @PropertyName("url")
    void setUrl(String url);
}
