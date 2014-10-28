package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author jstroot
 */
public class FileSetViewer extends Composite implements FileViewer, EditingSupport, StoreDataChangeEvent.StoreDataChangeHandler<String> {
    private static class HasPathModelKeyProvider implements ModelKeyProvider<String> {
        @Override
        public String getKey(String item) {
//            return item.getPath();
            return item;
        }
    }

    public interface FileSetEditorAppearance {

        Status.StatusAppearance getStatusAppearance();

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
    @UiField(provided = true)
    ListStore<String> listStore;

    private Boolean dirty;
    private File file;
    Logger LOG = Logger.getLogger(FileSetViewer.class.getName());

    @Inject
    public FileSetViewer(final FileSetEditorAppearance appearance) {
        this.appearance = appearance;

        listStore = new ListStore<>(new HasPathModelKeyProvider());
        listStore.addStoreDataChangeHandler(this);
        BorderLayoutContainer blc = ourUiBinder.createAndBindUi(this);

        // Reset button?
        initWidget(blc);
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

    public void setFile(final File file) {
        this.file = file;
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(FileSavedEvent.FileSavedEventHandler handler) {
        return getWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getInfoType() {
        return file == null ? null : file.getInfoType();
    }

    @Override
    public String getViewName() {
        return file == null ? "Untitled-" + Math.random() : file.getName();
    }

    @Override
    public void refresh() {/* Do nothing intentionally */ }

    @Override
    public void setData(Object data) {
        Preconditions.checkArgument(data instanceof List, "FileSetEditor.setData(..) expects a list");
        Preconditions.checkArgument(!((List)data).isEmpty(), "FileSetEditor.setData(..) expects a non-empty list");
//        Preconditions.checkArgument(((List)data).get(0) instanceof HasPath, "FileSetEditor.setData(..) expects a list of HasPath types");

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
    Status createEditingStatus() {
        return new Status(appearance.getStatusAppearance());
    }
}