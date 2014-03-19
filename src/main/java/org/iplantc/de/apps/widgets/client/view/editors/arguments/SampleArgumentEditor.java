package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent.ArgumentRequiredChangedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class SampleArgumentEditor implements AppTemplateForm.ArgumentEditor {

    @Override
    public HandlerRegistration addArgumentRequiredChangedEventHandler(ArgumentRequiredChangedEventHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addArgumentSelectedEventHandler(ArgumentSelectedEvent.ArgumentSelectedEventHandler handler) {
        return null;
    }

    @Override
    public Widget asWidget() {
        return null;
    }

    @Override
    public LeafValueEditor<String> descriptionEditor() {

        return null;
    }

    @Override
    public void disableOnNotVisible() {

    }

    @Override
    public void disableValidations() {

    }

    @Override
    public void flush() {

    }

    @Override
    public EditorDelegate<Argument> getEditorDelegate() {

        return null;
    }

    @Override
    public LeafValueEditor<String> idEditor() {

        return null;
    }

    @Override
    public boolean isDisabledOnNotVisible() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return false;
    }

    @Override
    public boolean isValidationDisabled() {
        return false;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public LeafValueEditor<String> labelEditor() {

        return null;
    }

    @Override
    public void onPropertyChange(String... paths) {

    }

    @Override
    public LeafValueEditor<Boolean> requiredEditor() {

        return null;
    }

    @Override
    public ValueAwareEditor<List<SelectionItem>> selectionItemsEditor() {
        return null;
    }

    @Override
    public void setDelegate(EditorDelegate<Argument> delegate) {

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {

    }

    @Override
    public void setValue(Argument value) {

    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public LeafValueEditor<ArgumentType> typeEditor() {

        return null;
    }

    @Override
    public LeafValueEditor<List<ArgumentValidator>> validatorsEditor() {

        return null;
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return null;
    }

    @Override
    public LeafValueEditor<Boolean> visibleEditor() {
        return null;
    }
}
