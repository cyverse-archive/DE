package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.diskResource.client.views.DiskResourceView.Presenter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

            ArrayList<File> newArrayList = Lists.newArrayList(DiskResourceUtil.extractFiles(event.getSelection()));
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

    private final DiskResourceView.Presenter presenter;
    private TextField selectedFileField;
    private List<File> selectedFileIds;

    @AssistedInject
    FileSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                     final FileSelectDialogAppearance appearance,
                     @Assisted boolean singleSelect) {
        this(presenterFactory, appearance, singleSelect,
             null, null,
             Collections.<InfoType>emptyList());
    }

    @AssistedInject
    FileSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                     final FileSelectDialogAppearance appearance,
                     @Assisted boolean singleSelect,
                     @Assisted List<DiskResource> diskResourcesToSelect) {
        this(presenterFactory, appearance, singleSelect,
             null, diskResourcesToSelect,
             Collections.<InfoType>emptyList());
    }

    @AssistedInject
    FileSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                     final FileSelectDialogAppearance appearance,
                     @Assisted boolean singleSelect,
                     @Assisted HasPath folderToSelect) {
        this(presenterFactory, appearance, singleSelect,
             folderToSelect, null,
             Collections.<InfoType>emptyList());
    }

    FileSelectDialog(final DiskResourcePresenterFactory presenterFactory,
                     final FileSelectDialogAppearance appearance,
                     final boolean singleSelect,
                     final HasPath folderToSelect,
                     final List<DiskResource> diskResourcesToSelect,
                     final List<InfoType> infoTypeFilters) {

        // Disable Ok button by default.
        getOkButton().setEnabled(false);

        setResizable(true);
        setSize("640", "480");
        setHeadingText(appearance.headerText());

        selectedFileField = new TextField();
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
    }

    public void cleanUp() {
        presenter.cleanUp();
    }

    public Set<DiskResource> getDiskResources() {
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

        List<String> pathList = DiskResourceUtil.asStringPathList(value);
        String fileNames = DiskResourceUtil.asCommaSeperatedNameList(pathList);
        selectedFileField.setValue(fileNames);
        getOkButton().setEnabled(true);
    }

}
