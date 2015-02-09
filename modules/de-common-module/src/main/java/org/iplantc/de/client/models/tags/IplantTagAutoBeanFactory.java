package org.iplantc.de.client.models.tags;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface IplantTagAutoBeanFactory extends AutoBeanFactory {

    AutoBean<IplantTagList> getTagList();

    AutoBean<Tag> getTag();

}
