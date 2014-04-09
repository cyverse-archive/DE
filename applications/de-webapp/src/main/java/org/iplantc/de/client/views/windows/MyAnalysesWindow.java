package org.iplantc.de.client.views.windows;

import org.iplantc.de.analysis.client.views.AnalysesView;
import org.iplantc.de.analysis.shared.AnalysisModule;
import org.iplantc.de.client.gin.DEInjector;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.views.windows.configs.AnalysisWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * @author sriram
 * 
 */
public class MyAnalysesWindow extends IplantWindowBase {

    private final AnalysesView.Presenter presenter;

    public MyAnalysesWindow(AnalysisWindowConfig config) {
        super(null, null);
        setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.analyses());
        setSize("670", "375");

        presenter = DEInjector.INSTANCE.getAnalysesViewPresenter();

        presenter.go(this, config.getSelectedAnalyses());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        presenter.setViewDebugId(baseID + AnalysisModule.Ids.ANALYSES_VIEW);
    }

    @Override
    public WindowState getWindowState() {
        AnalysisWindowConfig config = ConfigFactory.analysisWindowConfig();
        List<Analysis> selectedAnalyses = Lists.newArrayList();
        selectedAnalyses.addAll(presenter.getSelectedAnalyses());
        config.setSelectedAnalyses(selectedAnalyses);
        return createWindowState(config);
    }

    @Override
    public <C extends WindowConfig> void update(C config) {
        super.update(config);

        if (config instanceof AnalysisWindowConfig) {
            AnalysisWindowConfig analysisWindowConfig = (AnalysisWindowConfig)config;
            presenter.setSelectedAnalyses(analysisWindowConfig.getSelectedAnalyses());
        }
    }
}
