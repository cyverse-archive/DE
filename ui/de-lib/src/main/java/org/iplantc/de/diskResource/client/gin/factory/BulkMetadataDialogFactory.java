package org.iplantc.de.diskResource.client.gin.factory;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.views.dialogs.BulkMetadataDialog;

import com.google.inject.assistedinject.Assisted;

import java.util.List;

public interface BulkMetadataDialogFactory {

    BulkMetadataDialog create(DiskResourceSelectorFieldFactory factory,
                              @Assisted("destFolder") String destPath,
                              @Assisted("templates") List<MetadataTemplateInfo> templates,
                              @Assisted("mode") BulkMetadataDialog.BULK_MODE mode);

}
