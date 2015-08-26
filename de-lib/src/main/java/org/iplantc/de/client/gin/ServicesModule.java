package org.iplantc.de.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.*;
import org.iplantc.de.client.services.impl.*;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.shared.services.DiscEnvApiService;

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
        bind(AppBuilderMetadataServiceFacade.class).to(AppTemplateServicesImpl.class);
        bind(AppUserServiceFacade.class).to(AppUserServiceFacadeImpl.class);
        bind(AppServiceFacade.class).to(AppUserServiceFacadeImpl.class);
        bind(CollaboratorsServiceFacade.class).to(CollaboratorsServiceFacadeImpl.class);
        bind(DEFeedbackServiceFacade.class).to(DEFeedbackServiceFacadeImpl.class);
        bind(ToolServices.class).to(ToolServicesImpl.class);
        bind(FileEditorServiceFacade.class).to(FileEditorServiceFacadeImpl.class);
        bind(MessageServiceFacade.class).to(MessageServiceFacadeImpl.class);
        bind(SearchServiceFacade.class).to(SearchServiceFacadeImpl.class);
        bind(SystemMessageServiceFacade.class).to(SystemMessageServiceFacadeImpl.class);
        bind(UserSessionServiceFacade.class).to(UserSessionServiceFacadeImpl.class);
        bind(TagsServiceFacade.class).to(TagsServiceFacadeImpl.class);
        bind(FileSystemMetadataServiceFacade.class).to(FileSystemMetadataServiceFacadeImpl.class);
        bind(AppMetadataServiceFacade.class).to(AppMetadataServiceFacadeImpl.class);

        bind(DiscEnvApiService.class).in(Singleton.class);
    }

    @Provides public JsonUtil createJsonUtil() {
        return JsonUtil.getInstance();
    }

    @Provides public AppTemplateUtils createAppTemplateUtils() {
        return AppTemplateUtils.getInstance();
    }

    @Provides public DiskResourceUtil createDiskResourceUtil() {
        return DiskResourceUtil.getInstance();
    }

    @Provides @Singleton public DEProperties createDeProperties() {
        return DEProperties.getInstance();
    }

    @Provides @Singleton public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Provides @Singleton public EventBus createEventBus() {
        return EventBus.getInstance();
    }

}
