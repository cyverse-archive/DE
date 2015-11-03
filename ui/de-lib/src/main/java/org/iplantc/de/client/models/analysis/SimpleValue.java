package org.iplantc.de.client.models.analysis;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SimpleValue {

    @PropertyName("value")
    String getValue();

    @PropertyName("value")
    void setValue(String value);

}
