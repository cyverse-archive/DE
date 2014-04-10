package org.iplantc.de.client.gin;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.services.AnalysisServiceFacade;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class DEGinModule extends AbstractGinModule {
    @Provides
    @Singleton
    public EventBus createGlobalEventBus(){

        return EventBus.getInstance();
    }

    @Provides
    @Singleton
    public UserInfo createUserInfo() {
        return UserInfo.getInstance();
    }

    @Override
    protected void configure() {

    }

    @Provides
    public AnalysisServiceFacade createAnalysisService() {
        return ServicesInjector.INSTANCE.getAnalysisServiceFacade();
    }
}
