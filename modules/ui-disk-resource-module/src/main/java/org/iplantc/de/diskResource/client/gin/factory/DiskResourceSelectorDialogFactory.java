package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.FolderSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourceSelectorDialogFactory {
    FolderSelectDialog createFolderSelector(HasPath value, List<InfoType> infoTypeFilters);
    FolderSelectDialog createFolderSelector(HasPath folderToSelect);

    FileSelectDialog createFileSelector(boolean singleSelect);
    FileSelectDialog fileSelectDialogWithSelectedFolder(boolean singleSelect,
                                                        HasPath folderToSelect);
    FileSelectDialog fileSelectDialogWithSelectedResources(boolean singleSelect,
                                                           List<DiskResource> diskResourcesToSelect);
    SaveAsDialog createSaveAsDialog(HasPath folderToSelect);
}
