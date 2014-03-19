package org.iplantc.de.client.models.apps.proxy;


import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppSearchAutoBeanFactory extends AutoBeanFactory {
    static AppSearchAutoBeanFactory instance = GWT.create(AppSearchAutoBeanFactory.class);

    AutoBean<AppListLoadResult> dataLoadResult();

    AutoBean<AppLoadConfig> loadConfig();
}