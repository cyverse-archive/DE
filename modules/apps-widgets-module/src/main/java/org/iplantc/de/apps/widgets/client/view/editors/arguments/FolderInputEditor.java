package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToFolderConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector.HasDisableBrowseButtons;
import org.iplantc.de.diskResource.client.views.widgets.FolderSelectorField;

public class FolderInputEditor extends AbstractArgumentEditor implements HasDisableBrowseButtons {
    private final ArgumentEditorConverter<Folder> editorAdapter;
    private final FolderSelectorField folderSelector;

    public FolderInputEditor(AppTemplateWizardAppearance appearance) {
        super(appearance);
        folderSelector = new FolderSelectorField();
        editorAdapter = new ArgumentEditorConverter<Folder>(folderSelector, new SplittableToFolderConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public void disableBrowseButtons() {
        folderSelector.disableBrowseButtons();
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
