package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplateList;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.Endpoints;
import org.iplantc.de.client.services.ReservedBuckets;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("nls")
public class SearchServiceFacadeImpl implements SearchServiceFacade {

    public class SubmitSearchCallbackConverter extends AsyncCallbackConverter<String, List<DiskResource>> {
        private final DiskResourceAutoBeanFactory factory;
        private final DiskResourceQueryTemplate queryTemplate;
        private final UserInfo userInfo1;

        public SubmitSearchCallbackConverter(AsyncCallback<List<DiskResource>> callback, DiskResourceQueryTemplate queryTemplate, UserInfo userInfo, DiskResourceAutoBeanFactory drFactory) {
            super(callback);
            this.queryTemplate = queryTemplate;
            this.userInfo1 = userInfo;
            this.factory = drFactory;
        }

        @Override
        protected List<DiskResource> convertFrom(String object) {
            // Clear previous collections from template
            queryTemplate.setFiles(Lists.<File> newArrayList());
            queryTemplate.setFolders(Lists.<Folder> newArrayList());

            List<DiskResource> ret = Lists.newArrayList();
            Splittable split = StringQuoter.split(object);
            // Set the total returned on the query template
            queryTemplate.setTotal(Double.valueOf(split.get("total").asNumber()).intValue());
            queryTemplate.setExecutionTime(Double.valueOf(split.get("execution-time").asNumber()).longValue());
            if (split.get("matches").isIndexed()) {
                final int size = split.get("matches").size();
                for (int i = 0; i < size; i++) {
                    final Splittable child = split.get("matches").get(i);
                    final String asString = child.get("type").asString();
                    Splittable entity = child.get("entity");

                    reMapDateKeys(entity);
                    reMapPermissions(entity);
                    reMapPath(entity);

                    if (asString.equals("folder")) {
                        ret.add(decodeFolderIntoQueryTemplate(entity, queryTemplate, factory));
                    } else if (asString.equals("file")) {
                        reMapFileSize(entity);
                        ret.add(decodeFileIntoQueryTemplate(entity, queryTemplate, factory));
                    }
                }
            }

            return ret;
        }

        File decodeFileIntoQueryTemplate(Splittable entity, DiskResourceQueryTemplate queryTemplate, DiskResourceAutoBeanFactory factory) {
            // KLUDGE Re-map JSON keys until service JSON is unified.
            entity.get("fileSize").assign(entity, "file-size");
            final AutoBean<File> decodeFile = AutoBeanCodex.decode(factory, File.class, entity);
            queryTemplate.getFiles().add(decodeFile.as());
            return decodeFile.as();
        }

        Folder decodeFolderIntoQueryTemplate(Splittable entity, DiskResourceQueryTemplate queryTemplate, DiskResourceAutoBeanFactory factory) {
            final AutoBean<Folder> decodeFolder = AutoBeanCodex.decode(factory, Folder.class, entity);
            queryTemplate.getFolders().add(decodeFolder.as());
            return decodeFolder.as();
        }

        /**
         * KLUDGE Re-map JSON keys until service JSON is unified.
         */
        void reMapDateKeys(Splittable entity) {
            final long dateModifiedInSec = Double.valueOf(entity.get("dateModified").asNumber()).longValue();
            StringQuoter.create(dateModifiedInSec).assign(entity, "date-modified");
            final long dateCreatedInSec = Double.valueOf(entity.get("dateCreated").asNumber()).longValue();
            StringQuoter.create(dateCreatedInSec).assign(entity, "date-created");
        }

        /**
         * KLUDGE Re-map JSON keys until service JSON is unified.
         */
        void reMapFileSize(Splittable entity) {
            entity.get("fileSize").assign(entity, "file-size");
        }

        /**
         * KLUDGE Re-map JSON keys until service JSON is unified.
         */
        void reMapPath(Splittable entity) {
            final String id = entity.get("id").asString();
            // StringQuoter.create(DiskResourceUtil.parseParent(id)).assign(entity, "path");
            StringQuoter.create(id).assign(entity, "path");
        }

        void reMapPermissions(Splittable entity) {
            Splittable userPermissionsList = entity.get("userPermissions");
            for (int i = 0; i < userPermissionsList.size(); i++) {
                Splittable permission = userPermissionsList.get(i);
                final String permissionString = permission.get("permission").asString();
                final String userString = permission.get("user").asString();
                final Iterable<String> userStringSplit = Splitter.on("#").split(userString);
                Splittable newPermissionsSplit = StringQuoter.createSplittable();
                if (userStringSplit.iterator().next().equals(userInfo1.getUsername())) {
                    if (permissionString.equals("own")) {
                        StringQuoter.create(true).assign(newPermissionsSplit, "own");
                        StringQuoter.create(true).assign(newPermissionsSplit, "write");
                        StringQuoter.create(true).assign(newPermissionsSplit, "read");
                    } else if (permissionString.equals("write")) {
                        StringQuoter.create(false).assign(newPermissionsSplit, "own");
                        StringQuoter.create(true).assign(newPermissionsSplit, "write");
                        StringQuoter.create(true).assign(newPermissionsSplit, "read");
                    } else if (permissionString.equals("read")) {
                        StringQuoter.create(false).assign(newPermissionsSplit, "own");
                        StringQuoter.create(false).assign(newPermissionsSplit, "write");
                        StringQuoter.create(true).assign(newPermissionsSplit, "read");
                    }
                    newPermissionsSplit.assign(entity, "permissions");
                    break;
                }
            }
        }

    }

    class QueryTemplateListCallbackConverter extends AsyncCallbackConverter<String, List<DiskResourceQueryTemplate>> {
        private final SearchAutoBeanFactory factory;

        public QueryTemplateListCallbackConverter(AsyncCallback<List<DiskResourceQueryTemplate>> callback, SearchAutoBeanFactory searchAbFactory) {
            super(callback);
            this.factory = searchAbFactory;
        }

        @Override
        protected List<DiskResourceQueryTemplate> convertFrom(String object) {
            if (Strings.isNullOrEmpty(object)) {
                return Collections.emptyList();
            }
            final List<DiskResourceQueryTemplate> queryTemplateList = getQueryTemplateList(object);
            final List<DiskResourceQueryTemplate> retQueryTemplateList = Lists.newArrayList();
            for (DiskResourceQueryTemplate qt : queryTemplateList) {
                qt.setDirty(false);
                qt.setFiles(Lists.<File> newArrayList());
                qt.setFolders(Lists.<Folder> newArrayList());
                DiskResourceQueryTemplate savedFlagSet = setSavedFlag(qt);
                retQueryTemplateList.add(savedFlagSet);
            }

            return retQueryTemplateList;
        }

        /**
         * Helper method to encapsulate autobean manipulation
         * 
         * @param object
         * @return
         */
        List<DiskResourceQueryTemplate> getQueryTemplateList(String object) {
            // Expecting the string to be JSON list
            Splittable split = StringQuoter.createSplittable();
            StringQuoter.split(object).assign(split, DiskResourceQueryTemplateList.LIST_KEY);
            AutoBean<DiskResourceQueryTemplateList> decode = AutoBeanCodex.decode(factory, DiskResourceQueryTemplateList.class, split);
            return decode.as().getQueryTemplateList();
        }

        /**
         * Helper method to encapsulate autobean manipulation
         * 
         * @param qt
         * @return a query template whose isSaved() method will return true.
         */
        DiskResourceQueryTemplate setSavedFlag(DiskResourceQueryTemplate qt) {
            // Make sure all saved templates are set as saved.
            final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(qt));
            StringQuoter.create(true).assign(encode, "saved");
            final DiskResourceQueryTemplate as = AutoBeanCodex.decode(factory, DiskResourceQueryTemplate.class, encode).as();
            return as;
        }
    }

    class SavedSearchCallbackConverter extends AsyncCallbackConverter<String, List<DiskResourceQueryTemplate>> {
        private final SearchAutoBeanFactory factory;
        private final List<DiskResourceQueryTemplate> submittedTemplates;

        public SavedSearchCallbackConverter(AsyncCallback<List<DiskResourceQueryTemplate>> callback, List<DiskResourceQueryTemplate> queryTemplates, SearchAutoBeanFactory searchAbFactory) {
            super(callback);
            this.submittedTemplates = queryTemplates;
            this.factory = searchAbFactory;
        }

        @Override
        protected List<DiskResourceQueryTemplate> convertFrom(String object) {
            final Splittable split = StringQuoter.split(object);
            if (split.isUndefined("success")) {
                GWT.log("saveQueryTemplates callback return is missing \"success\" json key:\n\t" + split.getPayload());
                return Collections.emptyList();
            }
            if (!split.get("success").isBoolean()) {
                GWT.log("saveQueryTemplates callback \"success\" json key is not a boolean but should be:\n\t" + split.getPayload());
                return Collections.emptyList();
            }
            List<DiskResourceQueryTemplate> savedTemplates = Lists.newArrayList();

            for (DiskResourceQueryTemplate qt : submittedTemplates) {
                DiskResourceQueryTemplate savedFlagSet = setSavedFlag(qt);
                savedTemplates.add(savedFlagSet);
            }

            return savedTemplates;
        }

        /**
         * Helper method to encapsulate autobean manipulation
         * 
         * @param qt
         * @return a query template whose isSaved() method will return true.
         */
        DiskResourceQueryTemplate setSavedFlag(DiskResourceQueryTemplate qt) {
            // Make sure all saved templates are set as saved.
            final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(qt));
            StringQuoter.create(true).assign(encode, "saved");
            final DiskResourceQueryTemplate as = AutoBeanCodex.decode(factory, DiskResourceQueryTemplate.class, encode).as();
            return as;
        }
    }

    private final ReservedBuckets buckets;
    private final DEServiceFacade deServiceFacade;
    private final DiskResourceAutoBeanFactory drFactory;
    private final Endpoints endpoints;
    private final SearchAutoBeanFactory searchAbFactory;
    private final UserInfo userInfo;
    private final DEProperties deProperties;

    @Inject
    public SearchServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProperties, final SearchAutoBeanFactory searchAbFactory, final DiskResourceAutoBeanFactory drFactory,
            final Endpoints endpoints,
            final ReservedBuckets buckets, final UserInfo userInfo) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.searchAbFactory = searchAbFactory;
        this.drFactory = drFactory;
        this.endpoints = endpoints;
        this.buckets = buckets;
        this.userInfo = userInfo;
    }

    @Override
    public List<DiskResourceQueryTemplate> createFrozenList(List<DiskResourceQueryTemplate> queryTemplates) {
        List<DiskResourceQueryTemplate> toSave = Lists.newArrayList();
        for (DiskResourceQueryTemplate qt : queryTemplates) {
            DiskResourceQueryTemplate frozenTemplate = freezeQueryTemplate(qt);
            toSave.add(frozenTemplate);
        }
        return Collections.unmodifiableList(toSave);
    }

    @Override
    public void getSavedQueryTemplates(AsyncCallback<List<DiskResourceQueryTemplate>> callback) {
        //String address = endpoints.buckets() + "/" + userInfo.getUsername() + "/" + buckets.queryTemplates();
        String address = deProperties.getMuleServiceBaseUrl() + "buckets/" + userInfo.getUsername() + "/reserved/" + buckets.queryTemplates();
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new QueryTemplateListCallbackConverter(callback, searchAbFactory));
    }

    @Override
    public void saveQueryTemplates(List<DiskResourceQueryTemplate> queryTemplates, AsyncCallback<List<DiskResourceQueryTemplate>> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "buckets/" + userInfo.getUsername() + "/reserved/" + buckets.queryTemplates();

        /*
         * TODO check to see if query templates all have names, and that they are unique.throw illegal
         * argument exception
         */
        String payload = templateListToIndexedSplittablePayload(queryTemplates);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, new SavedSearchCallbackConverter(callback, queryTemplates, searchAbFactory));
    }

    @Override
    public void submitSearchFromQueryTemplate(DiskResourceQueryTemplate queryTemplate, FilterPagingLoadConfigBean loadConfig, SearchType searchType, AsyncCallback<List<DiskResource>> callback) {
        DataSearchQueryBuilder builder = new DataSearchQueryBuilder(queryTemplate, userInfo);
        String queryParameter = "q=" + URL.encodeQueryString(builder.buildFullQuery());
        String limitParameter = "&limit=" + loadConfig.getLimit();
        String offsetParameter = "&offset=" + loadConfig.getOffset();
        String typeParameter = "&type=" + ((searchType == null) ? SearchType.ANY.toString() : searchType.toString());
        String sortParameter = "";
        List<SortInfoBean> sortInfoList = loadConfig.getSortInfo();
        if (sortInfoList != null && !sortInfoList.isEmpty()) {
            SortInfoBean sortInfo = sortInfoList.get(0);
            String sortField = convertSortField(sortInfo.getSortField());
            if (!Strings.isNullOrEmpty(sortField)) {
                String sortDir = sortInfo.getSortDir() == null ? SortDir.ASC.toString() : sortInfo.getSortDir().toString();
                sortParameter = Format.substitute("&sort={0}:{1}", sortField, sortDir.toLowerCase());
            }
        }

        String address = deProperties.getDataMgmtBaseUrl() + "index?" + queryParameter + limitParameter + offsetParameter + typeParameter + sortParameter;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new SubmitSearchCallbackConverter(callback, queryTemplate, userInfo, drFactory));

    }

    String convertSortField(String sortField) {
        if ("id".equalsIgnoreCase(sortField)) {
            return "entity.id";
        }

        if ("size".equalsIgnoreCase(sortField)) {
            return "entity.fileSize";
        }

        if ("dateCreated".equalsIgnoreCase(sortField)) {
            return "entity.dateCreated";
        }

        if ("lastModified".equalsIgnoreCase(sortField)) {
            return "entity.dateModified";
        }

        if ("name".equalsIgnoreCase(sortField)) {
            return "entity.label";
        }

        return sortField;
    }

    DiskResourceQueryTemplate freezeQueryTemplate(DiskResourceQueryTemplate qt) {
        // Create copy of template
        Splittable qtSplittable = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(qt));
        AutoBean<DiskResourceQueryTemplate> decode = AutoBeanCodex.decode(searchAbFactory, DiskResourceQueryTemplate.class, qtSplittable);

        // Freeze the autobean
        decode.setFrozen(true);
        return decode.as();
    }

    String templateListToIndexedSplittablePayload(List<DiskResourceQueryTemplate> queryTemplates) {
        Splittable indexedSplittable = StringQuoter.createIndexed();
        int index = 0;
        for (DiskResourceQueryTemplate qt : queryTemplates) {
            final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(qt));
            encode.assign(indexedSplittable, index++);
        }
        return indexedSplittable.getPayload();
    }
}
