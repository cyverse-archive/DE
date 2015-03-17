package org.iplantc.de.client.models.tags;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author jstroot
 */
public interface Tag extends HasId {

    @PropertyName("id")
    void setId(String id);

    @PropertyName("description")
    void setDescription(String desc);

    @PropertyName("description")
    String getDescription();

    @PropertyName("value")
    void setValue(String value);

    @PropertyName("value")
    String getValue();

}
