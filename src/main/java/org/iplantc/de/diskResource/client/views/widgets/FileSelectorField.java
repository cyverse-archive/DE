package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEvent;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.TakesValue;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

import java.util.List;
import java.util.Set;

public class FileSelectorField extends AbstractDiskResourceSelector<File> {

    UserSettings userSettings = UserSettings.getInstance();

    public FileSelectorField() {
        setEmptyText(I18N.DISPLAY.selectAFile());
    }

    @Override
    protected void onBrowseSelected() {
        List<DiskResource> selected = null;

        DiskResource value = getValue();
        if (value != null) {
            selected = Lists.newArrayList();
            selected.add(value);
        }
        FileSelectDialog fileSD = null;
        if (selected != null && selected.size() > 0) {
            fileSD = FileSelectDialog.singleSelect(selected);
        } else {
            if (userSettings.isRememberLastPath()) {
                String id = userSettings.getLastPathId();
                if (id != null) {
                    fileSD = FileSelectDialog.selectParentFolderById(id,true);
                } else {
                    fileSD = FileSelectDialog.singleSelect(null);
                }
            } else {
                fileSD = FileSelectDialog.singleSelect(null);
            }
        }
        fileSD.addHideHandler(new FileDialogHideHandler(fileSD));
        fileSD.show();
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
                userSettings.setLastPathId(DiskResourceUtil.parseParent(selectedResource.getId()));
                UserSettingsUpdatedEvent usue = new UserSettingsUpdatedEvent();
                EventBus.getInstance().fireEvent(usue);
            }
            ValueChangeEvent.fire(FileSelectorField.this, selectedResource);
        }
    }

    @Override
    protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status) {
        // Only allow 1 file to be dropped in this field.
        if (dropData == null || dropData.size() != 1 || !(DiskResourceUtil.containsFile(dropData))) {
            status.setStatus(false);
            return false;
        }

        // Reset status message
        status.setStatus(true);
        status.update(I18N.DISPLAY.dataDragDropStatusText(dropData.size()));

        return true;
    }

    @Override
    public void onDrop(DndDropEvent event) {
        Set<DiskResource> dropData = getDropData(event.getData());

        if (validateDropStatus(dropData, event.getStatusProxy())) {
            File selectedFile = (File)dropData.iterator().next();
            setSelectedResource(selectedFile);
            ValueChangeEvent.fire(this, selectedFile);
        }
    }

    @Override
    public void setValueFromStringId(String path) {
        if (Strings.isNullOrEmpty(path)) {
            setValue(null);
          }
        DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
        setValue(AutoBeanCodex.decode(factory, File.class, "{\"path\":\"" + path + "\"}").as());
        
    }
}
