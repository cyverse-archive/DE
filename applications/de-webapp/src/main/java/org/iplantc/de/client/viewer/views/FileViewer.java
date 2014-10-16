/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author sriram, jstroot
 * 
 */
public interface FileViewer extends IsWidget, FileSavedEvent.HasFileSavedEventHandlers {

    int MIN_PAGE_SIZE_KB = 8;
    int MAX_PAGE_SIZE_KB = 1024;
    int PAGE_INCREMENT_SIZE_KB = 8;
    String COLUMNS_KEY = "columns";

    public interface Presenter extends FileSavedEvent.HasFileSavedEventHandlers {
        void go(HasOneWidget container, Folder parentFolder);

        void setViewDirtyState(boolean dirty);

        boolean isDirty();
        
        void setTitle(String windowTitle);
    }

    void setPresenter(Presenter p);

    void setData(Object data);

    String getInfoType();

    String getViewName();
    
    void refresh();

}
