package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.fileViewers.client.events.DirtyStateChangedEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author sriram, jstroot
 */
public interface FileViewer extends IsWidget, FileSavedEvent.HasFileSavedEventHandlers {

    public interface Presenter extends DirtyStateChangedEvent.HasDirtyStateChangedEventHandlers {
        String getTitle();

        void go(HasOneWidget container,
                File file,
                Folder parentFolder,
                boolean editing,
                boolean isVizTabFirst,
                AsyncCallback<String> asyncCallback);

        boolean isDirty();

        void newFileGo(HasOneWidget container,
                       String title,
                       MimeType contentType,
                       Folder parentFolder,
                       boolean editing,
                       boolean vizTabFirst,
                       boolean isTabularFile,
                       Integer columns, String separator);

        void saveFile();

        void setViewDirtyState(boolean dirty);
    }

    String COLUMNS_KEY = "columns";
    int MAX_PAGE_SIZE_KB = 1024;
    int MIN_PAGE_SIZE_KB = 8;
    int PAGE_INCREMENT_SIZE_KB = 8;

    String getInfoType();

    String getViewName();

    void refresh();

    void setData(Object data);

}
