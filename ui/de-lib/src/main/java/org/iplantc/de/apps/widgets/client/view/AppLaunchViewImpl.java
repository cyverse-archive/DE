package org.iplantc.de.apps.widgets.client.view;

import org.iplantc.de.apps.widgets.client.events.RequestAnalysisLaunchEvent;
import org.iplantc.de.apps.widgets.client.events.RequestAnalysisLaunchEvent.RequestAnalysisLaunchEventHandler;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.JobExecution;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.commons.client.widgets.CustomMask;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.List;

/**
 * @author jstroot
 */
public class AppLaunchViewImpl extends Composite implements AppLaunchView {
    
    @UiTemplate("AppLaunchView.ui.xml")
    interface AppWizardViewUIUiBinder extends UiBinder<Widget, AppLaunchViewImpl> {}

    interface EditorDriver extends SimpleBeanEditorDriver<AppTemplate, AppLaunchViewImpl> {}

    @UiField @Ignore TextButton launchButton;
    @UiField(provided = true) @Path("") AppTemplateForm wizard;

    private final AppTemplateUtils appTemplateUtils;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    private final LaunchAnalysisView law;
    private CustomMask customMask;

    AppLaunchViewAppearance appearance;

    @Inject
    public AppLaunchViewImpl(final AppWizardViewUIUiBinder binder,
                             final LaunchAnalysisView law,
                             final AppTemplateForm wizard,
                             final AppTemplateUtils appTemplateUtils,
                             AppLaunchViewAppearance appearance,
                             CustomMask customMask) {
        this.law = law;
        this.wizard = wizard;
        this.appTemplateUtils = appTemplateUtils;
        this.appearance = appearance;
        this.customMask = customMask;
        initWidget(binder.createAndBindUi(this));
        editorDriver.initialize(this);
    }

    @Override
    public HandlerRegistration addRequestAnalysisLaunchEventHandler(RequestAnalysisLaunchEventHandler handler) {
        return addHandler(handler, RequestAnalysisLaunchEvent.TYPE);
    }

    @Override
    public void analysisLaunchFailed() {
        launchButton.setEnabled(true);
        unmask();
    }

    @Override
    public void edit(final AppTemplate appTemplate, final JobExecution je) {
        law.edit(je, appTemplate.getAppType());
        editorDriver.edit(appTemplate);
        wizard.insertFirstInAccordion(law);

        if (appTemplate.isDeleted()) {
            mask(appearance.deprecatedAppMask());
            launchButton.setEnabled(false);
        }
    }

    @UiHandler("launchButton")
    void onLaunchButtonClicked(SelectEvent event) {

        // Flush the editor driver to perform validations before calling back to presenter.
        AppTemplate cleaned = appTemplateUtils.removeEmptyGroupArguments(editorDriver.flush());
        JobExecution je = law.flushJobExecution();
        if (editorDriver.hasErrors() || law.hasErrors()) {
            GWT.log("Editor has errors");
            List<EditorError> errors = Lists.newArrayList();
            errors.addAll(editorDriver.getErrors());
            errors.addAll(law.getErrors());
            for (EditorError error : errors) {
                GWT.log("\t-- " + ": " + error.getMessage());
            }
        } else {
            // If there are no errors, fire event.
            fireEvent(new RequestAnalysisLaunchEvent(cleaned, je));
            mask();
            launchButton.setEnabled(false);
        }
    }

    @Override
    public void mask(String message) {
        mask = true;
        maskMessage = message;
        customMask.mask(this, message);
    }
}
