package org.iplantc.de.apps.widgets.client.view.util;

import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;

import java.util.List;

/**
 * This handler automatically fires <code>ValueChange</code> events on behalf of a given
 * <code>HasValueChangeHandlers</code> object. Also, the events can be suppressed by the given
 * <code>HasEventSupression</code> class.
 * 
 * @author jstroot
 * 
 */
public abstract class SelectionItemValueChangeStoreHandler implements StoreHandlers<SelectionItem> {

    public interface HasEventSuppression {
        boolean isSuppressEvent();

        void setSuppressEvent(boolean suppressEventFire);
    }

    private final HasEventSuppression hasEventSuppression;
    private final HasValueChangeHandlers<List<SelectionItem>> valueChangeTarget;

    public SelectionItemValueChangeStoreHandler(HasEventSuppression hasEventSuppression, HasValueChangeHandlers<List<SelectionItem>> valueChangeTarget) {
        this.hasEventSuppression = hasEventSuppression;
        this.valueChangeTarget = valueChangeTarget;
    }

    @Override
    public void onAdd(StoreAddEvent<SelectionItem> event) {
        doFire();
    }

    @Override
    public void onClear(StoreClearEvent<SelectionItem> event) {
        doFire();
    }

    @Override
    public void onDataChange(StoreDataChangeEvent<SelectionItem> event) {
        doFire();
    }

    @Override
    public void onFilter(StoreFilterEvent<SelectionItem> event) {/* Do Nothing */}

    @Override
    public void onRecordChange(StoreRecordChangeEvent<SelectionItem> event) {
        doFire();
    }

    @Override
    public void onRemove(StoreRemoveEvent<SelectionItem> event) {
        doFire();
    }

    @Override
    public void onSort(StoreSortEvent<SelectionItem> event) {/*
                                                              * Do Nothing, sorting the grid does not
                                                              * sort the store.
                                                              */}

    @Override
    public void onUpdate(StoreUpdateEvent<SelectionItem> event) {
        doFire();
    }

    protected abstract List<SelectionItem> getCurrentValue();

    private void doFire() {
        if (hasEventSuppression.isSuppressEvent()) {
            return;
        }
        ValueChangeEvent.fire(valueChangeTarget, getCurrentValue());
    }
}