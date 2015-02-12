package org.iplantc.de.diskResource.client.presenters.grid.proxy;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.GridView;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.List;
import java.util.logging.Logger;

/**
 * This proxy is responsible for retrieving directory listings and search requests from the server.
 *
 * @author jstroot, psarando
 */
public class FolderContentsRpcProxyImpl extends RpcProxy<FolderContentsLoadConfig, PagingLoadResult<DiskResource>>
                                        implements GridView.FolderContentsRpcProxy {

    /**
     * Constructs a valid {@link PagingLoadResultBean} from the given search results.
     * 
     * @author jstroot
     * 
     */
    static class SearchResultsCallback implements AsyncCallback<List<DiskResource>> {
        private final FolderContentsLoadConfig loadConfig;
        private final AsyncCallback<PagingLoadResult<DiskResource>> callback;
        private final GridView.Presenter.Appearance appearance;
        private final IplantAnnouncer announcer1;
        private final HasSafeHtml hasSafeHtml1;

        public SearchResultsCallback(final IplantAnnouncer announcer,
                                     final FolderContentsLoadConfig loadConfig,
                                     final AsyncCallback<PagingLoadResult<DiskResource>> callback,
                                     final GridView.Presenter.Appearance appearance,
                                     final HasSafeHtml hasSafeHtml) {
            this.announcer1 = announcer;
            this.loadConfig = loadConfig;
            this.callback = callback;
            this.appearance = appearance;
            this.hasSafeHtml1 = hasSafeHtml;
        }

        @Override
        public void onSuccess(List<DiskResource> results) {
            if (callback == null || results == null) {
                onFailure(null);
                return;
            }
            callback.onSuccess(new PagingLoadResultBean<>(results, loadConfig.getFolder().getTotal(), loadConfig.getOffset()));
            DiskResourceQueryTemplate query = (DiskResourceQueryTemplate)loadConfig.getFolder();
            String searchText = setSearchText(query.getFileQuery());

            final String searchResultsHeader = appearance.searchDataResultsHeader(searchText, query.getTotal(), query.getExecutionTime() / 1000.0);
            hasSafeHtml1.setHTML(SafeHtmlUtils.fromString(searchResultsHeader));
        }

        private String setSearchText(String fileQuery) {
            String retString;
            if (Strings.isNullOrEmpty(fileQuery)) {
                retString = "Advanced Search";
            } else {
                retString = "\"" + fileQuery + "\"";
            }
            return retString;
        }

        @Override
        public void onFailure(Throwable caught) {
            if (loadConfig.getFolder() instanceof DiskResourceQueryTemplate) {
                announcer1.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromString(appearance.searchFailure()), true));
            }
            callback.onFailure(caught);
        }

        public FolderContentsLoadConfig getLoadConfig() {
            return loadConfig;
        }

        public AsyncCallback<PagingLoadResult<DiskResource>> getCallback() {
            return callback;
        }

    }

    /**
     * Constructs a valid {@link PagingLoadResultBean} from the given {@link Folder} result.
     * 
     * @author jstroot
     * 
     */
    static class FolderContentsCallback implements AsyncCallback<Folder> {
        private final FolderContentsLoadConfig loadConfig;
        private final AsyncCallback<PagingLoadResult<DiskResource>> callback;
        private final IplantAnnouncer announcer;
        private final HasSafeHtml hasSafeHtml1;
        private final GridView.Presenter.Appearance appearance;

        public FolderContentsCallback(final IplantAnnouncer announcer,
                                      final FolderContentsLoadConfig loadConfig,
                                      final AsyncCallback<PagingLoadResult<DiskResource>> callback,
                                      final HasSafeHtml hasSafeHtml,
                                      final GridView.Presenter.Appearance appearance) {
            this.announcer = announcer;
            this.loadConfig = loadConfig;
            this.callback = callback;
            this.hasSafeHtml1 = hasSafeHtml;
            this.appearance = appearance;
        }

        @Override
        public void onSuccess(Folder result) {
            if (callback == null || result == null) {
                onFailure(null);
                return;
            }
            // Create list of all items within the result folder
            List<DiskResource> list = Lists.newArrayList(Iterables.concat(result.getFolders(), result.getFiles()));
            // Update the loadConfig folder with the totalFiltered count.

            loadConfig.getFolder().setTotalFiltered(result.getTotalFiltered());

            callback.onSuccess(new PagingLoadResultBean<>(list, result.getTotal(), loadConfig.getOffset()));

            /* Set search results header to a non-breaking space to ensure it retains its height. */
            hasSafeHtml1.setHTML(SafeHtmlUtils.fromString((result.getName() != null) ? result.getName() : ""));
        }

        @Override
        public void onFailure(Throwable caught) {
            if (loadConfig.getFolder() instanceof DiskResourceQueryTemplate) {
                announcer.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromString(appearance.searchFailure()), true));
            }
            callback.onFailure(caught);
        }

        public FolderContentsLoadConfig getLoadConfig() {
            return loadConfig;
        }

        public AsyncCallback<PagingLoadResult<DiskResource>> getCallback() {
            return callback;
        }

    }

    static class FavoritesCallback implements AsyncCallback<Folder> {

        private final AsyncCallback<PagingLoadResult<DiskResource>> callback;
        private final FolderContentsLoadConfig loadConfig;
        private final IplantAnnouncer announcer;
        private final GridView.Presenter.Appearance appearance;

        public FavoritesCallback(final AsyncCallback<PagingLoadResult<DiskResource>> callback,
                                 final FolderContentsLoadConfig loadConfig,
                                 final IplantAnnouncer announcer,
                                 final GridView.Presenter.Appearance appearance) {
            this.callback = callback;
            this.loadConfig = loadConfig;
            this.announcer = announcer;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Throwable caught) {
            announcer.schedule(new ErrorAnnouncementConfig(appearance.favoritesError(caught.getMessage())));
        }

        @Override
        public void onSuccess(Folder result) {
            if (callback == null || result == null) {
                onFailure(null);
                return;
            }
            // Create list of all items within the result folder
            List<DiskResource> list = Lists.newArrayList(Iterables.concat(result.getFolders(), result.getFiles()));

            callback.onSuccess(new PagingLoadResultBean<>(list,
                    result.getTotal(),
                    loadConfig.getOffset()));
        }
    }

    private final DiskResourceServiceFacade drService;
    private final SearchServiceFacade searchService;
    private final FileSystemMetadataServiceFacade metadataService;
    private final IplantAnnouncer announcer;
    private final GridView.Presenter.Appearance appearance;
    private TYPE entityType = null;
    private HasSafeHtml hasSafeHtml;
    private final List<InfoType> infoTypeFilterList;

    final Logger LOG = Logger.getLogger(FolderContentsRpcProxyImpl.class.getName());

    @AssistedInject
    FolderContentsRpcProxyImpl(final DiskResourceServiceFacade drService,
                               final SearchServiceFacade searchService,
                               final FileSystemMetadataServiceFacade metadataService,
                               final IplantAnnouncer announcer,
                               final GridView.Presenter.Appearance appearance,
                               @Assisted final List<InfoType> infoTypeFilterList,
                               @Assisted final TYPE entityType){
        this.drService = drService;
        this.searchService = searchService;
        this.announcer = announcer;
        this.appearance = appearance;
        this.metadataService = metadataService;
        this.infoTypeFilterList = infoTypeFilterList;
        this.entityType = entityType;
    }

    @Override
    public void load(final FolderContentsLoadConfig loadConfig, final AsyncCallback<PagingLoadResult<DiskResource>> callback) {
        final Folder folder = loadConfig.getFolder();
        if (folder.isFilter()) {
            if (callback != null) {
                List<DiskResource> emptyResult = Lists.newArrayList();
                callback.onSuccess(new PagingLoadResultBean<>(emptyResult, 0, 0));
            }
        } else if (folder instanceof DiskResourceFavorite) {
            metadataService.getFavorites(infoTypeFilterList,
                                         entityType,
                                         loadConfig,
                                         new FavoritesCallback(callback,
                                                               loadConfig,
                                                               announcer,
                                                               appearance));
        } else if (folder instanceof DiskResourceQueryTemplate) {
            DiskResourceQueryTemplate qt = (DiskResourceQueryTemplate)folder;
            String infoTypeFilterQueryString = Joiner.on(" ").join(infoTypeFilterList);
            String newMetadataValueQuery = Joiner.on(" ")
                                                 .join(Strings.nullToEmpty(qt.getMetadataValueQuery()),
                                                       infoTypeFilterQueryString)
                                               .trim()                              // Trim the results
                                               .replaceAll("-", "\\\\-");           // Escape all hyphens

            qt.setMetadataValueQuery(newMetadataValueQuery);

            searchService.submitSearchFromQueryTemplate((DiskResourceQueryTemplate)folder,
                                                        loadConfig,
                                                        entityType,
                                                        new SearchResultsCallback(announcer,
                                                                                  loadConfig,
                                                                                  callback,
                                                                                  appearance,
                                                                                  hasSafeHtml));
        } else {
            drService.getFolderContents(folder,
                                        infoTypeFilterList,
                                        entityType,
                                        loadConfig,
                                        new FolderContentsCallback(announcer,
                                                                   loadConfig,
                                                                   callback,
                                                                   hasSafeHtml,
                                                                   appearance));
        }

    }

    @Override
    public void setHasSafeHtml(HasSafeHtml centerHeader) {
        this.hasSafeHtml = centerHeader;
    }
}
