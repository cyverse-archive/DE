package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToFileConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector.HasDisableBrowseButtons;
import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;

public class FileInputEditor extends AbstractArgumentEditor implements HasDisableBrowseButtons {
    private final ArgumentEditorConverter<File> editorAdapter;
    private final FileSelectorField fileSelector;

    public FileInputEditor(AppTemplateWizardAppearance appearance) {
        super(appearance);
        fileSelector = new FileSelectorField();
        editorAdapter = new ArgumentEditorConverter<File>(fileSelector, new SplittableToFileConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public void disableBrowseButtons() {
        fileSelector.disableBrowseButtons();
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
