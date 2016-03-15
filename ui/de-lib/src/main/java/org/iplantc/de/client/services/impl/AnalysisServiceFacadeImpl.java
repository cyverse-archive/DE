package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PATCH;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.AnalysesList;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.analysis.AnalysisParametersList;
import org.iplantc.de.client.models.analysis.AnalysisStepsInfo;
import org.iplantc.de.client.models.analysis.SimpleValue;
import org.iplantc.de.client.models.analysis.sharing.AnalysisSharingRequestList;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUnsharingRequestList;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

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
 * @author jstroot
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

            List<AnalysisParameter> parsedList = new ArrayList<>();
            for (AnalysisParameter ap : paramList) {
                if (appTemplateUtils.isTextType(ap.getType()) || ap.getType().equals(ArgumentType.Flag)) {
                    parsedList.addAll(parseStringValue(ap));
                } else if (isInputType(ap.getType()) || isReferenceGenomeType(ap.getType().toString())) {
                    if (!isReferenceGenomeType(ap.getType().toString())) {
                        parsedList.addAll(parseStringValue(ap));
                    } else {
                        parsedList.addAll(parseSelectionValue(ap));
                    }
                } else if (appTemplateUtils.isSelectionArgumentType(ap.getType())) {
                    parsedList.addAll(parseSelectionValue(ap));
                } else if (ap.getType().equals(ArgumentType.FileOutput)) {
                    parsedList.addAll(parseStringValue(ap));
                }
            }

            return parsedList;

        }

        Set<String> REFERENCE_GENOME_TYPES
                = Sets.newHashSet("referenceannotation", "referencesequence", "referencegenome");

        boolean isReferenceGenomeType(final String typeName) {
            return REFERENCE_GENOME_TYPES.contains(typeName.toLowerCase());
        }

        final Set<ArgumentType> INPUT_TYPES = Sets.immutableEnumSet(ArgumentType.Input, ArgumentType.FileInput, ArgumentType.FolderInput,
                                                                    ArgumentType.MultiFileSelector,
                                                                    ArgumentType.FileFolderInput);

        boolean isInputType(ArgumentType type) {
            return INPUT_TYPES.contains(type);
        }

        List<AnalysisParameter> parseSelectionValue(final AnalysisParameter ap) {
            Splittable s = ap.getValue();
            Splittable val = s.get("value");
            if ((val != null) && (Strings.isNullOrEmpty(val.getPayload()) || !val.isKeyed())) {
                return Collections.emptyList();
            }
            AutoBean<SelectionItem> ab = AutoBeanCodex.decode(factory, SelectionItem.class, val);
            ap.setDisplayValue(ab.as().getDisplay());
            return Lists.newArrayList(ap);
        }

        List<AnalysisParameter> parseStringValue(final AnalysisParameter ap) {
            List<AnalysisParameter> parsedList = new ArrayList<>();
            Splittable s = ap.getValue();
            AutoBean<SimpleValue> ab = AutoBeanCodex.decode(factory, SimpleValue.class, s);
            ap.setDisplayValue(ab.as().getValue());
            parsedList.add(ap);
            return parsedList;
        }
    }
    
    private class StringAnalaysisStepInfoConverter extends
                                                  AsyncCallbackConverter<String, AnalysisStepsInfo> {

        private final AnalysesAutoBeanFactory factory;

        public StringAnalaysisStepInfoConverter(AsyncCallback<AnalysisStepsInfo> callback,
                                                AnalysesAutoBeanFactory factory) {
            super(callback);
            this.factory = factory;
        }

        @Override
        protected AnalysisStepsInfo convertFrom(String object) {
            AnalysisStepsInfo as = AutoBeanCodex.decode(factory, AnalysisStepsInfo.class, object).as();
            return as;
        }
        
    }

    private final AnalysesAutoBeanFactory factory;
    private final AppTemplateUtils appTemplateUtils;
    private final DiskResourceUtil diskResourceUtil;
    private final DiscEnvApiService deServiceFacade;
    public static final String ANALYSES = "org.iplantc.services.analyses";


    @Inject
    public AnalysisServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                     final AnalysesAutoBeanFactory factory,
                                     final AppTemplateUtils appTemplateUtils,
                                     final DiskResourceUtil diskResourceUtil) {
        this.deServiceFacade = deServiceFacade;
        this.factory = factory;
        this.appTemplateUtils = appTemplateUtils;
        this.diskResourceUtil = diskResourceUtil;
    }

    /**
     * FIXME move service call into service facade.
     * @param loadConfig optional remote paging and sorting configs.
     * @param callback executed when RPC call completes.
     */
    @Override
    public void getAnalyses(final FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResultBean<Analysis>> callback) {
        StringBuilder address = new StringBuilder(ANALYSES);


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
                    address.append("&sort-dir="); //$NON-NLS-1$
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

                    if (!Strings.isNullOrEmpty(field) && value != null) {
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
        deServiceFacade.getServiceData(wrapper, new AsyncCallbackConverter<String, PagingLoadResultBean<Analysis>>(callback) {

            @Override
            protected PagingLoadResultBean<Analysis> convertFrom(String object) {
                AnalysesList ret = AutoBeanCodex.decode(factory, AnalysesList.class, object).as();
                return new PagingLoadResultBean<>(ret.getAnalysisList(), ret.getTotal(), loadConfig.getOffset());
            }

        });
    }

    @Override
    public void deleteAnalyses(List<Analysis> analysesToBeDeleted, AsyncCallback<String> callback) {
        String address = ANALYSES + "/shredder"; //$NON-NLS-1$ //$NON-NLS-2$
        final Splittable stringIdListSplittable = diskResourceUtil.createStringIdListSplittable(analysesToBeDeleted);
        final Splittable payload = StringQuoter.createSplittable();
        stringIdListSplittable.assign(payload, "analyses");
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload.getPayload());

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void renameAnalysis(Analysis analysis, String newName, AsyncCallback<Void> callback) {
        String address = ANALYSES + "/" + analysis.getId();
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(newName).assign(body, "name");

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address, body.getPayload());
        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void stopAnalysis(Analysis analysis, AsyncCallback<String> callback) {
        String address = ANALYSES + "/" + analysis.getId() + "/stop";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, "{}");

        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getAnalysisParams(Analysis analysis, AsyncCallback<List<AnalysisParameter>> callback) {
        String address = ANALYSES + "/" + analysis.getId() + "/parameters";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);

        deServiceFacade.getServiceData(wrapper, new StringListAsyncCallbackConverter(callback, factory));
    }

    @Override
    public void updateAnalysisComments(final Analysis analysis, final String newComment, AsyncCallback<Void> callback) {
        String address = ANALYSES + "/" + analysis.getId();
        Splittable body = StringQuoter.createSplittable();
        StringQuoter.create(newComment).assign(body, "description");

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PATCH, address, body.getPayload());
        deServiceFacade.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getAnalysisSteps(Analysis analysis, AsyncCallback<AnalysisStepsInfo> callback) {
        String address = ANALYSES + "/" + analysis.getId() + "/steps";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new StringAnalaysisStepInfoConverter(callback, factory));

    }

    @Override
    public void shareAnalyses(AnalysisSharingRequestList request, AsyncCallback<String> callback) {
        final String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();
        GWT.log("analyis sharing request:" + payload);
        String address = ANALYSES + "/" + "sharing";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void unshareAnalyses(AnalysisUnsharingRequestList request, AsyncCallback<String> callback) {
        final String payload = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();
        GWT.log("analysis un-sharing request:" + payload);
        String address = ANALYSES + "/" + "unsharing";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, callback);
    }

    @Override
    public void getPermissions(List<Analysis> analyses, AsyncCallback<String> callback) {
        Splittable anaObj = StringQuoter.createSplittable();
        Splittable idArr = StringQuoter.createIndexed();

        for(Analysis a : analyses) {
            Splittable item = StringQuoter.create(a.getId());
            item.assign(idArr, idArr.size());
        }

        idArr.assign(anaObj, "analyses");
        String address = ANALYSES + "/" + "permission-lister";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(BaseServiceCallWrapper.Type.POST, address, anaObj.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);
    }

}

