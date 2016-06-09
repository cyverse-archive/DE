package org.iplantc.de.desktop.client.views.windows.util;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.client.views.windows.AboutApplicationWindow;
import org.iplantc.de.desktop.client.views.windows.AppEditorWindow;
import org.iplantc.de.desktop.client.views.windows.AppLaunchWindow;
import org.iplantc.de.desktop.client.views.windows.DEAppsWindow;
import org.iplantc.de.desktop.client.views.windows.DeDiskResourceWindow;
import org.iplantc.de.desktop.client.views.windows.FileViewerWindow;
import org.iplantc.de.desktop.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.desktop.client.views.windows.MyAnalysesWindow;
import org.iplantc.de.desktop.client.views.windows.NotificationWindow;
import org.iplantc.de.desktop.client.views.windows.PipelineEditorWindow;
import org.iplantc.de.desktop.client.views.windows.SimpleDownloadWindow;
import org.iplantc.de.desktop.client.views.windows.SystemMessagesWindow;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.inject.Inject;

/**
 * Defines a factory for the creation of windows.
 *
 * @author jstroot
 */
public class WindowFactory {

    @Inject DEClientConstants constants;

    @Inject AsyncProviderWrapper<AboutApplicationWindow> aboutApplicationWindowAsyncProvider;
    @Inject AsyncProviderWrapper<MyAnalysesWindow> analysesWindowAsyncProvider;
    @Inject AsyncProviderWrapper<AppEditorWindow> appEditorWindowAsyncProvider;
    @Inject AsyncProviderWrapper<AppLaunchWindow> appLaunchWindowAsyncProvider;
    @Inject AsyncProviderWrapper<DEAppsWindow> appsWindowAsyncProvider;
    @Inject AsyncProviderWrapper<DeDiskResourceWindow> diskResourceWindowAsyncProvider;
    @Inject AsyncProviderWrapper<FileViewerWindow> fileViewerWindowAsyncProvider;
    @Inject AsyncProviderWrapper<NotificationWindow> notificationWindowAsyncProvider;
    @Inject AsyncProviderWrapper<SimpleDownloadWindow> simpleDownloadWindowAsyncProvider;
    @Inject AsyncProviderWrapper<PipelineEditorWindow> pipelineEditorWindowAsyncProvider;
    @Inject AsyncProviderWrapper<SystemMessagesWindow> systemMessagesWindowAsyncProvider;

    @Inject
    WindowFactory() { }

    /**
     * Constructs a DE window based on the given {@link WindowConfig} The "tag" for the window must be
     * constructed here.
     * 
     * @return an asynchronous provider for the appropriate window.
     */
    public <C extends WindowConfig> AsyncProviderWrapper<? extends IPlantWindowInterface> build(C config) {
        AsyncProviderWrapper<? extends IPlantWindowInterface> ret = null;
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
