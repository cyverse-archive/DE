package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.analysis.presenter.AnalysesPresenter;
import org.iplantc.de.client.analysis.views.AnalysesView;
import org.iplantc.de.client.analysis.views.AnalysesViewImpl;
import org.iplantc.de.client.analysis.views.cells.AnalysisAppNameCell;
import org.iplantc.de.client.analysis.views.cells.AnalysisCommentCell;
import org.iplantc.de.client.analysis.views.cells.AnalysisNameCell;
import org.iplantc.de.client.analysis.views.cells.EndDateTimeCell;
import org.iplantc.de.client.analysis.views.cells.StartDateTimeCell;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.views.windows.configs.AnalysisWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sriram
 * 
 */
public class MyAnalysesWindow extends IplantWindowBase {

    private CheckBoxSelectionModel<Analysis> checkBoxModel;
    private final EventBus eventBus;
    private final AnalysesView.Presenter presenter;

    public MyAnalysesWindow(AnalysisWindowConfig config, EventBus eventBus) {
        super(null, null);

        this.eventBus = eventBus;

        setTitle(org.iplantc.de.resources.client.messages.I18N.DISPLAY.analyses());
        setSize("670", "375");

        AnalysisKeyProvider provider = new AnalysisKeyProvider();
        ListStore<Analysis> listStore = new ListStore<Analysis>(provider);
        AnalysesView view = new AnalysesViewImpl(listStore, buildColumnModel(), checkBoxModel);
        presenter = new AnalysesPresenter(view, eventBus);

        presenter.go(this, config.getSelectedAnalyses());
    }

    @SuppressWarnings("unchecked")
    private ColumnModel<Analysis> buildColumnModel() {
        AnalysisProperties props = GWT.create(AnalysisProperties.class);

        List<ColumnConfig<Analysis, ?>> configs = new LinkedList<ColumnConfig<Analysis, ?>>();

        IdentityValueProvider<Analysis> valueProvider = new IdentityValueProvider<Analysis>();
        checkBoxModel = new CheckBoxSelectionModel<Analysis>(valueProvider);
        @SuppressWarnings("rawtypes")
        ColumnConfig colCheckBox = checkBoxModel.getColumn();
        configs.add(colCheckBox);

        ColumnConfig<Analysis, Analysis> name = new ColumnConfig<Analysis, Analysis>(valueProvider, 100);
        name.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.name());
        configs.add(name);
        name.setMenuDisabled(true);
        name.setCell(new AnalysisNameCell(eventBus));

        ColumnConfig<Analysis, Analysis> comment = new ColumnConfig<Analysis, Analysis>(valueProvider, 30);
        configs.add(comment);
        comment.setMenuDisabled(true);
        comment.setCell(new AnalysisCommentCell());

        ColumnConfig<Analysis, Analysis> app = new ColumnConfig<Analysis, Analysis>(valueProvider, 100);
        app.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.appName());
        configs.add(app);
        app.setMenuDisabled(true);
        app.setCell(new AnalysisAppNameCell(eventBus));

        ColumnConfig<Analysis, Analysis> startdate = new ColumnConfig<Analysis, Analysis>(valueProvider,
                150);
        startdate.setCell(new StartDateTimeCell());
        startdate.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.startDate());
        configs.add(startdate);

        ColumnConfig<Analysis, Analysis> enddate = new ColumnConfig<Analysis, Analysis>(valueProvider,
                150);
        enddate.setCell(new EndDateTimeCell());
        enddate.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.endDate());
        configs.add(enddate);

        ColumnConfig<Analysis, String> status = new ColumnConfig<Analysis, String>(props.status(), 100);
        status.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.status());
        configs.add(status);
        status.setMenuDisabled(true);

        return new ColumnModel<Analysis>(configs);

    }

    @Override
    public WindowState getWindowState() {
        AnalysisWindowConfig config = ConfigFactory.analysisWindowConfig();
        List<Analysis> selectedAnalyses = Lists.newArrayList();
        selectedAnalyses.addAll(presenter.getSelectedAnalyses());
        config.setSelectedAnalyses(selectedAnalyses);
        return createWindowState(config);
    }

    private class AnalysisKeyProvider implements ModelKeyProvider<Analysis> {

        @Override
        public String getKey(Analysis item) {
            return item.getId();
        }

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
