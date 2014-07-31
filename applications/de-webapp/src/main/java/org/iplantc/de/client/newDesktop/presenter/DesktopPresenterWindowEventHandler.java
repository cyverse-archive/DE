package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.analysis.client.events.OpenAppForRelaunchEvent;
import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewWorkflowEvent;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.client.events.WindowCloseRequestEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.viewer.callbacks.EnsemblUtil;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.AppWizardConfig;
import org.iplantc.de.commons.client.views.window.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.DiskResourceWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.IDropLiteWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.PipelineEditorWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.SimpleDownloadWindowConfig;
import org.iplantc.de.diskResource.client.events.*;
import org.iplantc.de.diskResource.client.views.dialogs.FileUploadByUrlDialog;
import org.iplantc.de.diskResource.client.views.dialogs.SimpleFileUploadDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

import java.util.List;
import java.util.Map;

/**
 * Event handling for all DE managed windows. Event handlers are not registered until after the
 * presenter is set.
 *
 * @author jstroot
 */
public class DesktopPresenterWindowEventHandler implements EditAppEvent.EditAppEventHandler,
                                                           CreateNewAppEvent.CreateNewAppEventHandler,
                                                           CreateNewWorkflowEvent.CreateNewWorkflowEventHandler,
                                                           EditWorkflowEvent.EditWorkflowEventHandler,
                                                           ShowFilePreviewEvent.ShowFilePreviewEventHandler,
                                                           CreateNewFileEvent.CreateNewFileEventHandler,
                                                           ShowAboutWindowEvent.ShowAboutWindowEventHandler,
                                                           WindowShowRequestEvent.WindowShowRequestEventHandler,
                                                           RunAppEvent.RunAppEventHandler,
                                                           ShowSystemMessagesEvent.Handler,
                                                           WindowCloseRequestEvent.WindowCloseRequestEventHandler,
                                                           OpenFolderEvent.OpenFolderEventHandler,
                                                           OpenAppForRelaunchEvent.OpenAppForRelaunchEventHandler,
                                                           RequestSendToCoGeEvent.RequestSendToCoGeEventHandler,
                                                           RequestSendToEnsemblEvent.RequestSendToEnsemblEventHandler,
                                                           RequestSendToTreeViewerEvent.RequestSendToTreeViewerEventHandler,
                                                           RequestBulkDownloadEvent.RequestBulkDownloadEventHandler,
                                                           RequestBulkUploadEvent.RequestBulkUploadEventHandler,
                                                           RequestImportFromUrlEvent.RequestImportFromUrlEventHandler,
                                                           RequestSimpleDownloadEvent.RequestSimpleDownloadEventHandler,
                                                           RequestSimpleUploadEvent.RequestSimpleUploadEventHandler {

    @Inject
    DEClientConstants clientConstants;
    @Inject
    Provider<DiskResourceServiceFacade> diskResourceServiceProvider;
    @Inject
    IplantDisplayStrings displayStrings;
    @Inject
    EventBus eventBus;
    @Inject
    UserInfo userInfo;
    private DesktopWindowManager desktopWindowManager;
    private NewDesktopPresenterImpl presenter;

    @Inject
    public DesktopPresenterWindowEventHandler() {
    }

    @Override
    public void createNewApp(CreateNewAppEvent event) {
        presenter.show(ConfigFactory.appsIntegrationWindowConfig(App.NEW_APP_ID), true);
    }

    @Override
    public void createNewWorkflow() {
        presenter.show(ConfigFactory.workflowIntegrationWindowConfig());
    }

    @Override
    public void onCreateNewFile(CreateNewFileEvent event) {
        if (event.geFileViewerWindowConfig() != null) {
            presenter.show(event.geFileViewerWindowConfig());
        } else {
            FileViewerWindowConfig fileViewerWindowConfig = ConfigFactory.fileViewerWindowConfig(null);
            fileViewerWindowConfig.setEditing(true);
            fileViewerWindowConfig.setParentFolder(event.getParentFolder());
            presenter.show(fileViewerWindowConfig);
        }
    }

    @Override
    public void onEditApp(EditAppEvent event) {
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig(event.getAppToEdit().getId());
        config.setOnlyLabelEditMode(event.isUserIntegratorAndAppPublic());
        presenter.show(config, true);
    }

    @Override
    public void onEditWorkflow(EditWorkflowEvent event) {
        PipelineEditorWindowConfig config = ConfigFactory.workflowIntegrationWindowConfig();
        Splittable serviceWorkflowJson = event.getServiceWorkflowJson();
        config.setServiceWorkflowJson(serviceWorkflowJson);
        presenter.show(config);
    }

    @Override
    public void onRequestBulkDownload(RequestBulkDownloadEvent event) {

        List<DiskResource> resources = Lists.newArrayList(event.getRequestedResources());
        if (isDownloadable(resources)) {
            // Build window config
            IDropLiteWindowConfig idlwc = ConfigFactory.iDropLiteDownloadWindowConfig();
            idlwc.setDisplayMode(IDropLiteUtil.DISPLAY_MODE_DOWNLOAD);
            idlwc.setResourcesToDownload(resources);
            idlwc.setTypeMap(buildTypeMap(resources));
            idlwc.setCurrentFolder(event.getCurrentFolder());
            idlwc.setSelectAll(event.isSelectAll());
            presenter.show(idlwc);
        } else {
            showErrorMsg();
        }
    }

    @Override
    public void onRequestBulkUpload(RequestBulkUploadEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        if (canUpload(uploadDest)) {
            // Build window config
            IDropLiteWindowConfig idlwc = ConfigFactory.iDropLiteUploadWindowConfig();
            idlwc.setDisplayMode(IDropLiteUtil.DISPLAY_MODE_UPLOAD);
            idlwc.setUploadFolderDest(uploadDest);
            idlwc.setCurrentFolder(uploadDest);
            presenter.show(idlwc);
        }
    }

    @Override
    public void onRequestOpenAppForRelaunch(OpenAppForRelaunchEvent event) {
        final Analysis analysisForRelaunch = event.getAnalysisForRelaunch();
        AppWizardConfig config = ConfigFactory.appWizardConfig(analysisForRelaunch.getAppId());
        config.setAnalysisId(analysisForRelaunch);
        config.setRelaunchAnalysis(true);

        presenter.show(config);
    }

    @Override
    public void onRequestOpenFolder(OpenFolderEvent event) {
        final HasPath path = CommonModelUtils.createHasPathFromString(event.getFolderPath());
        DiskResourceWindowConfig config = ConfigFactory.diskResourceWindowConfig(event.newViewRequested());
        config.setSelectedFolder(path);

        presenter.show(config, !event.newViewRequested());
    }

    @Override
    public void onRequestSendToCoGe(RequestSendToCoGeEvent event) {
        checkNotNull(event.getFile());
        showFile(event.getFile());
        presenter.doViewGenomes(event.getFile());
    }

    @Override
    public void onRequestSendToEnsembl(RequestSendToEnsemblEvent event) {
        checkNotNull(event.getFile());
        showFile(event.getFile());
        new EnsemblUtil(event.getFile(), event.getInfoType().toString(), null).sendToEnsembl();
    }

    @Override
    public void onRequestSendToTreeViewer(RequestSendToTreeViewerEvent event) {
        checkNotNull(event.getFile());
        showFile(event.getFile());
    }

    @Override
    public void onRequestSimpleDownload(RequestSimpleDownloadEvent event) {
        List<DiskResource> resources = Lists.newArrayList(event.getRequestedResources());
        if (isDownloadable(resources)) {
            if (resources.size() == 1) {
                // Download now. No folders possible here....
                final String encodedSimpleDownloadURL =
                    diskResourceServiceProvider.get().getEncodedSimpleDownloadURL(resources.get(0).getPath());

                WindowUtil.open(encodedSimpleDownloadURL, "width=100,height=100");
            } else {
                SimpleDownloadWindowConfig sdwc = ConfigFactory.simpleDownloadWindowConfig();
                sdwc.setResourcesToDownload(filterFolders(resources));
                presenter.show(sdwc);
            }
        } else {
            showErrorMsg();
        }
    }

    @Override
    public void onRequestSimpleUpload(RequestSimpleUploadEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        if (canUpload(uploadDest)) {
            new SimpleFileUploadDialog(uploadDest,
                                       diskResourceServiceProvider.get(),
                                       eventBus,
                                       UriUtils.fromTrustedString(clientConstants.fileUploadServlet()),
                                       userInfo.getUsername()).show();
        }
    }

    @Override
    public void onRequestUploadFromUrl(RequestImportFromUrlEvent event) {
        Folder uploadDest = event.getDestinationFolder();
        if (canUpload(uploadDest)) {
            new FileUploadByUrlDialog(uploadDest,
                                      diskResourceServiceProvider.get(),
                                      userInfo.getUsername()).show();
        }
        /* FIXME REFACTOR JDS Possibly move this into where event is fired.
         *                it is merely opening a dialog
         */
    }

    @Override
    public void onRunAppActionInitiated(RunAppEvent event) {
        AppWizardConfig config = ConfigFactory.appWizardConfig(event.getAppToRun().getId());
        presenter.show(config);
    }

    @Override
    public void onWindowCloseRequest(WindowCloseRequestEvent event) {
        desktopWindowManager.closeActiveWindow();
    }

    @Override
    public void onWindowShowRequest(WindowShowRequestEvent event) {
        presenter.show(event.getWindowConfig(), event.updateWithConfig());
    }

    public void setPresenter(final NewDesktopPresenterImpl presenter,
                             final DesktopWindowManager desktopWindowManager) {
        this.presenter = presenter;
        this.desktopWindowManager = desktopWindowManager;
        init(eventBus);
    }

    @Override
    public void showAboutWindowRequested(ShowAboutWindowEvent event) {
        presenter.show(ConfigFactory.aboutWindowConfig());
    }

    @Override
    public void showFilePreview(ShowFilePreviewEvent event) {
        FileViewerWindowConfig fileViewerWindowConfig;
        if (event.getConfig() == null) {
            fileViewerWindowConfig = ConfigFactory.fileViewerWindowConfig(event.getFile());
        } else {
            fileViewerWindowConfig = event.getConfig();
        }
        fileViewerWindowConfig.setEditing(DiskResourceUtil.isWritable(event.getFile()));
        presenter.show(fileViewerWindowConfig);
    }

    @Override
    public void showSystemMessages(final ShowSystemMessagesEvent event) {
        presenter.show(ConfigFactory.systemMessagesWindowConfig(null));
    }

    Map<String, String> buildTypeMap(List<DiskResource> resources) {
        Map<String, String> map = Maps.newHashMap();
        for (DiskResource dr : resources) {
            map.put(dr.getId(), dr instanceof Folder ? TYPE.FOLDER.toString() : TYPE.FILE.toString());
        }

        return map;
    }

    boolean canUpload(Folder uploadDest) {
        if (uploadDest != null && DiskResourceUtil.canUploadTo(uploadDest)) {
            return true;
        } else {
            showErrorMsg();
            return false;
        }
    }

    // remove folders from list to be displayed for simple download
    List<DiskResource> filterFolders(List<DiskResource> listToFilter) {
        List<DiskResource> filteredList = Lists.newArrayList();
        for (DiskResource dr : listToFilter) {
            if (!(dr instanceof Folder)) {
                filteredList.add(dr);
            }
        }
        return filteredList;
    }

    boolean isDownloadable(List<DiskResource> resources) {
        if ((resources == null) || resources.isEmpty()) {
            return false;
        }

        for (DiskResource dr : resources) {
            if (!DiskResourceUtil.isReadable(dr)) {
                return false;
            }
        }
        return true;
    }

    void showErrorMsg() {
        new AlertMessageBox(displayStrings.permissionErrorTitle(),
                            displayStrings.permissionErrorMessage()).show();
    }

    void showFile(final File file) {
        FileViewerWindowConfig fileViewerConfig = ConfigFactory.fileViewerWindowConfig(file);
        fileViewerConfig.setVizTabFirst(true);
        fileViewerConfig.setEditing(DiskResourceUtil.isWritable(file));
        presenter.show(fileViewerConfig);
    }

    private void init(EventBus eventBus) {
        List<HandlerRegistration> registrations = Lists.newArrayList();
        HandlerRegistration handlerRegistration = eventBus.addHandler(EditAppEvent.TYPE, this);
        registrations.add(handlerRegistration);

        handlerRegistration = eventBus.addHandler(CreateNewAppEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(CreateNewWorkflowEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(EditWorkflowEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(ShowFilePreviewEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(CreateNewFileEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(ShowAboutWindowEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(WindowShowRequestEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RunAppEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(ShowSystemMessagesEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(WindowCloseRequestEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(OpenAppForRelaunchEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(OpenFolderEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RequestSendToCoGeEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RequestSendToTreeViewerEvent.TYPE, this);
        registrations.add(handlerRegistration);


        handlerRegistration = eventBus.addHandler(RequestBulkDownloadEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RequestBulkUploadEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RequestImportFromUrlEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RequestSimpleDownloadEvent.TYPE, this);
        registrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(RequestSimpleUploadEvent.TYPE, this);
        registrations.add(handlerRegistration);
    }
}
