package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.models.diskResources.Folder;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;

import java.util.List;

/**
 * A LoadHandler, based on {@link ChildTreeStoreBinding}, that will recursively load all subfolders found
 * in the load event's results into the Folder TreeStore, since folders and their children may be cached
 * by the service facade. This will also prevent a view from firing a Refresh event if some child isn't
 * already loaded in the TreeStore, even though it may be cached by the service facade (a Refresh event
 * will cause the folder to be loaded from the service regardless its cached status).
 * 
 * @author psarando
 * 
 */
public class CachedFolderTreeStoreBinding implements LoadHandler<Folder, List<Folder>> {
    private final TreeStore<Folder> store;

    /**
     * Creates a {@link LoadEvent} handler for the given {@link TreeStore}.
     * 
     * @param store the store whose events will be handled
     */
    public CachedFolderTreeStoreBinding(TreeStore<Folder> store) {
        this.store = store;
    }

    @Override
    public void onLoad(LoadEvent<Folder, List<Folder>> event) {
        replaceSubfolders(event.getLoadConfig(), event.getLoadResult());
    }

    private void replaceSubfolders(Folder parent, List<Folder> subfolders) {
        if (subfolders != null) {
            store.replaceChildren(parent, subfolders);
            for (Folder folder : subfolders) {
                replaceSubfolders(folder, folder.getFolders());
            }
        }
    }
}
