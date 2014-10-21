package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasLabel;
import org.iplantc.de.client.models.apps.integration.FileParameters;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * An App Property wrapper for Input and Output File parameters.
 * 
 * @author psarando
 * 
 */
public interface AppFileParameters extends HasId, HasLabel, HasDescription {

    String getName();

    void setName(String name);

    String getValue();

    void setValue(String value);

    @PropertyName("isVisible")
    boolean isVisible();

    @PropertyName("isVisible")
    void setVisible(boolean visible);

    String getType();

    void setType(String type);

    @PropertyName("file_parameters")
    FileParameters getFileParameters();

    @PropertyName("file_parameters")
    void setFileParameters(FileParameters dataObj);
}
