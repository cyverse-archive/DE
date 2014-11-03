package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.fileViewers.client.events.DirtyStateChangedEvent;

import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * An interface for views which display files by {@link MimeType} and {@link InfoType}. Additionally,
 * some of these views may allow for editing/creation of the specific filetype.
 *
 * <h3>File Creation</h3>
 * For viewers capable of editing/creating files, new file creation is handled at view construction
 * time.
 *
 * @author sriram, jstroot
 */
public interface FileViewer extends IsWidget, IsMaskable, HasHandlers, FileSavedEvent.HasFileSavedEventHandlers {

    interface EditingSupport {

        boolean isDirty();

        void setDirty(Boolean dirty);

        void save();
    }

    /**
     * Responsible for initializing the appropriate view(s) for an existing file, or initializing
     * the specified view(s) for the creation of a new file.
     * <p/>
     * It will listen for {@link FileSavedEvent}s from the initialized views. When this occurs, it
     * will refresh all current views and fire a {@link DirtyStateChangedEvent}.
     * <p/>
     * Views will update notify this presenter of updates to their 'dirty' state via the
     * {@link #setViewDirtyState(boolean, FileViewer)} method.
     * <p/>
     */
    public interface Presenter extends DirtyStateChangedEvent.HasDirtyStateChangedEventHandlers {
        String getTitle();

        void go(HasOneWidget container,
                File file,
                Folder parentFolder,
                boolean editing,
                boolean isVizTabFirst,
                AsyncCallback<String> asyncCallback);

        boolean isDirty();

        void loadStructuredData(Integer pageNumber, Integer pageSize, String separator);

        void loadTextData(Integer pageNumber, Integer pageSize);

        void newFileGo(HasOneWidget container,
                       String title,
                       MimeType contentType,
                       Folder parentFolder,
                       boolean editing,
                       boolean vizTabFirst,
                       boolean isTabularFile,
                       Integer columns, String separator);

        void saveFile(FileViewer fileViewer);

        void saveFile();

        void saveFileWithExtension(FileViewer fileViewer, String viewerContent,
                                   String fileExtension);

        void setViewDirtyState(boolean dirty, FileViewer dirtyViewer);
    }

    String COLUMNS_KEY = "columns";

    String getEditorContent();

    boolean isDirty();

    String getViewName();

    void setData(Object data);
}
