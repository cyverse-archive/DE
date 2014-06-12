package org.iplantc.de.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppTemplateServices;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.CollaboratorsServiceFacade;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.DeployedComponentServices;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.SystemMessageServiceFacade;
import org.iplantc.de.client.services.ToolRequestServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.services.impl.AnalysisServiceFacadeImpl;
import org.iplantc.de.client.services.impl.AppTemplateServicesImpl;
import org.iplantc.de.client.services.impl.AppUserServiceFacadeImpl;
import org.iplantc.de.client.services.impl.CollaboratorsServiceFacadeImpl;
import org.iplantc.de.client.services.impl.DEFeedbackServiceFacadeImpl;
import org.iplantc.de.client.services.impl.DeployedComponentServicesImpl;
import org.iplantc.de.client.services.impl.DiskResourceServiceFacadeImpl;
import org.iplantc.de.client.services.impl.FileEditorServiceFacadeImpl;
import org.iplantc.de.client.services.impl.MessageServiceFacadeImpl;
import org.iplantc.de.client.services.impl.MetadataServiceFacadeImpl;
import org.iplantc.de.client.services.impl.SearchServiceFacadeImpl;
import org.iplantc.de.client.services.impl.SystemMessageServiceFacadeImpl;
import org.iplantc.de.client.services.impl.ToolRequestServiceFacadeImpl;
import org.iplantc.de.client.services.impl.UserSessionServiceFacadeImpl;
import org.iplantc.de.client.services.stubs.AnalysisServiceFacadeStub;
import org.iplantc.de.client.services.stubs.AppMetadataServiceStub;
import org.iplantc.de.client.services.stubs.AppServiceFacadeStub;
import org.iplantc.de.client.services.stubs.AppTemplateServicesStub;
import org.iplantc.de.client.services.stubs.AppUserServiceFacadeStub;
import org.iplantc.de.client.services.stubs.CollaboratorsServiceFacadeStub;
import org.iplantc.de.client.services.stubs.DEFeedbackServiceFacadeStub;
import org.iplantc.de.client.services.stubs.DeployedComponentServicesStub;
import org.iplantc.de.client.services.stubs.DiskResourceServiceFacadeStub;
import org.iplantc.de.client.services.stubs.FileEditorServiceFacadeStub;
import org.iplantc.de.client.services.stubs.MessageServiceFacadeStub;
import org.iplantc.de.client.services.stubs.MetadataServiceFacadeStub;
import org.iplantc.de.client.services.stubs.SearchServiceFacadeStub;
import org.iplantc.de.client.services.stubs.SystemMessageServiceFacadeStub;
import org.iplantc.de.client.services.stubs.ToolRequestServiceFacadeStub;
import org.iplantc.de.client.services.stubs.UserSessionServiceFacadeStub;
import org.iplantc.de.shared.services.ConfluenceServiceFacade;
import org.iplantc.de.shared.services.EmailServiceFacade;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

final class ServicesModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(DiskResourceServiceFacade.class).to(DiskResourceServiceFacadeImpl.class).in(Singleton.class);
        bind(ToolRequestServiceFacade.class).to(ToolRequestServiceFacadeImpl.class).in(Singleton.class);
        bind(AnalysisServiceFacade.class).to(AnalysisServiceFacadeImpl.class);
        bind(AppTemplateServices.class).to(AppTemplateServicesImpl.class);
        bind(AppMetadataServiceFacade.class).to(AppTemplateServicesImpl.class);
        bind(AppUserServiceFacade.class).to(AppUserServiceFacadeImpl.class);
        bind(AppServiceFacade.class).to(AppUserServiceFacadeImpl.class);
        bind(CollaboratorsServiceFacade.class).to(CollaboratorsServiceFacadeImpl.class);
        bind(DEFeedbackServiceFacade.class).to(DEFeedbackServiceFacadeImpl.class);
        bind(DeployedComponentServices.class).to(DeployedComponentServicesImpl.class);
        bind(FileEditorServiceFacade.class).to(FileEditorServiceFacadeImpl.class);
        bind(MessageServiceFacade.class).to(MessageServiceFacadeImpl.class);
        bind(SearchServiceFacade.class).to(SearchServiceFacadeImpl.class);
        bind(SystemMessageServiceFacade.class).to(SystemMessageServiceFacadeImpl.class);
        bind(UserSessionServiceFacade.class).to(UserSessionServiceFacadeImpl.class);
        bind(MetadataServiceFacade.class).to(MetadataServiceFacadeImpl.class);

        bind(DiskResourceServiceFacade.class).annotatedWith(Stub.class).to(DiskResourceServiceFacadeStub.class);
        bind(ToolRequestServiceFacade.class).annotatedWith(Stub.class).to(ToolRequestServiceFacadeStub.class);
        bind(AnalysisServiceFacade.class).annotatedWith(Stub.class).to(AnalysisServiceFacadeStub.class);
        bind(AppTemplateServices.class).annotatedWith(Stub.class).to(AppTemplateServicesStub.class);
        bind(AppMetadataServiceFacade.class).annotatedWith(Stub.class).to(AppMetadataServiceStub.class);
        bind(AppUserServiceFacade.class).annotatedWith(Stub.class).to(AppUserServiceFacadeStub.class);
        bind(AppServiceFacade.class).annotatedWith(Stub.class).to(AppServiceFacadeStub.class);
        bind(CollaboratorsServiceFacade.class).annotatedWith(Stub.class).to(CollaboratorsServiceFacadeStub.class);
        bind(DEFeedbackServiceFacade.class).annotatedWith(Stub.class).to(DEFeedbackServiceFacadeStub.class);
        bind(DeployedComponentServices.class).annotatedWith(Stub.class).to(DeployedComponentServicesStub.class);
        bind(FileEditorServiceFacade.class).annotatedWith(Stub.class).to(FileEditorServiceFacadeStub.class);
        bind(MessageServiceFacade.class).annotatedWith(Stub.class).to(MessageServiceFacadeStub.class);
        bind(SearchServiceFacade.class).annotatedWith(Stub.class).to(SearchServiceFacadeStub.class);
        bind(SystemMessageServiceFacade.class).annotatedWith(Stub.class).to(SystemMessageServiceFacadeStub.class);
        bind(UserSessionServiceFacade.class).annotatedWith(Stub.class).to(UserSessionServiceFacadeStub.class);
        bind(MetadataServiceFacade.class).annotatedWith(Stub.class).to(MetadataServiceFacadeStub.class);
    }

    @Provides
    public ConfluenceServiceFacade createConfluenceService() {
        return ConfluenceServiceFacade.getInstance();
    }

    @Provides
    EmailServiceFacade createEmailServiceFacade() {
        return EmailServiceFacade.getInstance();
    }

    @Provides
    @Singleton
    public DEServiceFacade createDeServiceFacade() {
        return DEServiceFacade.getInstance();
    }

    @Provides
    @Singleton
    public DEProperties createDeProperties() {
        return DEProperties.getInstance();
    }

    @Provides
    @Singleton
    public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Provides
    @Singleton
    public EventBus createEventBus() {
        return EventBus.getInstance();
    }

}
