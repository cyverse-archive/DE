/**
 *
 */
package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.viewer.views.AbstractFileViewer;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ImageViewerImpl;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.gwt.json.client.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * @author sriram, jstroot
 */
public class ImageDataViewCommand implements ViewCommand {

    private final IplantAnnouncer announcer = IplantAnnouncer.getInstance();

    @Override
    public List<AbstractFileViewer> execute(final File file,
                                            final String infoType,
                                            final boolean editing,
                                            final Folder parentFolder,
                                            final JSONObject manifest,
                                            final FileViewer.Presenter presenter) {

        AbstractFileViewer view = null;

        if (editing) {
            ErrorAnnouncementConfig config = new ErrorAnnouncementConfig("Editing is not supported for this type of file!");
            announcer.schedule(config);
        }

        if (file != null && !file.getId().isEmpty()) {
            // we got the url of an image... lets add a tab
            view = new ImageViewerImpl(file);
        }
        return Arrays.asList(view);

    }

}
