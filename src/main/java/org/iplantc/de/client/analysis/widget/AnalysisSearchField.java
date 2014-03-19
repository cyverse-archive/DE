package org.iplantc.de.client.analysis.widget;

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
    private final FilterConfig idFilter;
    private final FilterConfig nameFilter;
    private final FilterConfig appNameFilter;

    public AnalysisSearchField(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader) {
        super(loader);

        idFilter = new FilterConfigBean();
        nameFilter = new FilterConfigBean();
        appNameFilter = new FilterConfigBean();

        idFilter.setField("id"); //$NON-NLS-1$
        nameFilter.setField("name"); //$NON-NLS-1$
        appNameFilter.setField("analysis_name"); //$NON-NLS-1$
    }

    /**
     * Loads the loader with a FilterConfig with the given analysisId, setting the field's text to the
     * given analysisName. This filter will be cleared once this field is cleared or another query is
     * triggered.
     * 
     * @param analysisId
     * @param analysisName
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

    @Override
    protected List<FilterConfig> getConfigFilters(FilterPagingLoadConfig config) {
        List<FilterConfig> filters = super.getConfigFilters(config);

        filters.clear();
        filters.add(nameFilter);
        filters.add(appNameFilter);

        return filters;
    }
}
