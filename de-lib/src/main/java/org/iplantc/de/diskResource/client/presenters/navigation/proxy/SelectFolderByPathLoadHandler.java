package org.iplantc.de.diskResource.client.presenters.navigation.proxy;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.selection.RefreshFolderSelected;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
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
 * @author psarando, jstroot
 * 
 */
public class SelectFolderByPathLoadHandler implements LoadHandler<Folder, List<Folder>> {

    private final IsMaskable maskable;
    private final Stack<String> pathsToLoad = new Stack<>();
    private final LinkedList<String> path;
    private final RefreshFolderSelected.RefreshFolderSelectedHandler refreshHandler;
    private final NavigationView.Presenter.Appearance appearance;
    HandlerRegistration handlerRegistration;
    private boolean rootsLoaded;

    private final HasPath folderToSelect;

    private final NavigationView.Presenter navigationPresenter;
    private final IplantAnnouncer announcer;

    SelectFolderByPathLoadHandler(final HasPath folderToSelect,
                                  final NavigationView.Presenter navigationPresenter,
                                  final RefreshFolderSelected.RefreshFolderSelectedHandler refreshHandler,
                                  final NavigationView.Presenter.Appearance appearance,
                                  final IsMaskable maskable,
                                  final IplantAnnouncer announcer,
                                  final HandlerRegistration handlerRegistration) {
        this.appearance = appearance;
        this.handlerRegistration = handlerRegistration;

        this.maskable = maskable;
        maskable.mask(""); //$NON-NLS-1$
        this.folderToSelect = folderToSelect;
        this.navigationPresenter = navigationPresenter;
        this.refreshHandler = refreshHandler;
        this.announcer = announcer;

        // Split the string on "/"
        path = Lists.newLinkedList(Splitter.on("/")
                                           .trimResults()
                                           .omitEmptyStrings()
                                           .split(folderToSelect.getPath()));
    }

    SelectFolderByPathLoadHandler(final HasPath folderToSelect,
                                  final NavigationView.Presenter navigationPresenter,
                                  final RefreshFolderSelected.RefreshFolderSelectedHandler refreshHandler,
                                  final NavigationView.Presenter.Appearance appearance,
                                  final IsMaskable maskable,
                                  final IplantAnnouncer announcer) {
        this(folderToSelect, navigationPresenter, refreshHandler, appearance, maskable, announcer, null);
    }

    /**
     * This method will instantiate a SelectFolderByPathLoadHandler, add it to the given loader,
     * then initialize the lazy-loading logic. This method guarantees that the LoadHandler is added
     * to the Folder Loader before lazy-loading logic is initialized, since folders may be cached
     * and the {@link #onLoad(LoadEvent)} method may need to be called immediately during
     * initialization.
     *
     * @param folderToSelect The folder to load and select, potentially after many
     *                       {@link #onLoad(LoadEvent)} method calls.
     * @param navigationPresenter The presenter that has the folder tree and will trigger
     *                            lazy-loading by expanding folders.
     * @param refreshHandler The handler that may have to refresh a folder if it's already loaded
     *                       but a target child is not found.
     * @param appearance The appearance that has folder loading error messages.
     * @param maskable The view to unmask once lazy-loading is complete.
     * @param announcer The announcer that displays loading error messages.
     * @param loader The Folder Loader in which this LoadHandler will be registered.
     */
    public static void registerFolderLoader(final HasPath folderToSelect,
                                            final NavigationView.Presenter navigationPresenter,
                                            final RefreshFolderSelected.RefreshFolderSelectedHandler refreshHandler,
                                            final NavigationView.Presenter.Appearance appearance,
                                            final IsMaskable maskable,
                                            final IplantAnnouncer announcer,
                                            HasLoadHandlers<Folder, List<Folder>> loader) {
        if (loader == null) {
            return;
        }

        SelectFolderByPathLoadHandler handler = new SelectFolderByPathLoadHandler(folderToSelect,
                                                                                  navigationPresenter,
                                                                                  refreshHandler,
                                                                                  appearance,
                                                                                  maskable,
                                                                                  announcer);
        // Must add the LoadHandler to the Folder Loader before initPathsToLoad is called!
        // Folders may be cached and the onLoad method may need to be called immediately during init.
        handler.handlerRegistration = loader.addLoadHandler(handler);
        handler.initPathsToLoad();
    }

    private String getNextPathToLoad() {
        return "/".concat(Joiner.on("/").join(path));
    }

    /**
     * Verify if the desired selected folder has already been loaded in the {@link NavigationView}.
     * This only needs to occur once, but only after the root folders have been loaded. This method will
     * determine how much of the target folder's path has already been loaded, and if a parent of the
     * target folder needs to be expanded in order to trigger a load of its children, or a refresh to
     * force a reload of its children, in order to start the {@link #onLoad(LoadEvent)} callbacks.
     *
     * Must be called after this handler has been registered.
     */
    protected void initPathsToLoad() {
        rootsLoaded = navigationPresenter.rootsLoaded();
        if (!rootsLoaded) {
            return;
        }

        // Check if the requested folder's path is under a known root path.
        if (!navigationPresenter.isPathUnderKnownRoot(folderToSelect.getPath())) {
            String errMsg = appearance.diskResourceDoesNotExist(folderToSelect.getPath());
            announcer.schedule(new ErrorAnnouncementConfig(SafeHtmlUtils.fromTrustedString(errMsg)));

            unmaskView();
            return;
        }

        Folder folder = navigationPresenter.getFolderByPath(folderToSelect.getPath());
        // Find the paths which are not yet loaded, and push them onto the 'pathsToLoad' stack
        while ((folder == null) && !path.isEmpty()) {
            pathsToLoad.push(path.removeLast());
            folder = navigationPresenter.getFolderByPath(getNextPathToLoad());
        }

        if (folder == null) {
            // If no folders could be found in view
            unmaskView();
        } else {
            // A folder along the path to load has been found.
            if (folder.getPath().equals(folderToSelect.getPath())) {
                // Exit condition: The target folder has already been loaded, so just select it.
                if (!folder.equals(navigationPresenter.getSelectedFolder())) {
                    navigationPresenter.setSelectedFolder(folder);
                }
                unmaskView();
            } else if (navigationPresenter.isLoaded(folder)) {
                // One of the target folder's parents already has its children loaded, but the target
                // wasn't found, so refresh that parent.
                refreshFolder(folder);
            } else {
                // Once a valid folder is found in the view, remotely load the folder, which will add the
                // next folder in the path to the view's treeStore.
                navigationPresenter.expandFolder(folder);
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
                refreshHandler.onRefreshFolderSelected(new RefreshFolderSelected(folder));
            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * This method will be called whenever folders are loaded in the navigation tree of the
     * {@link NavigationView}, which may include after the root folders are initially loaded.
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
            // This onLoad was triggered by the initial load of the root folders.
            initPathsToLoad();
            return;
        }

        path.add(pathsToLoad.pop());
        Folder folder = navigationPresenter.getFolderByPath(getNextPathToLoad());

        if (folder != null) {
            if (pathsToLoad.isEmpty()) {
                // Exit condition
                navigationPresenter.setSelectedFolder(folder);
                unmaskView();
            } else {
                // Trigger remote load by expanding folder
                navigationPresenter.expandFolder(folder);
            }
        } else {
            // This handler has loaded as much as it can, but has encountered a folder along the path
            // that does not exist. Select the last folder loaded, then report the error.
            String folderName = SafeHtmlUtils.htmlEscape(path.getLast());
            SafeHtml errMsg = SafeHtmlUtils.fromTrustedString(appearance.diskResourceDoesNotExist(folderName));
            announcer.schedule(new ErrorAnnouncementConfig(errMsg));

            navigationPresenter.setSelectedFolder(event.getLoadConfig());
            unmaskView();
        }
    }

    void unmaskView() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
        maskable.unmask();
    }
}
