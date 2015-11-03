package org.iplantc.de.apps.integration.client.view.propertyEditors.widgets;

import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;

import java.util.ArrayList;
import java.util.List;

final class SelectionItemTreePropertyEditorDnDHandler implements DndDropHandler, DndDragStartHandler {
    private SelectionItemGroup dragParent;
    /**
     * 
     */
    private final SelectionItemTreePropertyEditor treeEditor;

    /**
     * @param selectionItemTreePropertyEditor
     */
    SelectionItemTreePropertyEditorDnDHandler(SelectionItemTreePropertyEditor selectionItemTreePropertyEditor) {
        this.treeEditor = selectionItemTreePropertyEditor;
    }

    @Override
    public void onDragStart(DndDragStartEvent event) {
        SelectionItem selection = getDragSelection((List<?>)event.getData());
        if (selection != null) {
            dragParent = (SelectionItemGroup)treeEditor.treeGrid.getTreeStore().getParent(selection);
        } else {
            dragParent = null;
        }
    }

    @Override
    public void onDrop(DndDropEvent event) {
        SelectionItem selection = getDragSelection((List<?>)event.getData());
        if (selection == null) {
            return;
        }

        SelectionItemGroup newParent = (SelectionItemGroup)treeEditor.store.getParent(selection);

        if (dragParent != null && newParent != dragParent) {
            if (selection instanceof SelectionItemGroup) {
                dragParent.getGroups().remove(selection);
            } else {
                dragParent.getArguments().remove(selection);
            }

            // Updating the store may not be needed for the grid, but it calls any update
            // commands added to store handlers.
            treeEditor.store.update(dragParent);
        }

        if (newParent != null) {
            // Reorder the new parent's children to match the store's order.
            List<SelectionItemGroup> groups = newParent.getGroups();
            if (groups == null) {
                groups = new ArrayList<SelectionItemGroup>();
                newParent.setGroups(groups);
            } else {
                groups.clear();
            }

            List<SelectionItem> arguments = newParent.getArguments();
            if (arguments == null) {
                arguments = new ArrayList<SelectionItem>();
                newParent.setArguments(arguments);
            } else {
                arguments.clear();
            }

            for (SelectionItem arg : treeEditor.store.getChildren(newParent)) {
                if (arg instanceof SelectionItemGroup) {
                    groups.add((SelectionItemGroup)arg);
                } else {
                    arguments.add(arg);
                }
            }

            // Updating the store may not be needed for the grid, but it calls any update
            // commands added to store handlers.
            treeEditor.store.update(newParent);
        }
    }

    /**
     * A helper method for getting the selected item from a drag-start or drop event.
     * 
     * @param items
     * @return
     */
    @SuppressWarnings("unchecked")
    private SelectionItem getDragSelection(List<?> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        SelectionItem selection;
        if (items.get(0) instanceof TreeStore.TreeNode) {
            selection = ((TreeStore.TreeNode<SelectionItem>)items.get(0)).getData();
        } else {
            selection = (SelectionItem)items.get(0);
        }

        return selection;
    }
}