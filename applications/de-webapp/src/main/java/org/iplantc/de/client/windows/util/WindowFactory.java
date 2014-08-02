package org.iplantc.de.client.windows.util;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.windows.*;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.*;

import com.google.inject.Inject;

/**
 * Defines a factory for the creation of windows.
 * 
 */
public class WindowFactory {

    private final DEClientConstants constants;

    @Inject
    public WindowFactory(final DEClientConstants constants) {
        this.constants = constants;
    }

    /**
     * Constructs a DE window based on the given {@link WindowConfig} The "tag" for the window must be
     * constructed here.
     * 
     * @param config
     * @return
     */
    public <C extends WindowConfig> IPlantWindowInterface build(C config) {
        final EventBus eventBus = EventBus.getInstance();
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
                ret = new FileViewerWindow((FileViewerWindowConfig)config, eventBus);
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
