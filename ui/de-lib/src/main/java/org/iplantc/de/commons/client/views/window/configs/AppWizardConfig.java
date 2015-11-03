package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.Splittable;

public interface AppWizardConfig extends WindowConfig {
    
    Splittable getAppTemplate();

    String getAppId();

    void setAppId(String appId);

    void setAppTemplate(Splittable appTemplate);

    boolean isRelaunchAnalysis();

    void setRelaunchAnalysis(boolean relaunchAnalysis);

    HasId getAnalysisId();

    void setAnalysisId(HasId analysisId);

}

