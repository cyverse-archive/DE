package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.analysis.client.events.OpenAppForRelaunchEvent;
import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewWorkflowEvent;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.client.events.WindowCloseRequestEvent;
import org.iplantc.de.client.events.WindowLayoutRequestEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.window.configs.AppWizardConfig;
import org.iplantc.de.commons.client.views.window.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.DiskResourceWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.PipelineEditorWindowConfig;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent;
import org.iplantc.de.diskResource.client.events.RequestBulkDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestBulkUploadEvent;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToCoGeEvent;
import org.iplantc.de.diskResource.client.events.RequestSendToTreeViewerEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

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
                                                           RequestSendToTreeViewerEvent.RequestSendToTreeViewerEventHandler,
                                                           RequestBulkDownloadEvent.RequestBulkDownloadEventHandler,
                                                           RequestBulkUploadEvent.RequestBulkUploadEventHandler,
                                                           RequestImportFromUrlEvent.RequestImportFromUrlEventHandler,
                                                           RequestSimpleDownloadEvent.RequestSimpleDownloadEventHandler,
                                                           RequestSimpleUploadEvent.RequestSimpleUploadEventHandler,
                                                           WindowLayoutRequestEvent.WindowLayoutRequestEventHandler {

    @Inject
    Provider<DiskResourceServiceFacade> diskresourceServiceProvider;
    @Inject
    EventBus eventBus;
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

    }

    @Override
    public void onRequestBulkUpload(RequestBulkUploadEvent event) {

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

    }

    @Override
    public void onRequestSendToTreeViewer(RequestSendToTreeViewerEvent event) {

    }

    @Override
    public void onRequestSimpleDownload(RequestSimpleDownloadEvent event) {

    }

    @Override
    public void onRequestSimpleUpload(RequestSimpleUploadEvent event) {
        // TODO JDS Possibly move this into where event is fired.
        // it is merely opening a dialog
    }

    @Override
    public void onRequestUploadFromUrl(RequestImportFromUrlEvent event) {
        // TODO JDS Possibly move this into where event is fired.
        // it is merely opening a dialog
    }

    @Override
    public void onRunAppActionInitiated(RunAppEvent event) {
        AppWizardConfig config = ConfigFactory.appWizardConfig(event.getAppToRun().getId());
        presenter.show(config);
    }

    @Override
    public void onWindowCloseRequest(WindowCloseRequestEvent event) {

    }

    @Override
    public void onWindowLayoutRequest(WindowLayoutRequestEvent event) {

    }

    @Override
    public void onWindowShowRequest(WindowShowRequestEvent event) {
        presenter.show(event.getWindowConfig(), event.updateWithConfig());
    }

    public void setPresenter(NewDesktopPresenterImpl presenter) {
        this.presenter = presenter;
        init(eventBus);
    }

    @Override
    public void showAboutWindowRequested(ShowAboutWindowEvent event) {
        presenter.show(ConfigFactory.aboutWindowConfig());
    }

    @Override
    public void showFilePreview(ShowFilePreviewEvent event) {
        FileViewerWindowConfig fileViewerWindowConfig = null;
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

        handlerRegistration = eventBus.addHandler(WindowLayoutRequestEvent.TYPE, this);
        registrations.add(handlerRegistration);
    }
}
