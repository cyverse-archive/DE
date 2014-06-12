package org.iplantc.de.commons.client.tags.models;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface IplantTagList {

    @PropertyName("suggestions")
    List<IplantTag> getTagList();

}
