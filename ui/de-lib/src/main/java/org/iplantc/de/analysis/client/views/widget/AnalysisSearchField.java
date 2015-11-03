package org.iplantc.de.analysis.client.views.widget;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.widgets.SearchField;

import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

import java.util.List;

/**
 * A SearchField that filters Analyses by Name and App Name simultaneously, or alternatively by Analysis
 * ID. The Analysis ID filter will be cleared once this field is cleared or another query is triggered.
 * 
 * @author psarando
 * 
 */
public class AnalysisSearchField extends SearchField<Analysis> {
    public static final String PARENT_ID = "parent_id";
    public static final String APP_NAME = "app_name";
    public static final String NAME = "name";
    public static final String ID = "id";
    private final FilterConfig idFilter;
    private final FilterConfig nameFilter;
    private final FilterConfig appNameFilter;
    private final FilterConfig idParentFilter;

    public AnalysisSearchField(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader) {
        super(loader);

        idFilter = new FilterConfigBean();
        nameFilter = new FilterConfigBean();
        appNameFilter = new FilterConfigBean();
        idParentFilter = new FilterConfigBean();

        idFilter.setField(ID); //$NON-NLS-1$
        nameFilter.setField(NAME); //$NON-NLS-1$
        appNameFilter.setField(APP_NAME); //$NON-NLS-1$
        idParentFilter.setField(PARENT_ID); //$NON-NLS-1$
    }

    /**
     * Loads the loader with a FilterConfig with the given analysisId, setting the field's text to the
     * given analysisName. This filter will be cleared once this field is cleared or another query is
     * triggered.
     */
    public void filterByAnalysisId(String analysisId, String analysisName) {
        setValue(analysisName);
        idFilter.setValue(analysisId);

        FilterPagingLoadConfig loadConfig = getParams(analysisId);
        List<FilterConfig> filters = super.getConfigFilters(loadConfig);
        filters.clear();
        filters.add(idFilter);

        loader.load(loadConfig);
    }

    /**
     * Loads the loader with a FilterConfig with the given parent analysisId of the HT Analysis, This
     * filter will be cleared once this field is cleared or another query is triggered.
     */
    public void filterByParentId(String analysisId) {
        FilterPagingLoadConfig loadConfig = getLoaderConfig();
        List<FilterConfig> filters = super.getConfigFilters(loadConfig);
        filters.clear();
        idParentFilter.setValue(analysisId);
        filters.add(idParentFilter);

        loader.load(loadConfig);

    }

    @Override
    protected void clearFilter() {
        // by default we need parent id to be empty
        filterByParentId("");
        setValue("");
    }

    @Override
    protected List<FilterConfig> getConfigFilters(FilterPagingLoadConfig config) {
        List<FilterConfig> filters = super.getConfigFilters(config);

        filters.clear();
        filters.add(nameFilter);
        filters.add(appNameFilter);

        return filters;
    }
}
