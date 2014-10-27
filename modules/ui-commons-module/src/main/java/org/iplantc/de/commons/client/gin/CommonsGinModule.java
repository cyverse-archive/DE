package org.iplantc.de.commons.client.gin;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.tags.proxy.TagSuggestionRpcProxy;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CommonsGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(TagSuggestionRpcProxy.class);
    }

    @Provides
    @Singleton
    public TagsServiceFacade createMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getMetadataService();
    }

    @Provides public IplantAnnouncer createAnnouncer() {
        return IplantAnnouncer.getInstance();
    }

}
