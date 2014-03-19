package org.iplantc.de.apps.widgets.client.view.editors;

import org.iplantc.de.apps.widgets.client.view.LaunchAnalysisView;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.validation.AnalysisOutputValidator;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.apps.integration.JobExecution;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.widgets.PreventEntryAfterLimitHandler;
import org.iplantc.de.diskResource.client.views.widgets.FolderSelectorField;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsDisplayMessages;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ConverterEditorAdapter;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public class LaunchAnalysisViewImpl implements LaunchAnalysisView {

    interface EditorDriver extends SimpleBeanEditorDriver<JobExecution, LaunchAnalysisViewImpl> {
    }

    @UiTemplate("LaunchAnalysisView.ui.xml")
    interface LaunchAnalysisWidgetUiBinder extends UiBinder<Widget, LaunchAnalysisViewImpl> {
    }

    @UiField(provided = true)
    AppsWidgetsDisplayMessages appWidgetStrings;

    @Ignore
    @UiField
    FolderSelectorField awFolderSel;

    @Ignore
    @UiField
    ContentPanel contentPanel;

    @UiField
    TextArea description;

    @UiField
    TextField name;

    @Path("outputDirectory")
    ConverterEditorAdapter<String, Folder, FolderSelectorField> outputDirectory;

    @UiField
    CheckBox retainInputs;

    @Inject
    private AppTemplateWizardAppearance appearance;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    private UserSettings userSettings;

    @Inject
    public LaunchAnalysisViewImpl(LaunchAnalysisWidgetUiBinder binder,
            AppsWidgetsDisplayMessages appWidgetStrings, final UserSettings userSettings) {
        this.appWidgetStrings = appWidgetStrings;
        this.userSettings = userSettings;
        binder.createAndBindUi(this);
        name.addValidator(new DiskResourceNameValidator());
        name.addKeyDownHandler(new PreventEntryAfterLimitHandler(name));
        name.addValidator(new MaxLengthValidator(PreventEntryAfterLimitHandler.DEFAULT_LIMIT));
        name.setAllowBlank(false);
        outputDirectory = new ConverterEditorAdapter<String, Folder, FolderSelectorField>(awFolderSel,
                new Converter<String, Folder>() {
                    @Override
                    public String convertFieldValue(Folder object) {
                        if (object == null) {
                            return null;
                        }
                        return object.getPath();
                    }

                    @Override
                    public Folder convertModelValue(String object) {
                        if (!Strings.isNullOrEmpty(object)) {
                            DiskResourceAutoBeanFactory factory = GWT
                                    .create(DiskResourceAutoBeanFactory.class);
                            AutoBean<Folder> FolderBean = factory.folder();
                            Folder folder = FolderBean.as();
                            folder.setPath(object);
                            return folder;
                        } else {
                            return null;
                        }
                    }
                });
        awFolderSel.setValidatePermissions(true);
        awFolderSel.addValidator(new AnalysisOutputValidator());
        this.editorDriver.initialize(this);
    }

    @Override
    public Widget asWidget() {
        return contentPanel;
    }

    @Override
    public void edit(JobExecution je) {
        editorDriver.edit(je);
        // Update header on initial binding.
        updateHeader(je.getName());
    }

    @Override
    public JobExecution flushJobExecution() {
        JobExecution flush = editorDriver.flush();
        updateHeader(flush.getName());
        return flush;
    }

    @Override
    public List<EditorError> getErrors() {
        List<EditorError> errors = Lists.newArrayList(editorDriver.getErrors());
        errors.addAll(awFolderSel.getErrors());
        return errors;
    }

    @Override
    public boolean hasErrors() {
        return ((editorDriver.getErrors() != null) && editorDriver.hasErrors())
                || !awFolderSel.getErrors().isEmpty();
    }

    @UiHandler("awFolderSel")
    void onFolderChanged(ValueChangeEvent<Folder> event) {
        if ((event.getValue() != null)
                && !event.getValue().getPath().equals(userSettings.getDefaultOutputFolder().getPath())) {
            awFolderSel.setInfoErrorText(appWidgetStrings.nonDefaultFolderWarning());
        } else {
            awFolderSel.setInfoErrorText(null);
        }

        flushJobExecution();
    }

    /**
     * @param event
     */
    @UiHandler("awFolderSel")
    void onInvalid(InvalidEvent event) {
        updateHeader(name.getCurrentValue());
    }

    /**
     * @param event
     */
    @UiHandler("name")
    void onNameChange(ValueChangeEvent<String> event) {
        updateHeader(name.getCurrentValue());
    }

    private void updateHeader(String appName) {
        SafeHtmlBuilder headerLabel = new SafeHtmlBuilder();
        if (hasErrors()) {
            headerLabel.appendHtmlConstant(appearance.getErrorIconImg().getString());
        }
        headerLabel.appendEscaped(appWidgetStrings.launchAnalysisDetailsHeadingPrefix() + appName);
        contentPanel.setHeadingHtml(headerLabel.toSafeHtml());
    }
}
