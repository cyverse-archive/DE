package org.iplantc.de.client.models.tool;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Created by aramsey on 10/30/15.
 */
public interface ToolImportUpdateRequest extends HasId, HasDescription, HasName {

    @PropertyName("type")
    void setType(String type);

    @PropertyName("type")
    String getType();

    @PropertyName("attribution")
    void setAttribution(String attribution);

    @PropertyName("attribution")
    String getAttribution();

    @PropertyName("version")
    void setVersion(String version);

    @PropertyName("version")
    String getVersion();

    @PropertyName("location")
    void setLocation(String location);

    @PropertyName("location")
    String getLocation();

    @PropertyName("implementation")
    ToolImplementation getImplementation();

    @PropertyName("implementation")
    void setImplementation(ToolImplementation implementation);

    @PropertyName("container")
    ToolContainer getContainer();

    @PropertyName("container")
    void setContainer(ToolContainer container);

}
