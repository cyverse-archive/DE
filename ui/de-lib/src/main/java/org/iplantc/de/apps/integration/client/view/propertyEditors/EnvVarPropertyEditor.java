package org.iplantc.de.apps.integration.client.view.propertyEditors;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds;
import org.iplantc.de.apps.integration.client.view.propertyEditors.util.EnvironmentVariableNameValidator;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToStringConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.EnvironmentVariableLabels;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author jstroot
 */
public class EnvVarPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, EnvVarPropertyEditor> { }

    interface EnvVarPropertyEditorUiBinder extends UiBinder<Widget, EnvVarPropertyEditor> { }

    @UiField(provided = true) AppsWidgetsPropertyPanelLabels appLabels;
    @UiField FieldLabel argLabelLabel;
    @UiField(provided = true) ArgumentEditorConverter<String> defaultValueEditor;
    @UiField FieldLabel defaultValueLabel;
    @UiField @Path("visible") CheckBoxAdapter doNotDisplay;
    @UiField(provided = true) EnvironmentVariableLabels envVarLabels;
    @UiField TextField label, name;
    @UiField FieldLabel nameLabel;
    @UiField CheckBoxAdapter requiredEditor;
    @UiField @Path("description") TextField toolTipEditor;
    @UiField FieldLabel toolTipLabel;

    private static EnvVarPropertyEditorUiBinder uiBinder = GWT.create(EnvVarPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public EnvVarPropertyEditor(final AppTemplateWizardAppearance appearance,
                                final AppsWidgetsPropertyPanelLabels appLabels,
                                final AppsWidgetsContextualHelpMessages help,
                                final IplantValidationConstants validationConstants) {
        super(appearance);
        this.appLabels = appLabels;
        this.envVarLabels = appLabels;

        TextField textField = new TextField();
        textField.setEmptyText(envVarLabels.envVarWidgetEmptyEditText());
        textField.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));
        defaultValueEditor = new ArgumentEditorConverter<>(textField, new SplittableToStringConverter());

        initWidget(uiBinder.createAndBindUi(this));

        name.addValidator(new EnvironmentVariableNameValidator());

        defaultValueLabel.setHTML(appearance.createContextualHelpLabel(envVarLabels.envVarDefaultLabel(), help.envVarDefaultValue()));
        toolTipLabel.setHTML(appearance.createContextualHelpLabel(appLabels.toolTipText(), help.toolTip()));
        nameLabel.setHTML(appearance.createContextualHelpLabel(appLabels.argumentOption(), help.argumentOption()));
        doNotDisplay.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.doNotDisplay()).toSafeHtml());

        nameLabel.setHTML(appearance.createContextualHelpLabel(appLabels.envVarNameLabel(), help.envVarDefaultName()));
        requiredEditor.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.isRequired()).toSafeHtml());
        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
        ensureDebugId(Ids.PROPERTY_EDITOR + Ids.ENV_VARIABLE);
    }

    @Override
    public void edit(Argument argument) {
        super.edit(argument);
        if (Strings.isNullOrEmpty(argument.getName())) {
            argument.setName(AppTemplateUtils.NEW_ENV_VAR_NAME);
        }
        editorDriver.edit(argument);

    }

    @Override
    public com.google.gwt.editor.client.EditorDriver<Argument> getEditorDriver() {
        return editorDriver;
    }

    @Override
    @Ignore
    protected LeafValueEditor<Splittable> getDefaultValueEditor() {
        return defaultValueEditor;
    }

    @Override
    protected void initLabelOnlyEditMode(boolean isLabelOnlyEditMode) {
        defaultValueEditor.setEnabled(!isLabelOnlyEditMode);
        doNotDisplay.setEnabled(!isLabelOnlyEditMode);
        name.setEnabled(!isLabelOnlyEditMode);
        requiredEditor.setEnabled(!isLabelOnlyEditMode);

        if (isLabelOnlyEditMode) {
            defaultValueEditor.getValidators().clear();
            doNotDisplay.getValidators().clear();
            name.getValidators().clear();
            requiredEditor.getValidators().clear();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + PropertyPanelIds.LABEL);
        doNotDisplay.getCheckBox().ensureDebugId(baseID + PropertyPanelIds.DO_NOT_DISPLAY);
        name.ensureDebugId(baseID + PropertyPanelIds.NAME);
        defaultValueEditor.ensureDebugId(baseID + PropertyPanelIds.DEFAULT_VALUE);
        requiredEditor.ensureDebugId(baseID + PropertyPanelIds.REQUIRED);
        toolTipEditor.ensureDebugId(baseID + PropertyPanelIds.TOOL_TIP);
    }

    @UiHandler("defaultValueEditor")
    void onDefaultValueChange(ValueChangeEvent<Splittable> event) {
        // Forward defaultValue onto value.
        model.setValue(event.getValue());
    }

}
