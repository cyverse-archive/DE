package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.File;
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
public class RenameFileDialog extends IPlantPromptDialog {

    public RenameFileDialog(final File file,
                            final DiskResourceView.Presenter presenter) {
        this(file, presenter, GWT.<DiskResourceView.Presenter.Appearance> create(DiskResourceView.Presenter.Appearance.class));
    }
    public RenameFileDialog(final File file,
                            final DiskResourceView.Presenter presenter,
                            final DiskResourceView.Presenter.Appearance displayStrings) {
        super(displayStrings.fileName(), -1, file.getName(), new DiskResourceNameValidator());

        setHeadingText(displayStrings.rename());
        addValidator(new DiskResourceSameNameValidator(file));

        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.doRenameDiskResource(file, getFieldText());
            }
        });
    }
}
