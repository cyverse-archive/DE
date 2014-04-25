package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantPromptDialog;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class CreateFolderDialog extends IPlantPromptDialog {

    public CreateFolderDialog(final Folder parentFolder,
            final DiskResourceView.DiskResourceViewToolbar.Presenter presenter) {
        super(I18N.DISPLAY.folderName(), -1, "", new DiskResourceNameValidator());
        setWidth("300px");
        setHeadingText(I18N.DISPLAY.newFolder());
        initDestPathLabel(parentFolder.getPath());

        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                presenter.doCreateNewFolder(parentFolder, getFieldText());
            }
        });

    }

    private void initDestPathLabel(String destPath) {
        HTML htmlDestText = new HTML("<div title='" + destPath + "' style='width:100%;padding:5px;text-overflow:ellipsis;'>" + Format.ellipse(I18N.DISPLAY.createIn(destPath), 50) + "</div>");
        insert(htmlDestText, 0);
    }
}
