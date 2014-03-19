package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface AppGroup extends HasId, HasName, HasDescription {

    @PropertyName("template_count")
    int getAppCount();

    List<AppGroup> getGroups();

    @PropertyName("is_public")
    boolean isPublic();

    @PropertyName("workspace_id")
    String getWorkspaceId();

    @PropertyName("template_count")
    void setAppCount(int templateCount);

    void setGroups(List<AppGroup> groups);

    @PropertyName("is_public")
    void setIsPublic(boolean isPublic);

    @PropertyName("workspace_id")
    void setWorkspaceId(String workspaceId);

    void setId(String id);

}
