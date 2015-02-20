package org.iplantc.de.analysis.client.gin;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisToolBarView;
import org.iplantc.de.analysis.client.gin.factory.AnalysesViewFactory;
import org.iplantc.de.analysis.client.gin.factory.AnalysisParamViewFactory;
import org.iplantc.de.analysis.client.gin.factory.AnalysisToolBarFactory;
import org.iplantc.de.analysis.client.presenter.AnalysesPresenterImpl;
import org.iplantc.de.analysis.client.presenter.proxy.AnalysisRpcProxy;
import org.iplantc.de.analysis.client.views.AnalysesToolBarImpl;
import org.iplantc.de.analysis.client.views.AnalysesViewImpl;
import org.iplantc.de.analysis.client.views.AnalysisColumnModel;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamViewColumnModel;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author jstroot
 */
public class AnalysisGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ListStore<Analysis>>() {}).toProvider(AnalysisModuleListStoreProvider.class);
        bind(AnalysisColumnModel.class);
        bind(AnalysisParamViewColumnModel.class);
        bind(AnalysesView.Presenter.class).to(AnalysesPresenterImpl.class);
        bind(AnalysisRpcProxy.class);

        install(new GinFactoryModuleBuilder()
                    .implement(AnalysesView.class, AnalysesViewImpl.class)
                    .build(AnalysesViewFactory.class));
        install(new GinFactoryModuleBuilder()
                    .implement(AnalysisToolBarView.class, AnalysesToolBarImpl.class)
                    .build(AnalysisToolBarFactory.class));
        install(new GinFactoryModuleBuilder()
                    .build(AnalysisParamViewFactory.class));
    }
}
