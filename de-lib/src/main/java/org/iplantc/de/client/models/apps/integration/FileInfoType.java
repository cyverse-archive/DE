package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasLabel;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface FileInfoType extends HasDescription, HasId, HasLabel {

    @PropertyName("name")
    FileInfoTypeEnum getType();

    String getHid();

}
