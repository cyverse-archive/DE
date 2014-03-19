package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.gin.DiskResourceInjector;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * An <code>IPlantDialog</code> which wraps the standard <code>DiskResourceView</code> for folder
 * selection.
 *
 * Users of this class are responsible adding hide handlers to get the selected folder.
 *
 * @author jstroot
 *
 */
public class FolderSelectDialog extends IPlantDialog implements TakesValue<Folder> {

    private final DiskResourceView.Presenter presenter;
    private final TextField selectedFolderField = new TextField();

    private Folder selectedFolder;

    public FolderSelectDialog() {
        this(null);
    }

    public FolderSelectDialog(HasId folderToSelect) {
        // Disable Ok button by default.
        getOkButton().setEnabled(false);

        setResizable(true);
        setSize("640", "480");
        setHeadingText(I18N.DISPLAY.selectAFolder());

        presenter = DiskResourceInjector.INSTANCE.getDiskResourceViewPresenter();

        final FieldLabel fl = new FieldLabel(selectedFolderField, I18N.DISPLAY.selectedFolder());

        presenter.getView().setSouthWidget(fl);
        presenter.addFolderSelectionHandler(new FolderSelectionChangedHandler(this, selectedFolderField, getOkButton()));

        // Tell the presenter to add the view with the north, east, and center widgets hidden.
        presenter.builder().hideNorth().hideCenter().hideEast().singleSelect().go(this);
        presenter.setSelectedFolderById(folderToSelect);
    }

    public void cleanUp() {
        presenter.cleanUp();
    }
    
    private final class FolderSelectionChangedHandler implements SelectionHandler<Folder> {
        private final HasValue<String> textBox;
        private final HasEnabled okButton;
        private final TakesValue<Folder> dlg;

        private FolderSelectionChangedHandler(final TakesValue<Folder> dlg, final HasValue<String> textBox, final HasEnabled okButton) {
            this.textBox = textBox;
            this.okButton = okButton;
            this.dlg = dlg;
        }

        @Override
        public void onSelection(SelectionEvent<Folder> event) {
            Folder diskResource = event.getSelectedItem();
            if (diskResource == null) {
                // Disable the okButton
                okButton.setEnabled(false);
                return;
            }

            dlg.setValue(diskResource);
            textBox.setValue(diskResource.getName());
            // Enable the okButton
            okButton.setEnabled(true);
        }
    }

    
    @Override
    public void onHide() {
        presenter.cleanUp();
    }
    
    
    
    @Override
    public Folder getValue() {
        return selectedFolder;
    }

    @Override
    public void setValue(Folder value) {
        this.selectedFolder = value;
    }

}
