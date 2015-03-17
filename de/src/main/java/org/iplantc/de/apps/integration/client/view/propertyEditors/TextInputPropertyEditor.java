package org.iplantc.de.apps.integration.client.view.propertyEditors;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds;
import org.iplantc.de.apps.integration.client.view.propertyEditors.widgets.ArgumentValidatorEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToStringConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.TextInputLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.editor.client.impl.Refresher;
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

import java.util.List;

/**
 * @author jstroot
 */
public class TextInputPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, TextInputPropertyEditor> {
    }

    interface TextInputPropertyEditorUiBinder extends UiBinder<Widget, TextInputPropertyEditor> {
    }

    @UiField(provided = true) AppsWidgetsPropertyPanelLabels appLabels;
    @UiField @Path("name") TextField argumentOptionEditor;
    @UiField(provided = true) ArgumentEditorConverter<String> defaultValueEditor;
    @UiField FieldLabel defaultValueLabel, argumentOptionLabel, toolTipLabel;
    @UiField @Path("visible") CheckBoxAdapter doNotDisplay;
    @UiField TextField label;
    @UiField CheckBoxAdapter omitIfBlank, requiredEditor;
    @UiField(provided = true) TextInputLabels textInputLabels;
    @UiField @Path("description") TextField toolTipEditor;
    @UiField(provided = true) @Path("") ArgumentValidatorEditor validatorsEditor;

    private static TextInputPropertyEditorUiBinder uiBinder = GWT.create(TextInputPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public TextInputPropertyEditor(final AppTemplateWizardAppearance appearance,
                                   final AppsWidgetsPropertyPanelLabels appLabels,
                                   final AppsWidgetsContextualHelpMessages help,
                                   final ArgumentValidatorEditor validatorsEditor,
                                   final IplantValidationConstants validationConstants) {
        super(appearance);
        this.appLabels = appLabels;
        this.textInputLabels = appLabels;
        this.validatorsEditor = validatorsEditor;

        TextField textField = new TextField();
        textField.setEmptyText(textInputLabels.textInputWidgetEmptyEditText());
        textField.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));
        defaultValueEditor = new ArgumentEditorConverter<>(textField, new SplittableToStringConverter());

        initWidget(uiBinder.createAndBindUi(this));

        argumentOptionEditor.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));
        defaultValueLabel.setHTML(appearance.createContextualHelpLabel(appLabels.textInputDefaultLabel(), help.textInputDefaultText()));

        toolTipLabel.setHTML(appearance.createContextualHelpLabel(appLabels.toolTipText(), help.toolTip()));
        argumentOptionLabel.setHTML(appearance.createContextualHelpLabel(appLabels.argumentOption(), help.argumentOption()));
        doNotDisplay.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.doNotDisplay()).toSafeHtml());

        requiredEditor.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.isRequired()).toSafeHtml());


        omitIfBlank.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;")
                                                 .append(appearance.createContextualHelpLabelNoFloat(appLabels.excludeWhenEmpty(), help.textInputExcludeArgument()))
                                                 .toSafeHtml());
        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
        ensureDebugId(Ids.PROPERTY_EDITOR + Ids.TEXT);
    }

    @Override
    public void edit(Argument argument) {
        super.edit(argument);
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
        argumentOptionEditor.setEnabled(!isLabelOnlyEditMode);
        defaultValueEditor.setEnabled(!isLabelOnlyEditMode);
        doNotDisplay.setEnabled(!isLabelOnlyEditMode);
        omitIfBlank.setEnabled(!isLabelOnlyEditMode);
        requiredEditor.setEnabled(!isLabelOnlyEditMode);

        if (isLabelOnlyEditMode) {
            argumentOptionEditor.getValidators().clear();
            defaultValueEditor.getValidators().clear();
            doNotDisplay.getValidators().clear();
            omitIfBlank.getValidators().clear();
            requiredEditor.getValidators().clear();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + PropertyPanelIds.LABEL);
        argumentOptionEditor.ensureDebugId(baseID + PropertyPanelIds.ARGUMENT_OPTION);
        defaultValueEditor.ensureDebugId(baseID + PropertyPanelIds.DEFAULT_VALUE);
        doNotDisplay.ensureDebugId(baseID + PropertyPanelIds.DO_NOT_DISPLAY);
        requiredEditor.ensureDebugId(baseID + PropertyPanelIds.REQUIRED);
        omitIfBlank.ensureDebugId(baseID + PropertyPanelIds.OMIT_IF_BLANK);
        toolTipEditor.ensureDebugId(baseID + PropertyPanelIds.TOOL_TIP);
        validatorsEditor.ensureDebugId(baseID + PropertyPanelIds.VALIDATOR_RULES);
    }

    @UiHandler("defaultValueEditor")
    void onDefaultValueChange(ValueChangeEvent<Splittable> event) {
        // Forward defaultValue onto value.
        model.setValue(event.getValue());
    }

    @UiHandler("validatorsEditor")
    void onValidatorListChanged(@SuppressWarnings("unused") ValueChangeEvent<List<ArgumentValidator>> event) {
        editorDriver.flush();
        this.getBoundEditorDelegate().accept(new Refresher());
    }

}
