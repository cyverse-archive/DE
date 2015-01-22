package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;
import org.iplantc.de.diskResource.client.DiskResourceView;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.TakesValue;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.List;

/**
 * @author jstroot
 */
public class FileFolderSelectDialog extends IPlantDialog implements TakesValue<DiskResource> {

    private static class DiskResourceSelectionChangedEventHandler implements DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler {
        private final TakesValue<DiskResource> dlg;

        public DiskResourceSelectionChangedEventHandler(final TakesValue<DiskResource> dlg) {
            this.dlg = dlg;
        }

        @Override
        public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
            Preconditions.checkNotNull(event.getSelection());
            Preconditions.checkArgument(event.getSelection().size() <= 1, "Only single select is supported for FolderSelectDialog");

            List<DiskResource> selection = event.getSelection();
            DiskResource selectedDiskResource;
            selectedDiskResource = selection.get(0);

            dlg.setValue(selectedDiskResource);
        }
    }

    private static class FolderSelectionEventHandler implements FolderSelectionEvent.FolderSelectionEventHandler {
        private final TakesValue<DiskResource> dlg;

        public FolderSelectionEventHandler(final TakesValue<DiskResource> dlg) {
            this.dlg = dlg;
        }

        @Override
        public void onFolderSelected(FolderSelectionEvent event) {
            Folder diskResource = event.getSelectedFolder();
            dlg.setValue(diskResource);
        }
    }

    public interface FileFolderSelectDialogAppearance {

        String getHeaderText();

        String getHeight();

        int getMinHeight();

        int getMinWidth();

        String getWidth();

        String selectorFieldLabel();
    }

    private final TextField selectedItemTextField;
    private DiskResource selectedDiskResource;
    private final DiskResourceView.Presenter presenter;

    @Inject
    FileFolderSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                           final FileFolderSelectDialogAppearance appearance,
                           @Assisted final HasPath folderToSelect,
                           @Assisted final List<DiskResource> diskResourcesToSelect,
                           @Assisted final List<InfoType> infoTypeFilters){
        getOkButton().setEnabled(false);
        setResizable(true);
        setSize(appearance.getWidth(), appearance.getHeight());
        setMinHeight(appearance.getMinHeight());
        setMinWidth(appearance.getMinWidth());
        setHeadingText(appearance.getHeaderText());

        selectedItemTextField = new TextField();
        final FieldLabel fl = new FieldLabel(selectedItemTextField, appearance.selectorFieldLabel());

        presenter = presenterFactory.filtered(true,
                                              true,
                                              true,// Single select
                                              true,
                                              folderToSelect,
                                              infoTypeFilters,
                                              TYPE.ANY,
                                              fl);
        presenter.addFolderSelectedEventHandler(new FolderSelectionEventHandler(this));
        presenter.addDiskResourceSelectionChangedEventHandler(new DiskResourceSelectionChangedEventHandler(this));
        presenter.go(this, folderToSelect, diskResourcesToSelect);
    }

    @Override
    public void onHide() {
        presenter.cleanUp();
    }

    @Override
    public void setValue(DiskResource value) {
        this.selectedDiskResource = value;
        if(value == null){
            selectedItemTextField.clear();
            getOkButton().setEnabled(false);
            return;
        }

        selectedItemTextField.setValue(value.getName());
        getOkButton().setEnabled(true);
    }

    @Override
    public DiskResource getValue() {
        return selectedDiskResource;
    }
}
