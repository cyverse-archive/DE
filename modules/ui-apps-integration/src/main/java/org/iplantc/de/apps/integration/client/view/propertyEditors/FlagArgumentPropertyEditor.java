package org.iplantc.de.apps.integration.client.view.propertyEditors;

import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids.FLAG;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids.PROPERTY_EDITOR;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds.*;
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

    public final class FlagArgumentOptionEditor implements ValueAwareEditor<String>, LeafValueEditor<String>, InvalidHandler, ValueChangeHandler<String>, HasValueChangeHandlers<String> {
        public final class ArgOptionsNotEmptyValidator implements Validator<String> {
            private final TakesValue<String> otherArgOption;

            public ArgOptionsNotEmptyValidator(TakesValue<String> otherArgOption) {
                this.otherArgOption = otherArgOption;
            }

            @Override
            public List<EditorError> validate(Editor<String> editor, String value) {
                if (Strings.isNullOrEmpty(value) && Strings.isNullOrEmpty(otherArgOption.getValue())) {
                    return Lists.<EditorError>newArrayList(new DefaultEditorError(editor, "At least one argument option must be defined.", value));
                }
                return null;
            }
        }

        public final class HasArgumentOptionValidator implements Validator<String> {
            private final TakesValue<String> argOption;

            public HasArgumentOptionValidator(TakesValue<String> argOption) {
                this.argOption = argOption;
            }

            @Override
            public List<EditorError> validate(Editor<String> editor, String value) {
                if (!Strings.isNullOrEmpty(value) && Strings.isNullOrEmpty(argOption.getValue())) {
                    return Lists.<EditorError>newArrayList(new DefaultEditorError(editor, "An argument value cannot be defined without a corresponding argument option.", value));
                }
                return null;
            }
        }

        private final TextField checkedArgOption1;
        private final TextField checkedValue1;
        private final ArrayList<EditorError> errors;
        private final TextField unCheckedArgOption1;
        private final TextField unCheckedValue1;
        private EditorDelegate<String> delegate;
        private String flagArgModel;
        private HandlerManager handlerManager;

        public FlagArgumentOptionEditor(TextField checkedArgOption, TextField checkedValue,
                                        TextField unCheckedArgOption, TextField unCheckedValue) {
            this.checkedArgOption1 = checkedArgOption;
            this.checkedValue1 = checkedValue;
            this.unCheckedArgOption1 = unCheckedArgOption;
            this.unCheckedValue1 = unCheckedValue;
            errors = Lists.newArrayList();
            init();
        }

        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
            return ensureHandlers().addHandler(ValueChangeEvent.getType(), handler);
        }

        @Override
        public void fireEvent(GwtEvent<?> event) {
            ensureHandlers().fireEvent(event);
        }

        @Override
        public void flush() {
            errors.clear();

            checkedArgOption1.validate();
            checkedValue1.validate();
            unCheckedArgOption1.validate();
            unCheckedValue1.validate();
        }

        @Override
        public String getValue() {
            // Only update if there are no errors.
            if (errors.isEmpty()) {
                String checked = Strings.nullToEmpty(checkedArgOption1.getValue()) + " " + Strings.nullToEmpty(checkedValue1.getValue());
                String unChecked = Strings.nullToEmpty(unCheckedArgOption1.getValue()) + " " + Strings.nullToEmpty(unCheckedValue1.getValue());

                flagArgModel = checked.trim() + ", " + unChecked.trim();
            }
            return flagArgModel;
        }

        @Override
        public void setValue(String value) {
            this.flagArgModel = value;
            // Split value
            LinkedList<String> newLinkedList = Lists.newLinkedList(Splitter.on(",").trimResults().split(Strings.nullToEmpty(value)));
            if (newLinkedList.peek() != null) {
                setCheckedFields(newLinkedList.removeFirst());
                if (newLinkedList.peek() != null) {
                    setUncheckedFields(newLinkedList.removeFirst());
                }
            }
        }

        @Override
        public void onInvalid(InvalidEvent event) {
            errors.addAll(event.getErrors());
            for (EditorError err : event.getErrors()) {
                delegate.recordError(err.getMessage(), err.getValue(), err.getUserData());
            }
        }

        @Override
        public void onPropertyChange(String... paths) {/* Do Nothing */}

        @Override
        public void onValueChange(ValueChangeEvent<String> event) {
            // Validate when a subfield changes value.
            if (event.getSource() instanceof IsField<?>) {
                ((IsField<?>) event.getSource()).validate(false);
            }
            ValueChangeEvent.fire(this, flagArgModel);
        }

        @Override
        public void setDelegate(EditorDelegate<String> delegate) {
            this.delegate = delegate;
        }

        HandlerManager ensureHandlers() {
            return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
        }

        private HandlerManager createHandlerManager() {
            return new HandlerManager(this);
        }

        private void init() {
            // Add arg option validators
            checkedArgOption1.addValidator(new CmdLineArgCharacterValidator());
            unCheckedArgOption1.addValidator(new CmdLineArgCharacterValidator());

            // Add local validators
            checkedValue1.addValidator(new HasArgumentOptionValidator(checkedArgOption1));
            unCheckedValue1.addValidator(new HasArgumentOptionValidator(unCheckedArgOption1));

            checkedArgOption1.addValidator(new ArgOptionsNotEmptyValidator(unCheckedArgOption1));
            unCheckedArgOption1.addValidator(new ArgOptionsNotEmptyValidator(checkedArgOption1));

            // Add invalid handlers for forwarding editor errors
            checkedArgOption1.addInvalidHandler(this);
            checkedValue1.addInvalidHandler(this);
            unCheckedArgOption1.addInvalidHandler(this);
            unCheckedValue1.addInvalidHandler(this);

            // Add ourselves as handlers to sub fields to forward ValueChangeEvents
            checkedArgOption1.addValueChangeHandler(this);
            checkedValue1.addValueChangeHandler(this);
            unCheckedArgOption1.addValueChangeHandler(this);
            unCheckedValue1.addValueChangeHandler(this);
        }

        private void setCheckedFields(String pop) {
            if (pop == null) {
                checkedArgOption1.clear();
                checkedValue1.clear();
                return;
            }
            LinkedList<String> newLinkedList = Lists.newLinkedList(Splitter.on(" ").omitEmptyStrings().trimResults().split(pop));
            if (newLinkedList.peek() != null) {
                checkedArgOption1.setValue(newLinkedList.removeFirst(), false);
                if (newLinkedList.peek() != null) {
                    checkedValue1.setValue(Joiner.on(" ").join(newLinkedList), false);
                }
            }
        }

        private void setUncheckedFields(String pop) {
            if (pop == null) {
                unCheckedArgOption1.clear();
                unCheckedValue1.clear();
                return;
            }

            LinkedList<String> newLinkedList = Lists.newLinkedList(Splitter.on(" ").omitEmptyStrings().trimResults().split(pop));
            if (newLinkedList.peek() != null) {
                unCheckedArgOption1.setValue(newLinkedList.removeFirst(), false);
                if (newLinkedList.peek() != null) {
                    unCheckedValue1.setValue(Joiner.on(" ").join(newLinkedList), false);
                }
            }
        }
    }

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

        toolTipLabel.setHTML(appearance.createContextualHelpLabel(appLabels.toolTipText(), help.toolTip()));
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
