package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.JobExecution;
import org.iplantc.de.client.services.AppTemplateServices;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppTemplateServicesStub implements AppTemplateServices {
    @Override
    public void cmdLinePreview(AppTemplate at, AsyncCallback<String> callback) {

    }

    @Override
    public void getAppTemplate(HasId appId, AsyncCallback<AppTemplate> callback) {

    }

    @Override
    public AppTemplateAutoBeanFactory getAppTemplateFactory() {
        return null;
    }

    @Override
    public void getAppTemplateForEdit(HasId appId, AsyncCallback<AppTemplate> callback) {

    }

    @Override
    public void getAppTemplatePreview(AppTemplate at, AsyncCallback<AppTemplate> callback) {

    }

    @Override
    public void launchAnalysis(AppTemplate at, JobExecution je, AsyncCallback<String> callback) {

    }

    @Override
    public void rerunAnalysis(HasId analysisId, AsyncCallback<AppTemplate> callback) {

    }

    @Override
    public void saveAndPublishAppTemplate(AppTemplate at, AsyncCallback<AppTemplate> callback) {

    }

    @Override
    public void updateAppLabels(AppTemplate at, AsyncCallback<AppTemplate> callback) {

    }

    @Override
    public void createAppTemplate(AppTemplate at, AsyncCallback<AppTemplate> callback) {
        // TODO Auto-generated method stub

    }
}
