package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.validators.DiskResourceSameNameValidator;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * @author jstroot
 */
public class RenameFileDialog extends IPlantPromptDialog {

    public RenameFileDialog(final File file,
                            final DiskResourceView.Presenter presenter) {
        this(file, presenter, GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }
    public RenameFileDialog(final File file,
                            final DiskResourceView.Presenter presenter,
                            final IplantDisplayStrings displayStrings) {
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
