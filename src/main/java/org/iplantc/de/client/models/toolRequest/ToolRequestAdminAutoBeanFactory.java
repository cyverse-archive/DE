package org.iplantc.de.client.models.toolRequest;

import com.google.web.bindery.autobean.shared.AutoBean;

public interface ToolRequestAdminAutoBeanFactory extends ToolRequestAutoBeanFactory {

    AutoBean<ToolRequestList> toolRequestList();

}
