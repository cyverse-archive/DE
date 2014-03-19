package org.iplantc.de.apps.widgets.client.view;

import org.iplantc.de.apps.widgets.client.events.RequestAnalysisLaunchEvent.RequestAnalysisLaunchEventHandler;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.JobExecution;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

public class AppLaunchPreviewViewImpl extends Window implements AppLaunchPreviewView {
    
    @UiTemplate("AppLaunchView.ui.xml")
    interface AppWizardPreviewUiBinder extends UiBinder<Widget, AppLaunchPreviewViewImpl> {}
    interface EditorDriver extends SimpleBeanEditorDriver<AppTemplate, AppLaunchPreviewView> {
    }

    @Ignore
    @UiField(provided = true)
    AppTemplateForm wizard;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public AppLaunchPreviewViewImpl(final AppWizardPreviewUiBinder binder, final AppTemplateForm wizard) {
        this.wizard = wizard;
        setWidget(binder.createAndBindUi(this));
        setSize("640", "375");
        setBorders(false);
        editorDriver.initialize(this);
        this.getHideButton();
    }

    @Override
    public HandlerRegistration addRequestAnalysisLaunchEventHandler(RequestAnalysisLaunchEventHandler handler) {
        throw new UnsupportedOperationException("App Launch preview does not support launch request events.");
    }

    @Override
    public void analysisLaunchFailed() {

    }

    @Override
    public void edit(AppTemplate appTemplate, JobExecution je) {
        setHeadingText("Preview of " + appTemplate.getName());
        editorDriver.edit(appTemplate);
    }

    @Override
    public AppTemplateForm getWizard() {
        return wizard;
    }

    /**
     * @param event
     */
    @UiHandler("launchButton")
    void onLaunchButtonClicked(SelectEvent event) {
        // Flush the editor driver to perform validations.
        editorDriver.flush();
    }
}
