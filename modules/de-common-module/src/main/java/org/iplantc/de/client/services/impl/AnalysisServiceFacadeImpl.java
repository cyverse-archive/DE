package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.*;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Provides access to remote services for analyses management operations.
 */
public class AnalysisServiceFacadeImpl implements AnalysisServiceFacade {

    private class StringListAsyncCallbackConverter extends AsyncCallbackConverter<String, List<AnalysisParameter>> {
        private final AnalysesAutoBeanFactory factory;

        public StringListAsyncCallbackConverter(AsyncCallback<List<AnalysisParameter>> callback, AnalysesAutoBeanFactory factory) {
            super(callback);
            this.factory = factory;
        }

        @Override
        protected List<AnalysisParameter> convertFrom(String object) {
            AnalysisParametersList as = AutoBeanCodex.decode(factory, AnalysisParametersList.class, object).as();
            return parse(as.getParameterList());
        }

        List<AnalysisParameter> parse(final List<AnalysisParameter> paramList) {

            List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
            for (AnalysisParameter ap : paramList) {
                if (AppTemplateUtils.isTextType(ap.getType()) || ap.getType().equals(ArgumentType.Flag)) {
                    parsedList.addAll(parseStringValue(ap));
                } else if (isInputType(ap.getType())) {
                    if (!isReferenceGenomeType(ap.getInfoType())) {
                        parsedList.addAll(parseStringValue(ap));
                    } else {
                        parsedList.addAll(parseSelectionValue(ap));
                    }
                } else if (AppTemplateUtils.isSelectionArgumentType(ap.getType())) {
                    parsedList.addAll(parseSelectionValue(ap));
                }
            }

            return parsedList;

        }

        Set<String> REFERENCE_GENOME_TYPES
                = Sets.newHashSet("referenceannotation", "referencesequence", "referencegenome");

        boolean isReferenceGenomeType(final String typeName) {
            return REFERENCE_GENOME_TYPES.contains(typeName.toLowerCase());
        }

        final Set<ArgumentType> INPUT_TYPES = Sets.immutableEnumSet( ArgumentType.Input, ArgumentType.FileInput, ArgumentType.FolderInput,
                                                                           ArgumentType.MultiFileSelector);

        boolean isInputType(ArgumentType type) {
            return INPUT_TYPES.contains(type);
        }

        List<AnalysisParameter> parseSelectionValue(final AnalysisParameter ap) {
            Splittable s = ap.getValue();
            Splittable val = s.get("value");
            if ((val != null) && (Strings.isNullOrEmpty(val.getPayload()) || !val.isKeyed())) {
                return Collections.emptyList();
            }
            AutoBean<SelectionValue> ab = AutoBeanCodex.decode(factory, SelectionValue.class, val);
            ap.setDisplayValue(ab.as().getDisplay());
            return Lists.<AnalysisParameter> newArrayList(ap);
        }

        List<AnalysisParameter> parseStringValue(final AnalysisParameter ap) {
            List<AnalysisParameter> parsedList = new ArrayList<AnalysisParameter>();
            Splittable s = ap.getValue();
            AutoBean<SimpleValue> ab = AutoBeanCodex.decode(factory, SimpleValue.class, s);
            ap.setDisplayValue(ab.as().getValue());
            parsedList.add(ap);
            return parsedList;
        }
    }

    private final DEProperties deProperties;
    private final UserInfo userInfo;
    private final AnalysesAutoBeanFactory factory;
    private final DEServiceFacade deServiceFacade;


    @Inject
    public AnalysisServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProperties, final UserInfo userInfo, final AnalysesAutoBeanFactory factory) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.userInfo = userInfo;
        this.factory = factory;
    }

    @Override
    public void getAnalyses(final FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResultBean<Analysis>> callback) {
        getAnalyses(userInfo.getWorkspaceId(), loadConfig, new AsyncCallbackConverter<String, PagingLoadResultBean<Analysis>>(callback) {

            @Override
            protected PagingLoadResultBean<Analysis> convertFrom(String object) {
                AnalysesList ret = AutoBeanCodex.decode(factory, AnalysesList.class, object).as();
                PagingLoadResultBean<Analysis> loadResult = new PagingLoadResultBean<Analysis>(ret.getAnalysisList(), ret.getTotal(), loadConfig.getOffset());
                return loadResult;
            }

        });
    }

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

    @Override
    public void renameAnalysis(Analysis analysis, String newName, AsyncCallback<Void> callback) {
        // TODO CORE-5307 implement when new service is created.
        throw new UnsupportedOperationException("Not yet implemented");

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

    @Override
    public void getAnalysisParams(Analysis analysis, AsyncCallback<List<AnalysisParameter>> callback){
        String address = deProperties.getMuleServiceBaseUrl()
                                 + "get-property-values/" + analysis.getId();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, new StringListAsyncCallbackConverter(callback, factory));
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
