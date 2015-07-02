package org.iplantc.de.apps.integration.client.view.propertyEditors;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids.FLAG;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids.PROPERTY_EDITOR;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds.*;
import org.iplantc.de.apps.integration.client.view.propertyEditors.widgets.FlagArgumentOptionEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToBooleanConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.CheckboxInputLabels;

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
public class FlagArgumentPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, FlagArgumentPropertyEditor> { }

    interface FlagArgumentPropertyEditorUiBinder extends UiBinder<Widget, FlagArgumentPropertyEditor> { }

    @UiField(provided = true) AppsWidgetsPropertyPanelLabels appLabels;
    @UiField FieldLabel argLabelLabel;
    @UiField(provided = true) CheckboxInputLabels checkBoxLabels;
    @UiField @Ignore TextField checkedArgOption, checkedValue, unCheckedArgOption, unCheckedValue;
    @UiField(provided = true) ArgumentEditorConverter<Boolean> defaultValueEditor;
    @UiField @Path("visible") CheckBoxAdapter doNotDisplay;
    @UiField TextField label;
    @UiField @Path("description") TextField toolTipEditor;
    @UiField FieldLabel toolTipLabel;

    @Path("name") FlagArgumentOptionEditor argumentOptionEditor;

    private static FlagArgumentPropertyEditorUiBinder uiBinder = GWT.create(FlagArgumentPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public FlagArgumentPropertyEditor(final AppTemplateWizardAppearance appearance,
                                      final AppsWidgetsPropertyPanelLabels appLabels,
                                      final AppsWidgetsContextualHelpMessages help,
                                      final IplantValidationConstants validationConstants) {
        super(appearance);
        this.appLabels = appLabels;
        this.checkBoxLabels = appLabels;

        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter();
        checkBoxAdapter.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").appendEscaped(checkBoxLabels.checkboxDefaultLabel()).toSafeHtml());
        defaultValueEditor = new ArgumentEditorConverter<>(checkBoxAdapter, new SplittableToBooleanConverter());

        initWidget(uiBinder.createAndBindUi(this));

        CmdLineArgCharacterValidator argOptValidator = new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars());
        CmdLineArgCharacterValidator argValueValidator = new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars());

        checkedArgOption.addValidator(argOptValidator);
        checkedValue.addValidator(argValueValidator);
        unCheckedArgOption.addValidator(argOptValidator);
        unCheckedValue.addValidator(argValueValidator);

        toolTipLabel.setHTML(appearance.createChkBoxContextualHelpLabel(appLabels.toolTipText(),
                                                                  help.toolTip()));
        doNotDisplay.setHTML(new SafeHtmlBuilder().appendHtmlConstant("&nbsp;").append(appLabels.doNotDisplay()).toSafeHtml());

        argumentOptionEditor = new FlagArgumentOptionEditor(checkedArgOption, checkedValue, unCheckedArgOption, unCheckedValue,
                                                            validationConstants);

        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
        ensureDebugId(PROPERTY_EDITOR + FLAG);
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
        defaultValueEditor.setEnabled(!isLabelOnlyEditMode);
        doNotDisplay.setEnabled(!isLabelOnlyEditMode);
        checkedArgOption.setEnabled(!isLabelOnlyEditMode);
        checkedValue.setEnabled(!isLabelOnlyEditMode);
        unCheckedArgOption.setEnabled(!isLabelOnlyEditMode);
        unCheckedValue.setEnabled(!isLabelOnlyEditMode);

        if (isLabelOnlyEditMode) {
            defaultValueEditor.getValidators().clear();
            doNotDisplay.getValidators().clear();
            checkedArgOption.getValidators().clear();
            checkedValue.getValidators().clear();
            unCheckedArgOption.getValidators().clear();
            unCheckedValue.getValidators().clear();
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + LABEL);
        checkedArgOption.ensureDebugId(baseID + CHECKED_OPTION);
        checkedValue.ensureDebugId(baseID + CHECKED_VALUE);
        unCheckedArgOption.ensureDebugId(baseID + UNCHECKED_OPTION);
        unCheckedValue.ensureDebugId(baseID + UNCHECKED_VALUE);
        defaultValueEditor.ensureDebugId(baseID + DEFAULT_VALUE);
        doNotDisplay.ensureDebugId(baseID + DO_NOT_DISPLAY);
        toolTipEditor.ensureDebugId(baseID + TOOL_TIP);
    }

    @UiHandler("defaultValueEditor")
    void onDefaultValueChange(ValueChangeEvent<Splittable> event) {
        // Forward defaultValue onto value.
        model.setValue(event.getValue());
    }

}
