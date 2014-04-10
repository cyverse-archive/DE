package org.iplantc.de.analysis.client.gin;


import org.iplantc.de.analysis.client.presenter.AnalysesPresenterImpl;
import org.iplantc.de.analysis.client.presenter.proxy.AnalysisRpcProxy;
import org.iplantc.de.analysis.client.views.*;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;

public class AnalysisModule extends AbstractGinModule {

    private static class AnalysisModelKeyProvider implements ModelKeyProvider<Analysis> {
        @Override
        public String getKey(Analysis item) {
            return item.getId();
        }
    }

    private CheckBoxSelectionModel<Analysis> analysisCheckBoxSelectionModel = new CheckBoxSelectionModel<>(new IdentityValueProvider<Analysis>());

    @Override
    protected void configure() {
        bind(AnalysesView.class).to(AnalysesViewImpl.class);
        bind(AnalysesView.ViewMenu.class).to(AnalysesViewMenuImpl.class);
        bind(AnalysisColumnModel.class);
        bind(AnalysisParamViewColumnModel.class);
        bind(AnalysesView.Presenter.class).to(AnalysesPresenterImpl.class);
        bind(AnalysisRpcProxy.class);
    }

    @Provides
    @Singleton
    public CheckBoxSelectionModel<Analysis> createCheckboxSelectionModel(){
        return analysisCheckBoxSelectionModel;
    }

    @Provides
    public ListStore<Analysis> createAnalysisListStore(){
        return new ListStore<>(new AnalysisModelKeyProvider());
    }
}
