package org.iplantc.de.client.viewer.presenter;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.client.viewer.callbacks.TreeUrlCallback;
import org.iplantc.de.client.viewer.commands.ViewCommand;
import org.iplantc.de.client.viewer.events.DirtyStateChangedEvent;
import org.iplantc.de.client.viewer.factory.MimeTypeViewerResolverFactory;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.PlainTabPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author sriram
 * 
 */
public class FileViewerPresenterImpl implements FileViewer.Presenter, FileSavedEvent.FileSavedEventHandler {
    private final IplantDisplayStrings displayStrings;
    private final FileEditorServiceFacade fileEditorService;

    // A presenter can handle more than one view of the same data at a time
    private List<FileViewer> viewers;

    private PlainTabPanel tabPanel;

    /**
     * The file shown in the window.
     */
    private File file;

    /**
     * The manifest of file contents
     */
    private JSONObject manifest;

    private boolean editing;

    private boolean isDirty;

    private boolean isVizTabFirst;

    private String title;

    Logger LOG = Logger.getLogger(FileViewerPresenterImpl.class.getName());

    @Inject
    public FileViewerPresenterImpl(final FileEditorServiceFacade fileEditorService,
                                   final IplantDisplayStrings displayStrings) {
        this.fileEditorService = fileEditorService;
        this.displayStrings = displayStrings;
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
                   JSONObject manifest,
                   boolean editing,
                   boolean isVizTabFirst){
        this.file = file;
        this.manifest = manifest;
        this.editing = editing;
        this.isVizTabFirst = isVizTabFirst;
        HasOneWidget container1 = container;
        // TODO: JDS Need to figure out how to manage service calls on critical path.
        composeView(parentFolder);
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
        }
        setViewDirtyState(false);
    }

    void composeView(Folder parentFolder) {
//        container.mask(displayStrings.loadingMask());
        tabPanel.mask(displayStrings.loadingMask());
        String mimeType = JsonUtil.getString(manifest, "content-type");
        ViewCommand cmd = MimeTypeViewerResolverFactory.getViewerCommand(MimeType.fromTypeString(mimeType));
        String infoType = JsonUtil.getString(manifest, "info-type");
        List<? extends FileViewer> viewers_list = cmd.execute(file, infoType, editing, parentFolder, manifest);

        if (viewers_list != null && viewers_list.size() > 0) {
            viewers.addAll(viewers_list);
            for (FileViewer view : viewers) {
                view.setPresenter(this);
//                container.getWidget().add(view.asWidget(), view.getViewName());
                tabPanel.add(view.asWidget(), view.getViewName());
            }
//            container.unmask();
            tabPanel.unmask();
        }

        boolean treeViewer = DiskResourceUtil.isTreeTab(DiskResourceUtil.createInfoTypeSplittable(infoType));
        boolean cogeViewer = DiskResourceUtil.isGenomeVizTab(DiskResourceUtil.createInfoTypeSplittable(infoType));
        boolean ensembleViewer = DiskResourceUtil.isEnsemblVizTab(DiskResourceUtil.createInfoTypeSplittable(infoType));

        if (treeViewer || cogeViewer || ensembleViewer) {
            cmd = MimeTypeViewerResolverFactory.getViewerCommand(MimeType.fromTypeString("viz"));
            List<? extends FileViewer> vizViewers = cmd.execute(file, infoType, editing, parentFolder, null);
            List<VizUrl> urls = getManifestVizUrls();
            if (urls != null && urls.size() > 0) {
                vizViewers.get(0).setData(urls);
            } else if (treeViewer) {
                callTreeCreateService(vizViewers.get(0));
            }
            viewers.add(vizViewers.get(0));
            if (isVizTabFirst) {
                Widget asWidget = vizViewers.get(0).asWidget();
//                container.getWidget().insert(asWidget, 0, new TabItemConfig(vizViewers.get(0).getViewName()));
//                container.getWidget().setActiveWidget(asWidget);
                tabPanel.insert(asWidget, 0, new TabItemConfig(vizViewers.get(0).getViewName()));
                tabPanel.setActiveWidget(asWidget);
            } else {
//                container.getWidget().add(vizViewers.get(0).asWidget(), vizViewers.get(0).getViewName());
                tabPanel.add(vizViewers.get(0).asWidget(), vizViewers.get(0).getViewName());
            }
        }

        if (viewers.size() == 0) {
//            container.unmask();
//            container.add(new HTML(displayStrings.fileOpenMsg()));
            tabPanel.unmask();
            tabPanel.add(new HTML(displayStrings.fileOpenMsg()));
        } else {
            // Add ourselves as FileSaved handlers
            addFileSavedEventHandler(this);
        }

    }

    /**
     * Gets the tree-urls json array from the manifest.
     * 
     * @return A json array of at least one tree URL, or null otherwise.
     */
    private List<VizUrl> getManifestVizUrls() {
        return TreeUrlCallback.getTreeUrls(manifest.toString());

    }

    /**
     * Calls the tree URL service to fetch the URLs to display in the grid.
     */
    void callTreeCreateService(final FileViewer viewer) {
//        container.mask(displayStrings.loadingMask());
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
//                                                         container,
                                                         maskable,
                                                         viewer));
    }

    @Override
    public void setViewDirtyState(boolean dirty) {
        this.isDirty = dirty;
//        updateWindowTitle();
        tabPanel.fireEvent(new DirtyStateChangedEvent(dirty));
    }

    /*private void updateWindowTitle() {
        if (isDirty) {
            container.setHeadingText(container.getHeader().getText() + "<span style='color:red; vertical-align: super'> * </span>");
        } else {
            container.setHeadingText(title);
        }
    }*/

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setTitle(String windowTitle) {
        this.title = windowTitle;
    }

}
