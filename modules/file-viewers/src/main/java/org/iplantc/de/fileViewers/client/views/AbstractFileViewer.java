/**
 *
 */
package org.iplantc.de.fileViewers.client.views;

import static org.iplantc.de.client.events.FileSavedEvent.FileSavedEventHandler;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.fileViewers.client.FileViewer;

import com.google.gwt.event.shared.HandlerRegistration;

import com.sencha.gxt.widget.core.client.Composite;

/**
 * @author sriram, jstroot
 */
public abstract class AbstractFileViewer extends Composite implements FileViewer {

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
    public String getViewName(String fileName) {
        if (fileName != null) {
            return fileName;
        } else {
            return "Untitled-" + Math.random();
        }
    }

    @Override
    public abstract void setData(Object data);

    protected long getFileSize() {
        if (file != null) {
            return file.getSize();
        }

        return 0;
    }

    @Override
    public void refresh() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }
}
