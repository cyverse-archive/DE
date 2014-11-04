package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.fileViewers.client.FileViewer;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import java.util.logging.Logger;

/**
 * -- DnD of files/folders to this widget
 * -- DnD re-ordering of items within this widget
 * -- Line numbers
 *
 * This view differs from the {@link TextViewerImpl} and {@link StructuredTextViewer} views by
 * integrating the toolbar with the view in lieu of implementing the toolbar in a separate class.
 *
 * This view is a list which will contain all available {@code FileSet} items, so a paging-style
 * view will not be supported.
 *
 * @author jstroot
 */
public class FileSetViewer extends AbstractStructuredTextViewer implements StoreDataChangeEvent.StoreDataChangeHandler<Splittable> {

    public interface FileSetEditorAppearance extends AbstractStructuredTextViewerAppearance { }

    interface FileListViewerUiBinder extends UiBinder<BorderLayoutContainer, FileSetViewer> { }

    private static FileListViewerUiBinder ourUiBinder = GWT.create(FileListViewerUiBinder.class);

    private FileSetEditorAppearance appearance = GWT.create(FileSetEditorAppearance.class);

    private final File file;

    Logger LOG = Logger.getLogger(FileSetViewer.class.getName());

    public FileSetViewer(final File file,
                         final String infoType,
                         final boolean editing,
                         final FileViewer.Presenter presenter){
        super(file, infoType, editing, presenter);
        if(file != null){
            Preconditions.checkArgument(InfoType.FILE_SET.toString().equals(file.getInfoType()));
            presenter.loadFileSetData(pagingToolBar.getPageNumber(),
                                      (int) pagingToolBar.getPageSize(),
                                      getSeparator());
        } else {
            Preconditions.checkArgument(editing, "New files must be editable");
        }
        this.file = file;

        initWidget(ourUiBinder.createAndBindUi(this));
        listStore.addStoreDataChangeHandler(this);
    }

    @Override
    public void onDataChange(StoreDataChangeEvent<Splittable> event) {
         // For now, just set dirty flag whenever stuff changes
        setDirty(true);
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(FileSavedEvent.FileSavedEventHandler handler) {
        return getWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getViewName() {
        return file == null ? "Untitled-" + Math.random() : file.getName();
    }
}