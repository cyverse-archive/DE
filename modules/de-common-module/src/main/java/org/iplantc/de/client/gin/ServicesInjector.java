package org.iplantc.de.client.gin;

import org.iplantc.de.client.services.*;
import org.iplantc.de.client.services.ToolRequestServiceFacade;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(ServicesModule.class)
public interface ServicesInjector extends Ginjector {

    final ServicesInjector INSTANCE = GWT.create(ServicesInjector.class);

    AnalysisServiceFacade getAnalysisServiceFacade();

    AppServiceFacade getAppServiceFacade();

    AppTemplateServices getAppTemplateServices();

    AppMetadataServiceFacade getAppMetadataService();

    AppUserServiceFacade getAppUserServiceFacade();

    CollaboratorsServiceFacade getCollaboratorsServiceFacade();

    DEFeedbackServiceFacade getDeFeedbackServiceFacade();

    DeployedComponentServices getDeployedComponentServices();

    DiskResourceServiceFacade getDiskResourceServiceFacade();

    FileEditorServiceFacade getFileEditorServiceFacade();

    MessageServiceFacade getMessageServiceFacade();

    SearchServiceFacade getSearchServiceFacade();

    SystemMessageServiceFacade getSystemMessageServiceFacade();

    ToolRequestServiceFacade getToolRequestServiceProvider();

    UserSessionServiceFacade getUserSessionServiceFacade();

    UUIDServiceAsync getUUIDService();


}
