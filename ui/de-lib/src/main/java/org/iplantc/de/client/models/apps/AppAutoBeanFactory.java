package org.iplantc.de.client.models.apps;

import org.iplantc.de.client.models.apps.integration.FileParameters;
import org.iplantc.de.client.models.apps.sharing.AppSharingRequest;
import org.iplantc.de.client.models.apps.sharing.AppSharingRequestList;
import org.iplantc.de.client.models.apps.sharing.AppUnSharingRequestList;
import org.iplantc.de.client.models.apps.sharing.AppUnsharingRequest;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface AppAutoBeanFactory extends AutoBeanFactory {

    AutoBean<App> app();

    AutoBean<AppFeedback> appFeedback();

    AutoBean<PipelineEligibility> pipelineEligibility();

    AutoBean<AppFileParameters> appFileParameters();

    AutoBean<FileParameters> fileParameters();

    AutoBean<AppList> appList();

    AutoBean<AppCategory> appGroup();

    AutoBean<AppCategoryList> appGroups();

    AutoBean<AppRefLink> appRefLink();

    AutoBean<AppDoc> appDoc();

    AutoBean<AppSharingRequest> appSharingRequest();

    AutoBean<AppUnsharingRequest> appUnSharingRequest();

    AutoBean<AppSharingRequestList> appSharingRequestList();

    AutoBean<AppUnSharingRequestList> appUnSharingRequestList();

    AutoBean<PublishAppRequest> publishAppRequest();

}
