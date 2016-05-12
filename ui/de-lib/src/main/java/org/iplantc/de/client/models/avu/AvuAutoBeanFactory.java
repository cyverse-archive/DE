package org.iplantc.de.client.models.avu;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author aramsey
 */
public interface AvuAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Avu> getAvu();

    AutoBean<AvuList> getAvuList();
}
