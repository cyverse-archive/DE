package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasLabel;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * An App Property wrapper for Input and Output File parameters.
 * 
 * @author psarando
 * 
 */
public interface AppFileParameters extends HasId, HasLabel, HasDescription, HasName {

    String getValue();

    void setValue(String value);

    @PropertyName("isVisible")
    boolean isVisible();

    @PropertyName("isVisible")
    void setVisible(boolean visible);

    String getType();

    void setType(String type);

    String getFormat();

    void setFormat(String format);

    @PropertyName("required")
    boolean getRequired();

    @PropertyName("required")
    void setRequired(boolean required);
}
