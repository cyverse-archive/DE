package org.iplantc.de.diskResource.client.views.dialogs;


import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.gin.DiskResourceInjector;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

public class SaveAsDialog extends IPlantDialog {

	private final DiskResourceView.Presenter presenter;
	private final TextField selectedFolderField = new TextField();
	private final TextField fileNameField = new TextField();
	private Folder selectedFolder = null;

	public SaveAsDialog() {
		selectedFolderField.setAllowBlank(false);
		selectedFolderField.setReadOnly(true);
		selectedFolderField.setAutoValidate(true);
		fileNameField.setAllowBlank(false);
		fileNameField.addValidator(new DiskResourceNameValidator());
		fileNameField.setAutoValidate(true);
		setHideOnButtonClick(false);

		initDialog();

		addKeyHandlers(getOkButton());

		presenter = DiskResourceInjector.INSTANCE
				.getDiskResourceViewPresenter();

		final FieldLabel fl1 = new FieldLabel(selectedFolderField,
				I18N.DISPLAY.selectedFolder());
		final FieldLabel fl2 = new FieldLabel(fileNameField,
				I18N.DISPLAY.fileName());

		VerticalLayoutContainer vlc = buildLayout(fl1, fl2);

		initPresenter(getOkButton(), vlc);

		setDefaultSelectedFolder();

	}

	private void setDefaultSelectedFolder() {
		// if not refresh and currently nothing was selected and remember path
		// is enabled, the go
		// back to last selected folder
		UserSettings instance = UserSettings.getInstance();
		String id = instance.getDefaultFileSelectorPath();
		boolean remember = instance.isRememberLastPath();
		if (remember && !Strings.isNullOrEmpty(id)) {
			CommonModelAutoBeanFactory factory = GWT
					.create(CommonModelAutoBeanFactory.class);
			HasId folderAb = AutoBeanCodex.decode(factory, HasId.class,
					"{\"id\": \"" + id + "\"}").as();
			presenter.setSelectedFolderById(folderAb);
		}
	}

	private void initPresenter(TextButton okButton, VerticalLayoutContainer vlc) {
		presenter.getView().setSouthWidget(vlc, 60);
		presenter.addFolderSelectionHandler(new FolderSelectionChangedHandler());
		presenter.builder().hideNorth().hideCenter().hideEast().singleSelect()
				.go(this);
	}

	private VerticalLayoutContainer buildLayout(final FieldLabel fl1,
			final FieldLabel fl2) {
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(fl1, new VerticalLayoutData(.9, -1));
		vlc.add(fl2, new VerticalLayoutData(.9, -1));
		return vlc;
	}

	private void addKeyHandlers(final TextButton okBtn) {
		fileNameField.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				boolean vaild = isVaild();
				if (vaild) {
					if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
						onButtonPressed(okBtn);
					}
				}
			}
		});
	}

	private void initDialog() {
		setResizable(true);
		setSize("480", "425");
		setHeadingText(I18N.DISPLAY.saveAs());
	}

	public void cleanUp() {
		presenter.cleanUp();
	}

	@Override
	public void onHide() {
		cleanUp();
	}

	public boolean isVaild() {
		return selectedFolderField.isValid() & fileNameField.isValid();
	}

	private final class FolderSelectionChangedHandler implements
			SelectionHandler<Folder> {

		private FolderSelectionChangedHandler() {
		}

		@Override
		public void onSelection(SelectionEvent<Folder> event) {
			if (event.getSelectedItem() == null) {
				return;
			}
			selectedFolder = event.getSelectedItem();
			selectedFolderField.setValue(selectedFolder.getPath(), true);
			selectedFolderField.validate();
		}
	}

	public String getFileName() {
		return fileNameField.getCurrentValue();
	}

	public Folder getSelectedFolder() {
		return selectedFolder;
	}

}
