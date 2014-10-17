package org.iplantc.de.client.viewer.presenter;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorGetManifest;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.callbacks.TreeUrlCallback;
import org.iplantc.de.client.viewer.commands.ViewCommand;
import org.iplantc.de.client.viewer.events.DirtyStateChangedEvent;
import org.iplantc.de.client.viewer.factory.MimeTypeViewerResolverFactory;
import org.iplantc.de.client.viewer.views.EditingSupport;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 * 
 */
public class FileViewerPresenterImpl implements FileViewer.Presenter, FileSavedEvent.FileSavedEventHandler {
    private class GetManifestCallback implements AsyncCallback<String> {
        private final FileViewerPresenterImpl presenter;
        private final File file;
        private final Folder parentFolder;
        private final MimeType mimeType;
        private final boolean editing;
        private final boolean isVizTabFirst;
        private final AsyncCallback<String> asyncCallback;
        private final IplantErrorStrings errorStrings;

        public GetManifestCallback(FileViewerPresenterImpl presenter,
                                   File file,
                                   Folder parentFolder,
                                   MimeType mimeType,
                                   boolean editing,
                                   boolean isVizTabFirst,
                                   AsyncCallback<String> asyncCallback,
                                   IplantErrorStrings errorStrings) {

            this.presenter = presenter;
            this.file = file;
            this.parentFolder = parentFolder;
            this.mimeType = mimeType;
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
            presenter.setTitle(file.getName());
            presenter.composeView(file, parentFolder, manifest, mimeType, infoType, editing, isVizTabFirst);
        }
    }

    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final FileEditorServiceFacade fileEditorService;

    // A presenter can handle more than one view of the same data at a time
    private List<FileViewer> viewers;

    private PlainTabPanel tabPanel;

    /**
     * The file shown in the window.
     */
    private File file;

    private boolean isDirty;

    private String title;

    Logger LOG = Logger.getLogger(FileViewerPresenterImpl.class.getName());
    private boolean vizTabFirst;

    @Inject
    public FileViewerPresenterImpl(final FileEditorServiceFacade fileEditorService,
                                   final IplantDisplayStrings displayStrings,
                                   final IplantErrorStrings errorStrings) {
        this.fileEditorService = fileEditorService;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
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

    @Override
    public void go(HasOneWidget container,
                   File file,
                   Folder parentFolder,
                   MimeType mimeType,
                   boolean editing,
                   boolean isVizTabFirst,
                   AsyncCallback<String> asyncCallback){
        Preconditions.checkState(!tabPanel.isAttached(), "You cannot 'go' this presenter more than once.");
        Preconditions.checkArgument(file != null, "File cannot be null.\n" +
                                                      "To create new files, user 'newFileGo(..)'.");
        container.setWidget(tabPanel);
        tabPanel.mask(displayStrings.loadingMask());
        this.file = file;
        this.vizTabFirst = isVizTabFirst;

        fileEditorService.getManifest(file, new GetManifestCallback(this,
                                                                    file,
                                                                    parentFolder,
                                                                    mimeType,
                                                                    editing,
                                                                    isVizTabFirst,
                                                                    asyncCallback,
                                                                    errorStrings));
    }

    @Override
    public void newFileGo(HasOneWidget container,
                          String title,
                          MimeType contentType,
                          Folder parentFolder,
                          boolean editing,
                          boolean vizTabFirst,
                          boolean isTabularFile,
                          String delimiter) {
        Preconditions.checkState(!tabPanel.isAttached(), "You cannot 'go' this presenter more than once.");
        container.setWidget(tabPanel);
        tabPanel.mask(displayStrings.loadingMask());
        this.vizTabFirst = vizTabFirst;

        // Assemble manifest
        JSONObject manifest = new JSONObject();
        if(contentType != null){
            manifest.put("content-type", new JSONString(contentType.toString()));
        }

        if(isTabularFile){
            Preconditions.checkArgument(!Strings.isNullOrEmpty(delimiter), "Must specify a delimiter.");
            Preconditions.checkArgument(delimiter.matches("(,|\\\\t)"), "Unrecognized delimiter \"" + delimiter + "\"");
            JSONString infoType = null;
            if(",".equals(delimiter)){
                infoType = new JSONString("csv");
            } else if("\t".equals(delimiter)){
                infoType = new JSONString("tsv");
            }
            manifest.put("info-type", infoType);
        }
        setTitle(title);
        composeView(null, parentFolder, manifest, contentType, null, editing, vizTabFirst);
    }

    @Override
    public void saveFile() {
        for(FileViewer fileViewer : viewers){
            if(fileViewer instanceof EditingSupport){
                ((EditingSupport)fileViewer).save();
            }
        }
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(FileSavedEvent.FileSavedEventHandler handler) {
        Preconditions.checkState((viewers != null) && !viewers.isEmpty(), "No viewers found. There should be at least one viewer in the presenter.");
        // All file viewers created per presenter are for the same file. Only one of the tabs should
        for(FileViewer fileViewer : viewers){
            HandlerRegistration hr = fileViewer.addFileSavedEventHandler(handler);
            if(hr != null){
                return hr;
            }
        }
        LOG.info("No file viewers registered the FileSavedEventHandler");
        return null;
    }

    @Override
    public void onFileSaved(FileSavedEvent event) {
        if(file == null) {
            file = event.getFile();
            tabPanel.clear();
            setTitle(file.getName());
            // FIXME: JDS Need to recompose view
            // FIXME: Need to get manifest, mimeType, and infotype
            composeView(file, null, null, null, file.getInfoType(), true, vizTabFirst);
        }
        setViewDirtyState(false);
    }

    void composeView(File file,
                     Folder parentFolder,
                     JSONObject manifest,
                     MimeType mimeType,
                     String infoType,
                     boolean editing,
                     boolean isVizTabFirst) {
        ViewCommand cmd = MimeTypeViewerResolverFactory.getViewerCommand(mimeType);
        List<? extends FileViewer> viewers_list = cmd.execute(file, infoType, editing, parentFolder, manifest);

        if (viewers_list != null && viewers_list.size() > 0) {
            viewers.addAll(viewers_list);
            for (FileViewer view : viewers) {
                view.setPresenter(this);
                tabPanel.add(view.asWidget(), view.getViewName());
            }
            tabPanel.unmask();
        }

        boolean treeViewer = DiskResourceUtil.isTreeTab(DiskResourceUtil.createInfoTypeSplittable(infoType));
        boolean cogeViewer = DiskResourceUtil.isGenomeVizTab(DiskResourceUtil.createInfoTypeSplittable(infoType));
        boolean ensembleViewer = DiskResourceUtil.isEnsemblVizTab(DiskResourceUtil.createInfoTypeSplittable(infoType));

        if (treeViewer || cogeViewer || ensembleViewer) {
            cmd = MimeTypeViewerResolverFactory.getViewerCommand(MimeType.fromTypeString("viz"));
            List<? extends FileViewer> vizViewers = cmd.execute(file, infoType, editing, parentFolder, null);
            List<VizUrl> urls = getManifestVizUrls(manifest);
            if (urls != null && urls.size() > 0) {
                vizViewers.get(0).setData(urls);
            } else if (treeViewer) {
                callTreeCreateService(vizViewers.get(0), file);
            }
            viewers.add(vizViewers.get(0));
            if (isVizTabFirst) {
                Widget asWidget = vizViewers.get(0).asWidget();
                tabPanel.insert(asWidget, 0, new TabItemConfig(vizViewers.get(0).getViewName()));
                tabPanel.setActiveWidget(asWidget);
            } else {
                tabPanel.add(vizViewers.get(0).asWidget(), vizViewers.get(0).getViewName());
            }
        }

        if (viewers.size() == 0) {
            tabPanel.add(new HTML(displayStrings.fileOpenMsg()));
        } else {
            // Add ourselves as FileSaved handlers
            addFileSavedEventHandler(this);
        }
        tabPanel.unmask();
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

    @Override
    public void setViewDirtyState(boolean dirty) {
        this.isDirty = dirty;
        tabPanel.fireEvent(new DirtyStateChangedEvent(dirty));
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setTitle(String windowTitle) {
        this.title = windowTitle;
    }

}
