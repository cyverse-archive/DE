package org.iplantc.de.commons.client.gin;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.services.MetadataServiceFacade;
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
    public MetadataServiceFacade createMetadataServiceFacade() {
        return ServicesInjector.INSTANCE.getMetadataService();
    }

}
