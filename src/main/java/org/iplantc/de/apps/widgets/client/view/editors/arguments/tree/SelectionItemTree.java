package org.iplantc.de.apps.widgets.client.view.editors.arguments.tree;

import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent.BeforeCheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeView;

import java.util.ArrayList;
import java.util.List;

/**
 * A Checkable Tree for displaying SelectionItem in a wizard.
 * 
 * @author psarando, jstroot
 * 
 */
class SelectionItemTree extends Tree<SelectionItem, String> implements HasValueChangeHandlers<List<SelectionItem>> {
    private ToolTip CORE_4653 = null;
    private boolean forceSingleSelection = false;
    private boolean restoreCheckedSelectionFromTree;
    private SelectionItemGroup root;

    SelectionItemTree(TreeStore<SelectionItem> store, ValueProvider<SelectionItem, String> valueProvider) {
        super(store, valueProvider);


        setBorders(true);
        setAutoLoad(true);

        setCheckable(true);
        setCheckStyle(CheckCascade.TRI);

        addBeforeCheckChangeHandler(new BeforeCheckChangeHandler<SelectionItem>() {
            @Override
            public void onBeforeCheckChange(BeforeCheckChangeEvent<SelectionItem> event) {
                if (!forceSingleSelection) {
                    return;
                }

                if (event.getChecked() == CheckState.UNCHECKED) {
                    boolean isGroup = event.getItem() instanceof SelectionItemGroup;
                    boolean cascadeToChildren = getCheckStyle() == CheckCascade.TRI || getCheckStyle() == CheckCascade.CHILDREN;

                    if (isGroup && cascadeToChildren) {
                        // Do not allow groups to be checked if SingleSelection is enabled and
                        // selections cascade to children.
                        event.setCancelled(true);
                        return;
                    }

                    List<SelectionItem> checked = getCheckedSelection();

                    if (checked != null && !checked.isEmpty()) {
                        // uncheck all other selections first.
                        setCheckedSelection(null);
                    }
                }

            }
        });

        // Store the tree's Checked state in each item's isDefault field.
        addCheckChangeHandler(new CheckChangeHandler<SelectionItem>() {

            @Override
            public void onCheckChange(CheckChangeEvent<SelectionItem> event) {
                SelectionItem ruleArg = event.getItem();
                boolean checked = event.getChecked() == CheckState.CHECKED;
                boolean isGroup = ruleArg instanceof SelectionItemGroup;

                // Don't set the checked value for Groups if the store is filtered, since a check cascade
                // can check a group when its filtered-out children are not checked.
                if (!(checked && isGroup && getStore().isFiltered())) {
                    ruleArg.setDefault(checked);
                }
            }
        });
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<SelectionItem>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Returns items selected in the tree, even if they are currently filtered out by the view.
     * 
     * @return List of selected SelectionItems and groups.
     */
    public List<SelectionItem> getSelection() {
        List<SelectionItem> selected = new ArrayList<SelectionItem>();

        addSelectedFromGroup(selected, root);

        return selected;
    }

    @Override
    protected void onCheckCascade(SelectionItem model, CheckState checked) {
        // temporarily enable group selections during cascade events, if SingleSelection is enabled.
        boolean restoreForceSingleSelection = forceSingleSelection;
        forceSingleSelection = false;

        super.onCheckCascade(model, checked);

        forceSingleSelection = restoreForceSingleSelection;
    }

    @Override
    protected void onCheckClick(Event event, TreeNode<SelectionItem> node) {
        if (getSelectionModel().isLocked()) {
            if (CORE_4653 != null) {
                CORE_4653.showAt(event.getClientX(), event.getClientY());
            }
            return;
        }
        super.onCheckClick(event, node);

        // Keep track of which node the user clicked on in the isDefault field.
        // This helps with restoring checked state if the tree gets filtered, and will allow any checked
        // args to be submitted to the job, even when filtered out.
        SelectionItem ruleArg = node.getModel();
        ruleArg.setDefault(getChecked(ruleArg) != CheckState.UNCHECKED);
    }

    @Override
    protected SafeHtml renderChild(SelectionItem parent, SelectionItem child, int depth,
            TreeView.TreeViewRenderMode renderMode) {
        SafeHtml html = super.renderChild(parent, child, depth, renderMode);

        String tooltip = child.getDescription();
        if (tooltip == null || tooltip.isEmpty()) {
            // This node has no description.
            return html;
        }

        // Apply the description as a tool-tip to this node.
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<span title='" + tooltip + "'>"); //$NON-NLS-1$ //$NON-NLS-2$
        builder.append(html);
        builder.appendHtmlConstant("</span>"); //$NON-NLS-1$

        return builder.toSafeHtml();
    }

    void setCore4653Kludge() {
        ToolTipConfig ttc = new ToolTipConfig("To make default selections, use the \"" + I18N.APPS_LABELS.treeSelectionCreateLabel() + "\" button located in the details panel.");
        ttc.setShowDelay(10000);
        CORE_4653 = new ToolTip(this, ttc);
    }

    /**
     * Resets the tree's contents with the SelectionItems in the given root.
     * 
     * @param root A SelectionItemGroup containing the items to populate in this tree.
     */
    void setItems(SelectionItemGroup root) {
        store.clear();
        this.root = root;

        if (root == null) {
            return;
        }

        forceSingleSelection = root.isSingleSelect();
        setCheckCascade(root);

        List<SelectionItem> defaultSelection = new ArrayList<SelectionItem>();

        if (root.getGroups() != null) {
            for (SelectionItemGroup group : root.getGroups()) {
                List<SelectionItem> addGroupToStore = addGroupToStore(null, group);
                if (restoreCheckedSelectionFromTree) {
                    defaultSelection.addAll(addGroupToStore);
                }
            }
        }

        if (root.getArguments() != null) {
            for (SelectionItem ruleArg : root.getArguments()) {
                updateOrAdd(ruleArg);

                if (restoreCheckedSelectionFromTree && ruleArg.isDefault()) {
                    defaultSelection.add(ruleArg);
                } else if (!restoreCheckedSelectionFromTree && ruleArg.isDefault()) {
                    ruleArg.setDefault(false);
                }
            }
        }

        if (restoreCheckedSelectionFromTree && !defaultSelection.isEmpty()) {
            setCheckedSelection(defaultSelection);
        }
    }

    /**
     * Sets a variable which controls whether the tree's checked selections will be restored from a given
     * tree in {@link #setItems(SelectionItemGroup)}.
     * 
     * @param restoreCheckedSelectionFromTree if true, the tree's selections will be set from the items
     *            marked as "isDefault" true in the given tree passed into
     *            {@link #setItems(SelectionItemGroup)}
     */
    void setRestoreCheckedSelectionFromTree(boolean restoreCheckedSelectionFromTree) {
        this.restoreCheckedSelectionFromTree = restoreCheckedSelectionFromTree;
    }

    void setSelection(List<SelectionItem> items) {

        for (SelectionItem si : items) {
            SelectionItem found = getStore().findModelWithKey(si.getId());
            if (found != null) {
                setChecked(found, CheckState.CHECKED);
                setExpanded(found, true);
            } else {
                GWT.log("SelectionItemTree.setSelection(List<SelectionItem>) => Given SelectionItem could not be found. SelectionItem = \""
                        + AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(si)).getPayload());
            }
        }
    }

    /**
     * Adds the group to the parent, or as a root if parent is null, then adds group's children to the
     * store. Returns a list of any items that should be selected by default.
     * 
     * @param parent The parent of group.
     * @param group The group to add to the store.
     * @return A list of any items that should be selected by default.
     */
    private List<SelectionItem> addGroupToStore(SelectionItemGroup parent, SelectionItemGroup group) {
        List<SelectionItem> defaultSelection = new ArrayList<SelectionItem>();

        if (group != null) {
            if (parent != null) {
                updateOrAdd(parent, group);
            } else {
                updateOrAdd(group);
            }

            setLeaf(group, false);

            if (group.getGroups() != null) {

                for (SelectionItemGroup child : group.getGroups()) {
                    List<SelectionItem> addGroupToStore = addGroupToStore(group, child);
                    if (restoreCheckedSelectionFromTree) {
                        defaultSelection.addAll(addGroupToStore);
                    }
                }
            }

            if (group.getArguments() != null) {
                for (SelectionItem child : group.getArguments()) {
                    updateOrAdd(group, child);

                    if (restoreCheckedSelectionFromTree && child.isDefault()) {
                        defaultSelection.add(child);
                    } else if (!restoreCheckedSelectionFromTree && child.isDefault()) {
                        child.setDefault(false);
                    }
                }
            }

            if (restoreCheckedSelectionFromTree && group.isDefault()) {
                defaultSelection.add(group);
            }
        }

        return defaultSelection;
    }

    private void addSelectedFromGroup(List<SelectionItem> selected, SelectionItemGroup group) {
        if (group == null) {
            return;
        }

        if (group.getArguments() != null) {
            for (SelectionItem ruleArg : group.getArguments()) {
                if (ruleArg.isDefault()) {
                    selected.add(ruleArg);
                }
            }
        }

        if (group.getArguments() != null) {
            for (SelectionItemGroup subgroup : group.getGroups()) {
                if (subgroup.isDefault()) {
                    selected.add(subgroup);
                }

                addSelectedFromGroup(selected, subgroup);
            }
        }
    }

    private void setCheckCascade(SelectionItemGroup root) {
        CheckCascade cascade = null;

        if (root != null && root.getSelectionCascade() != null) {
            cascade = CheckCascade.valueOf(root.getSelectionCascade().name());
        }

        if (cascade != null) {
            setCheckStyle(cascade);
        }
    }

    private void updateOrAdd(SelectionItem item) {
        SelectionItem findModelWithKey = store.findModelWithKey(item.getId());
        if (findModelWithKey != null) {
            store.update(item);
        } else {
            store.add(item);
        }
    }

    private void updateOrAdd(SelectionItemGroup parent, SelectionItem child) {
        SelectionItem findModelWithKey = store.findModelWithKey(child.getId());
        if ((findModelWithKey != null) && (store.getParent(child) == parent)) {
            store.update(child);
        } else {
            store.add(parent, child);
        }
    }
}
