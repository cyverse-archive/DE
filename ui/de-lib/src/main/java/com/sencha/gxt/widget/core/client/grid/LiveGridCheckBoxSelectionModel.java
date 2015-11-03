package com.sencha.gxt.widget.core.client.grid;

import org.iplantc.de.client.models.diskResources.DiskResource;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent;
import com.sencha.gxt.widget.core.client.event.RowClickEvent;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A checkbox selection model which supports selection persistence for LiveGridViews. This class can only
 * be used with {@code Grid}s whose view is a {@code LiveGridView}. The {@link #bindGrid(Grid)} method
 * will throw an error otherwise. Also, the supported number of individual selections is limited to the
 * bound {@code LiveGridView}'s cache size.
 * <p/>
 * This effectively gives this selection model two different states; when the total number of items in
 * the bound collection (determined by {@link PagingLoader#getTotalCount()}) <b>does not</b> exceed the
 * {@code LiveGridView} cache size, and when the total number of items <b>does</b> exceed the cache size.
 * <p/>
 * <h2>Operation when total number of items <u><i>does not exceed</i></u> cache size</h2>
 * When the user selects individual items, they will remain selected regardless of whether they are
 * visible or not. <br/>
 * If the select all checkbox is enabled, all items will be selected, and attempts to retrieve the
 * selection will reflect what is visually selected. Classes which utilize this selection model <b>must
 * not</b> rely on {@link #getSelectedItems()} or {@link #getSelection()} returning all items which are
 * visually selected. See next section for details.
 * <p/>
 * <h2>Operation when total number of items <u><i>exceeds</i></u> cache size</h2>
 * Individually selected items will be persisted as before, unless the number of selected items exceeds
 * the cache size. When this happens, the user will be prevented from making selections which will result
 * in a selection size exceeding the cache size. <br/>
 * If the select all checkbox is enabled, select all will make all items in the view appear to be
 * selected, but attempts to retrieve the selected items will result in an empty list. It is the
 * responsibility of classes using this selection model to check whether the select all checkbox is or is
 * not selected, and act accordingly. For all intents and purposes, the select all checkbox should be
 * considered a selection which covers the source of the currently bound selection (i.e. the contents of
 * a selected {@link org.iplantc.de.client.models.diskResources.Folder}) <br/>
 * If the user clicks the select all checkbox, and then tries to de-select a single item (which would
 * conceptually result in a selection greater than the cache size) the de-selection will be prevented.
 *
 * @author jstroot
 * @see LiveGridView#getCacheSize()
 * @see PagingLoader#getTotalCount()
 */
public class LiveGridCheckBoxSelectionModel extends CheckBoxSelectionModel<DiskResource> implements
                                                                                         LiveGridViewUpdateEvent.LiveGridViewUpdateHandler {

    private static class IsNotFilteredPredicate implements Predicate<DiskResource> {
        @Override
        public boolean apply(@Nullable DiskResource input) {
            return !input.isFilter();
        }
    }

    @Override
    public void bindGrid(Grid<DiskResource> grid) {
        checkArgument(grid.getView() instanceof LiveGridView, "the grid's view must be a LiveGridView");
        super.bindGrid(grid);
        // Handle LiveGridViewUpdate events
        final GridView<DiskResource> gridView = grid.getView();
        if (gridView instanceof LiveGridViewUpdateEvent.HasLiveGridViewUpdateHandlers) {
            ((LiveGridViewUpdateEvent.HasLiveGridViewUpdateHandlers) gridView).addLiveGridViewUpdateHandler(this);
        }
    }

    public void clear() {
        super.onClear(null);
    }

    /**
     * Return the number of currently selected items. If the select all checkbox is checked and the total
     * number of items exceeds the bound {@link LiveGridView#getCacheSize()}, then the total number of
     * items will be returned. Otherwise, the {@link #selected} collection's size will be returned.
     *
     * @return the number of currently selected items, virtual or not.
     */
    public int getSelectedCount() {
        if (!isSupportedSelectionSize() && isSelectAllChecked()) {
            return ((PagingLoader<?, ?>) grid.getLoader()).getTotalCount();
        }

        return selected.size();
    }

    @Override
    public List<DiskResource> getSelectedItems() {
        // Return empty list if selection is greater than LiveGridView cache size
        if (!isSupportedSelectionSize() && isSelectAllChecked()) {
            return Collections.emptyList();
        }
        return super.getSelectedItems();
    }

    @Override
    public boolean isSelected(final DiskResource item) {
        if (item.isFilter()) {
            return false;
        }
        // It is selected if it is in the selection or select all is checked
        final ModelKeyProvider<? super DiskResource> keyProvider = store.getKeyProvider();
        final String itemKey = keyProvider.getKey(item);
        boolean isInSelectedList = false;
        for (DiskResource dr : selected) {
            final String key = keyProvider.getKey(dr);
            if (key.equals(itemKey)) {
                isInSelectedList = true;
                break;
            }
        }
        if (isSupportedSelectionSize()) {
            return isInSelectedList;
        } else {

            return isSelectAllChecked() || isInSelectedList;
        }
    }

    @Override
    public void onUpdate(LiveGridViewUpdateEvent event) {
        refresh();
    }

    /**
     * Necessary for maintaining selections after view resizes, etc.
     */
    @Override
    public void refresh() {
        List<DiskResource> sel = Lists.newArrayList();

        for (DiskResource dr : store.getAll()) {
            if (isSelected(dr)) {
                sel.add(dr);
            }
        }
        lastSelected = null;
        setLastFocused(null);
        doSelect(sel, Style.SelectionMode.MULTI.equals(selectionMode), true);
    }

    @Override
    public void selectAll() {
        select(getCacheStore().getAll(), true);
    }

    @Override
    protected void doMultiSelect(List<DiskResource> models, boolean keepExisting,
                                 boolean suppressEvent) {
        if (locked)
            return;
        boolean change = false;
        if (!keepExisting && selected.size() > 0) {
            change = true;
            doDeselect(new ArrayList<>(selected), true);
        }
        for (DiskResource m : models) {
            if (m.isFilter()) {
                continue;
            }
            boolean isSelected = isSelected(m);
            if (!suppressEvent && !isSelected) {
                BeforeSelectionEvent<DiskResource> evt = BeforeSelectionEvent.fire(this, m);
                if (evt != null && evt.isCanceled()) {
                    continue;
                }
            }

            lastSelected = m;
            change = true;

            // Only add if not selected
            if (!isSelected) {
                selected.add(m);
            }
            setLastFocused(lastSelected);

            onSelectChange(m, true);
            if (!isSelected) {
                if (!suppressEvent) {
                    SelectionEvent.fire(this, m);
                }
            }
        }

        if (change && !suppressEvent) {
            fireSelectionChange();
        }
    }

    @Override
    protected void doSingleSelect(DiskResource model, boolean suppressEvent) {
        if (locked)
            return;

        if (model.isFilter())
            return;

        int index = -1;
        if (store instanceof ListStore) {
            ListStore<DiskResource> ls = (ListStore<DiskResource>) store;
            index = ls.indexOf(model);
        }
        if (store instanceof TreeStore) {
            TreeStore<DiskResource> ls = (TreeStore<DiskResource>) store;
            index = ls.indexOf(model);
        }
        final boolean isSelected = isSelected(model);
        if (index == -1 || isSelected) {
            return;
        } else {
            if (!suppressEvent) {
                BeforeSelectionEvent<DiskResource> evt = BeforeSelectionEvent.fire(this, model);
                if (evt != null && evt.isCanceled()) {
                    return;
                }
            }
        }

        boolean change = false;
        if (selected.size() > 0) {
            // Deselect all items, since this is single select
            doDeselect(selected, true);
            change = true;
        }
        if (selected.size() == 0) {
            change = true;
        }
        selected.add(model);
        lastSelected = model;
        onSelectChange(model, true);
        setLastFocused(lastSelected);

        if (!suppressEvent) {
            SelectionEvent.fire(this, model);
        }

        if (change && !suppressEvent) {
            fireSelectionChange();
        }
    }

    @Override
    protected void onClear(StoreClearEvent<DiskResource> event) {
        // View's Store clear event
        // Override and do nothing. Selection will be refreshed/purged in #refresh()
    }

    @Override
    protected void onRemove(DiskResource model) {
        // View's Store remove event
        // Override and do nothing. We do not remove selections from store remove events
    }

    @Override
    protected void onRowClick(RowClickEvent event) {
        // Prevent selections of filtered items
        DiskResource model = listStore.get(event.getRowIndex());
        if ((model != null) && model.isFilter()) {
            return;
        }

        super.onRowClick(event);
    }

    @Override
    protected void onRowMouseDown(RowMouseDownEvent event) {
        // Prevent selections of filtered items
        DiskResource model = listStore.get(event.getRowIndex());
        if ((model != null) && model.isFilter()) {
            return;
        }

        super.onRowMouseDown(event);
    }

    @Override
    protected void onSelectChange(DiskResource model, boolean select) {
        // Prevent selections of filtered items
        if (model.isFilter()) {
            return;
        }
        super.onSelectChange(model, select);
    }

    @Override
    protected void updateHeaderCheckBox() {
        if (!(grid.getLoader() instanceof PagingLoader<?, ?>)) {
            return;
        }
        if (getColumn().isHidden())
            return;

        if (isSupportedSelectionSize()) {
            int sizeMinusFiltered = Iterables.size(Iterables.filter(getCacheStore().getAll(),
                                                                    new IsNotFilteredPredicate()));
            setChecked((sizeMinusFiltered != 0) && (selected.size() == sizeMinusFiltered));
        }
    }

    ListStore<DiskResource> getCacheStore() {
        return ((LiveGridView<DiskResource>) grid.getView()).cacheStore;
    }

    boolean isSupportedSelectionSize() {
        int cacheSize = ((LiveGridView<?>) grid.getView()).getCacheSize();
        int totalCount = ((PagingLoader<?, ?>) grid.getLoader()).getTotalCount();
        return totalCount <= cacheSize;
    }

}
