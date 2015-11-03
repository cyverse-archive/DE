package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.TakesValue;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author Sriram
 */
public interface TemplateAttributeSelectionItem extends HasId, TakesValue<String> {
    void setId(String id);

    @PropertyName("is_default")
    boolean isDefaultValue();

    @PropertyName("is_default")
    void setDefaultValue(boolean isDefault);
}
