package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * An Input/Output DataObject AutoBean.
 * 
 * @author psarando
 * 
 */
public interface DataObject extends HasId, HasDescription {

    String getName();

    void setName(String name);

    boolean getRequired();

    void setRequired(boolean required);

    String getFormat();

    void setFormat(String format);

    int getOrder();

    void setOrder(int order);

    @PropertyName("file_info_type")
    String getFileInfoType();

    @PropertyName("file_info_type")
    void setFileInfoType(String file_info_type);

    boolean getRetain();

    void setRetain(boolean retain);

    String getMultiplicity();

    void setMultiplicity(String multiplicity);

    String getCmdSwitch();

    void setCmdSwitch(String cmdSwitch);
}
