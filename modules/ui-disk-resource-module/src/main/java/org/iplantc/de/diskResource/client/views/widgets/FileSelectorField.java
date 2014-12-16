package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.events.LastSelectedPathChangedEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorDialogFactory;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.collect.Lists;
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
public class FileSelectorField extends AbstractDiskResourceSelector<File> {

    private class FileDialogHideHandler implements HideHandler {
        private final TakesValue<List<File>> takesValue;

        public FileDialogHideHandler(TakesValue<List<File>> dlg) {
            this.takesValue = dlg;
        }

        @Override
        public void onHide(HideEvent event) {
            if ((takesValue.getValue() == null) || takesValue.getValue().isEmpty())
                return;

            // This class is single select, so only grab first element
            File selectedResource = takesValue.getValue().get(0);
            setSelectedResource(selectedResource);
            // cache the last used path
            if (userSettings.isRememberLastPath()) {
                userSettings.setLastPath(diskResourceUtil.parseParent(selectedResource.getPath()));
                eventBus.fireEvent(new LastSelectedPathChangedEvent(true));
            }
            ValueChangeEvent.fire(FileSelectorField.this, selectedResource);
        }
    }

    @Inject UserSettings userSettings;
    @Inject EventBus eventBus;
    @Inject DiskResourceSelectorDialogFactory dialogFactory;
    @Inject DiskResourceUtil diskResourceUtil;

    final IplantDisplayStrings displayStrings;
    final List<InfoType> infoTypeFilters;

    @AssistedInject
    FileSelectorField(final IplantDisplayStrings displayStrings,
                      @Assisted final List<InfoType> infoTypeFilters){
        this.displayStrings = displayStrings;
        this.infoTypeFilters = infoTypeFilters;
    }

    @AssistedInject
    FileSelectorField(final IplantDisplayStrings displayStrings){
        this(displayStrings,
             Collections.<InfoType>emptyList());
        setEmptyText(displayStrings.selectAFile());
    }

    @Override
    public void onDrop(DndDropEvent event) {
        Set<DiskResource> dropData = getDropData(event.getData());

        if (validateDropStatus(dropData, event.getStatusProxy())) {
            File selectedFile = (File) dropData.iterator().next();
            setSelectedResource(selectedFile);
            ValueChangeEvent.fire(this, selectedFile);
        }
    }

    @Override
    protected void onBrowseSelected() {
        List<DiskResource> selected = null;

        DiskResource value = getValue();
        if (value != null) {
            selected = Lists.newArrayList();
            selected.add(value);
        }
        FileSelectDialog fileSD;
        if (selected != null && selected.size() > 0) {
            fileSD = dialogFactory.createFilteredFileSelectorWithResources(true, selected, infoTypeFilters);
        } else {
            if (userSettings.isRememberLastPath()) {
                String path = userSettings.getLastPath();
                if (path != null) {
                    HasPath hasPath = CommonModelUtils.getInstance().createHasPathFromString(path);
                    fileSD = dialogFactory.createFilteredFileSelectorWithFolder(true, hasPath, infoTypeFilters);
                } else {
                    fileSD = dialogFactory.createFilteredFileSelectorWithFolder(true, null, infoTypeFilters);
                }
            } else {
                fileSD = dialogFactory.createFilteredFileSelectorWithFolder(true, null, infoTypeFilters);

            }
        }
        fileSD.addHideHandler(new FileDialogHideHandler(fileSD));
        fileSD.show();
    }

    @Override
    protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status) {
        // Only allow 1 file to be dropped in this field.
        if (dropData == null || dropData.size() != 1 || !(diskResourceUtil.containsFile(dropData))) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(displayStrings.dataDragDropStatusText(dropData.size()));

        return true;
    }
}
