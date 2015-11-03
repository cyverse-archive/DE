package org.iplantc.de.desktop.client.views.windows.util;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.client.views.windows.*;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.inject.Inject;

/**
 * Defines a factory for the creation of windows.
 *
 * @author jstroot
 */
public class WindowFactory {

    @Inject DEClientConstants constants;

    @Inject AsyncProvider<AboutApplicationWindow> aboutApplicationWindowAsyncProvider;
    @Inject AsyncProvider<MyAnalysesWindow> analysesWindowAsyncProvider;
    @Inject AsyncProvider<AppEditorWindow> appEditorWindowAsyncProvider;
    @Inject AsyncProvider<AppLaunchWindow> appLaunchWindowAsyncProvider;
    @Inject AsyncProvider<DEAppsWindow> appsWindowAsyncProvider;
    @Inject AsyncProvider<DeDiskResourceWindow> diskResourceWindowAsyncProvider;
    @Inject AsyncProvider<FileViewerWindow> fileViewerWindowAsyncProvider;
    @Inject AsyncProvider<NotificationWindow> notificationWindowAsyncProvider;
    @Inject AsyncProvider<SimpleDownloadWindow> simpleDownloadWindowAsyncProvider;
    @Inject AsyncProvider<PipelineEditorWindow> pipelineEditorWindowAsyncProvider;
    @Inject AsyncProvider<SystemMessagesWindow> systemMessagesWindowAsyncProvider;

    @Inject
    WindowFactory() { }

    /**
     * Constructs a DE window based on the given {@link WindowConfig} The "tag" for the window must be
     * constructed here.
     * 
     * @return an asynchronous provider for the appropriate window.
     */
    public <C extends WindowConfig> AsyncProvider<? extends IPlantWindowInterface> build(C config) {
        AsyncProvider<? extends IPlantWindowInterface> ret = null;
        switch (config.getWindowType()) {
            case ABOUT:
                ret = aboutApplicationWindowAsyncProvider;
                break;
            case ANALYSES:
                ret = analysesWindowAsyncProvider;
                break;
            case APP_INTEGRATION:
                ret = appEditorWindowAsyncProvider;
                break;
            case APP_WIZARD:
                ret = appLaunchWindowAsyncProvider;
                break;
            case APPS:
                ret = appsWindowAsyncProvider;
                break;
            case DATA:
                ret = diskResourceWindowAsyncProvider;
                break;
            case DATA_VIEWER:
                ret = fileViewerWindowAsyncProvider;
                break;
            case HELP:
                WindowUtil.open(constants.deHelpFile());
                break;
            case NOTIFICATIONS:
                ret = notificationWindowAsyncProvider;
                break;
            case SIMPLE_DOWNLOAD:
                ret = simpleDownloadWindowAsyncProvider;
                break;
            case WORKFLOW_INTEGRATION:
                ret = pipelineEditorWindowAsyncProvider;
                break;
            case SYSTEM_MESSAGES:
                ret = systemMessagesWindowAsyncProvider;
            default:
                break;
        }
        return ret;
    }
}
