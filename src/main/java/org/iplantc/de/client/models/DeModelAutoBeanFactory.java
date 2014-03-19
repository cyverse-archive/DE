package org.iplantc.de.client.models;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface DeModelAutoBeanFactory extends AutoBeanFactory {
    AutoBean<AboutApplicationData> aboutApplicationData();
}
