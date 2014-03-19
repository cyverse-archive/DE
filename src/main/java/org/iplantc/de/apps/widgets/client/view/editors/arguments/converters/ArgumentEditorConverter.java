package org.iplantc.de.apps.widgets.client.view.editors.arguments.converters;

import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.IArgumentEditorConverter;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.ArgumentValidatorType;
import org.iplantc.de.commons.client.validators.IPlantDefaultValidator;
import org.iplantc.de.commons.client.widgets.PreventEntryAfterLimitHandler;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceSelector;

import com.google.common.collect.Lists;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;

import java.util.List;

/**
 * @author jstroot
 * 
 * @param <T>
 */
public class ArgumentEditorConverter<T> extends Composite implements IArgumentEditorConverter, ValueChangeHandler<T> {
	
    private final Converter<Splittable, T> converter;
    private EditorDelegate<Splittable> delegate;

    private final List<EditorError> errors;

	private final IsField<T> field;

	private final List<HandlerRegistration> preventEntryKeyDownHandlers = Lists.newArrayList();

    @SuppressWarnings("unchecked")
    public ArgumentEditorConverter(IsField<T> editor, Converter<Splittable, T> converter) {
        this.field = editor;
		this.converter = converter;
        errors = Lists.newArrayList();
        // Forward value change events from wrapped field
        if (editor instanceof HasValueChangeHandlers<?>) {
            ((HasValueChangeHandlers<T>)editor).addValueChangeHandler(this);
        }
        initWidget(editor.asWidget());
	}

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Splittable> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void applyValidators(List<ArgumentValidator> validators, boolean isValidationDisabled) {
        if (validators == null) {
            return;
        }
        if (validators.isEmpty()) {
            // JDS If validators are empty, we need to cleanup.
            if (field instanceof Field<?>) {
                Field<T> tmpField = (Field<T>)field;
                List<Validator<T>> validatorsToRemove = Lists.newArrayList(tmpField.getValidators());
                for (Validator<T> v : validatorsToRemove) {
                    if (v instanceof IPlantDefaultValidator) {
                        continue;
                    }
                    tmpField.removeValidator(v);
                    if ((v instanceof MaxLengthValidator)) {
                        for (HandlerRegistration hr : preventEntryKeyDownHandlers) {
                            hr.removeHandler();
                        }
                        preventEntryKeyDownHandlers.clear();
                    }
                }
            }
        }
        if (field instanceof Field<?>) {
            Field<T> fieldObj = (Field<T>)field;

            // JDS Clear all existing validators
            List<Validator<T>> validatorsToRemove = Lists.newArrayList(fieldObj.getValidators());
            for (Validator<T> v : validatorsToRemove) {
                if (v instanceof IPlantDefaultValidator) {
                    continue;
                }
                fieldObj.removeValidator(v);
            }

            for (ArgumentValidator av : validators) {
                AutoBean<ArgumentValidator> autoBean = AutoBeanUtils.getAutoBean(av);

                if (av.getType().equals(ArgumentValidatorType.CharacterLimit)) {
                    // Remove previous keyDown handler (if it exists)
                    Object hndlrReg = autoBean.getTag(ArgumentValidator.KEY_DOWN_HANDLER_REG);
                    if ((hndlrReg != null)) {
                        ((HandlerRegistration)hndlrReg).removeHandler();
                        preventEntryKeyDownHandlers.remove(hndlrReg);
                    }

                    // Re-apply the keyDown handler (if it exists)
                    Object keyDownHndlr = autoBean.getTag(ArgumentValidator.KEY_DOWN_HANDLER);
                    if (keyDownHndlr != null) {
                        HandlerRegistration newHndlrReg = field.asWidget().addDomHandler((KeyDownHandler)keyDownHndlr, KeyDownEvent.getType());
                        autoBean.setTag(ArgumentValidator.KEY_DOWN_HANDLER_REG, newHndlrReg);
                        preventEntryKeyDownHandlers.add(newHndlrReg);
                    } else {
                        int maxCharLimit = Double.valueOf(av.getParams().get(0).asNumber()).intValue();
                        @SuppressWarnings("unchecked")
                        TakesValue<String> takesValue = (TakesValue<String>)field;
                        PreventEntryAfterLimitHandler newHandler = new PreventEntryAfterLimitHandler(takesValue, maxCharLimit);
                        HandlerRegistration newHndlrReg = field.asWidget().addDomHandler(newHandler, KeyDownEvent.getType());
                        autoBean.setTag(ArgumentValidator.KEY_DOWN_HANDLER_REG, newHndlrReg);
                        autoBean.setTag(ArgumentValidator.KEY_DOWN_HANDLER, newHandler);
                    }

                }

                // If validation is not disabled, Re-apply the actual validator
                if (!isValidationDisabled) {
                    Object validator = autoBean.getTag(ArgumentValidator.VALIDATOR);
                    if ((field instanceof Field<?>) && (validator != null)) {
                        @SuppressWarnings("unchecked")
                        Validator<T> val = (Validator<T>)validator;
                        ((Field<T>)field).addValidator(val);
                    }
                }
            }
        }

        validate(false);

    }

    @Override
    public void clear() {
        field.clear();
    }

    @Override
    public void clearInvalid() {
        field.clearInvalid();
    }

    @Override
    public void flush() {
        if (field instanceof Field<?>) {
            ((Field<T>)field).flush();
        }
        validate(false);
        if (delegate != null) {
            for (EditorError e : errors) {
                delegate.recordError(e.getMessage(), e.getValue(), this);
            }
        }

    }

    @Override
    public Splittable getValue() {
        return converter.convertFieldValue(field.getValue());
	}

    @Override
    public boolean isValid(boolean preventMark) {
        boolean valid = field.isValid(preventMark);
        collectErrorsFromField();
        return valid;
    }

    @Override
    public void onPropertyChange(String... paths) {}

    @Override
    public void onValueChange(ValueChangeEvent<T> event) {
        ValueChangeEvent.fire(this, converter.convertFieldValue(event.getValue()));
    }

    @Override
    public void reset() {
        field.reset();
    }

    @Override
    public void setDelegate(EditorDelegate<Splittable> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (field.asWidget() instanceof HasEnabled) {
            ((HasEnabled)field.asWidget()).setEnabled(enabled);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void setRequired(boolean isRequired, boolean isValidationDisabled) {
        if (field == null) {
            return;
        }

        if (isRequired && !isValidationDisabled) {
            EmptyValidator<T> emptyValidator = new EmptyValidator<T>();
            if (field instanceof ValueBaseField<?>) {
                ((ValueBaseField<T>)field).setAllowBlank(false);
            } else if (field instanceof DiskResourceSelector) {
                ((DiskResourceSelector)field).setRequired(true);
            } else if (field instanceof Field<?>) {
                Field<T> castField = (Field<T>)field;
                castField.addValidator(emptyValidator);
            }
        } else {
            // JDS Clear required validators
            if (field instanceof ValueBaseField<?>) {
                ((ValueBaseField<T>)field).setAllowBlank(true);
            } else if (field instanceof DiskResourceSelector) {
                ((DiskResourceSelector)field).setRequired(false);
            } else if (field instanceof Field<?>) {
                Field<T> castField = (Field<T>)field;
                List<Validator<T>> validators = castField.getValidators();
                List<Validator<?>> validatorsToRemove = Lists.newArrayList();
                for (Validator<T> v : validators) {
                    if (v instanceof EmptyValidator<?>) {
                        validatorsToRemove.add(v);
                    }
                }
                for (Validator<?> v : validatorsToRemove) {
                    castField.removeValidator((Validator<T>)v);
                }

            }

        }
    }

    @Override
    public void setValue(Splittable value) {
        field.setValue(converter.convertModelValue(value));
	}

    @Override
    public boolean validate(boolean preventMark) {
        boolean valid = field.validate(preventMark);
        collectErrorsFromField();
        return valid;
    }

    private void collectErrorsFromField() {
        errors.clear();
        if (field instanceof Field<?>) {
            for (Validator<T> v : ((Field<T>)field).getValidators()) {
                List<EditorError> errs = v.validate(field, field.getValue());
                if (errs != null) {
                    errors.addAll(errs);
                }
            }
        } else if (field instanceof DiskResourceSelector) {
            errors.addAll(((DiskResourceSelector)field).getErrors());
        } else {
            return;
        }
    }

    public List<Validator<T>> getValidators() {
        if (field instanceof Field<?>) {
            return ((Field<T>)field).getValidators();
        }

        return Lists.newArrayList();
    }
}
