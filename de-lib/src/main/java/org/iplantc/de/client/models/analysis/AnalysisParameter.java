/**
 * 
 */
package org.iplantc.de.client.models.analysis;

import org.iplantc.de.client.models.apps.integration.ArgumentType;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * @author sriram
 * 
 */
public interface AnalysisParameter extends Cloneable {

    @PropertyName("param_id")
    void setId(String ig);

    @PropertyName("param_id")
    String getId();

    @PropertyName("param_name")
    void setName(String name);

    @PropertyName("param_name")
    String getName();

    @PropertyName("param_type")
    void setType(ArgumentType type);

    @PropertyName("param_type")
    ArgumentType getType();

    @PropertyName("param_value")
    void setValue(Splittable value);

    @PropertyName("param_value")
    Splittable getValue();

    @PropertyName("info_type")
    String getInfoType();

    @PropertyName("info_type")
    void setInfoType(String infoType);

    @PropertyName("data_format")
    String getDataFormat();

    @PropertyName("data_format")
    void setDataFormat(String format);

    void setDisplayValue(String value);

    String getDisplayValue();
}
