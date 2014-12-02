package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.AbstractArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToDiskResourceConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorFieldFactory;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector;
import org.iplantc.de.diskResource.client.views.widgets.FileFolderSelectorField;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.Collections;

/**
 * @author jstroot
 */
public class FileFolderInputEditor extends AbstractArgumentEditor implements DiskResourceSelector.HasDisableBrowseButtons {

    private final ArgumentEditorConverter<DiskResource> editorConverter;
    private final FileFolderSelectorField fileFolderSelector;

    @Inject
    FileFolderInputEditor(final DiskResourceSelectorFieldFactory fileFolderSelectorFactory,
                          @Assisted final AppTemplateWizardAppearance appearance){
        super(appearance);
        fileFolderSelector = fileFolderSelectorFactory.createFilteredFileFolderSelector(Collections.<InfoType>emptyList());
        editorConverter = new ArgumentEditorConverter<>(fileFolderSelector, new SplittableToDiskResourceConverter());

        argumentLabel.setWidget(editorConverter);
    }

    @Override
    public void disableBrowseButtons() {
        fileFolderSelector.disableBrowseButtons();
    }

    @Override
    public AppTemplateForm.IArgumentEditorConverter valueEditor() {
        return editorConverter;
    }
}
