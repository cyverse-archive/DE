package org.iplantc.de.client.models.tool;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Created by aramsey on 11/4/15.
 */
public interface ToolImportUpdateRequestList {

    @PropertyName("tools")
    List<ToolImportUpdateRequest> getToolImportList();

    @PropertyName("tools")
    void setToolImportList(List<ToolImportUpdateRequest> toolImportList);
}
