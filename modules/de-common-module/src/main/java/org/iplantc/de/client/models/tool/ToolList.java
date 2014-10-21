/**
 * 
 */
package org.iplantc.de.client.models.tool;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface ToolList {

    @PropertyName("tools")
    List<Tool> getToolList();

    @PropertyName("tools")
    void setToolList(List<Tool> tools);

}
