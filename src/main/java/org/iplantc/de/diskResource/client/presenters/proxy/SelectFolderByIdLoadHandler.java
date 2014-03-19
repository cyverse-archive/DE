package org.iplantc.de.diskResource.client.presenters.proxy;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.diskResource.client.views.HasHandlerRegistrationMgmt;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A <code>LoadHandler</code> which is used to lazily load, expand, and select a desired folder.
 * 
 * 
 * @author jstroot
 * 
 */
public class SelectFolderByIdLoadHandler implements LoadHandler<Folder, List<Folder>> {

    private final Stack<String> pathsToLoad = new Stack<String>();
    private final LinkedList<String> path;
    private boolean rootsLoaded;

    private final HasId folderToSelect;

    private final DiskResourceView view;
    private final DiskResourceView.Presenter presenter;
    private final HasHandlerRegistrationMgmt regMgr;
    private final IplantAnnouncer announcer;

    public SelectFolderByIdLoadHandler(final HasId folderToSelect,
            final DiskResourceView.Presenter presenter, final IplantAnnouncer announcer) {
        presenter.mask(""); //$NON-NLS-1$
        this.folderToSelect = folderToSelect;
        this.presenter = presenter;
        this.regMgr = presenter;
        this.view = presenter.getView();
        this.announcer = announcer;

        // Split the string on "/"
        path = Lists.newLinkedList(Splitter.on("/").trimResults().omitEmptyStrings().split(folderToSelect.getId())); //$NON-NLS-1$

        rootsLoaded = view.getTreeStore().getAllItemsCount() > 0;
        if (rootsLoaded) {
            initPathsToLoad();
        }
    }

    private String getNextPathToLoad() {
        return "/".concat(Joiner.on("/").join(path)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * {@inheritDoc}
     * 
     * This method will be called whenever folders are loaded in the navigation tree of the
     * {@link DiskResourceView}, which may include after the root folders are initially loaded.
     * 
     * <ul>
     * This method will handle these possible cases, and either select the target folder, or trigger a
     * load of one of its parents.
     * <li>The root folders have just been loaded.</li>
     * <li>Some parent of the target folder has just been loaded.</li>
     * <li>Some parent of the target folder has been loaded and its children have already been loaded.</li>
     * <li>The target folder has just been loaded.</li>
     * </ul>
     */
    @Override
    public void onLoad(LoadEvent<Folder, List<Folder>> event) {
        if (!rootsLoaded) {
            // Folders must have been loaded to have this method called. Set this flag before calling
            // initPathsToLoad, since it may attempt to load sub-folders, which may not be an async call,
            // which will in turn call this method again before initPathsToLoad returns.
            rootsLoaded = true;
            initPathsToLoad();
            return;
        }

        path.add(pathsToLoad.pop());
        Folder folder = view.getFolderById(getNextPathToLoad());

        if (folder != null) {
            if (pathsToLoad.isEmpty()) {
                // Exit condition
                view.setSelectedFolder(folder);
                unmaskView();
            } else {
                // Trigger remote load by expanding folder
                view.expandFolder(folder);
            }
        } else {
            // This handler has loaded as much as it can, but has encountered a folder along the path
            // that does not exist. Select the last folder loaded, then report the error.
            String folderName = SafeHtmlUtils.htmlEscape(path.getLast());
            SafeHtml errMsg = SafeHtmlUtils.fromTrustedString(I18N.ERROR
                    .diskResourceDoesNotExist(folderName));
            announcer.schedule(new ErrorAnnouncementConfig(errMsg));

            view.setSelectedFolder(event.getLoadConfig());
            unmaskView();
        }
    }

    /**
     * Verify if the desired selected folder has already been loaded in the {@link DiskResourceView}.
     * This only needs to occur once, but only after the root folders have been loaded. This method will
     * determine how much of the target folder's path has already been loaded, and if a parent of the
     * target folder needs to be expanded in order to trigger a load of its children, or a refresh to
     * force a reload of its children, in order to start the {@link #onLoad(LoadEvent)} callbacks.
     */
    private void initPathsToLoad() {
        Folder folder = view.getFolderById(folderToSelect.getId());
        // Find the paths which are not yet loaded, and push them onto the 'pathsToLoad' stack
        while ((folder == null) && !path.isEmpty()) {
            pathsToLoad.push(path.removeLast());
            folder = view.getFolderById(getNextPathToLoad());
        }

        if (folder == null) {
            // If no folders could be found in view
            unmaskView();
        } else {
            // A folder along the path to load has been found.
            if (folder.getPath().equals(folderToSelect.getId())) {
                // Exit condition: The target folder has already been loaded, so just select it.
                if (!folder.equals(presenter.getSelectedFolder())) {
                    view.setSelectedFolder(folder);
                }
                unmaskView();
            } else if (view.isLoaded(folder)) {
                // One of the target folder's parents already has its children loaded, but the target
                // wasn't found, so refresh that parent.
                refreshFolder(folder);
            } else {
                // Once a valid folder is found in the view, remotely load the folder, which will add the
                // next folder in the path to the view's treeStore.
                view.expandFolder(folder);
            }
        }
    }

    void refreshFolder(final Folder folder) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                // The refresh event must be deferred, since it's possible that the presenter
                // was initialized, and we reached this point, while the EventBus was
                // handling other events (such as showing the Data window). This means the
                // presenter's refresh handler will be deferred and will not handle this
                // refresh event.
                presenter.doRefresh(folder);
            }
        });
    }

    void unmaskView() {
        regMgr.unregisterHandler(this);
        presenter.unmask();
    }
}