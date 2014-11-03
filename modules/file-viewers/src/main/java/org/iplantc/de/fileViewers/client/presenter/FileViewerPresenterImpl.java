package org.iplantc.de.fileViewers.client.presenter;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorGetManifest;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.fileViewers.client.callbacks.FileSaveCallback;
import org.iplantc.de.fileViewers.client.callbacks.TreeUrlCallback;
import org.iplantc.de.fileViewers.client.events.DirtyStateChangedEvent;
import org.iplantc.de.fileViewers.client.views.ExternalVisualizationURLViewerImpl;
import org.iplantc.de.fileViewers.client.views.FileViewer;
import org.iplantc.de.fileViewers.client.views.SaveAsDialogCancelSelectHandler;
import org.iplantc.de.fileViewers.client.views.SaveAsDialogOkSelectHandler;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import static com.google.common.base.Preconditions.*;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.widget.core.client.PlainTabPanel;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class FileViewerPresenterImpl implements FileViewer.Presenter, FileSavedEvent.FileSavedEventHandler {
    private class GetManifestCallback implements AsyncCallback<String> {
        private final AsyncCallback<String> asyncCallback;
        private final boolean editing;
        private final IplantErrorStrings errorStrings;
        private final File file;
        private final boolean isVizTabFirst;
        private final Folder parentFolder;
        private final FileViewerPresenterImpl presenter;

        public GetManifestCallback(final FileViewerPresenterImpl presenter,
                                   final File file,
                                   final Folder parentFolder,
                                   final boolean editing,
                                   final boolean isVizTabFirst,
                                   final AsyncCallback<String> asyncCallback,
                                   final IplantErrorStrings errorStrings) {

            this.presenter = presenter;
            this.file = file;
            this.parentFolder = parentFolder;
            this.editing = editing;
            this.isVizTabFirst = isVizTabFirst;
            this.asyncCallback = asyncCallback;
            this.errorStrings = errorStrings;
        }

        @Override
        public void onFailure(Throwable caught) {
            asyncCallback.onFailure(caught);
            DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
            String message = caught.getMessage();

            if (JsonUtils.safeToEval(message)) {
                AutoBean<ErrorGetManifest> errorBean = AutoBeanCodex.decode(factory,
                                                                            ErrorGetManifest.class,
                                                                            message);
                ErrorHandler.post(errorBean.as(), caught);
            } else {
                ErrorHandler.post(errorStrings.retrieveStatFailed(),
                                  caught);
            }
        }

        @Override
        public void onSuccess(String result) {
            asyncCallback.onSuccess(null);

            JSONObject manifest = JsonUtil.getObject(result);
            String infoType = JsonUtil.getString(manifest, "info-type");
            MimeType contentType = MimeType.fromTypeString(JsonUtil.getString(manifest, "content-type"));
            checkNotNull(contentType);
            presenter.setTitle(file.getName());
            presenter.setManifest(manifest);
            presenter.setContentType(contentType);
            presenter.composeView(file, parentFolder, manifest, contentType, infoType, editing, isVizTabFirst);
            LOG.info("Manifest retrieved: " + file.getName());
        }
    }

    Logger LOG = Logger.getLogger(FileViewerPresenterImpl.class.getName());
    @Inject IplantDisplayStrings displayStrings;
    @Inject IplantErrorStrings errorStrings;
    @Inject MimeTypeViewerResolverFactory mimeFactory;
    @Inject CommonModelAutoBeanFactory factory;
    @Inject FileEditorServiceFacade fileEditorService;

    private MimeType contentType;
    /**
     * The file shown in the window.
     */
    private File file;
    private boolean isDirty;
    private JSONObject manifest;
    private Folder parentFolder;
    private PlainTabPanel tabPanel;
    private String title;
    /**
     * A presenter can handle more than one view of the same data at a time
     */
    private List<FileViewer> viewers;
    private boolean vizTabFirst;

    @Inject
    public FileViewerPresenterImpl() {
        viewers = Lists.newArrayList();
        tabPanel = new PlainTabPanel();
    }

    @Override
    public HandlerRegistration addDirtyStateChangedEventHandler(DirtyStateChangedEvent.DirtyStateChangedEventHandler handler) {
        return tabPanel.addHandler(handler, DirtyStateChangedEvent.TYPE);
    }

    @Override
    public String getTitle() {
        return title;
    }

    void setTitle(String windowTitle) {
        this.title = windowTitle;
    }

    @Override
    public void go(final HasOneWidget container,
                   final File file,
                   final Folder parentFolder,
                   final boolean editing,
                   final boolean isVizTabFirst,
                   final AsyncCallback<String> asyncCallback) {
        this.parentFolder = parentFolder;
        checkState(!tabPanel.isAttached(), "You cannot 'go' this presenter more than once.");
        checkArgument(file != null, "File cannot be null.\n" +
                                        "To create new files, user 'newFileGo(..)'.");
        container.setWidget(tabPanel);
        tabPanel.mask(displayStrings.loadingMask());
        this.file = file;
        this.vizTabFirst = isVizTabFirst;

        fileEditorService.getManifest(file, new GetManifestCallback(this,
                                                                    file,
                                                                    parentFolder,
                                                                    editing,
                                                                    isVizTabFirst,
                                                                    asyncCallback,
                                                                    errorStrings));
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void loadStructuredData(Integer pageNumber, Integer pageSize, String separator) {
        if(file == null){
            return;
        }

        // Todo MaskView
        fileEditorService.readCsvChunk(file, separator, pageNumber, pageSize, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO unmask view
                ErrorHandler.post(errorStrings.unableToRetrieveFileData(file.getName()));
            }

            @Override
            public void onSuccess(String result) {
                // TODO unmask view
                // Get data from result
                StructuredText structuredText = getStructuredText(result);
                for(FileViewer view : viewers){
                    // FIXME Possible issue with data compatibility between views
                    view.setData(structuredText);
                }
            }

            private StructuredText getStructuredText(String result) {
                AutoBean<StructuredText> textAutoBean = AutoBeanCodex.decode(factory, StructuredText.class, result);
                return textAutoBean.as();
            }
        });
    }

    @Override
    public void loadTextData(Integer pageNumber, Integer pageSize) {
        if(file == null){
            return;
        }
        // TODO MaskView
        long chunkPosition = pageSize * (pageNumber - 1);
        fileEditorService.readChunk(file, chunkPosition, pageSize, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO unmask view
                ErrorHandler.post(errorStrings.unableToRetrieveFileData(file.getName()));
            }

            @Override
            public void onSuccess(String result) {
                // TODO unmask view
                // Get Data from result
                String data = StringQuoter.split(result).get("chunk").asString();
                for(FileViewer view : viewers){
                    view.setData(data);
                }
            }
        } );
    }

    @Override
    public void newFileGo(final HasOneWidget container,
                          final String title,
                          final MimeType contentType,
                          final Folder parentFolder,
                          final boolean editing,
                          final boolean vizTabFirst,
                          final boolean isTabularFile,
                          final Integer columns,
                          final String delimiter) {
        this.parentFolder = parentFolder;
        checkNotNull(contentType);
        checkState(!tabPanel.isAttached(), "You cannot 'go' this presenter more than once.");
        container.setWidget(tabPanel);
        tabPanel.mask(displayStrings.loadingMask());
        this.vizTabFirst = vizTabFirst;

        // Assemble manifest
        JSONObject manifest = new JSONObject();
        manifest.put("content-type", new JSONString(contentType.toString()));

        if (isTabularFile) {
            checkArgument(!Strings.isNullOrEmpty(delimiter), "Must specify a delimiter.");
            checkArgument(delimiter.matches("(,|\\t)"), "Unrecognized delimiter \"" + delimiter + "\"");
            checkNotNull(columns, "Number of columns must be specified for new tabular files.");
            checkArgument(columns >= 1, "Must specify a non-zero, positive number of columns.");

            JSONString infoType = null;
            if (",".equals(delimiter)) {
                infoType = new JSONString("csv");
            } else if ("\t".equals(delimiter)) {
                infoType = new JSONString("tsv");
            }
            manifest.put("info-type", infoType);

            manifest.put(FileViewer.COLUMNS_KEY, new JSONNumber(columns));
        }
        setTitle(title);
        setManifest(manifest);
        setContentType(contentType);
        String infoType = JsonUtil.getString(manifest, "info-type");
        composeView(null, parentFolder, manifest, contentType, infoType, editing, vizTabFirst);
    }

    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (file == null) {
            file = event.getFile();
            /* Iterate through tab collection and individually remove. TabPanel.clear() does not
             * correctly clear the tabs.
             */
            for (int i = tabPanel.getWidgetCount() - 1; i >= 0; i--) {
                tabPanel.remove(i);
            }
            viewers.clear();
            setTitle(file.getName());
            composeView(file, parentFolder, manifest, contentType, file.getInfoType(), true, vizTabFirst);
        }
        setViewDirtyState(false, null);
    }

    @Override
    public void saveFile() {
        // Locate any dirty editors
        for(FileViewer viewer : viewers){
            // First viewer wins
            if(viewer.isDirty()){
                saveFile(viewer);
            }
        }
    }

    @Override
    public void saveFile(final FileViewer fileViewer) {
        if(file == null) {
            final SaveAsDialog saveDialog = new SaveAsDialog(parentFolder);
            SaveAsDialogOkSelectHandler okSelectHandler = new SaveAsDialogOkSelectHandler(fileViewer,
                                                                                          fileViewer,
                                                                                          saveDialog,
                                                                                          displayStrings.savingMask(),
                                                                                          fileViewer.getEditorContent(),
                                                                                          fileEditorService);
            SaveAsDialogCancelSelectHandler cancelSelectHandler = new SaveAsDialogCancelSelectHandler(fileViewer,
                                                                                                      saveDialog);
            saveDialog.addOkButtonSelectHandler(okSelectHandler);
            saveDialog.addCancelButtonSelectHandler(cancelSelectHandler);
            saveDialog.show();
            saveDialog.toFront();
        } else {
            fileEditorService.uploadTextAsFile(file.getPath(),
                                               fileViewer.getEditorContent(),
                                               false,
                                               new FileSaveCallback(file.getPath(),
                                                                    false,
                                                                    fileViewer,
                                                                    fileViewer));
        }
    }

    @Override
    public void saveFileWithExtension(FileViewer fileViewer, String viewerContent,
                                      String fileExtension) {
        Preconditions.checkState(file != null, "File should not be null when calling this method.");

        String destination = file.getPath() + fileExtension;
        fileEditorService.uploadTextAsFile(destination,
                                           viewerContent,
                                           true,
                                           new FileSaveCallback(destination,
                                                                true,
                                                                fileViewer,
                                                                fileViewer));
    }

    @Override
    public void setViewDirtyState(boolean dirty, FileViewer dirtyViewer) {
        // Return if state has not changed
        if(this.isDirty == dirty){
            return;
        }
        this.isDirty = dirty;
        tabPanel.fireEvent(new DirtyStateChangedEvent(dirty));

        // Exit if there is not dirtyViewer
        if(dirtyViewer == null){
            return;
        }
        // Update other viewers with dirty viewer contents
        for(FileViewer viewer : viewers){
            // Don't update the dirty viewer
            if(viewer == dirtyViewer){
                continue;
            }
            /* FIXME Update other viewers with dirtyViewer contents
             * This implies being able to convert back and forth with text and structured text
             */
        }
    }

    /**
     * Calls the tree URL service to fetch the URLs to display in the grid.
     */
    void callTreeCreateService(final FileViewer viewer, File file) {
        tabPanel.mask(displayStrings.loadingMask());
        IsMaskable maskable = new IsMaskable() {
            @Override
            public void mask(String loadingMask) {
                tabPanel.mask(loadingMask);
            }

            @Override
            public void unmask() {
                tabPanel.unmask();
            }
        };
        fileEditorService.getTreeUrl(file.getPath(),
                                     false,
                                     new TreeUrlCallback(file,
                                                         maskable,
                                                         viewer));
    }

    void composeView(final File file,
                     final Folder parentFolder,
                     final JSONObject manifest,
                     final MimeType contentType,
                     final String infoType,
                     final boolean editing,
                     final boolean isVizTabFirst) {
        checkNotNull(contentType);

        List<? extends FileViewer> viewers_list = mimeFactory.getViewerCommand(file, infoType, editing, manifest, this, contentType);

        viewers.addAll(viewers_list);

        Splittable infoTypeSplittable = DiskResourceUtil.createInfoTypeSplittable(infoType);
        boolean treeViewer = DiskResourceUtil.isTreeTab(infoTypeSplittable);
        boolean cogeViewer = DiskResourceUtil.isGenomeVizTab(infoTypeSplittable);
        boolean ensembleViewer = DiskResourceUtil.isEnsemblVizTab(infoTypeSplittable);

        if (treeViewer || cogeViewer || ensembleViewer) {
            FileViewer vizViewer = new ExternalVisualizationURLViewerImpl(file, infoType);
            List<VizUrl> urls = getManifestVizUrls(manifest);

            if (urls != null && !urls.isEmpty()) {
                vizViewer.setData(urls);
            } else if (treeViewer) {
                callTreeCreateService(vizViewer, file);
            }
            if (isVizTabFirst) {
                viewers.add(0, vizViewer);
            } else {
                viewers.add(vizViewer);
            }
        }

        for (FileViewer view : viewers) {
            // Add ourselves as FileSaved handlers
            view.addFileSavedEventHandler(this);
            tabPanel.add(view.asWidget(), view.getViewName());

        }

        if (viewers.size() == 0) {
            // FIXME This add method not supported
            // Need to wrap in a simple container and add to that, or add this as a tab.
            tabPanel.add(new HTML(displayStrings.fileOpenMsg()));
        }
        tabPanel.unmask();
    }

    void setContentType(MimeType contentType) {
        this.contentType = contentType;
    }

    void setManifest(JSONObject manifest) {
        this.manifest = manifest;
    }

    /**
     * Gets the tree-urls json array from the manifest.
     *
     * @param manifest the file manifest.
     * @return A json array of at least one tree URL, or null otherwise.
     */
    private List<VizUrl> getManifestVizUrls(JSONObject manifest) {
        return TreeUrlCallback.getTreeUrls(manifest.toString());
    }

}
