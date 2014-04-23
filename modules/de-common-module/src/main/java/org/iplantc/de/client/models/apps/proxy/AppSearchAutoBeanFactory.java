package org.iplantc.de.client.models.apps.proxy;


import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppSearchAutoBeanFactory extends AutoBeanFactory {

    AutoBean<AppListLoadResult> dataLoadResult();

    AutoBean<AppLoadConfig> loadConfig();
}