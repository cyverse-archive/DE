package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.util.WindowUtil;

import java.util.List;

/**
 * @author sriram
 * 
 */
public class PdfDataViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType, boolean editing, Folder parentFolder) {
        String fileId = file.getId();
        if (editing) {
            ErrorAnnouncementConfig config = new ErrorAnnouncementConfig(
                    "Editing is not supported for this type of file!");
            IplantAnnouncer.getInstance().schedule(config);
        }
        if (fileId != null && !fileId.isEmpty()) {
            // // we got the url of the PDF file, so open it in a new window
            WindowUtil.open(ServicesInjector.INSTANCE.getFileEditorServiceFacade().getServletDownloadUrl(fileId) + "&attachment=0"); //$NON-NLS-1$
        }

        return null;
    }
}
