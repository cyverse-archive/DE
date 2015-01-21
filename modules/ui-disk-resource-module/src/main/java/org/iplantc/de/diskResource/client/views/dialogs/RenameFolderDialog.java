package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.validators.DiskResourceSameNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * @author jstroot
 */
public class RenameFolderDialog extends IPlantPromptDialog {

    public RenameFolderDialog(final Folder folder,
                              final DiskResourceView.Presenter presenter) {
        this(folder, presenter, GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    public RenameFolderDialog(final Folder folder,
                              final DiskResourceView.Presenter presenter,
                              final IplantDisplayStrings displayStrings) {
        super(displayStrings.folderName(), -1, folder.getName(), new DiskResourceNameValidator());

        setHeadingText(displayStrings.rename());
        addValidator(new DiskResourceSameNameValidator(folder));

        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.doRenameDiskResource(folder, getFieldText());
            }
        });
    }
}
