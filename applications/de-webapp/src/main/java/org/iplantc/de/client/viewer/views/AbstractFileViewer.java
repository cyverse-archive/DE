/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import static org.iplantc.de.client.events.FileSavedEvent.FileSavedEventHandler;
import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author sriram, jstroot
 * 
 */
public abstract class AbstractFileViewer implements FileViewer {

    protected File file;

    protected String infoType;

    protected Presenter presenter;

    public AbstractFileViewer(File file, String infoType) {
        this.file = file;
        this.infoType = infoType;
    }

    @Override
    public abstract Widget asWidget();

    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEventHandler handler){
        // Subclasses which use the FileSaveCallback, or otherwise fire a FileSavedEvent will override this method
        return null;
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @Override
    public abstract void setData(Object data);

    public abstract void loadData();
    
    protected long getFileSize() {
        if (file != null) {
            return file.getSize();
        }

        return 0;
    }

    @Override
    public String getInfoType() {
        return infoType;
    }

    @Override
    public String getViewName() {
        if (file != null) {
            return file.getName();
        } else {
            return "Untitled-" + Math.random();
        }
    }

}
