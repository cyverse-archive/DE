package org.iplantc.de.apps.client.gin;

import org.iplantc.de.client.models.apps.AppGroup;

import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;

public class AppGroupTreeStoreProvider implements Provider<TreeStore<AppGroup>> {

    @Override
    public TreeStore<AppGroup> get() {
       return new TreeStore<AppGroup>(new ModelKeyProvider<AppGroup>() {

        @Override
        public String getKey(AppGroup item) {
                return item.getId();
        }
    });
    }

}
