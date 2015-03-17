package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.DiskResourceView.Presenter;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * An <code>IPlantDialog</code> which wraps the standard <code>DiskResourceView</code> for file
 * selection.
 * <p/>
 * Users of this class are responsible adding hide handlers to get the selected file.
 * FIXME JDS Needs to support MultiSelect, TakesValue<List<String>>
 *
 * @author jstroot
 */
public class FileSelectDialog extends IPlantDialog implements TakesValue<List<File>> {

    private final class FileSelectionChangedHandler implements DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler {
        private final TakesValue<List<File>> dlg;

        private FileSelectionChangedHandler(final TakesValue<List<File>> dlg) {
            this.dlg = dlg;
        }

        @Override
        public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
            Preconditions.checkNotNull(event.getSelection(), "Selection should not be null");

            ArrayList<File> newArrayList = Lists.newArrayList(diskResourceUtil.extractFiles(event.getSelection()));
            dlg.setValue(newArrayList);
        }
    }

    private final class SelectedFileFieldKeyUpHandler implements KeyUpHandler {
        private final HasValue<String> hasValue;
        private final Presenter presenter;

        public SelectedFileFieldKeyUpHandler(final DiskResourceView.Presenter presenter,
                                             final HasValue<String> hasValue) {
            this.presenter = presenter;
            this.hasValue = hasValue;
        }

        @Override
        public void onKeyUp(KeyUpEvent event) {
            if ((event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE)
                    || (event.getNativeKeyCode() == KeyCodes.KEY_DELETE)) {
                presenter.deSelectDiskResources();
                hasValue.setValue(null);
            } else {
                event.preventDefault();
            }

        }
    }

    public interface FileSelectDialogAppearance {
        String headerText();

        String selectorFieldLabel();
    }

    private final DiskResourcePresenterFactory presenterFactory;
    private final FileSelectDialogAppearance appearance;

    private DiskResourceView.Presenter presenter;
    private final TextField selectedFileField;
    private List<File> selectedFileIds;
    @Inject DiskResourceUtil diskResourceUtil;

    @Inject
    FileSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                     final FileSelectDialogAppearance appearance) {

        this.presenterFactory = presenterFactory;
        this.appearance = appearance;
        // Disable Ok button by default.
        getOkButton().setEnabled(false);

        setResizable(true);
        setSize("640", "480");
        setHeadingText(appearance.headerText());

        selectedFileField = new TextField();
    }

    public void show(final boolean singleSelect,
                     final HasPath folderToSelect,
                     final List<DiskResource> diskResourcesToSelect,
                     final List<InfoType> infoTypeFilters){
        final FieldLabel fl = new FieldLabel(selectedFileField, appearance.selectorFieldLabel());
        // Tell the presenter to add the view with the north and east widgets hidden.
        presenter = presenterFactory.filtered(true,
                                              true,
                                              singleSelect,
                                              true,
                                              folderToSelect,
                                              infoTypeFilters,
                                              null, // Always want to show files and folders
                                              fl);

        selectedFileField.addKeyUpHandler(new SelectedFileFieldKeyUpHandler(presenter, selectedFileField));
        presenter.addDiskResourceSelectionChangedEventHandler(new FileSelectionChangedHandler(this));
        presenter.go(this, folderToSelect, diskResourcesToSelect);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("");
    }

    public void cleanUp() {
        presenter.cleanUp();
    }

    public List<DiskResource> getDiskResources() {
        return presenter.getSelectedDiskResources();
    }

    @Override
    public List<File> getValue() {
        return selectedFileIds;
    }

    @Override
    public void onHide() {
        cleanUp();
    }

    @Override
    public void setValue(List<File> value) {
        this.selectedFileIds = value;
        if(value.isEmpty()){
            selectedFileField.clear();
            getOkButton().setEnabled(false);
            return;
        }

        List<String> pathList = diskResourceUtil.asStringPathList(value);
        String fileNames = diskResourceUtil.asCommaSeparatedNameList(pathList);
        selectedFileField.setValue(fileNames);
        getOkButton().setEnabled(true);
    }

}
