package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.events.LastSelectedPathChangedEvent;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

    public interface FileSelectorFieldAppearance extends SelectorAppearance {


        String selectAFile();
    }

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
    @Inject AsyncProviderWrapper<FileSelectDialog> fileSelectDialogProvider;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject CommonModelUtils commonModelUtils;

    private final FileSelectorFieldAppearance appearance;
    final List<InfoType> infoTypeFilters;

    @AssistedInject
    FileSelectorField(final DiskResourceServiceFacade diskResourceService,
                      final FileSelectorFieldAppearance appearance,
                      @Assisted final List<InfoType> infoTypeFilters){
        super(diskResourceService, appearance);
        this.appearance = appearance;
        this.infoTypeFilters = infoTypeFilters;
    }

    @AssistedInject
    FileSelectorField(final DiskResourceServiceFacade diskResourceService,
                      final FileSelectorFieldAppearance appearance){
        this(diskResourceService,
             appearance,
             Collections.<InfoType>emptyList());
        setEmptyText(appearance.selectAFile());
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

        DiskResource value = getValue();
        final List<DiskResource> selected = (value == null) ? null : Lists.<DiskResource>newArrayList();
        if (value != null) {
            selected.add(value);
        }

        fileSelectDialogProvider.get(new AsyncCallback<FileSelectDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(FileSelectDialog result) {
                HasPath folderToSelect = null;

                if (selected != null && selected.size() > 0) {
                    String folderPath = diskResourceUtil.parseParent(selected.get(0).getPath());

                    folderToSelect = commonModelUtils.createHasPathFromString(folderPath);
                } else {
                    if (userSettings.isRememberLastPath()) {
                        String path = userSettings.getLastPath();
                        if (path != null) {
                            folderToSelect = commonModelUtils.createHasPathFromString(path);
                        } else {
                            folderToSelect = null;
                        }
                    } else {
                        folderToSelect = null;

                    }
                }
                result.addHideHandler(new FileDialogHideHandler(result));
                result.show(true,
                            folderToSelect,
                            null,
                            infoTypeFilters);
            }
        });

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
        status.update(appearance.dataDragDropStatusText(dropData.size()));

        return true;
    }
}
