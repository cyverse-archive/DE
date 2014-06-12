package org.iplantc.de.commons.client.tags.models;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface IpalntTagAutoBeanFactory extends AutoBeanFactory {

    AutoBean<IplantTagList> getTagList();

    AutoBean<IplantTag> getTag();

}
