package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.fileViewers.client.FileViewer;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import java.util.List;
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
public class FileSetViewer extends Composite implements FileViewer, FileViewer.EditingSupport, StoreDataChangeEvent.StoreDataChangeHandler<String> {
    private class GetFileContentsCallback implements AsyncCallback<String> {
        private final Component maskable;
        private final String fileName;
        private final FileSetEditorAppearance appearance;

        GetFileContentsCallback(final Component maskable,
                                final String fileName,
                                final FileSetEditorAppearance appearance){
            this.maskable = maskable;
            this.fileName = fileName;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.failedToRetrieveFileData(fileName), caught);
            maskable.unmask();
        }

        @Override
        public void onSuccess(String result) {
            Splittable splitResult = StringQuoter.split(result);
            String data = splitResult.get("csv").asString();
            setData(data);
            maskable.unmask();
        }
    }

    private static class StringModelKeyProvider implements ModelKeyProvider<String> {
        @Override
        public String getKey(String item) {
            return item;
        }
    }

    public interface FileSetEditorAppearance {

        String failedToRetrieveFileData(String fileName);

        Status.StatusAppearance getStatusAppearance();

        String isEditingText();

        String loadingMask();

        String notEditingText();

        String saveBtnText();

        ImageResource saveBtnIcon();
    }

    interface FileListViewerUiBinder extends UiBinder<BorderLayoutContainer, FileSetViewer> { }

    private static FileListViewerUiBinder ourUiBinder = GWT.create(FileListViewerUiBinder.class);

    @UiField(provided = true)
    final FileSetEditorAppearance appearance;
    @UiField
    Status editingStatus;
    @UiField
    TextButton saveBtn;
    @UiField
    ListView<String, String> fileSet;
    @UiField
    ListStore<String> listStore;
    @UiField
    BorderLayoutContainer blc;

    private final FileEditorServiceFacade fileEditorService;
    private final File file;

    private boolean dirty;
    Logger LOG = Logger.getLogger(FileSetViewer.class.getName());

    public FileSetViewer(final File file,
                         final boolean isEditing,
                         final FileEditorServiceFacade fileEditorService){
        this(GWT.<FileSetEditorAppearance> create(FileSetEditorAppearance.class),
             file,
             isEditing,
             fileEditorService);
    }

    FileSetViewer(final FileSetEditorAppearance appearance,
                  final File file,
                  final boolean isEditing,
                  final FileEditorServiceFacade fileEditorService) {
        if(file != null){
            Preconditions.checkArgument(InfoType.FILE_SET.toString().equals(file.getInfoType()));
        } else {
            Preconditions.checkArgument(isEditing, "New files must be editable");
        }
        this.appearance = appearance;
        this.file = file;
        this.fileEditorService = fileEditorService;

        // Reset button?
        initWidget(ourUiBinder.createAndBindUi(this));
        listStore.addStoreDataChangeHandler(this);
        initialize(file, isEditing);
    }

    void initialize(final File file,
                    final boolean isEditing) {
        // Initialize the toolbar status box
        String editingStatusText = isEditing ? appearance.isEditingText() : appearance.notEditingText();
        editingStatus.setText(editingStatusText);

        // If file is not null, get content
        if(file != null) {
            // FileSets should never have to page.
            blc.mask(appearance.loadingMask());
            fileEditorService.readChunk(file, 0, file.getSize(), new GetFileContentsCallback(blc, file.getName(), appearance));
        }
    }

    @Override
    public String getEditorContent() {
        return null;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void onDataChange(StoreDataChangeEvent<String> event) {
         // For now, just set dirty flag whenever stuff changes
        dirty = true;
    }

    @Override
    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void save() {
        String content = "";
        for(String hasPath : listStore.getAll()){
            content += hasPath + "\n";
        }
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(FileSavedEvent.FileSavedEventHandler handler) {
        return getWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getViewName() {
        return file == null ? "Untitled-" + Math.random() : file.getName();
    }

    @Override
    public void setData(Object data) {
        Preconditions.checkArgument(data instanceof List, "FileSetEditor.setData(..) expects a list");
        Preconditions.checkArgument(!((List)data).isEmpty(), "FileSetEditor.setData(..) expects a non-empty list");

        List<String> hasPathList = (List<String>)data;
        LOG.fine("Data passed to setData(): " + hasPathList);
        listStore.addAll(hasPathList);
    }

    @UiFactory
    ListView<String, String> createFileSet(){
        ValueProvider<String, String> valueProvider = new ValueProvider<String, String>() {
            @Override
            public String getValue(String object) {
                return object;
            }

            @Override
            public void setValue(String object, String value) { }

            @Override
            public String getPath() {
                return "";
            }
        };
        return new ListView<>(listStore, valueProvider);
    }

    @UiFactory
    ListStore<String> createListStore(){
        return new ListStore<>(new StringModelKeyProvider());
    }

    @UiFactory
    Status createEditingStatus() {
        return new Status(appearance.getStatusAppearance());
    }
}