package org.iplantc.de.diskResource.client.presenters.proxy;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.RootFolders;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.client.loader.RpcProxy;

import java.util.Collections;
import java.util.List;

public class FolderRpcProxyImpl extends RpcProxy<Folder, List<Folder>> implements DiskResourceView.FolderRpcProxy {

    class GetSavedQueryTemplatesCallback implements AsyncCallback<List<DiskResourceQueryTemplate>> {
        final IplantAnnouncer ipAnnouncer2;

        public GetSavedQueryTemplatesCallback(IplantAnnouncer ipAnnouncer) {
            this.ipAnnouncer2 = ipAnnouncer;
        }

        @Override
        public void onFailure(Throwable caught) {
            ipAnnouncer2.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromString("Failed to retrieve saved filters"), true));
        }

        @Override
        public void onSuccess(List<DiskResourceQueryTemplate> result) {
            // Save result
            fireEvent(new SavedSearchesRetrievedEvent(result));
        }
    }

    class RootFolderCallback implements AsyncCallback<RootFolders> {

        final AsyncCallback<List<Folder>> callback;
        final IplantAnnouncer ipAnnouncer;
        final IsMaskable maskable;
        final SearchServiceFacade searchSvc;

        public RootFolderCallback(final SearchServiceFacade searchService,
                                  final AsyncCallback<List<Folder>> callback,
                                  final IplantAnnouncer announcer,
                                  final IsMaskable isMaskable) {
            this.searchSvc = searchService;
            this.callback = callback;
            this.ipAnnouncer = announcer;
            this.maskable = isMaskable;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.retrieveFolderInfoFailed(), caught);

            if (callback != null) {
                callback.onFailure(caught);
            }
            maskable.unmask();
        }

        @Override
        public void onSuccess(final RootFolders rootFolders) {
            if (callback != null) {
                List<Folder> roots = rootFolders.getRoots();
                callback.onSuccess(roots);
            }
            // Retrieve any saved searches.
            searchSvc.getSavedQueryTemplates(new GetSavedQueryTemplatesCallback(ipAnnouncer));
            Scheduler.get().scheduleFinally(new ScheduledCommand() {

                @Override
                public void execute() {
                    fireEvent(new RootFoldersRetrievedEvent());
                }
            });
            maskable.unmask();
        }
    }

    class SubFoldersCallback implements AsyncCallback<List<Folder>> {
        final AsyncCallback<List<Folder>> callback;

        public SubFoldersCallback(AsyncCallback<List<Folder>> callback) {
            this.callback = callback;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(I18N.ERROR.retrieveFolderInfoFailed(), caught);
            if (callback != null) {
                callback.onFailure(caught);
            }
        }

        @Override
        public void onSuccess(List<Folder> result) {
            if (callback != null) {
                callback.onSuccess(result);
            }
        }
    }

    private final IplantAnnouncer announcer;
    private final DiskResourceServiceFacade drService;
    private final HandlerManager handlerManager;
    private final SearchServiceFacade searchService;
    private IsMaskable isMaskable;

    @Inject
    FolderRpcProxyImpl(final DiskResourceServiceFacade drService,
                       final SearchServiceFacade searchService,
                       final IplantAnnouncer announcer,
                       @Assisted final IsMaskable isMaskable) {

        this.drService = drService;
        this.searchService = searchService;
        this.announcer = announcer;
        this.isMaskable = isMaskable;

        handlerManager = new HandlerManager(this);
    }

    @Override
    public HandlerRegistration addRootFoldersRetrievedEventHandler(RootFoldersRetrievedEvent.RootFoldersRetrievedEventHandler handler) {
        return handlerManager.addHandler(RootFoldersRetrievedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addSavedSearchedRetrievedEventHandler(SavedSearchesRetrievedEvent.SavedSearchesRetrievedEventHandler handler) {
        return handlerManager.addHandler(SavedSearchesRetrievedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEventHandler handler) {
        return handlerManager.addHandler(SubmitDiskResourceQueryEvent.TYPE, handler);
    }

    @Override
    public void load(final Folder parentFolder, final AsyncCallback<List<Folder>> callback) {
        if (parentFolder == null) {
            // Performing initial load of root folders and saved searches
            isMaskable.mask("");
            drService.getRootFolders(new RootFolderCallback(searchService, callback, announcer, isMaskable));
        } else if (parentFolder.isFilter() || parentFolder instanceof DiskResourceFavorite) {
            if (callback != null) {
                callback.onSuccess(Collections.<Folder>emptyList());
            }
        } else if (parentFolder instanceof DiskResourceQueryTemplate) {
            fireEvent(new SubmitDiskResourceQueryEvent((DiskResourceQueryTemplate) parentFolder));
        } else {
            drService.getSubFolders(parentFolder, new SubFoldersCallback(callback));
        }
    }

    void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

}
