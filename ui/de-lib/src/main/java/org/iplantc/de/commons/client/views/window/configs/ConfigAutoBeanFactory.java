package org.iplantc.de.commons.client.views.window.configs;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author jstroot
 */
public interface ConfigAutoBeanFactory extends AutoBeanFactory {
    
    AutoBean<AboutWindowConfig> aboutWindowConfig();

    AutoBean<AnalysisWindowConfig> analysisWindowConfig();
    
    AutoBean<AppsIntegrationWindowConfig> appsIntegrationWindowConfig();

    AutoBean<AppsWindowConfig> appsWindowConfig();

    AutoBean<AppWizardConfig> appWizardConfig();

    AutoBean<DiskResourceWindowConfig> diskResourceWindowConfig();

    AutoBean<FileViewerWindowConfig> fileViewerWindowConfig();

    AutoBean<NotifyWindowConfig> notifyWindowConfig();

    AutoBean<SimpleDownloadWindowConfig> simpleDownloadWindowConfig();

    AutoBean<PipelineEditorWindowConfig> pipelineEditorWindowConfig();
    
    AutoBean<SystemMessagesWindowConfig> systemMessagesWindowConfig();
    
    AutoBean<TabularFileViewerWindowConfig> newTabularFileViewerWindowConfig();

    AutoBean<PathListWindowConfig> pathListWindowConfig();
    
}
