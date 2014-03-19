package org.iplantc.de.client.analysis.views;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;

import java.util.List;

/**
 * 
 * 
 * 
 * @author sriram
 * 
 */
public interface AnalysesView extends IsWidget {
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void go(final HasOneWidget container, List<Analysis> selectedAnalyses);

        void onAnalysesSelection(List<Analysis> selectedItems);
        
        List<Analysis> getSelectedAnalyses();

        void setSelectedAnalyses(List<Analysis> selectedAnalyses);
    }

    public void setPresenter(final Presenter presenter);

    void setNorthWidget(IsWidget widget);

    public void loadAnalyses();

    public List<Analysis> getSelectedAnalyses();

    public void setSelectedAnalyses(List<Analysis> selectedAnalyses);

    public void removeFromStore(List<Analysis> items);

    public ListStore<Analysis> getListStore();

    public void setLoader(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader);

    public TextButton getRefreshButton();

    public HandlerRegistration addLoadHandler(
            LoadHandler<FilterPagingLoadConfig, PagingLoadResult<Analysis>> handler);
}
