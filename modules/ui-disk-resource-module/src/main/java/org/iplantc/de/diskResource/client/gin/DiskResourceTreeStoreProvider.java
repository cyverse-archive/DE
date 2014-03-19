package org.iplantc.de.diskResource.client.gin;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.views.DiskResourceModelKeyProvider;

import com.google.inject.Provider;

import com.sencha.gxt.data.shared.TreeStore;

public class DiskResourceTreeStoreProvider implements Provider<TreeStore<Folder>> {

    @Override
    public TreeStore<Folder> get() {
        return new TreeStore<Folder>(new DiskResourceModelKeyProvider());
    }

}
