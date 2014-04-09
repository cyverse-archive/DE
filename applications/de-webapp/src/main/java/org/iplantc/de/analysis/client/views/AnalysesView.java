package org.iplantc.de.analysis.client.views;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import java.util.List;

/**
 * 
 * 
 * 
 * @author sriram
 * 
 */
public interface AnalysesView extends IsWidget, SelectionChangedEvent.HasSelectionChangedHandlers {
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter, SelectionChangedEvent.SelectionChangedHandler<Analysis> {

        void go(final HasOneWidget container, List<Analysis> selectedAnalyses);

        List<Analysis> getSelectedAnalyses();

        void setSelectedAnalyses(List<Analysis> selectedAnalyses);

        void setViewDebugId(String baseId);
    }

    public interface ViewMenu extends IsWidget {
        void setDeleteButtonEnabled(boolean enabled);

        void setViewParamButtonEnabled(boolean enabled);

        void setCancelButtonEnabled(boolean enabled);

        void setRelaunchAnalysisEnabled(boolean enabled);
    }

    ViewMenu getViewMenu();

    public void loadAnalyses();

    public List<Analysis> getSelectedAnalyses();

    public void setSelectedAnalyses(List<Analysis> selectedAnalyses);

    public void removeFromStore(List<Analysis> items);

    public ListStore<Analysis> getListStore();

    public HandlerRegistration addLoadHandler(
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> handler);
}
