package org.iplantc.de.admin.apps.client.gin;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;

public class AppCategoryTreeStoreProvider implements Provider<TreeStore<AppCategory>> {

    @Override
    public TreeStore<AppCategory> get() {
       return new TreeStore<>(new ModelKeyProvider<AppCategory>() {

        @Override
        public String getKey(AppCategory item) {
                return item.getId();
        }
    });
    }

}
