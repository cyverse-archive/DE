package org.iplantc.de.client.models.tags;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface IplantTagList {

    @PropertyName("suggestions")
    List<IplantTag> getTagList();

}
