package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourcePresenterFactory;

import com.google.common.base.Strings;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class SaveAsDialog extends IPlantDialog {

    private final class FolderSelectionChangedHandler implements FolderSelectionEvent.FolderSelectionEventHandler {

        private FolderSelectionChangedHandler() {
        }

        @Override
        public void onFolderSelected(FolderSelectionEvent event) {
            if (event.getSelectedFolder() == null) {
                return;
            }
            selectedFolder = event.getSelectedFolder();
            selectedFolderField.setValue(selectedFolder.getPath(), true);
            selectedFolderField.validate();

        }

    }

    public interface SaveAsDialogAppearance {

        String dialogHeight();

        String dialogWidth();

        String fileName();

        String saveAsHeadingText();

        String selectedFolder();
    }

    private final DiskResourcePresenterFactory presenterFactory;
    final UserSettings userSettings;
    private final TextField fileNameField = new TextField();
    private DiskResourceView.Presenter presenter;
    private final TextField selectedFolderField = new TextField();
    private Folder selectedFolder = null;
    private final VerticalLayoutContainer vlc;

    @Inject
    SaveAsDialog(final DiskResourcePresenterFactory presenterFactory,
                 final UserSettings userSettings,
                 final SaveAsDialogAppearance appearance) {
        this.presenterFactory = presenterFactory;
        this.userSettings = userSettings;
        selectedFolderField.setAllowBlank(false);
        selectedFolderField.setReadOnly(true);
        selectedFolderField.setAutoValidate(true);
        fileNameField.setAllowBlank(false);
        fileNameField.addValidator(new DiskResourceNameValidator());
        fileNameField.setAutoValidate(true);
        setHideOnButtonClick(false);

        setResizable(true);
        setSize(appearance.dialogWidth(),
                appearance.dialogHeight());
        setHeadingText(appearance.saveAsHeadingText());

        addKeyHandlers(getOkButton());

        final FieldLabel fl1 = new FieldLabel(selectedFolderField,
                                              appearance.selectedFolder());
        final FieldLabel fl2 = new FieldLabel(fileNameField,
                                              appearance.fileName());

        vlc = buildLayout(fl1, fl2);

    }

    public void show(final HasPath selectedFolder){
        // if not refresh and currently nothing was selected and remember path is enabled, the go
        // back to last selected folder
        String path = userSettings.getLastPath();
        boolean remember = userSettings.isRememberLastPath();
        HasPath hasPath = selectedFolder;
        if (hasPath == null && remember && !Strings.isNullOrEmpty(path)) {
            hasPath = CommonModelUtils.getInstance().createHasPathFromString(path);
        }

        presenter = presenterFactory.createSelectorWithSouthWidgetHeight(true,
                                                                         true,
                                                                         true,
                                                                         true,
                                                                         hasPath,
                                                                         vlc,
                                                                         60);
        presenter.addFolderSelectedEventHandler(new FolderSelectionChangedHandler());
        presenter.go(this);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("");
    }

    public void cleanUp() {
        presenter.cleanUp();
    }

    public String getFileName() {
        return fileNameField.getCurrentValue();
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public boolean isValid() {
        return selectedFolderField.isValid() & fileNameField.isValid();
    }

    @Override
    public void onHide() {
        cleanUp();
    }

    private void addKeyHandlers(final TextButton okBtn) {
        fileNameField.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (!isValid()) {
                    return;
                }
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    onButtonPressed(okBtn);
                }
            }
        });
    }

    private VerticalLayoutContainer buildLayout(final FieldLabel fl1,
                                                final FieldLabel fl2) {
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        vlc.add(fl1, new VerticalLayoutData(.9, -1));
        vlc.add(fl2, new VerticalLayoutData(.9, -1));
        return vlc;
    }

}
