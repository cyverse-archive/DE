package org.iplantc.de.client.models.tool;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface Tool extends HasId, HasDescription, HasName {

    @PropertyName("location")
    String getLocation();

    @PropertyName("location")
    void setLocation(String location);

    @PropertyName("type")
    void setType(String type);

    @PropertyName("type")
    String getType();

    @PropertyName("attribution")
    String getAttribution();

    @PropertyName("attribution")
    void setAttribution(String attribution);

    @PropertyName("version")
    void setVersion(String version);

    @PropertyName("version")
    String getVersion();

    @PropertyName("implementation")
    ToolImplementation getImplementation();

    @PropertyName("implementation")
    void setImplementation(ToolImplementation implementation);

    @PropertyName("container")
    ToolContainer getContainer();

    @PropertyName("container")
    void setContainer(ToolContainer container);

}
