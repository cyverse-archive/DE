package org.iplantc.de.apps.widgets.client.gin.factory;

import org.iplantc.de.apps.widgets.client.view.editors.FileFolderInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FileInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.FolderInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.MultiFileInputEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;

/**
 * @author jstroot
 */
public interface ArgumentEditorGinFactory {
    FileInputEditor fileInputEditor(AppTemplateWizardAppearance appearance);
    FolderInputEditor folderInputEditor(AppTemplateWizardAppearance appearance);
    MultiFileInputEditor multiFileInputEditor(AppTemplateWizardAppearance appearance);
    FileFolderInputEditor fileFolderInputEditor(AppTemplateWizardAppearance appearance);

}
