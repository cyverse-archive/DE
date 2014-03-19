package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.DELETE;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PUT;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

import java.util.List;

/**
 * Provides access to remote services for analyses management operations.
 */
public class AnalysisServiceFacadeImpl implements AnalysisServiceFacade {

    private final DEProperties deProperties;
    private final DEServiceFacade deServiceFacade;

    @Inject
    public AnalysisServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProperties) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.AnalysisServiceFacade#getAnalyses(java.lang.String, com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getAnalyses(String workspaceId, FilterPagingLoadConfig loadConfig,
            AsyncCallback<String> callback) {
        StringBuilder address = new StringBuilder(deProperties.getMuleServiceBaseUrl());

        address.append("workspaces/"); //$NON-NLS-1$
        address.append(workspaceId);
        address.append("/executions/list"); //$NON-NLS-1$

        if (loadConfig != null) {
            address.append("?limit="); //$NON-NLS-1$
            address.append(loadConfig.getLimit());

            address.append("&offset="); //$NON-NLS-1$
            address.append(loadConfig.getOffset());

            List<? extends SortInfo> sortInfoList = loadConfig.getSortInfo();
            if (sortInfoList != null && !sortInfoList.isEmpty()) {
                SortInfo sortInfo = sortInfoList.get(0);

                String sortField = sortInfo.getSortField();
                if (!Strings.isNullOrEmpty(sortField)) {
                    address.append("&sort-field="); //$NON-NLS-1$
                    address.append(sortField);
                }

                SortDir sortDir = sortInfo.getSortDir();
                if (sortDir == SortDir.ASC || sortDir == SortDir.DESC) {
                    address.append("&sort-order="); //$NON-NLS-1$
                    address.append(sortDir.toString());
                }
            }

            List<FilterConfig> filters = loadConfig.getFilters();
            if (filters != null && !filters.isEmpty()) {
                JSONArray jsonFilters = new JSONArray();
                int filterIndex = 0;

                for (FilterConfig filter : filters) {
                    String field = filter.getField();
                    String value = filter.getValue();

                    if (!Strings.isNullOrEmpty(field) && !Strings.isNullOrEmpty(value)) {
                        JSONObject jsonFilter = new JSONObject();

                        jsonFilter.put("field", new JSONString(field)); //$NON-NLS-1$
                        jsonFilter.put("value", new JSONString(value)); //$NON-NLS-1$

                        jsonFilters.set(filterIndex++, jsonFilter);
                    }
                }

                if (jsonFilters.size() > 0) {
                    address.append("&filter="); //$NON-NLS-1$
                    address.append(URL.encodeQueryString(jsonFilters.toString()));
                }
            }
        }

        ServiceCallWrapper wrapper = new ServiceCallWrapper(address.toString());
        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.AnalysisServiceFacade#deleteAnalysis(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void deleteAnalysis(String workspaceId, String json, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "workspaces/" //$NON-NLS-1$
                + workspaceId + "/executions" + "/delete"; //$NON-NLS-1$ //$NON-NLS-2$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, json);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.AnalysisServiceFacade#stopAnalysis(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void stopAnalysis(String analysisId, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "stop-analysis/"
                + analysisId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.AnalysisServiceFacade#getAnalysisParams(java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void getAnalysisParams(String analysisId, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl()
                + "get-property-values/" + analysisId;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.AnalysisServiceFacade#launchAnalysis(java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void launchAnalysis(String workspaceId, String json, AsyncCallback<String> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "workspaces/" //$NON-NLS-1$
                + workspaceId + "/newexperiment"; //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, json);

        deServiceFacade.getServiceData(wrapper, callback);
    }

    /* (non-Javadoc)
     * @see org.iplantc.de.client.services.impl.AnalysisServiceFacade#relaunchAnalysis(org.iplantc.de.client.models.HasId, com.google.gwt.user.client.rpc.AsyncCallback)
     */
    @Override
    public void relaunchAnalysis(HasId analyisId, AsyncCallback<String> callback) {
        String address = deProperties.getUnproctedMuleServiceBaseUrl() + "analysis-rerun-info/" + analyisId.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, callback);
    }

}
