package org.iplantc.de.client.views.windows.configs;


import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface ConfigAutoBeanFactory extends AutoBeanFactory {
    
    AutoBean<AboutWindowConfig> aboutWindowConfig();

    AutoBean<AnalysisWindowConfig> analysisWindowConfig();
    
    AutoBean<AppsIntegrationWindowConfig> appsIntegrationWindowConfig();

    AutoBean<AppsWindowConfig> appsWindowConfig();

    AutoBean<AppWizardConfig> appWizardConfig();

    AutoBean<DiskResourceWindowConfig> diskResourceWindowConfig();

    AutoBean<FileViewerWindowConfig> fileViewerWindowConfig();

    AutoBean<IDropLiteWindowConfig> iDropLiteWindowConfig();

    AutoBean<NotifyWindowConfig> notifyWindowConfig();

    AutoBean<SimpleDownloadWindowConfig> simpleDownloadWindowConfig();

    AutoBean<PipelineEditorWindowConfig> pipelineEditorWindowConfig();
    
    AutoBean<SystemMessagesWindowConfig> systemMessagesWindowConfig();
    
}
