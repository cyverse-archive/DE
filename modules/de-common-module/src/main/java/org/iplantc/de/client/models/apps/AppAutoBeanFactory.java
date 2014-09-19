package org.iplantc.de.client.models.apps;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppAutoBeanFactory extends AutoBeanFactory {

    AutoBean<App> app();

    AutoBean<AppFeedback> appFeedback();

    AutoBean<PipelineEligibility> pipelineEligibility();

    AutoBean<AppDataObject> appDataObject();

    AutoBean<DataObject> dataObject();

    AutoBean<AppList> appList();

    AutoBean<AppCategory> appGroup();

    AutoBean<AppCategoryList> appGroups();

    AutoBean<AppRefLink> appRefLink();
}
