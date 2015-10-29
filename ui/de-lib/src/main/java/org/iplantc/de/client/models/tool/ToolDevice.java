package org.iplantc.de.client.models.tool;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Created by aramsey on 10/30/15.
 */
public interface ToolDevice extends HasId {

    @PropertyName("id")
    void setId(String id);

    @PropertyName("host_path")
    void setHostPath(String hostPath);

    @PropertyName("host_path")
    String getHostPath();

    @PropertyName("container_path")
    void setContainerPath(String containerPath);

    @PropertyName("container_path")
    String getContainerPath();
}
