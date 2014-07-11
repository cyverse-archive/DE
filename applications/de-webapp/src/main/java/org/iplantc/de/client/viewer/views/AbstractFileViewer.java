/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author sriram
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.iplantc.de.client.viewer.views.FileViewer#setPresenter(org.iplantc.de.client.viewer.views.
     * FileViewer.Presenter)
     */
    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;

    }

    @Override
    public abstract void setData(Object data);


    @Override
    public abstract void loadData();
    
    @Override
    public void cleanUp() {
    	//do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.viewer.views.FileViewer#getFileSize()
     */
    @Override
    public long getFileSize() {
        if (file != null) {
            return file.getSize();
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.iplantc.de.client.viewer.views.FileViewer#getInfoType()
     */
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
