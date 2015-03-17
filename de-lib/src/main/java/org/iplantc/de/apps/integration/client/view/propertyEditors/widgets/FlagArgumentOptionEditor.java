package org.iplantc.de.apps.integration.client.view.propertyEditors.widgets;

import org.iplantc.de.commons.client.validators.CmdLineArgCharacterValidator;
import org.iplantc.de.resources.client.constants.IplantValidationConstants;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;

import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jstroot
 */
public final class FlagArgumentOptionEditor implements ValueAwareEditor<String>, LeafValueEditor<String>, InvalidEvent.InvalidHandler, ValueChangeHandler<String>, HasValueChangeHandlers<String> {
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
    IplantValidationConstants validationConstants;

    public FlagArgumentOptionEditor(TextField checkedArgOption, TextField checkedValue,
                                    TextField unCheckedArgOption, TextField unCheckedValue,
                                    final IplantValidationConstants validationConstants) {
        this.checkedArgOption1 = checkedArgOption;
        this.checkedValue1 = checkedValue;
        this.unCheckedArgOption1 = unCheckedArgOption;
        this.unCheckedValue1 = unCheckedValue;
        this.validationConstants = validationConstants;
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
        checkedArgOption1.clear();
        checkedValue1.clear();
        unCheckedArgOption1.clear();
        unCheckedValue1.clear();

        checkedArgOption1.setValue("");
        checkedValue1.setValue("");
        unCheckedArgOption1.setValue("");
        unCheckedValue1.setValue("");

        LinkedList<String> newLinkedList = Lists.newLinkedList(Splitter.on(",").trimResults().split(Strings.nullToEmpty(value)));
        if (!Strings.isNullOrEmpty(newLinkedList.peek())) {
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
        checkedArgOption1.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));
        unCheckedArgOption1.addValidator(new CmdLineArgCharacterValidator(validationConstants.restrictedCmdLineChars()));

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
        LinkedList<String> newLinkedList = Lists.newLinkedList(Splitter.on(" ").omitEmptyStrings().trimResults().split(pop));
        if (newLinkedList.peek() != null) {
            checkedArgOption1.setValue(newLinkedList.removeFirst(), false);
            if (newLinkedList.peek() != null) {
                checkedValue1.setValue(Joiner.on(" ").join(newLinkedList), false);
            }
        }
    }

    private void setUncheckedFields(String pop) {
        LinkedList<String> newLinkedList = Lists.newLinkedList(Splitter.on(" ").omitEmptyStrings().trimResults().split(pop));
        if (newLinkedList.peek() != null) {
            unCheckedArgOption1.setValue(newLinkedList.removeFirst(), false);
            if (newLinkedList.peek() != null) {
                unCheckedValue1.setValue(Joiner.on(" ").join(newLinkedList), false);
            }
        }
    }
}
