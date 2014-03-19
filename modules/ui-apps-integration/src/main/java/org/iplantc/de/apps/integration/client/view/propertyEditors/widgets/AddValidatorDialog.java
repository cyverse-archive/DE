package org.iplantc.de.apps.integration.client.view.propertyEditors.widgets;

import org.iplantc.de.apps.widgets.client.view.editors.arguments.AbstractArgumentEditor;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.ArgumentValidatorType;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.uiapps.widgets.ArgumentValidatorMessages;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.event.TwinTriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TwinTriggerClickEvent.TwinTriggerClickHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.SpinnerField;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Map;
import java.util.Set;

/**
 * Purpose of the dialog is to add a validator. On close, a fully constructed validator should be
 * available.
 * 
 * Note: Application of validators should only affect how it actually runs, or how it is shown in preview
 * mode.
 * 
 * @author jstroot
 * 
 */
class AddValidatorDialog extends IPlantDialog implements ValidHandler, InvalidHandler {

    interface AddValidatorDialogUiBinder extends UiBinder<Widget, AddValidatorDialog> {
    }

    private final class AVTLabelKeyProvider implements LabelProvider<ArgumentValidatorType>,
            ModelKeyProvider<ArgumentValidatorType> {
        @Override
        public String getKey(ArgumentValidatorType item) {
            return item.name();
        }

        @Override
        public String getLabel(ArgumentValidatorType item) {
            String retVal = "";
            switch (item) {
                case Regex:
                    retVal = avMessages.regexLabel();
                    break;
                case CharacterLimit:
                    retVal = avMessages.characterLimitLabel();
                    break;

                case IntAbove:
                    retVal = avMessages.intAboveLabel();
                    break;

                case IntBelow:
                    retVal = avMessages.intBelowLabel();
                    break;
                case IntRange:
                    retVal = avMessages.intRangeLabel();
                    break;

                case DoubleAbove:
                    retVal = avMessages.dblAboveLabel();
                    break;
                case DoubleBelow:
                    retVal = avMessages.dblBelowLabel();
                    break;

                case DoubleRange:
                    retVal = avMessages.dblRangeLabel();
                    break;

                default:
                    retVal = item.name();
                    break;
            }
            return retVal;
        }
    }

    private final class DualTriggerClickHandler implements TriggerClickHandler, TwinTriggerClickHandler {
        @Override
        public void onTriggerClick(TriggerClickEvent event) {
            handleClick(event.getSource(), false);
        }

        @Override
        public void onTwinTriggerClick(TwinTriggerClickEvent event) {
            handleClick(event.getSource(), true);
        }

        private void handleClick(Object eventSource, boolean decrement) {
            if (!(eventSource instanceof SpinnerField<?>)) {
                return;
            }
            int sign = decrement ? -1 : 1;
            Object value = ((SpinnerField<?>)eventSource).getCurrentValue();
            if (value == null) {
                // If the value is null, we will pass the signed increment of the spinner field to both
                // rangeFieldChanged methods. Each of those methods will only operate based off their
                // respective instance checks, so this is safe (even though it is ambibuous).

                dblRangeFieldChanged(eventSource,
                        sign * ((SpinnerField<?>)eventSource).getIncrement(null).doubleValue());
                intRangeFieldChanged(eventSource,
                        sign * ((SpinnerField<?>)eventSource).getIncrement(null).intValue());
            } else if (value instanceof Double) {
                @SuppressWarnings("unchecked")
                SpinnerField<Double> dblField = (SpinnerField<Double>)eventSource;
                if (dblField.getCurrentValue() != null) {
                    double aboveFieldCurVal = dblField.getCurrentValue();
                    dblRangeFieldChanged(eventSource,
                            aboveFieldCurVal + (sign * dblField.getIncrement(null).doubleValue()));
                }

            } else if (value instanceof Integer) {
                @SuppressWarnings("unchecked")
                SpinnerField<Integer> intField = (SpinnerField<Integer>)eventSource;
                if (intField.getCurrentValue() != null) {
                    int aboveFieldCurVal = intField.getCurrentValue();
                    intRangeFieldChanged(eventSource,
                            aboveFieldCurVal + (sign * intField.getIncrement(null).intValue()));
                }

            }

        }
    }

    private static AddValidatorDialogUiBinder BINDER = GWT.create(AddValidatorDialogUiBinder.class);

    @UiField
    CardLayoutContainer cardLC;
    @UiField(provided = true)
    NumberField<Integer> charLimitField;
    @UiField(provided = true)
    SpinnerField<Double> dblAboveField, dblBelowField, dblRangeAboveField, dblRangeBelowField;

    // Double validators
    @UiField
    VerticalLayoutContainer dblAboveValidatorCon, dblBelowValidatorCon, dblRangeValidatorCon;

    @UiField(provided = true)
    SpinnerField<Integer> intAboveField, intBelowField, intRangeAboveField, intRangeBelowField;

    // Integer validators
    @UiField
    VerticalLayoutContainer intAboveValidatorCon, intBelowValidatorCon, intRangeValidatorCon;

    @UiField
    TextField regexField;

    // Text validators
    @UiField
    VerticalLayoutContainer regexValidatorCon, characterLimitValidatorCon;
    @UiField(provided = true)
    ComboBox<ArgumentValidatorType> validatorTypeCB;
    private final ArgumentValidatorMessages avMessages;

    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);

    private final ListStore<ArgumentValidatorType> validatorTypes;

    // Need a way of associating validator type with a card
    private final Map<ArgumentValidatorType, VerticalLayoutContainer> validatorTypeToCardMap;

    /**
     * @param supportedValidatorTypes use these to construct content of the combo box
     */
    AddValidatorDialog(Set<ArgumentValidatorType> supportedValidatorTypes,
            ArgumentValidatorMessages avMessages) {
        this.avMessages = avMessages;

        setHeadingText(avMessages.validatorDialogHeading());
        setAutoHide(false);
        setSize("400", "250");
        // Initalize the ComboBox list store with the given Set<..>
        validatorTypes = new ListStore<ArgumentValidatorType>(new AVTLabelKeyProvider());
        validatorTypes.addAll(supportedValidatorTypes);

        // Initialize the Combobox
        validatorTypeCB = new ComboBox<ArgumentValidatorType>(validatorTypes, new AVTLabelKeyProvider());
        validatorTypeCB.setForceSelection(true);
        validatorTypeCB.setAllowBlank(false);
        validatorTypeCB.setTriggerAction(TriggerAction.ALL);

        // Construct all "provided" fields.
        constructDoubleSpinnerFields();
        constructIntegerSpinnerFields();
        charLimitField = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
        charLimitField.setAllowBlank(false);
        charLimitField.addValidHandler(this);
        charLimitField.addInvalidHandler(this);

        add(BINDER.createAndBindUi(this));

        // Initialize validatorTypeToCardMap
        validatorTypeToCardMap = Maps.newHashMap();
        validatorTypeToCardMap.put(ArgumentValidatorType.DoubleAbove, dblAboveValidatorCon);
        validatorTypeToCardMap.put(ArgumentValidatorType.DoubleBelow, dblBelowValidatorCon);
        validatorTypeToCardMap.put(ArgumentValidatorType.DoubleRange, dblRangeValidatorCon);

        validatorTypeToCardMap.put(ArgumentValidatorType.IntAbove, intAboveValidatorCon);
        validatorTypeToCardMap.put(ArgumentValidatorType.IntBelow, intBelowValidatorCon);
        validatorTypeToCardMap.put(ArgumentValidatorType.IntRange, intRangeValidatorCon);

        validatorTypeToCardMap.put(ArgumentValidatorType.Regex, regexValidatorCon);
        validatorTypeToCardMap.put(ArgumentValidatorType.CharacterLimit, characterLimitValidatorCon);

        // Set default values.
        ArgumentValidatorType next = supportedValidatorTypes.iterator().next();
        validatorTypeCB.setValue(next, true);
        cardLC.setActiveWidget(validatorTypeToCardMap.get(next));
    }

    @Override
    public void onInvalid(InvalidEvent event) {
        getOkButton().setEnabled(false);
    }

    @Override
    public void onValid(ValidEvent event) {
        getOkButton().setEnabled(true);
    }

    @Override
    protected void onButtonPressed(TextButton button) {
        if (button.getText().equals(PredefinedButton.OK.toString())) {
            // Validate current card
            boolean isValid = FormPanelHelper.isValid((HasWidgets)cardLC.getActiveWidget());
            if (isValid) {
                super.onButtonPressed(button);
            }
        } else {
            super.onButtonPressed(button);
        }
    }


    @Override
    protected void onOkButtonClicked() {
        hide();
    }

    /**
     * This method should be called when after okButton is clicked.
     * 
     * @return
     */
    ArgumentValidator getArgumentValidator() {
        Splittable params = StringQuoter.createIndexed();

        switch (validatorTypeCB.getCurrentValue()) {
            case Regex:
                String regex = regexField.getCurrentValue();
                StringQuoter.create(regex).assign(params, 0);
                break;
            case CharacterLimit:
                int charLimit = charLimitField.getCurrentValue();
                StringQuoter.create(charLimit).assign(params, 0);
                break;

            case IntAbove:
                int intAbove = intAboveField.getCurrentValue();
                StringQuoter.create(intAbove).assign(params, 0);
                break;

            case IntBelow:
                int intBelow = intBelowField.getCurrentValue();
                StringQuoter.create(intBelow).assign(params, 0);
                break;
            case IntRange:
                int intRangeAbove = intRangeAboveField.getCurrentValue();
                int intRangeBelow = intRangeBelowField.getCurrentValue();
                StringQuoter.create(intRangeAbove).assign(params, 0);
                StringQuoter.create(intRangeBelow).assign(params, 1);
                break;

            case DoubleAbove:
                double dblAbove = dblAboveField.getCurrentValue();
                StringQuoter.create(dblAbove).assign(params, 0);
                break;
            case DoubleBelow:
                double dblBelow = dblBelowField.getCurrentValue();
                StringQuoter.create(dblBelow).assign(params, 0);
                break;

            case DoubleRange:
                double dblRangeAbove = dblRangeAboveField.getCurrentValue();
                double dblRangeBelow = dblRangeBelowField.getCurrentValue();
                StringQuoter.create(dblRangeAbove).assign(params, 0);
                StringQuoter.create(dblRangeBelow).assign(params, 1);
                break;
            default:
                break;
        }
        AutoBean<ArgumentValidator> avAutobean = factory.argumentValidator();
        // JDS Set a temporary id for this client-created validator. This is for client purposes, and
        // will be ignored if submitted to the server.
        avAutobean.as().setId("TEMP_ID_" + System.currentTimeMillis());
        avAutobean.as().setType(validatorTypeCB.getCurrentValue());
        avAutobean.as().setParams(params);

        // JDS Get the actual validator, and add it as metadata to the autobean.
        AbstractArgumentEditor.createAndAttachValidator(avAutobean.as());

        return avAutobean.as();
    }

    @UiHandler("validatorTypeCB")
    void onComboBoxSelection(SelectionEvent<ArgumentValidatorType> event) {
        // On selection of a type, set appropriate card
        cardLC.setActiveWidget(validatorTypeToCardMap.get(event.getSelectedItem()));
    }

    /**
     * This method is used to update this widget to reflect the given argument validator
     * 
     * @param selectedItem
     */
    void setArgumentValidator(final ArgumentValidator av) {
        // Set the combobox item and corresponding card.
        validatorTypeCB.setValue(av.getType(), true);
        cardLC.setActiveWidget(validatorTypeToCardMap.get(av.getType()));
        Splittable params = av.getParams();
        switch (av.getType()) {
            case Regex:
                String regex = params.get(0).asString();
                regexField.setValue(regex);
                break;
            case CharacterLimit:
                int charLimit = Double.valueOf(params.get(0).asNumber()).intValue();
                charLimitField.setValue(charLimit);
                break;

            case IntAbove:
                int intAbove = Double.valueOf(params.get(0).asNumber()).intValue();
                intAboveField.setValue(intAbove);
                break;

            case IntBelow:
                int intBelow = Double.valueOf(params.get(0).asNumber()).intValue();
                intBelowField.setValue(intBelow);
                break;
            case IntRange:
                int intRangeAbove = Double.valueOf(params.get(0).asNumber()).intValue();
                int intRangeBelow = Double.valueOf(params.get(1).asNumber()).intValue();
                intRangeAboveField.setValue(intRangeAbove);
                intRangeBelowField.setValue(intRangeBelow);
                break;

            case DoubleAbove:
                double dblAbove = Double.valueOf(params.get(0).asNumber());
                dblAboveField.setValue(dblAbove);
                break;
            case DoubleBelow:
                double dblBelow = Double.valueOf(params.get(0).asNumber());
                dblBelowField.setValue(dblBelow);
                break;

            case DoubleRange:
                double dblRangeAbove = Double.valueOf(params.get(0).asNumber());
                double dblRangeBelow = Double.valueOf(params.get(1).asNumber());
                dblRangeAboveField.setValue(dblRangeAbove);
                dblRangeBelowField.setValue(dblRangeBelow);
                break;

            default:
                break;
        }
    }

    /**
     * Used to construct the UiField 'provided' SpinnerField Double fields.
     */
    private void constructDoubleSpinnerFields() {
        NumberPropertyEditor.DoublePropertyEditor dblPropEditor = new NumberPropertyEditor.DoublePropertyEditor();
        dblPropEditor.setIncrement(0.1);
        dblAboveField = new SpinnerField<Double>(dblPropEditor);
        dblBelowField = new SpinnerField<Double>(dblPropEditor);
        dblRangeAboveField = new SpinnerField<Double>(dblPropEditor);
        dblRangeBelowField = new SpinnerField<Double>(dblPropEditor);

        dblAboveField.setMinValue(-Double.MAX_VALUE);
        dblBelowField.setMinValue(-Double.MAX_VALUE);
        dblRangeAboveField.setMinValue(-Double.MAX_VALUE);
        dblRangeBelowField.setMinValue(-Double.MAX_VALUE);

        dblAboveField.addValidHandler(this);
        dblBelowField.addValidHandler(this);
        dblRangeAboveField.addValidHandler(this);
        dblRangeBelowField.addValidHandler(this);

        dblAboveField.addInvalidHandler(this);
        dblBelowField.addInvalidHandler(this);
        dblRangeAboveField.addInvalidHandler(this);
        dblRangeBelowField.addInvalidHandler(this);

        dblAboveField.setAllowBlank(false);
        dblBelowField.setAllowBlank(false);
        dblRangeAboveField.setAllowBlank(false);
        dblRangeBelowField.setAllowBlank(false);

        ValueChangeHandler<Double> vcHandler = new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                dblRangeFieldChanged(event.getSource(), event.getValue());
            }
        };
        DualTriggerClickHandler dualTriggerClickHandler = new DualTriggerClickHandler();

        dblRangeAboveField.addTwinTriggerClickHandler(dualTriggerClickHandler);
        dblRangeAboveField.addTriggerClickHandler(dualTriggerClickHandler);
        dblRangeAboveField.addValueChangeHandler(vcHandler);

        dblRangeBelowField.addTwinTriggerClickHandler(dualTriggerClickHandler);
        dblRangeBelowField.addTriggerClickHandler(dualTriggerClickHandler);
        dblRangeBelowField.addValueChangeHandler(vcHandler);
    }

    /**
     * Used to construct the UiField 'provided' SpinnerField Integer fields.
     */
    private void constructIntegerSpinnerFields() {
        NumberPropertyEditor.IntegerPropertyEditor intPropEditor = new NumberPropertyEditor.IntegerPropertyEditor();
        intAboveField = new SpinnerField<Integer>(intPropEditor);
        intBelowField = new SpinnerField<Integer>(intPropEditor);
        intRangeAboveField = new SpinnerField<Integer>(intPropEditor);
        intRangeBelowField = new SpinnerField<Integer>(intPropEditor);

        intAboveField.setMinValue(-Integer.MAX_VALUE);
        intBelowField.setMinValue(-Integer.MAX_VALUE);
        intRangeAboveField.setMinValue(-Integer.MAX_VALUE);
        intRangeBelowField.setMinValue(-Integer.MAX_VALUE);

        intAboveField.addValidHandler(this);
        intBelowField.addValidHandler(this);
        intRangeAboveField.addValidHandler(this);
        intRangeBelowField.addValidHandler(this);

        intAboveField.addInvalidHandler(this);
        intBelowField.addInvalidHandler(this);
        intRangeAboveField.addInvalidHandler(this);
        intRangeBelowField.addInvalidHandler(this);

        intAboveField.setAllowBlank(false);
        intBelowField.setAllowBlank(false);
        intRangeAboveField.setAllowBlank(false);
        intRangeBelowField.setAllowBlank(false);

        ValueChangeHandler<Integer> vcHandler = new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                intRangeFieldChanged(event.getSource(), event.getValue());
            }
        };
        intRangeAboveField.addValueChangeHandler(vcHandler);
        intRangeBelowField.addValueChangeHandler(vcHandler);
    }

    private void dblRangeFieldChanged(Object eventSource, Double value) {
        // Determine which field was changed
        if (eventSource == dblRangeAboveField) {
            // Check for exit condition
            if ((dblRangeBelowField.getCurrentValue() != null)
                    && (value < dblRangeBelowField.getCurrentValue())) {
                return;
            }
            // LOWERBOUND Changed
            if ((dblRangeBelowField.getCurrentValue() == null)
                    || (value >= dblRangeBelowField.getCurrentValue())) {
                // If the upper bound value is null, equal to LOWERBOUND, or upper bound is less
                // than LOWERBOUND, then we increase the upper bound.
                dblRangeBelowField.setValue(value + dblRangeBelowField.getIncrement(null).doubleValue());
            }

        } else if (eventSource == dblRangeBelowField) {
            // Check for exit condition
            if ((dblRangeAboveField.getCurrentValue() != null)
                    && (dblRangeAboveField.getCurrentValue() < value)) {
                return;
            }
            // UPPERBOUND Changed
            if ((dblRangeAboveField.getCurrentValue() == null)
                    || ((dblRangeAboveField.getCurrentValue() >= value))) {
                // If the lower bound value is null, equal to UPPERBOUND, or lower bound is
                // greater than UPPERBOUND, then we decrement the lower bound.
                dblRangeAboveField.setValue(value - dblRangeAboveField.getIncrement(null).doubleValue());
            }
        }
    }

    private void intRangeFieldChanged(Object eventSource, Integer value) {
        // Determine which field was changed
        if (eventSource == intRangeAboveField) {
            // Check for exit condition
            if ((intRangeBelowField.getCurrentValue() != null)
                    && (value < intRangeBelowField.getCurrentValue())) {
                return;
            }
            // LOWERBOUND Changed
            if ((intRangeBelowField.getCurrentValue() == null)
                    || (value >= intRangeBelowField.getCurrentValue())) {
                // If the upper bound value is null, equal to LOWERBOUND, or upper bound is less
                // than LOWERBOUND, then we increase the upper bound.
                intRangeBelowField.setValue(value + intRangeBelowField.getIncrement(null).intValue());
            }
        } else if (eventSource == intRangeBelowField) {
            // Check for exit condition
            if ((intRangeAboveField.getCurrentValue() != null)
                    && (intRangeAboveField.getCurrentValue() < value)) {
                return;
            }
            // UPPERBOUND Changed
            if ((intRangeAboveField.getCurrentValue() == null)
                    || ((intRangeAboveField.getCurrentValue() > value))) {
                // If the lower bound value is null, equal to UPPERBOUND, or lower bound is
                // greater than UPPERBOUND, then we decrement the lower bound.
                intRangeAboveField.setValue(value - intRangeAboveField.getIncrement(null).intValue());
            }
        }
    }

}
