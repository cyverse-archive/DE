package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.events.LastSelectedPathChangedEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorDialogFactory;
import org.iplantc.de.diskResource.client.views.dialogs.FolderSelectDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.TakesValue;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 */
public class FolderSelectorField extends AbstractDiskResourceSelector<Folder> {

    private class FolderDialogHideHandler implements HideHandler {
        private final TakesValue<Folder> takesValue;

        public FolderDialogHideHandler(TakesValue<Folder> dlg) {
            this.takesValue = dlg;
        }

        @Override
        public void onHide(HideEvent event) {
            Folder value = takesValue.getValue();
            if (value == null)
                return;

            setSelectedResource(value);
            // cache the last used path
            if (userSettings.isRememberLastPath()) {
                userSettings.setLastPath(value.getPath());
                eventBus.fireEvent(new LastSelectedPathChangedEvent(true));
            }
            ValueChangeEvent.fire(FolderSelectorField.this, value);
        }
    }

    @Inject UserSettings userSettings;
    @Inject EventBus eventBus;
    @Inject DiskResourceSelectorDialogFactory selectorDialogFactory;
    @Inject DiskResourceUtil diskResourceUtil;

    final IplantDisplayStrings displayStrings;
    private final DiskResourceServiceFacade diskResourceService;
    private final List<InfoType> infoTypeFilters;

    @AssistedInject
    FolderSelectorField(final IplantDisplayStrings displayStrings,
                        final DiskResourceServiceFacade diskResourceService){
        this(displayStrings,
             diskResourceService,
             Collections.<InfoType>emptyList());
    }

    @AssistedInject
    FolderSelectorField(final IplantDisplayStrings displayStrings,
                        final DiskResourceServiceFacade diskResourceService,
                        @Assisted List<InfoType> infoTypeFilters) {
        this.displayStrings = displayStrings;
        this.diskResourceService = diskResourceService;
        this.infoTypeFilters = infoTypeFilters;
        setEmptyText(displayStrings.selectAFolder());
    }

    @Override
    public void onDrop(DndDropEvent event) {
        Set<DiskResource> dropData = getDropData(event.getData());

        if (validateDropStatus(dropData, event.getStatusProxy())) {
            DiskResource diskResource = dropData.iterator().next();
            Folder selectedItem;
            if(!(diskResource instanceof Folder)){
                // It's valid, it just needs to look like a folder
                selectedItem = diskResourceService.convertToFolder(diskResource);
            } else {
                selectedItem =  (Folder) diskResource;
            }
            setSelectedResource(selectedItem);
            ValueChangeEvent.fire(this, selectedItem);
        }
    }

    @Override
    protected void onBrowseSelected() {
        HasPath value = getValue();
        FolderSelectDialog folderSD;
        if (value == null && userSettings.isRememberLastPath()) {
            String path = userSettings.getLastPath();
            if (path != null) {
                value = CommonModelUtils.createHasPathFromString(path);
            }
        }
        folderSD = selectorDialogFactory.createFilteredFolderSelector(value, infoTypeFilters);
        folderSD.addHideHandler(new FolderDialogHideHandler(folderSD));
        folderSD.show();
    }

    @Override
    protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status) {
        boolean isValid = false;
        // Only allow 1 folder to be dropped in this field.
        if ((dropData == null)
                || dropData.size() != 1) {
            isValid = false;
        } else if (diskResourceUtil.containsFolder(dropData)){
            isValid = true;
        } else {
            DiskResource droppedResource = dropData.iterator().next();
            InfoType infoType = InfoType.fromTypeString(droppedResource.getInfoType());
            for (InfoType it : infoTypeFilters) {
                // If the filter list is empty, this code will not be executed
                if (it.equals(infoType)) {
                    // Reset status message
                    status.setStatus(true);
                    status.update(displayStrings.dataDragDropStatusText(dropData.size()));
                    return true;
                }
            }
        }

        // Reset status message
        status.setStatus(isValid);
        if(isValid){
            status.update(displayStrings.dataDragDropStatusText(dropData.size()));
        }

        return isValid;
    }
}
