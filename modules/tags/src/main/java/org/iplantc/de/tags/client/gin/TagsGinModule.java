package org.iplantc.de.tags.client.gin;

import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagItemFactory;
import org.iplantc.de.tags.client.gin.factory.TagsViewFactory;
import org.iplantc.de.tags.client.presenter.TagsViewPresenterImpl;
import org.iplantc.de.tags.client.proxy.TagSuggestionProxyImpl;
import org.iplantc.de.tags.client.views.TagItemImpl;
import org.iplantc.de.tags.client.views.TagsViewImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * @author jstroot
 */
public class TagsGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder()
                    .implement(TagsView.TagItem.class, TagItemImpl.class)
                    .build(TagItemFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(TagsView.class, TagsViewImpl.class)
                    .build(TagsViewFactory.class));
        bind(TagsView.Presenter.class).to(TagsViewPresenterImpl.class);
        bind(TagsView.TagSuggestionProxy.class).to(TagSuggestionProxyImpl.class);
    }
}
