package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.views.dialogs.FileFolderSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.FolderSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourceSelectorDialogFactory {
    FolderSelectDialog createFolderSelector(HasPath folderToSelect);
    FolderSelectDialog createFilteredFolderSelector(HasPath value,
                                                    List<InfoType> infoTypeFilters);

    FileSelectDialog createFileSelector(boolean singleSelect,
                                        HasPath folderToSelect);
    FileSelectDialog createFilteredFileSelectorWithFolder(boolean singleSelect,
                                                          HasPath folderToSelect,
                                                          List<InfoType> infoTypeFilters);
    FileSelectDialog createFileSelectDialog(boolean singleSelect,
                                            List<DiskResource> diskResourcesToSelect);
    FileSelectDialog createFilteredFileSelectorWithResources(boolean singleSelect,
                                                             List<DiskResource> diskResourcesToSelect,
                                                             List<InfoType> infoTypeFilters);
    SaveAsDialog createSaveAsDialog(HasPath folderToSelect);

    FileFolderSelectDialog createFileFolderSelectDialog(HasPath folderToSelect,
                                                        List<DiskResource> diskResourcesToSelect,
                                                        List<InfoType> infoTypeFilters);
}
