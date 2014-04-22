package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEvent;
import org.iplantc.de.diskResource.client.views.dialogs.FolderSelectDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.TakesValue;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

import java.util.Set;

public class FolderSelectorField extends AbstractDiskResourceSelector<Folder> {

    UserSettings userSettings = UserSettings.getInstance();

    public FolderSelectorField() {
        setEmptyText(I18N.DISPLAY.selectAFolder());
    }

    @Override
    protected void onBrowseSelected() {
        HasPath value = getValue();
        FolderSelectDialog folderSD = null;
        if (value == null && userSettings.isRememberLastPath()) {
            String path = userSettings.getLastPath();
            if (path != null) {
                value = CommonModelUtils.createHasPathFromString(path);
            }
        }
        folderSD = new FolderSelectDialog(value);
        folderSD.addHideHandler(new FolderDialogHideHandler(folderSD));
        folderSD.show();
    }

    @Override
    public void setValue(Folder value) {
        super.setValue(value);
    }

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
                UserSettingsUpdatedEvent usue = new UserSettingsUpdatedEvent();
                EventBus.getInstance().fireEvent(usue);
            }
            ValueChangeEvent.fire(FolderSelectorField.this, value);
        }
    }

    @Override
    protected boolean validateDropStatus(Set<DiskResource> dropData, StatusProxy status) {
        // Only allow 1 folder to be dropped in this field.
        if (dropData == null || dropData.size() != 1 || !(DiskResourceUtil.containsFolder(dropData))) {
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
            Folder selectedFolder = (Folder)dropData.iterator().next();
            setSelectedResource(selectedFolder);
            ValueChangeEvent.fire(this, selectedFolder);
        }
    }

    @Override
    public void setValueFromStringId(String path) {
        if (Strings.isNullOrEmpty(path)) {
            setValue(null);
          }
        DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
        setValue(AutoBeanCodex.decode(factory, Folder.class, "{\"path\":\"" + path + "\"}").as());
        
    }
}
