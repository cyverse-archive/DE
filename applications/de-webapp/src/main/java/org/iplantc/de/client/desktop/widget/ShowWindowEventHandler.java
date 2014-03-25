package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewAppEvent.CreateNewAppEventHandler;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditAppEvent.EditAppEventHandler;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent.EditWorkflowEventHandler;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.apps.client.events.RunAppEvent.RunAppEventHandler;
import org.iplantc.de.apps.client.events.handlers.CreateNewWorkflowEventHandler;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowAboutWindowEvent.ShowAboutWindowEventHandler;
import org.iplantc.de.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.events.WindowShowRequestEvent.WindowShowRequestEventHandler;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.AppsIntegrationWindowConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.FileViewerWindowConfig;
import org.iplantc.de.client.views.windows.configs.PipelineEditorWindowConfig;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent.CreateNewFileEventHandler;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;

import com.google.web.bindery.autobean.shared.Splittable;

final class ShowWindowEventHandler implements ShowAboutWindowEventHandler, ShowFilePreviewEventHandler,
        CreateNewAppEventHandler, CreateNewWorkflowEventHandler, WindowShowRequestEventHandler,
        RunAppEventHandler, EditAppEventHandler, EditWorkflowEventHandler,
        ShowSystemMessagesEvent.Handler, CreateNewFileEventHandler {

    private final Desktop desktop;

    ShowWindowEventHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void showAboutWindowRequested(ShowAboutWindowEvent event) {
        desktop.showWindow(ConfigFactory.aboutWindowConfig());
    }

    @Override
    public void showFilePreview(ShowFilePreviewEvent event) {
        FileViewerWindowConfig fileViewerWindowConfig = ConfigFactory.fileViewerWindowConfig(
                event.getFile(), false);
        fileViewerWindowConfig.setEditing(event.getFile().writable());
        desktop.showWindow(fileViewerWindowConfig);
    }

    @Override
    public void onWindowShowRequest(WindowShowRequestEvent event) {
        desktop.showWindow(event.getWindowConfig(), event.updateWithConfig());
    }

    @Override
    public void onRunAppActionInitiated(RunAppEvent event) {
        AppWizardConfig config = ConfigFactory.appWizardConfig(event.getAppToRun().getId());
        desktop.showWindow(config);
    }

    @Override
    public void createNewApp(CreateNewAppEvent event) {
        desktop.showWindow(ConfigFactory.appsIntegrationWindowConfig(App.NEW_APP_ID),
                true);
    }

    @Override
    public void onEditApp(EditAppEvent event) {
        AppsIntegrationWindowConfig config = ConfigFactory.appsIntegrationWindowConfig(event
                .getAppToEdit().getId());
        config.setOnlyLabelEditMode(event.isUserIntegratorAndAppPublic());
        desktop.showWindow(config, true);
    }

    @Override
    public void createNewWorkflow() {
        desktop.showWindow(ConfigFactory.workflowIntegrationWindowConfig());
    }

    @Override
    public void onEditWorkflow(EditWorkflowEvent event) {
        PipelineEditorWindowConfig config = ConfigFactory.workflowIntegrationWindowConfig();
        Splittable serviceWorkflowJson = event.getServiceWorkflowJson();
        config.setServiceWorkflowJson(serviceWorkflowJson);
        desktop.showWindow(config);
    }

    @Override
    public void showSystemMessages(final ShowSystemMessagesEvent event) {
        desktop.showWindow(ConfigFactory.systemMessagesWindowConfig(null));
    }

    @Override
    public void onCreateNewFile(CreateNewFileEvent event) {
        FileViewerWindowConfig fileViewerWindowConfig = ConfigFactory
                .fileViewerWindowConfig(null, false);
        fileViewerWindowConfig.setEditing(true);
        desktop.showWindow(fileViewerWindowConfig);
    }

}
