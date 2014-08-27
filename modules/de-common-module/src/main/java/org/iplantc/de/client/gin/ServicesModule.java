package org.iplantc.de.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.*;
import org.iplantc.de.client.services.impl.*;
import org.iplantc.de.client.services.stubs.*;
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
        bind(TagsServiceFacade.class).to(TagsServiceFacadeImpl.class);
        bind(MetadataServiceFacade.class).to(FileSystemMetadataServiceFacadeImpl.class);

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
        bind(TagsServiceFacade.class).annotatedWith(Stub.class).to(MetadataServiceFacadeStub.class);
    }

    @Provides
    EmailServiceFacade createEmailServiceFacade() {
        return EmailServiceFacade.getInstance();
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
