package org.iplantc.de.client.models.analysis;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * A class to model selection param type values
 * 
 */
public interface SelectionValue {

    @PropertyName("isDefault")
    void setDefault(String def);

    @PropertyName("isDefault")
    String getDefault();

    @PropertyName("name")
    String getName();

    @PropertyName("name")
    void setName(String name);

    @PropertyName("value")
    String getValue();

    @PropertyName("value")
    void setValue(String value);

    @PropertyName("display")
    void setDisplay(String value);

    @PropertyName("display")
    String getDisplay();

}
