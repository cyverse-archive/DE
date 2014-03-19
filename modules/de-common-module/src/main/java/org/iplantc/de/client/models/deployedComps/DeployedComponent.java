package org.iplantc.de.client.models.deployedComps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface DeployedComponent extends HasId, HasDescription, HasName {

    @PropertyName("hid")
    String getHid();
    
    @PropertyName("hid")
    void setHid(String hid);

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
    
}
