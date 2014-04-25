package org.iplantc.de.analysis.client.views;

import org.iplantc.de.analysis.client.events.AnalysisAppSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisCommentSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.AnalysisParamValueSelectedEvent;
import org.iplantc.de.analysis.client.events.SaveAnalysisParametersEvent;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import java.util.List;

/**
 *
 * @author sriram
 * 
 */
@SuppressWarnings("rawtypes")
public interface AnalysesView extends IsWidget, SelectionChangedEvent.HasSelectionChangedHandlers, AnalysisParamValueSelectedEvent.HasAnalysisParamValueSelectedEventHandlers, AnalysisAppSelectedEvent.HasAnalysisAppSelectedEventHandlers, AnalysisCommentSelectedEvent.HasAnalysisCommentSelectedEventHandlers, AnalysisNameSelectedEvent.HasAnalysisNameSelectedEventHandlers {

    public interface Presenter extends SaveAnalysisParametersEvent.SaveAnalysisParametersEventHandler {

        void cancelSelectedAnalyses();

        void deleteSelectedAnalyses();

        List<Analysis> getSelectedAnalyses();

        void go(final HasOneWidget container, List<Analysis> selectedAnalyses);

        void goToSelectedAnalysisFolder();

        void relaunchSelectedAnalysis();

        void renameSelectedAnalysis();

        void retrieveParameterData(Analysis analysis, AnalysisParamView apv);

        void setSelectedAnalyses(List<Analysis> selectedAnalyses);

        void setViewDebugId(String baseId);

        void updateComments();

        void loadAnalyses();

    }

    public interface ViewMenu extends IsWidget, SelectionChangedEvent.SelectionChangedHandler<Analysis> {

        void filterByAnalysisId(String analysisId, String name);

        void init(Presenter presenter, AnalysesView parent, PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader);
    }

    void filterByAnalysisId(String id, String name);

    public void loadAnalyses();

    public List<Analysis> getSelectedAnalyses();

    void setPresenter(Presenter presenter);

    public void setSelectedAnalyses(List<Analysis> selectedAnalyses);

    public void removeFromStore(List<Analysis> items);

    public ListStore<Analysis> getListStore();

    public HandlerRegistration addLoadHandler(
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> handler);

    void viewParams();
}
