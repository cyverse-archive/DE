package org.iplantc.de.client.windows.util;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.windows.*;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.*;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;

/**
 * Defines a factory for the creation of windows.
 * 
 */
public class WindowFactory {

    private final DEClientConstants constants;
    private EventBus eventBus;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private FileEditorServiceFacade fileEditorService;
    private Scheduler scheduler;

    @Inject
    public WindowFactory(final DEClientConstants constants,
                         final EventBus eventBus,
                         final IplantDisplayStrings displayStrings,
                         final IplantErrorStrings errorStrings,
                         final FileEditorServiceFacade fileEditorService,
                         final Scheduler scheduler) {
        this.constants = constants;
        this.eventBus = eventBus;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.fileEditorService = fileEditorService;
        this.scheduler = scheduler;
    }

    /**
     * Constructs a DE window based on the given {@link WindowConfig} The "tag" for the window must be
     * constructed here.
     * 
     * @param config
     * @return
     */
    public <C extends WindowConfig> IPlantWindowInterface build(C config) {
        IPlantWindowInterface ret = null;
        switch (config.getWindowType()) {
            case ABOUT:
                ret = new AboutApplicationWindow((AboutWindowConfig)config);
                break;
            case ANALYSES:
                ret = new MyAnalysesWindow((AnalysisWindowConfig)config);
                break;
            case APP_INTEGRATION:
                ret = new AppEditorWindow((AppsIntegrationWindowConfig)config, eventBus);
                break;
            case APP_WIZARD:
                ret = new AppLaunchWindow((AppWizardConfig)config);
                break;
            case APPS:
                ret = new DEAppsWindow((AppsWindowConfig)config);
                break;
            case DATA:
                ret = new DeDiskResourceWindow((DiskResourceWindowConfig)config);
                break;
            case DATA_VIEWER:
                ret = new FileViewerWindow((FileViewerWindowConfig)config, eventBus, displayStrings, errorStrings, fileEditorService, scheduler);
                break;
            case HELP:
                WindowUtil.open(constants.deHelpFile());
                break;
            case IDROP_LITE_DOWNLOAD:
            case IDROP_LITE_UPLOAD:
                ret = new IDropLiteAppletWindow((IDropLiteWindowConfig)config);
                break;
            case NOTIFICATIONS:
                ret = new NotificationWindow((NotifyWindowConfig)config);
                break;
            case SIMPLE_DOWNLOAD:
                ret = new SimpleDownloadWindow((SimpleDownloadWindowConfig)config);
                break;
            case WORKFLOW_INTEGRATION:
                ret = new PipelineEditorWindow(config);
                break;
            case SYSTEM_MESSAGES:
                ret = new SystemMessagesWindow((SystemMessagesWindowConfig)config);
            default:
                break;
        }
        return ret;
    }
}
