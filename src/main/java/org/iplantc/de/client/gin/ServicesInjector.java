package org.iplantc.de.client.gin;

import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.CollaboratorsServiceFacade;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.SystemMessageServiceFacade;
import org.iplantc.de.client.services.ToolRequestProvider;
import org.iplantc.de.client.services.UUIDServiceAsync;
import org.iplantc.de.client.services.UserSessionServiceFacade;

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

    ToolRequestProvider getToolRequestServiceProvider();

    UserSessionServiceFacade getUserSessionServiceFacade();

    UUIDServiceAsync getUUIDService();


}
