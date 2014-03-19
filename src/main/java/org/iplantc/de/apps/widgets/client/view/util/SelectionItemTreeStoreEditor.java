package org.iplantc.de.apps.widgets.client.view.util;

import org.iplantc.de.apps.widgets.client.view.util.SelectionItemValueChangeStoreHandler.HasEventSuppression;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.client.util.AppTemplateUtils;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;

import java.util.List;

/**
 * Binds a {@link TreeStore} of {@link SelectionItem}s to a {@link List} property in an edited model
 * object.
 * This class is modeled after {@link ListStoreEditor}.
 * <p>
 * If bound to a null value, no changes will be made when flushed.
 * </p>
 * 
 * @author jstroot
 * 
 */
public abstract class SelectionItemTreeStoreEditor implements ValueAwareEditor<List<SelectionItem>>, HasEventSuppression {
    boolean suppressValueChangeEventFire = false;
    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private List<SelectionItem> model;
    private final TreeStore<SelectionItem> store;

    public SelectionItemTreeStoreEditor(TreeStore<SelectionItem> store, HasValueChangeHandlers<List<SelectionItem>> valueChangeTarget) {
        this.store = store;
        store.addStoreHandlers(new SelectionItemValueChangeStoreHandler(this, valueChangeTarget) {

            @Override
            protected List<SelectionItem> getCurrentValue() {
                return Lists.<SelectionItem> newArrayList(getCurrentTree());
            }
        });
    }

    @Override
    public void flush() {
        if (!shouldFlush()) {
            return;
        }
        setSuppressEvent(true);
        store.commitChanges();

        if (model != null) {
            model.clear();
            model.add(getCurrentTree());
        }
        setSuppressEvent(false);
    }

    public SelectionItemGroup getCurrentTree() {
        SelectionItemGroup root = factory.selectionItemGroup().as();

        CheckCascade checkStyle = getCheckStyle();
        boolean singleSelect = getSingleSelect();
        root.setSelectionCascade(checkStyle);
        root.setSingleSelect(singleSelect);

        List<SelectionItem> siList = Lists.newArrayList();
        List<SelectionItemGroup> siGroupsList = Lists.newArrayList();
        for (SelectionItem si : store.getRootItems()) {
            if (si instanceof SelectionItemGroup) {
                siGroupsList.add((SelectionItemGroup)si);
            } else {
                siList.add(si);
            }
        }
        root.setArguments(siList);
        root.setGroups(siGroupsList);
        return root;
    }

    public TreeStore<SelectionItem> getStore() {
        return store;
    }

    @Override
    public boolean isSuppressEvent() {
        return suppressValueChangeEventFire;
    }


    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<List<SelectionItem>> delegate) {
        // ignore for now, this could be used to pass errors into the view
    }

    @Override
    public void setSuppressEvent(boolean suppressValueChangeEventFire) {
        this.suppressValueChangeEventFire = suppressValueChangeEventFire;
    }

    @Override
    public void setValue(List<SelectionItem> value) {
        if ((value == null) || (value.size() != 1)) {

            return;
        }
        setSuppressEvent(true);
        model = value;
        SelectionItemGroup newRoot = null;

        /*
         * JDS This list of SelectionItems is for TreeSelection, and therefore, should only consist of
         * one element which can be cast to a SelectionItemGroup. This item is used to set the selection
         * and selection cascade modes of the tree.
         */
        if (value.size() == 1) {
            /*
             * JDS Have to deserialize the root SelectionItem and reserialize it to a SelectionItemGroup
             * since it is illegal to "downcast" types (we can't directly cast from SelectionItem to
             * SelectionItemGroup
             */
            Splittable newSplit = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(value.get(0)));
            newRoot = AutoBeanCodex.decode(factory, SelectionItemGroup.class, newSplit).as();
            if (store.getRootItems().size() == 1) {
                // Then we want to compare the new with the old
                Splittable currSplit = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(store.getRootItems().get(0)));
                SelectionItemGroup currRoot = AutoBeanCodex.decode(factory, SelectionItemGroup.class, currSplit).as();
                if (!hasChanged(currRoot, newRoot)) {

                    return;
                }

            }

        } else {
            GWT.log("SelectionItemTreeStoreEditor" + ".setValue(List<SelectionItem>) given list which is not equal to 1.");
            return;
        }
        // JDS Populate TreeStore.
        setItems(newRoot);

        // JDS Get the "isSingleSelect" and "selectionCascade" items from the root SelectionItemGroup
        setSingleSelect(newRoot.isSingleSelect());

        // JDS Propagate tree check style.
        setCheckStyle(newRoot.getSelectionCascade());
        setSuppressEvent(false);
    }

    protected abstract CheckCascade getCheckStyle();

    protected abstract boolean getSingleSelect();

    protected abstract void setCheckStyle(CheckCascade checkCascade);

    protected abstract void setItems(SelectionItemGroup root);

    protected abstract void setSingleSelect(boolean singleSelect);

    protected abstract boolean shouldFlush();

    private boolean hasChanged(SelectionItemGroup currSig, SelectionItemGroup newSig) {

        // Verify the getArguments() child collections
        boolean a = currSig.getArguments() == null;
        boolean b = newSig.getArguments() == null;
        if ((a) ^ (b)) {
            // Both arguments collections need to be null or !null, but aren't
            return true;
        } else if (!a && !b) {
            if (currSig.getArguments().size() != newSig.getArguments().size()) {
                return true;
            }
            for (int i = 0; i < currSig.getArguments().size(); i++) {
                if (!AppTemplateUtils.areEqual(currSig.getArguments().get(i), newSig.getArguments().get(i))) {
                    return true;
                }
            }
        }

        if (currSig.getSelectionCascade() != newSig.getSelectionCascade()) {
            return true;
        }
        if (currSig.isSingleSelect() ^ newSig.isSingleSelect()) {
            return true;
        }

        // Verify the getGroups() child collections
        boolean c = currSig.getGroups() == null;
        boolean d = newSig.getGroups() == null;
        if ((c) ^ (d)) {
            // Both groups collections need to be null or !null, but aren't
            return true;
        } else if (!c && !d) {
            if (currSig.getGroups().size() != newSig.getGroups().size()) {
                return true;
            }
            for (int j = 0; j < currSig.getGroups().size(); j++) {
                SelectionItemGroup currChildGrp = currSig.getGroups().get(j);
                SelectionItemGroup newChildGrp = newSig.getGroups().get(j);
                if (!currChildGrp.getDescription().equals(newChildGrp.getDescription())) {
                    return true;
                }
                if (!currChildGrp.getDisplay().equals(newChildGrp.getDisplay())) {
                    return true;
                }
                if (!currChildGrp.getId().equals(newChildGrp.getId())) {
                    return true;
                }
                if (!currChildGrp.getName().equals(newChildGrp.getName())) {
                    return true;
                }
                if (currChildGrp.isDefault() ^ newChildGrp.isDefault()) {
                    return true;
                }
                if (hasChanged(currChildGrp, newChildGrp)) {
                    return true;
                }
            }
        }

        return false;

    }

}
