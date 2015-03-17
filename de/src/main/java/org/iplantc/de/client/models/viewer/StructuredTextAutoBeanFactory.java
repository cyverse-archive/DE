package org.iplantc.de.client.models.viewer;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface StructuredTextAutoBeanFactory extends AutoBeanFactory {

    AutoBean<StructuredText> getStructuredText();
}
