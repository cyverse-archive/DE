package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.validators.DiskResourceSameNameValidator;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceViewToolbar;
import org.iplantc.de.resources.client.messages.I18N;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class RenameFolderDialog extends IPlantPromptDialog {

    public RenameFolderDialog(final Folder folder, final DiskResourceViewToolbar.Presenter presenter) {
        super(I18N.DISPLAY.folderName(), -1, folder.getName(), new DiskResourceNameValidator());

        setHeadingText(I18N.DISPLAY.rename());
        addValidator(new DiskResourceSameNameValidator(folder));

        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.doRename(folder, getFieldText());
            }
        });
    }
}
