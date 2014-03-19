package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToHasIdListConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector.HasDisableBrowseButtons;
import org.iplantc.de.diskResource.client.views.widgets.MultiFileSelectorField;

import java.util.List;

public class MultiFileInputEditor extends AbstractArgumentEditor implements HasDisableBrowseButtons{
    private final ArgumentEditorConverter<List<HasId>> editorAdapter;
    private final MultiFileSelectorField multiFileSelector;

    public MultiFileInputEditor(AppTemplateWizardAppearance appearance) {
        super(appearance);
        multiFileSelector = new MultiFileSelectorField();
        editorAdapter = new ArgumentEditorConverter<List<HasId>>(multiFileSelector, new SplittableToHasIdListConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public void disableBrowseButtons() {
        multiFileSelector.disableBrowseButtons();
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
