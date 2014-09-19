package org.iplantc.de.apps.client.gin;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.theme.gray.client.tree.GrayTreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class AppCategoryTreeProvider implements Provider<Tree<AppCategory, String>> {
    
    private final TreeStore<AppCategory> treeStore;
    
    @Inject
    public AppCategoryTreeProvider(TreeStore<AppCategory> treeStore) {
        this.treeStore = treeStore;
    }

    @Override
    public Tree<AppCategory, String> get() {
        return new Tree<>(treeStore, new ValueProvider<AppCategory, String>() {

            @Override
            public String getValue(AppCategory object) {
                return object.getName() + " (" + object.getAppCount() + ")";
            }

            @Override
            public void setValue(AppCategory object, String value) {
                // do nothing intentionally
            }

            @Override
            public String getPath() {
                return null;
            }
        }, new GrayTreeAppearance());

    }

}
