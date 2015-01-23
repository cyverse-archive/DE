package org.iplantc.de.tags.client.gin;

import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagListPresenterFactory;
import org.iplantc.de.tags.client.gin.factory.TagsViewFactory;
import org.iplantc.de.tags.client.presenter.IplantTagListPresenter;
import org.iplantc.de.tags.client.proxy.TagSuggestionRpcProxy;
import org.iplantc.de.tags.client.views.TagViewImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * @author jstroot
 */
public class TagsGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder()
                    .implement(TagsView.class, TagViewImpl.class)
                    .build(TagsViewFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(TagsView.Presenter.class, IplantTagListPresenter.class)
                    .build(TagListPresenterFactory.class));
        bind(TagsView.TagSuggestionProxy.class).to(TagSuggestionRpcProxy.class);
    }
}
