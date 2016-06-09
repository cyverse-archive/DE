package org.iplantc.de.fileViewers.client.presenter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.iplantc.de.client.services.FileEditorServiceFacade.COMMA_DELIMITER;
import static org.iplantc.de.client.services.FileEditorServiceFacade.TAB_DELIMITER;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorGetManifest;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.callbacks.FileSaveCallback;
import org.iplantc.de.fileViewers.client.callbacks.TreeUrlCallback;
import org.iplantc.de.fileViewers.client.events.DirtyStateChangedEvent;
import org.iplantc.de.fileViewers.client.views.AbstractStructuredTextViewer;
import org.iplantc.de.fileViewers.client.views.ExternalVisualizationURLViewerImpl;
import org.iplantc.de.fileViewers.client.views.SaveAsDialogCancelSelectHandler;
import org.iplantc.de.fileViewers.client.views.SaveAsDialogOkSelectHandler;
import org.iplantc.de.fileViewers.client.views.TextViewerImpl;
import org.iplantc.de.shared.AsyncProviderWrapper;

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
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class FileViewerPresenterImpl implements FileViewer.Presenter, FileSavedEvent.FileSavedEventHandler {
    private class GetManifestCallback implements AsyncCallback<String> {
        private final AsyncCallback<String> asyncCallback;
        private final FileViewer.FileViewerPresenterAppearance presenterAppearance;
        private final boolean editing;
        private final SimpleContainer simpleContainer;
        private final File file;
        private final boolean isVizTabFirst;
        private final Folder parentFolder;
        private final FileViewerPresenterImpl presenter;

        public GetManifestCallback(final FileViewerPresenterImpl presenter,
                                   final SimpleContainer simpleContainer,
                                   final File file,
                                   final Folder parentFolder,
                                   final boolean editing,
                                   final boolean isVizTabFirst,
                                   final AsyncCallback<String> asyncCallback,
                                   final FileViewer.FileViewerPresenterAppearance presenterAppearance) {

            this.presenter = presenter;
            this.simpleContainer = simpleContainer;
            this.file = file;
            this.parentFolder = parentFolder;
            this.editing = editing;
            this.isVizTabFirst = isVizTabFirst;
            this.asyncCallback = asyncCallback;
            this.presenterAppearance = presenterAppearance;
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
                ErrorHandler.post(presenterAppearance.retrieveFileManifestFailed(), caught);
            }
        }

        @Override
        public void onSuccess(String result) {
            asyncCallback.onSuccess(null);

            JSONObject manifest = jsonUtil.getObject(result);
            String infoType = jsonUtil.getString(manifest, DiskResource.INFO_TYPE_KEY);
            MimeType contentType = MimeType.fromTypeString(jsonUtil.getString(manifest, "content-type"));
            checkNotNull(contentType);
            presenter.setTitle(file.getName());
            presenter.setContentType(contentType);
            presenter.composeView(file, parentFolder, manifest, contentType, infoType, editing, isVizTabFirst);
            LOG.info("Manifest retrieved: " + file.getName());
            simpleContainer.unmask();
        }
    }

    Logger LOG = Logger.getLogger(FileViewerPresenterImpl.class.getName());
    @Inject MimeTypeViewerResolverFactory mimeFactory;
    @Inject CommonModelAutoBeanFactory factory;
    @Inject FileEditorServiceFacade fileEditorService;
    @Inject FileViewer.FileViewerPresenterAppearance appearance;
    @Inject AsyncProviderWrapper<SaveAsDialog> saveAsDialogProvider;
    @Inject UserSessionServiceFacade userSessionService;
    @Inject DiskResourceServiceFacade diskResourceServiceFacade;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject JsonUtil jsonUtil;
    @Inject IplantAnnouncer announcer;

    private MimeType contentType;
    /**
     * The file shown in the window.
     */
    private File file;
    private boolean isDirty;
    private Folder parentFolder;
    private final PlainTabPanel tabPanel;
    private String title;
    private final SimpleContainer simpleContainer;
    /**
     * A presenter can handle more than one view of the same data at a time
     */
    private final List<FileViewer> viewers;
    private boolean vizTabFirst;

    @Inject
    public FileViewerPresenterImpl() {
        viewers = Lists.newArrayList();
        tabPanel = new PlainTabPanel();
        simpleContainer = new SimpleContainer();
        simpleContainer.setWidget(tabPanel);
    }

    @Override
    public HandlerRegistration addDirtyStateChangedEventHandler(DirtyStateChangedEvent.DirtyStateChangedEventHandler handler) {
        return simpleContainer.addHandler(handler, DirtyStateChangedEvent.TYPE);
    }

    @Override
    public String getPathListFileIdentifier() {
        return fileEditorService.getPathListFileIdentifier();
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
        container.setWidget(simpleContainer);
        simpleContainer.mask(appearance.retrieveFileManifestMask());
        this.file = file;
        this.vizTabFirst = isVizTabFirst;

        fileEditorService.getManifest(file, new GetManifestCallback(this,
                                                                    simpleContainer,
                                                                    file,
                                                                    parentFolder,
                                                                    editing,
                                                                    isVizTabFirst,
                                                                    asyncCallback,
                                                                    appearance));
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void loadStructuredData(Integer pageNumber, Long pageSize, String separator) {
        Preconditions.checkNotNull(file, "Cannot have null file if attempting to load its contents");

        simpleContainer.mask(appearance.retrievingFileContentsMask());
        fileEditorService.readCsvChunk(file, separator, pageNumber, pageSize, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                simpleContainer.unmask();
                ErrorHandler.post(appearance.unableToRetrieveFileData(file.getName()));
            }

            @Override
            public void onSuccess(String result) {
                // Get data from result
                StructuredText structuredText = getStructuredText(result);
                for(FileViewer view : viewers){
                    // FIXME Possible issue with data compatibility between views
                    if (view instanceof AbstractStructuredTextViewer) {
                        view.setData(structuredText);
                    }
                }
                simpleContainer.unmask();
            }

            private StructuredText getStructuredText(String result) {
                AutoBean<StructuredText> textAutoBean = AutoBeanCodex.decode(factory, StructuredText.class, result);
                return textAutoBean.as();
            }
        });
    }

    @Override
    public void loadTextData(Integer pageNumber, Long pageSize) {
        Preconditions.checkNotNull(file, "Cannot have null file if attempting to load its contents");

        simpleContainer.mask(appearance.retrievingFileContentsMask());
        long chunkPosition = pageSize * (pageNumber - 1);
        fileEditorService.readChunk(file, chunkPosition, pageSize, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                simpleContainer.unmask();
                ErrorHandler.post(appearance.unableToRetrieveFileData(file.getName()));
            }

            @Override
            public void onSuccess(String result) {
                // Get Data from result
                String data = StringQuoter.split(result).get(StructuredText.TEXT_CHUNK_KEY).asString();
                for(FileViewer view : viewers){
                    // set text data only to TextViewer.
                    if (view instanceof TextViewerImpl) {
                        view.setData(data);
                    }
                }
                simpleContainer.unmask();
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
                          boolean isPathListFile, final Integer columns,
                          final String delimiter) {
        this.parentFolder = parentFolder;
        checkNotNull(contentType);
        checkState(!tabPanel.isAttached(), "You cannot 'go' this presenter more than once.");
        container.setWidget(simpleContainer);
        simpleContainer.mask(appearance.initializingFileViewer());
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
            if (COMMA_DELIMITER.equals(delimiter)) {
                infoType = new JSONString(InfoType.CSV.toString());
            } else if (TAB_DELIMITER.equals(delimiter)) {
                infoType = new JSONString(InfoType.TSV.toString());
            }
            manifest.put(DiskResource.INFO_TYPE_KEY, infoType);

            manifest.put(FileViewer.COLUMNS_KEY, new JSONNumber(columns));
        }
        if(isPathListFile){
            manifest.put(DiskResource.INFO_TYPE_KEY, new JSONString(InfoType.HT_ANALYSIS_PATH_LIST.toString()));
            manifest.put(FileViewer.PATH_LIST_KEY, new JSONString("true"));
        }
        setTitle(title);
        setContentType(contentType);
        String infoType = jsonUtil.getString(manifest, DiskResource.INFO_TYPE_KEY);
        composeView(null, parentFolder, manifest, contentType, infoType, editing, vizTabFirst);
    }

    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (file == null) {
            file = event.getFile();
            // Update tab panel names
            for (int i = tabPanel.getWidgetCount() - 1; i >= 0; i--) {
                Widget widget = tabPanel.getWidget(i);
                tabPanel.update(widget, new TabItemConfig(viewers.get(i).getViewName(file.getName())));
            }
            setTitle(file.getName());
            for(FileViewer viewer : viewers){
                viewer.refresh();
            }
        } else {
            simpleContainer.unmask();
        }

        setViewDirtyState(false, null);
    }

    @Override
    public void saveFile() {
        // Locate any dirty editors
        LOG.fine("no.of viewers:" + viewers.size());
        // assuming the widget that is active is the dirty widget. save it
        for (FileViewer viewer : viewers) {
            if (viewer.isDirty()) {
                saveFile(viewer);
                break;
            }
        }
    }

    @Override
    public void saveFile(final FileViewer fileViewer) {
        LOG.fine("saving file...");
        if(file == null) {
            saveAsDialogProvider.get(new AsyncCallback<SaveAsDialog>() {
                @Override
                public void onFailure(Throwable caught) {
                    announcer.schedule(new ErrorAnnouncementConfig("Something happened while we tried to save your file. Please try again or contact support."));
                }

                @Override
                public void onSuccess(SaveAsDialog result) {
                    SaveAsDialogOkSelectHandler okSelectHandler = new SaveAsDialogOkSelectHandler(userSessionService,
                                                                                                  asMaskable(simpleContainer),
                                                                                                  fileViewer,
                                                                                                  result,
                                                                                                  appearance.savingMask(),
                                                                                                  fileViewer.getEditorContent(),
                                                                                                  fileEditorService);
                    SaveAsDialogCancelSelectHandler cancelSelectHandler = new SaveAsDialogCancelSelectHandler(fileViewer,
                                                                                                              result);
                    result.addOkButtonSelectHandler(okSelectHandler);
                    result.addCancelButtonSelectHandler(cancelSelectHandler);
                    result.show(parentFolder);
                    result.toFront();
                }
            });

        } else {
            simpleContainer.mask(appearance.savingMask());
            fileEditorService.uploadTextAsFile(file.getPath(),
                                               fileViewer.getEditorContent(),
                                               false,
                                               new FileSaveCallback(userSessionService,
                                                                    file.getPath(),
                                                                    false,
                                                                    asMaskable(simpleContainer),
                                                                    fileViewer));
        }
    }

    private IsMaskable asMaskable(final Component component) {
        IsMaskable ret = new IsMaskable() {
            @Override
            public void mask(String loadingMask) {
                component.mask(loadingMask);
            }

            @Override
            public void unmask() {
                component.unmask();
            }
        };
        return ret;
    }

    @Override
    public void saveFileWithExtension(FileViewer fileViewer, String viewerContent,
                                      String fileExtension) {
        Preconditions.checkState(file != null, "File should not be null when calling this method.");

        String destination = file.getPath() + fileExtension;
        fileEditorService.uploadTextAsFile(destination,
                                           viewerContent,
                                           true,
                                           new FileSaveCallback(userSessionService,
                                                                destination,
                                                                true,
                                                                fileViewer,
                                                                fileViewer));
    }

    @Override
    public void setViewDirtyState(boolean dirty, FileViewer dirtyViewer) {
        // Return if state has not changed
        if (this.isDirty == dirty) {
            return;
        }
        this.isDirty = dirty;
        simpleContainer.fireEvent(new DirtyStateChangedEvent(dirty));

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
        simpleContainer.mask(appearance.retrieveTreeUrlsMask());
        IsMaskable maskable = new IsMaskable() {
            @Override
            public void mask(String loadingMask) {
                simpleContainer.mask(loadingMask);
            }

            @Override
            public void unmask() {
                simpleContainer.unmask();
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

        Splittable infoTypeSplittable = diskResourceUtil.createInfoTypeSplittable(infoType);
        boolean treeViewer = diskResourceUtil.isTreeTab(infoTypeSplittable);
        boolean cogeViewer = diskResourceUtil.isGenomeVizTab(infoTypeSplittable);
        boolean ensembleViewer = diskResourceUtil.isEnsemblVizTab(infoTypeSplittable);

        if (treeViewer || cogeViewer || ensembleViewer) {
            FileViewer vizViewer = new ExternalVisualizationURLViewerImpl(file, infoType, fileEditorService, diskResourceServiceFacade);
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
            String fileName = file != null ? file.getName() : null;
            tabPanel.add(view.asWidget(), view.getViewName(fileName));

        }

        if (viewers.size() == 0) {
            tabPanel.add(new HTML(appearance.fileOpenMsg()), "");
        }
        simpleContainer.unmask();
    }

    void setContentType(MimeType contentType) {
        this.contentType = contentType;
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
