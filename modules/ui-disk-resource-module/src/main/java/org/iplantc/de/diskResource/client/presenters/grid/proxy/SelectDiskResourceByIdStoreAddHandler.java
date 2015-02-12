package org.iplantc.de.diskResource.client.presenters.grid.proxy;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.GridView;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;

import java.util.List;

/**
 * @author jstroot
 */
public final class SelectDiskResourceByIdStoreAddHandler implements StoreAddHandler<DiskResource> {
    private final List<? extends HasId> diskResourcesToSelect;
    private final GridView.Presenter presenter;

    public SelectDiskResourceByIdStoreAddHandler(final List<? extends HasId> diskResourcesToSelect,
                                                 final GridView.Presenter presenter) {
        this.diskResourcesToSelect = diskResourcesToSelect;
        this.presenter = presenter;
    }

    @Override
    public void onAdd(StoreAddEvent<DiskResource> event) {
        List<DiskResource> items = event.getItems();
        if (diskResourcesToSelect == null
                || diskResourcesToSelect.size() <= 0
                || items == null
                || items.size() <= 0) {
            return;
        }

        for (DiskResource addedItem : items) {
            for (HasId toSelect : diskResourcesToSelect) {
                // If we match at least one of the disk resources to select, send them
                // to the view.
                if (addedItem.getId().equals(toSelect.getId())) {
                    // Have to make the call as a deferred command to give the view time
                    // to catch up.
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            presenter.setSelectedDiskResourcesById(diskResourcesToSelect);
                        }
                    });
                    presenter.unRegisterHandler(this);
                    return;
                }
            }
        }
    }
}