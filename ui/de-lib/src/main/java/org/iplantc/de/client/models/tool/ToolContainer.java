package org.iplantc.de.client.models.tool;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author aramsey
 */


public interface ToolContainer extends HasName {

    @PropertyName("container")
    ToolContainer getToolContainer();

    @PropertyName("container")
    void setToolContainer(ToolContainer container);

    @PropertyName("working_directory")
    String getWorkingDirectory();

    @PropertyName("working_directory")
    void setWorkingDirectory(String directory);

    @PropertyName("entrypoint")
    String getEntryPoint();

    @PropertyName("entrypoint")
    void setEntryPoint(String entryPoint);

    @PropertyName("memory_limit")
    Integer getMemoryLimit();

    @PropertyName("memory_limit")
    void setMemoryLimit(Integer memoryLimit);

    @PropertyName("cpu_shares")
    Integer getCpuShares();

    @PropertyName("cpu_shares")
    void setCpuShares(Integer cpuShares);

    @PropertyName("network_mode")
    String getNetworkMode();

    @PropertyName("network_mode")
    void setNetworkMode(String networkMode);

    @PropertyName("container_devices")
    List<ToolDevice> getDeviceList();

    @PropertyName("container_devices")
    void setDeviceList(List<ToolDevice> devices);

    @PropertyName("container_volumes")
    List<ToolVolume> getContainerVolumes();

    @PropertyName("container_volumes")
    void setContainerVolumes(List<ToolVolume> containerVolumes);

    @PropertyName("image")
    ToolImage getImage();

    @PropertyName("image")
    void setImage(ToolImage toolImage);

    @PropertyName("container_volumes_from")
    List<ToolVolumesFrom> getContainerVolumesFrom();

    @PropertyName("container_volumes_from")
    void setContainerVolumesFrom(List<ToolVolumesFrom> toolVolumesFroms);
}
