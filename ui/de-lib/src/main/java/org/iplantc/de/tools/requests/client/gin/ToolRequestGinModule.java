package org.iplantc.de.tools.requests.client.gin;

import org.iplantc.de.tools.requests.client.gin.factory.NewToolRequestFormPresenterFactory;
import org.iplantc.de.tools.requests.client.gin.factory.NewToolRequestFormViewFactory;
import org.iplantc.de.tools.requests.client.presenter.NewToolRequestFormPresenterImpl;
import org.iplantc.de.tools.requests.client.views.NewToolRequestFormView;
import org.iplantc.de.tools.requests.client.views.NewToolRequestFormViewImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

public class ToolRequestGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder()
                    .implement(NewToolRequestFormView.class, NewToolRequestFormViewImpl.class)
                    .build(NewToolRequestFormViewFactory.class));

        install(new GinFactoryModuleBuilder()
                    .implement(NewToolRequestFormView.Presenter.class, NewToolRequestFormPresenterImpl.class)
                    .build(NewToolRequestFormPresenterFactory.class));
    }
}
