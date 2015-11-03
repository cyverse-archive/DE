package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToHasPathListConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector.HasDisableBrowseButtons;
import org.iplantc.de.diskResource.client.views.widgets.MultiFileSelectorField;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

public class MultiFileInputEditor extends AbstractArgumentEditor implements HasDisableBrowseButtons{
    private final ArgumentEditorConverter<List<HasPath>> editorAdapter;
    private final MultiFileSelectorField multiFileSelector;

    @Inject
    MultiFileInputEditor(final DiskResourceSelectorFieldFactory selectorFactory,
                         @Assisted AppTemplateWizardAppearance appearance) {
        super(appearance);
        this.multiFileSelector = selectorFactory.creaeteMultiFileSelector(false);
        editorAdapter = new ArgumentEditorConverter<>(multiFileSelector,
                                                      new SplittableToHasPathListConverter());

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
