package org.iplantc.de.apps.client.gin;

import org.iplantc.de.client.models.apps.AppGroup;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.theme.gray.client.tree.GrayTreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class AppGroupTreeProvider implements Provider<Tree<AppGroup, String>> {
    
    private final TreeStore<AppGroup> treeStore;
    
    @Inject
    public AppGroupTreeProvider(TreeStore<AppGroup> treeStore) {
        this.treeStore = treeStore;
    }

    @Override
    public Tree<AppGroup, String> get() {
        return new Tree<AppGroup, String>(treeStore, new ValueProvider<AppGroup, String>() {

            @Override
            public String getValue(AppGroup object) {
                return object.getName() + " (" + object.getAppCount() + ")";
            }

            @Override
            public void setValue(AppGroup object, String value) {
                // do nothing intentionally
            }

            @Override
            public String getPath() {
                return null;
            }
        }, new GrayTreeAppearance());

    }

}
