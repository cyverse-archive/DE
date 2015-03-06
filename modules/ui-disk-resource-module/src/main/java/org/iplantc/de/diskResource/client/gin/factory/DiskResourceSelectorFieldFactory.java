package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.views.widgets.FileFolderSelectorField;
import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;
import org.iplantc.de.diskResource.client.views.widgets.FolderSelectorField;
import org.iplantc.de.diskResource.client.views.widgets.MultiFileSelectorField;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourceSelectorFieldFactory {
    FolderSelectorField createFilteredFolderSelector(List<InfoType> infoTypes);

    FolderSelectorField defaultFolderSelector();


    FileSelectorField createFilteredFileSelector(List<InfoType> infoTypes);
    FileSelectorField defaultFileSelector();

    FileFolderSelectorField createFilteredFileFolderSelector(List<InfoType> infotypes);

    MultiFileSelectorField creaeteMultiFileSelector(boolean allowFolderSelect);


}
