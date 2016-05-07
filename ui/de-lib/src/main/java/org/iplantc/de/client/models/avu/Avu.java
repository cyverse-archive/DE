package org.iplantc.de.client.models.avu;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author aramsey
 */
public interface Avu extends HasId {

    @PropertyName("modified_on")
    Integer getModifiedOn();

    @PropertyName("modified_by")
    String getModifiedBy();

    String getUnit();

    void setUnit(String unit);

    String getValue();

    void setValue(String value);

    @PropertyName("target_id")
    String getTargetId();

    @PropertyName("created_on")
    Integer getCreatedOn();

    @PropertyName("created_by")
    String getCreatedBy();

    @PropertyName("attr")
    void setAttr(String attribute);

    @PropertyName("attr")
    String getAttr();

}
