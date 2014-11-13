package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.views.widgets.FolderSelectorField;

import java.util.List;

/**
 * @author jstroot
 */
public interface FolderSelectorFieldFactory {
    FolderSelectorField create(List<InfoType> infoTypes);

    FolderSelectorField defaultFolderSelector();
}
