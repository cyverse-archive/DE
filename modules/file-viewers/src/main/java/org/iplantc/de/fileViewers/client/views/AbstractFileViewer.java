/**
 *
 */
package org.iplantc.de.fileViewers.client.views;

import static org.iplantc.de.client.events.FileSavedEvent.FileSavedEventHandler;
import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author sriram, jstroot
 */
public abstract class AbstractFileViewer implements FileViewer {

    protected File file;

    protected String infoType;

    public AbstractFileViewer(File file, String infoType) {
        this.file = file;
        this.infoType = infoType;
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEventHandler handler) {
        // Subclasses which use the FileSaveCallback, or otherwise fire a FileSavedEvent will override this method
        return null;
    }

    @Override
    public abstract Widget asWidget();

    @Override
    public String getViewName() {
        if (file != null) {
            return file.getName();
        } else {
            return "Untitled-" + Math.random();
        }
    }

    public abstract void loadData();

    @Override
    public abstract void setData(Object data);

    protected long getFileSize() {
        if (file != null) {
            return file.getSize();
        }

        return 0;
    }

}
