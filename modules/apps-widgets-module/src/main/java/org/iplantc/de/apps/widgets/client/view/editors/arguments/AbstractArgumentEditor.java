package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent.ArgumentRequiredChangedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.commons.client.validators.DiskResourceNameValidator;
import org.iplantc.de.commons.client.validators.DoubleAboveValidator;
import org.iplantc.de.commons.client.validators.DoubleBelowValidator;
import org.iplantc.de.commons.client.validators.IntAboveValidator;
import org.iplantc.de.commons.client.validators.IntBelowValidator;
import org.iplantc.de.commons.client.validators.NumberRangeValidator;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import static com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign.TOP;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import java.util.List;

/**
 * 
 * @author jstroot
 * 
 */
public abstract class AbstractArgumentEditor extends Composite implements AppTemplateForm.ArgumentEditor {

    class RequiredLeafEditor extends LabelLeafEditor<Boolean> {

        public RequiredLeafEditor(HasSafeHtml hasHtml, ArgumentEditor argEditor, AppTemplateWizardAppearance appearance) {
            super(hasHtml, argEditor, appearance);
        }

        @Override
        public void setValue(Boolean value) {
            super.setValue(value);

            if ((value == null) || (argEditor.valueEditor() == null)) {
                return;
            }

            argEditor.valueEditor().setRequired(value.booleanValue(), isValidationDisabled());
            argEditor.asWidget().fireEvent(new ArgumentRequiredChangedEvent(value));
        }

    }

    class ValidatorListEditor implements LeafValueEditor<List<ArgumentValidator>> {

        private final AbstractArgumentEditor abstractArgumentEditor;
        private List<ArgumentValidator> vleModel;

        public ValidatorListEditor(AbstractArgumentEditor abstractArgumentEditor) {
            this.abstractArgumentEditor = abstractArgumentEditor;
        }

        @Override
        public List<ArgumentValidator> getValue() {
            return vleModel;
        }

        @Override
        public void setValue(List<ArgumentValidator> value) {
            this.vleModel = value;
            if (value == null) {
                return;
            }

            abstractArgumentEditor.applyValidators(value);
        }

    }

    public static void createAndAttachValidator(ArgumentValidator av) {
        AutoBean<ArgumentValidator> ab = AutoBeanUtils.getAutoBean(av);
        Validator<?> validator;
        switch (av.getType()) {
            case IntRange:
                // array of two integers
                int minInt = Double.valueOf(av.getParams().get(0).asNumber()).intValue();
                int maxInt = Double.valueOf(av.getParams().get(1).asNumber()).intValue();
                validator = new NumberRangeValidator<Integer>(minInt, maxInt);
                break;
            case IntAbove:
                // array of one integer
                int min2 = Double.valueOf(av.getParams().get(0).asNumber()).intValue();
                validator = new IntAboveValidator(min2);
                break;
            case IntBelow:
                // array of one integer
                int max2 = Double.valueOf(av.getParams().get(0).asNumber()).intValue();
                validator = new IntBelowValidator(max2);
                break;
            case DoubleRange:
                // Array of two doubles
                double minDbl = Double.valueOf(av.getParams().get(0).asNumber());
                double maxDbl = Double.valueOf(av.getParams().get(1).asNumber());
                validator = new NumberRangeValidator<Double>(minDbl, maxDbl);
                break;
            case DoubleAbove:
                // Array of one double
                double minDbl2 = Double.valueOf(av.getParams().get(0).asNumber());
                validator = new DoubleAboveValidator(minDbl2);
                break;
            case DoubleBelow:
                // Array of one double
                double maxDbl2 = Double.valueOf(av.getParams().get(0).asNumber());
                validator = new DoubleBelowValidator(maxDbl2);
                break;
            case CharacterLimit:
                // Array containing single integer
                int maxCharLimit = Double.valueOf(av.getParams().get(0).asNumber()).intValue();
                validator = new MaxLengthValidator(maxCharLimit);

                break;
            case Regex:
                // Array containing one string
                String regex = av.getParams().get(0).asString();
                validator = new RegExValidator(regex, "Input must match the Regular Expression: " + regex);
                break;
            case FileName:
                validator = new DiskResourceNameValidator();
                break;
            default:
                throw new UnsupportedOperationException("Given validator type is not a String validator type.");

        }
        // JDS Add ArgumentValidator metadata
        ab.setTag(ArgumentValidator.VALIDATOR, validator);
    }

    public static void setDefaultValue(Argument argument) {
        argument.setValue(argument.getDefaultValue());
    }

    protected AppTemplateWizardAppearance appearance;

    protected FieldLabel argumentLabel = new FieldLabel();

    protected final LabelLeafEditor<String> descriptionEditor;

    protected Argument model;

    protected final RequiredLeafEditor requiredEditor;

    private EditorDelegate<Argument> delegate;

    private boolean disableOnNotVisible = false;

    private final SimpleEditor<String> idEditor;

    private final LabelLeafEditor<String> labelLeafEditor;

    private boolean labelOnlyEditMode = false;

    private final SimpleEditor<ArgumentType> typeEditor;

    private boolean validationDisabled = false;

    private final ValidatorListEditor validatorListEditor;

    private final VisibilityEditor visibilityEditor;

    protected AbstractArgumentEditor(AppTemplateWizardAppearance appearance) {
        this.appearance = appearance;
        argumentLabel.setLabelAlign(TOP);
        new QuickTip(argumentLabel);
        init();
        validatorListEditor = new ValidatorListEditor(this);

        labelLeafEditor = new LabelLeafEditor<String>(argumentLabel, this, appearance);
        idEditor = SimpleEditor.<String> of();
        typeEditor = SimpleEditor.<ArgumentType> of();
        requiredEditor = new RequiredLeafEditor(argumentLabel, this, appearance);
        descriptionEditor = new LabelLeafEditor<String>(argumentLabel, this, appearance);
        visibilityEditor = new VisibilityEditor(this);
    }

    @Override
    public HandlerRegistration addArgumentRequiredChangedEventHandler(ArgumentRequiredChangedEventHandler handler) {
        return addHandler(handler, ArgumentRequiredChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addArgumentSelectedEventHandler(ArgumentSelectedEvent.ArgumentSelectedEventHandler handler) {
        return addHandler(handler, ArgumentSelectedEvent.TYPE);
    }

    @Override
    public LeafValueEditor<String> descriptionEditor() {
        return descriptionEditor;
    }

    @Override
    public void disableOnNotVisible() {
        this.disableOnNotVisible = true;

        // Refresh the visibility editor
        Boolean value = visibilityEditor.getValue();
        visibilityEditor.setValue(value);
    }

    @Override
    public void disableValidations() {
        validationDisabled = true;
        if (valueEditor() != null) {
            valueEditor().clearInvalid();
        }

        // Refresh the isRequired editor
        Boolean value = requiredEditor.getValue();
        requiredEditor.setValue(value);
    }

    @Override
    public void flush() {

    }

    @Override
    public EditorDelegate<Argument> getEditorDelegate() {
        return delegate;
    }

    @Override
    public LeafValueEditor<String> idEditor() {
        return idEditor;
    }

    @Override
    public boolean isDisabledOnNotVisible() {
        return disableOnNotVisible;
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return labelOnlyEditMode;
    }

    @Override
    public boolean isValidationDisabled() {
        return validationDisabled;
    }

    @Override
    public LeafValueEditor<String> labelEditor() {
        return labelLeafEditor;
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public LeafValueEditor<Boolean> requiredEditor() {
        return requiredEditor;
    }

    @Override
    public ValueAwareEditor<List<SelectionItem>> selectionItemsEditor() {
        return null;
    }

    @Override
    public void setDelegate(EditorDelegate<Argument> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (valueEditor() != null) {
            valueEditor().setEnabled(enabled);
        }
    }

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {
        this.labelOnlyEditMode = labelOnlyEditMode;
        if (valueEditor() != null) {
            valueEditor().setEnabled(!labelOnlyEditMode);
        }
    }

    @Override
    public void setValue(Argument value) {
        if (value == null) {
            return;
        }
        this.model = value;
        typeEditor.setValue(value.getType());
    }

    @Override
    public LeafValueEditor<ArgumentType> typeEditor() {
        return typeEditor;
    }

    @Override
    public LeafValueEditor<List<ArgumentValidator>> validatorsEditor() {
        return validatorListEditor;
    }

    @Override
    public LeafValueEditor<Boolean> visibleEditor() {
        return visibilityEditor;
    }

    /**
     * Applies the given validators to an {@code ArgumentEditor}'s valueEditor.
     * 
     * @param validators
     */
    protected void applyValidators(List<ArgumentValidator> validators) {

        for(ArgumentValidator av : validators){
            if(!isValidatorAttached(av)){
                createAndAttachValidator(av);
            }
        }
        
        if (valueEditor() == null) {
            return;
        }
        valueEditor().applyValidators(validators, isValidationDisabled());
    }

    protected void init() {
        argumentLabel.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AbstractArgumentEditor.this.fireEvent(new ArgumentSelectedEvent(model));
            }
        }, ClickEvent.getType());
        initWidget(argumentLabel);
    }

    private boolean isValidatorAttached(ArgumentValidator av) {
        AutoBean<ArgumentValidator> autoBean = AutoBeanUtils.getAutoBean(av);
        return autoBean.getTag(ArgumentValidator.VALIDATOR) != null;
    }
}
