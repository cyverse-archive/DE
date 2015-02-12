package org.iplantc.de.diskResource.client.presenters.navigation.proxy;

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
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
public class FolderRpcProxyImpl extends RpcProxy<Folder, List<Folder>> implements DiskResourceView.FolderRpcProxy {

    class GetSavedQueryTemplatesCallback implements AsyncCallback<List<DiskResourceQueryTemplate>> {
        final IplantAnnouncer ipAnnouncer2;
        private final NavigationView.Presenter.Appearance appearance;

        public GetSavedQueryTemplatesCallback(IplantAnnouncer ipAnnouncer,
                                              final NavigationView.Presenter.Appearance appearance) {
            this.ipAnnouncer2 = ipAnnouncer;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Throwable caught) {
            ipAnnouncer2.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromString(appearance.savedFiltersRetrievalFailure()), true));
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
        private final NavigationView.Presenter.Appearance appearance;
        final SearchServiceFacade searchSvc;

        public RootFolderCallback(final SearchServiceFacade searchService,
                                  final AsyncCallback<List<Folder>> callback,
                                  final IplantAnnouncer announcer,
                                  final IsMaskable isMaskable,
                                  final NavigationView.Presenter.Appearance appearance) {
            this.searchSvc = searchService;
            this.callback = callback;
            this.ipAnnouncer = announcer;
            this.maskable = isMaskable;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.retrieveFolderInfoFailed(), caught);

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
            searchSvc.getSavedQueryTemplates(new GetSavedQueryTemplatesCallback(ipAnnouncer,
                                                                                appearance));
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
        private final NavigationView.Presenter.Appearance appearance;

        public SubFoldersCallback(AsyncCallback<List<Folder>> callback,
                                  final NavigationView.Presenter.Appearance appearance) {
            this.callback = callback;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.retrieveFolderInfoFailed(), caught);
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
    private final NavigationView.Presenter.Appearance appearance;
    private final DiskResourceServiceFacade drService;
    private final HandlerManager handlerManager;
    private final SearchServiceFacade searchService;
    private IsMaskable isMaskable;

    @Inject
    FolderRpcProxyImpl(final DiskResourceServiceFacade drService,
                       final SearchServiceFacade searchService,
                       final IplantAnnouncer announcer,
                       final NavigationView.Presenter.Appearance appearance) {

        this.drService = drService;
        this.searchService = searchService;
        this.announcer = announcer;
        this.appearance = appearance;

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
            drService.getRootFolders(new RootFolderCallback(searchService,
                                                            callback,
                                                            announcer,
                                                            isMaskable,
                                                            appearance));
        } else if (parentFolder.isFilter() || parentFolder instanceof DiskResourceFavorite) {
            if (callback != null) {
                callback.onSuccess(Collections.<Folder>emptyList());
            }
        } else if (parentFolder instanceof DiskResourceQueryTemplate) {
            fireEvent(new SubmitDiskResourceQueryEvent((DiskResourceQueryTemplate) parentFolder));
        } else {
            drService.getSubFolders(parentFolder, new SubFoldersCallback(callback,
                                                                         appearance));
        }
    }

    @Override
    public void setMaskable(IsMaskable maskable) {
        this.isMaskable = maskable;
    }

    void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

}
