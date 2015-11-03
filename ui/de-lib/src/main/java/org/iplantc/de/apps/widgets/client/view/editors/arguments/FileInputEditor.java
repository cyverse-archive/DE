package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToFileConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector.HasDisableBrowseButtons;
import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class FileInputEditor extends AbstractArgumentEditor implements HasDisableBrowseButtons {
    private final ArgumentEditorConverter<File> editorAdapter;
    private final FileSelectorField fileSelector;

    @Inject
    FileInputEditor(final DiskResourceSelectorFieldFactory fileSelectorFactory,
                    @Assisted AppTemplateWizardAppearance appearance) {

        super(appearance);
        this.fileSelector = fileSelectorFactory.defaultFileSelector();
        editorAdapter = new ArgumentEditorConverter<>(fileSelector, new SplittableToFileConverter());

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
