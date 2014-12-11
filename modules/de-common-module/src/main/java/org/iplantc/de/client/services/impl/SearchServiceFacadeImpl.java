package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplateList;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import java.util.logging.Logger;

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
                    reMapInfoType(entity);

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

        void reMapInfoType(Splittable entity) {
            // If infotype is already defined, return
            if(!entity.isUndefined("infoType")){
                LOG.info("Search results are returning entities with 'infoType' keys.");
                // If this code is hit consistenly, this code is probably no longer necessary.
                return;
            }

            Splittable metadata = entity.get("metadata");
            Preconditions.checkArgument(metadata.isIndexed(), "'metadata key is not indexed.");
            for(int i = 0; i < metadata.size(); i++){
                Splittable metadataItem = metadata.get(i);
                if(metadataItem.get("attribute").asString().equals("ipc-filetype")){
                    // Then forward value to infoType
                    metadataItem.get("value").assign(entity, "infoType");
                }
            }
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


        void reMapPermissions(Splittable entity) {
            Splittable userPermissionsList = entity.get("userPermissions");
            for (int i = 0; i < userPermissionsList.size(); i++) {
                Splittable permission = userPermissionsList.get(i);
                final String permissionString = permission.get("permission").asString();
                final String userString = permission.get("user").asString();
                final Iterable<String> userStringSplit = Splitter.on("#").split(userString);
                Splittable newPermissionsSplit = StringQuoter.createSplittable();
                if (userStringSplit.iterator().next().equals(userInfo1.getUsername())) {
                    switch (permissionString) {
                        case "own":
                            StringQuoter.create(true).assign(newPermissionsSplit, "own");
                            StringQuoter.create(true).assign(newPermissionsSplit, "write");
                            StringQuoter.create(true).assign(newPermissionsSplit, "read");
                            break;
                        case "write":
                            StringQuoter.create(false).assign(newPermissionsSplit, "own");
                            StringQuoter.create(true).assign(newPermissionsSplit, "write");
                            StringQuoter.create(true).assign(newPermissionsSplit, "read");
                            break;
                        case "read":
                            StringQuoter.create(false).assign(newPermissionsSplit, "own");
                            StringQuoter.create(false).assign(newPermissionsSplit, "write");
                            StringQuoter.create(true).assign(newPermissionsSplit, "read");
                            break;
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
            Splittable splitList = StringQuoter.split(object);
            if(!splitList.isIndexed()){
                return Collections.emptyList();
            }
            splitList.assign(split, DiskResourceQueryTemplateList.LIST_KEY);
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
            return AutoBeanCodex.decode(factory, DiskResourceQueryTemplate.class, encode).as();
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
            return AutoBeanCodex.decode(factory, DiskResourceQueryTemplate.class, encode).as();
        }
    }

    private final DiscEnvApiService deServiceFacade;
    private final DiskResourceAutoBeanFactory drFactory;
    private final SearchAutoBeanFactory searchAbFactory;
    private final UserInfo userInfo;
    private final DEProperties deProperties;
    final Logger LOG = Logger.getLogger(SearchServiceFacadeImpl.class.getName());

    @Inject
    public SearchServiceFacadeImpl(final DiscEnvApiService deServiceFacade,
                                   final DEProperties deProperties,
                                   final SearchAutoBeanFactory searchAbFactory,
                                   final DiskResourceAutoBeanFactory drFactory,
                                   final UserInfo userInfo) {
        this.deServiceFacade = deServiceFacade;
        this.deProperties = deProperties;
        this.searchAbFactory = searchAbFactory;
        this.drFactory = drFactory;
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
        String address = deProperties.getMuleServiceBaseUrl() + "saved-searches";
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deServiceFacade.getServiceData(wrapper, new QueryTemplateListCallbackConverter(callback, searchAbFactory));
    }

    @Override
    public void saveQueryTemplates(List<DiskResourceQueryTemplate> queryTemplates, AsyncCallback<List<DiskResourceQueryTemplate>> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "saved-searches";

        /*
         * TODO check to see if query templates all have names, and that they are unique.throw illegal
         * argument exception
         */
        String payload = templateListToIndexedSplittablePayload(queryTemplates);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, payload);
        deServiceFacade.getServiceData(wrapper, new SavedSearchCallbackConverter(callback, queryTemplates, searchAbFactory));
    }

    @Override
    public void deleteQueryTemplates(List<DiskResourceQueryTemplate> queryTemplates,
                                     AsyncCallback<List<DiskResourceQueryTemplate>> callback) {
        String address = deProperties.getMuleServiceBaseUrl() + "saved-searches";

        /*
         * TODO check to see if query templates all have names, and that they are unique.throw illegal
         * argument exception
         */
        String payload = templateListToIndexedSplittablePayload(queryTemplates);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.DELETE, address, payload);
        deServiceFacade.getServiceData(wrapper, new SavedSearchCallbackConverter(callback,
                                                                                 queryTemplates,
                                                                                 searchAbFactory));
    }

    @Override
    public void submitSearchFromQueryTemplate(DiskResourceQueryTemplate queryTemplate, FilterPagingLoadConfigBean loadConfig, TYPE searchType, AsyncCallback<List<DiskResource>> callback) {
        DataSearchQueryBuilder builder = new DataSearchQueryBuilder(queryTemplate, userInfo);
        String queryParameter = "";
        String tags = "";
        
        String buildFullQuery = builder.buildFullQuery();
        if (!Strings.isNullOrEmpty(buildFullQuery)) {
            queryParameter = "q=" + URL.encodeQueryString(buildFullQuery);
        }
        String limitParameter = "&limit=" + loadConfig.getLimit();
        String offsetParameter = "&offset=" + loadConfig.getOffset();
        String typeParameter = "&type=" + ((searchType == null) ? TYPE.ANY.toString() : searchType.toString());
        if (!Strings.isNullOrEmpty(builder.taggedWith())) {
            tags = "&tags=" + URL.encodeQueryString(builder.taggedWith());
        }
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

        StringBuilder addressSb = new StringBuilder().append(deProperties.getDataMgmtBaseUrl() + "index?");
        if (!Strings.isNullOrEmpty(queryParameter)) {
            addressSb.append(queryParameter);
        }

        if (!Strings.isNullOrEmpty(tags)) {
            addressSb.append(tags);
        }

        addressSb.append(limitParameter);
        addressSb.append(offsetParameter);
        addressSb.append(typeParameter);
        addressSb.append(sortParameter);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, addressSb.toString());
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

        if ("path".equalsIgnoreCase(sortField)) {
            return "entity.path";
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
            StringQuoter.createIndexed().assign(encode, "files");
            StringQuoter.createIndexed().assign(encode, "folders");
            encode.assign(indexedSplittable, index++);
        }
        return indexedSplittable.getPayload();
    }
}
