package org.iplantc.de.commons.client.widgets;

import com.google.common.base.Preconditions;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * @author aramsey
 *
 * This is basically a clone-and-one of Sencha's Tree, but with the ability
 * to define a way to set the debug IDs on the TreeNodes.
 *
 * A class will need to extend DETree and fill out the generateDebugId method
 * The view that uses the class then has to call tree.ensureDebugId(id) in the
 * view's onEnsureDebugId(String baseID) method like you would for any other
 * UI component
 */
public abstract class DETree<M, C> extends Tree<M, C> {

    private String baseId;

    public DETree(TreeStore<M> store, ValueProvider<? super M, C> valueProvider) {
        super(store, valueProvider);
    }

    public DETree(TreeStore<M> store,
                  ValueProvider<? super M, C> valueProvider,
                  TreeAppearance appearance) {
        super(store, valueProvider, appearance);
    }

    public static class DETreeNode<M> extends TreeNode<M> {

        protected DETreeNode(String modelId, M m, String domId) {
            super(modelId, m, domId);
        }
    }

    @Override
    protected String register(M m) {
        String id = generateModelId(m);
        if (nodes.containsKey(id)) {
            return nodes.get(id).getDomId();
        } else {
            String domId = generateDebugId(m);
            TreeNode<M> node = new DETreeNode<>(id, m, domId);
            nodes.put(id, node);
            nodesByDom.put(domId, node);
            return domId;
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        this.baseId = baseID;
    }

    public String getBaseId() {
        Preconditions.checkNotNull(baseId);
        return baseId;
    }

    public abstract String generateDebugId(M m);
}
