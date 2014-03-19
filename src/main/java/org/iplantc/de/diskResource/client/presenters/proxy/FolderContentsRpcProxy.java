package org.iplantc.de.diskResource.client.presenters.proxy;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.List;

/**
 * This proxy is responsible for retrieving directory listings and search requests from the server.
 *
 */
public class FolderContentsRpcProxy extends RpcProxy<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> {

    /**
     * Constructs a valid {@link PagingLoadResultBean} from the given search results.
     * 
     * @author jstroot
     * 
     */
    class SearchResultsCallback implements AsyncCallback<List<DiskResource>> {
        private final FolderContentsLoadConfig loadConfig;
        private final AsyncCallback<PagingLoadResult<DiskResource>> callback;
        private final IplantAnnouncer announcer1;
        private final IplantDisplayStrings dStrings;
        private final HasSafeHtml hasSafeHtml1;

        private SearchResultsCallback(IplantAnnouncer announcer, FolderContentsLoadConfig loadConfig, AsyncCallback<PagingLoadResult<DiskResource>> callback, IplantDisplayStrings displayStrings,
                HasSafeHtml hasSafeHtml) {
            this.announcer1 = announcer;
            this.loadConfig = loadConfig;
            this.callback = callback;
            this.dStrings = displayStrings;
            this.hasSafeHtml1 = hasSafeHtml;
        }

        @Override
        public void onSuccess(List<DiskResource> results) {
            if (callback == null || results == null) {
                onFailure(null);
                return;
            }
            callback.onSuccess(new PagingLoadResultBean<DiskResource>(results, loadConfig.getFolder().getTotal(), loadConfig.getOffset()));
            DiskResourceQueryTemplate query = (DiskResourceQueryTemplate)loadConfig.getFolder();
            String searchText = setSearchText(query.getFileQuery());

            final String searchResultsHeader = dStrings.searchDataResultsHeader(searchText, query.getTotal(), query.getExecutionTime() / 1000.0);
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
                announcer1.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromString("Unable to search. Please try again later."), true));
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
	class FolderContentsCallback implements AsyncCallback<Folder> {
        private final FolderContentsLoadConfig loadConfig;
        private final AsyncCallback<PagingLoadResult<DiskResource>> callback;
        private final IplantAnnouncer announcer;
        private final HasSafeHtml hasSafeHtml1;

        private FolderContentsCallback(IplantAnnouncer announcer, FolderContentsLoadConfig loadConfig, AsyncCallback<PagingLoadResult<DiskResource>> callback, HasSafeHtml hasSafeHtml) {
            this.announcer = announcer;
            this.loadConfig = loadConfig;
            this.callback = callback;
            this.hasSafeHtml1 = hasSafeHtml;
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

            callback.onSuccess(new PagingLoadResultBean<DiskResource>(list, result.getTotal(), loadConfig.getOffset()));

            /* Set search results header to a non-breaking space to ensure it retains its height. */
            hasSafeHtml1.setHTML(SafeHtmlUtils.fromSafeConstant("&nbsp;"));
        }

        @Override
        public void onFailure(Throwable caught) {
            if (loadConfig.getFolder() instanceof DiskResourceQueryTemplate) {
                announcer.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromString("Unable to search. Please try again later."), true));
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

    private final DiskResourceServiceFacade drService;
    private final SearchServiceFacade searchService;
    private final IplantAnnouncer announcer;
    private final IplantDisplayStrings displayStrings;
    private HasSafeHtml hasSafeHtml;

    @Inject
    public FolderContentsRpcProxy(final DiskResourceServiceFacade drService, final SearchServiceFacade searchService, final IplantAnnouncer announcer, final IplantDisplayStrings displayStrings) {
        this.drService = drService;
        this.searchService = searchService;
        this.announcer = announcer;
        this.displayStrings = displayStrings;
    }
    
    @Override
    public void load(final FolderContentsLoadConfig loadConfig,
                     final AsyncCallback<PagingLoadResult<DiskResource>> callback) {
        final Folder folder = loadConfig.getFolder();
        if (folder.isFilter()) {
        	if (callback != null) {
        		List<DiskResource> emptyResult = Lists.newArrayList();
        		callback.onSuccess(new PagingLoadResultBean<DiskResource>(
        				emptyResult, 0, 0));
        	}
        	return;
        } else if(folder instanceof DiskResourceQueryTemplate){
            searchService.submitSearchFromQueryTemplate((DiskResourceQueryTemplate)folder, loadConfig, null, new SearchResultsCallback(announcer, loadConfig, callback, displayStrings, hasSafeHtml));
        } else {
            drService.getFolderContents(folder, loadConfig, new FolderContentsCallback(announcer, loadConfig, callback, hasSafeHtml));
        }

    }

    public void init(final HasSafeHtml hasSafeHtml) {
        this.hasSafeHtml = hasSafeHtml;
    }
}
