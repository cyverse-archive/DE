package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.validators.DiskResourceSameNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.DiskResourceView;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * FIXME Do not pass presenter in. Places utilizing this dlg should handle ok select.
 * @author jstroot
 */
public class RenameFolderDialog extends IPlantPromptDialog {

    public RenameFolderDialog(final Folder folder,
                              final DiskResourceView.Presenter presenter) {
        this(folder, presenter, GWT.<DiskResourceView.Presenter.Appearance> create(DiskResourceView.Presenter.Appearance.class));
    }

    public RenameFolderDialog(final Folder folder,
                              final DiskResourceView.Presenter presenter,
                              final DiskResourceView.Presenter.Appearance displayStrings) {
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
