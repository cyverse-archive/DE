package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.TakesValue;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Arrays;
import java.util.List;

/**
 * @author jstroot
 */
public class FileFolderSelectDialog extends IPlantDialog implements TakesValue<List<DiskResource>> {

    private static class DiskResourceSelectionChangedEventHandler implements DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler {
        private final TakesValue<List<DiskResource>> dlg;

        public DiskResourceSelectionChangedEventHandler(final TakesValue<List<DiskResource>> dlg) {
            this.dlg = dlg;
        }

        @Override
        public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
            Preconditions.checkNotNull(event.getSelection());
            List<DiskResource> selection = event.getSelection();
            dlg.setValue(selection);
        }
    }

    private static class FolderSelectionEventHandler implements FolderSelectionEvent.FolderSelectionEventHandler {
        private final TakesValue<List<DiskResource>> dlg;

        public FolderSelectionEventHandler(final TakesValue<List<DiskResource>> dlg) {
            this.dlg = dlg;
        }

        @Override
        public void onFolderSelected(FolderSelectionEvent event) {
            Folder diskResource = event.getSelectedFolder();
            dlg.setValue(Arrays.asList((DiskResource)diskResource));
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

    private final DiskResourcePresenterFactory presenterFactory;
    private final FileFolderSelectDialogAppearance appearance;

    private final TextField selectedItemTextField;
    private List<DiskResource> selectedDiskResource;
    private DiskResourceView.Presenter presenter;
    @Inject
    DiskResourceUtil diskResourceUtil;

    @Inject
    FileFolderSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                           final FileFolderSelectDialogAppearance appearance){
        this.presenterFactory = presenterFactory;
        this.appearance = appearance;
        getOkButton().setEnabled(false);
        setResizable(true);
        setSize(appearance.getWidth(), appearance.getHeight());
        setMinHeight(appearance.getMinHeight());
        setMinWidth(appearance.getMinWidth());
        setHeadingText(appearance.getHeaderText());

        selectedItemTextField = new TextField();

    }

    public void show(final HasPath folderToSelect,
                     final List<DiskResource> diskResourcesToSelect,
                     final List<InfoType> infoTypeFilters,
                     final boolean singleSelect) {

        final FieldLabel fl = new FieldLabel(selectedItemTextField, appearance.selectorFieldLabel());
        presenter = presenterFactory.filtered(true,
                                              true,
 singleSelect,// Single select
                                              true,
                                              folderToSelect,
                                              infoTypeFilters,
                                              TYPE.ANY,
                                              fl);
        presenter.addFolderSelectedEventHandler(new FolderSelectionEventHandler(this));
        presenter.addDiskResourceSelectionChangedEventHandler(new DiskResourceSelectionChangedEventHandler(this));
        presenter.go(this, folderToSelect, diskResourcesToSelect);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void onHide() {
        presenter.cleanUp();
    }

    @Override
    public void setValue(List<DiskResource> value) {
        this.selectedDiskResource = value;
        if(value == null){
            selectedItemTextField.clear();
            getOkButton().setEnabled(false);
            return;
        }
        List<String> pathList = diskResourceUtil.asStringPathList(value);
        String fileNames = diskResourceUtil.asCommaSeparatedNameList(pathList);
        selectedItemTextField.setValue(fileNames);
        getOkButton().setEnabled(true);
    }

    @Override
    public List<DiskResource> getValue() {
        return selectedDiskResource;
    }
}
