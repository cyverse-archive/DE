package org.iplantc.de.apps.integration.client.view.propertyEditors;


import org.iplantc.de.apps.integration.client.events.DeleteArgumentGroupEvent;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentGroupEvent.DeleteArgumentGroupEventHandler;
import org.iplantc.de.apps.integration.client.events.DeleteArgumentGroupEvent.HasDeleteArgumentGroupEventHandlers;
import org.iplantc.de.apps.integration.client.view.propertyEditors.style.AppTemplateWizardPropertyContentPanelAppearance;
import org.iplantc.de.apps.integration.client.view.propertyEditors.util.FinishEditing;
import org.iplantc.de.apps.integration.client.view.propertyEditors.util.PrefixedHasTextEditor;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm.ArgumentGroupEditor;
import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.ArgumentGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

public class ArgumentGroupPropertyEditor extends Composite implements Editor<ArgumentGroup>, HasDeleteArgumentGroupEventHandlers, HasLabelOnlyEditMode {

    interface ArgumentGroupPropertyEditorUiBinder extends UiBinder<Widget, ArgumentGroupPropertyEditor> {}

    interface EditorDriver extends SimpleBeanEditorDriver<ArgumentGroup, ArgumentGroupPropertyEditor> {}

    private static ArgumentGroupPropertyEditorUiBinder BINDER = GWT.create(ArgumentGroupPropertyEditorUiBinder.class);

    @UiField 
    ContentPanel cp;

    @Ignore
    @UiField
    TextButton deleteButton;
    
    @UiField
    TextField label;

    final PrefixedHasTextEditor labelEditor;

    private String absoluteEditorPath;

    private ArgumentGroupEditor argumentGroupEditor;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    private boolean labelOnlyEditMode = false;

    private ArgumentGroup model;

    @Inject
    public ArgumentGroupPropertyEditor(AppTemplateWizardAppearance appearance) {
        initWidget(BINDER.createAndBindUi(this));
        labelEditor = new PrefixedHasTextEditor(cp.getHeader(), appearance);
        editorDriver.initialize(this);
    }

    @Override
    public HandlerRegistration addDeleteArgumentGroupEventHandler(DeleteArgumentGroupEventHandler handler) {
        return addHandler(handler, DeleteArgumentGroupEvent.TYPE);
    }

    public void edit(ArgumentGroup argumentGroup, String absoluteEditorPath) {
        if (model != null) {
            editorDriver.accept(new FinishEditing());
        }
        editorDriver.edit(argumentGroup);
        this.model = argumentGroup;
        this.absoluteEditorPath = absoluteEditorPath;
    }

    @Ignore
    public String getAbsoluteEditorPath() {
        return absoluteEditorPath;
    }

    @Ignore
    public com.google.gwt.editor.client.EditorDriver<ArgumentGroup> getEditorDriver() {
        return editorDriver;
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return labelOnlyEditMode;
    }

    public void setBoundArgumentGroupEditor(ArgumentGroupEditor argumentGroupEditor) {
        this.argumentGroupEditor = argumentGroupEditor;
    }

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {
        this.labelOnlyEditMode = labelOnlyEditMode;
        if (labelOnlyEditMode) {
            // Perform labelOnlyEdit actions
            deleteButton.disable();
        }
    }

    @UiFactory
    ContentPanel createContentPanel() {
        return new ContentPanel(new AppTemplateWizardPropertyContentPanelAppearance());
    }

    /**
     * @param event
     */
    @UiHandler("deleteButton")
    void deleteButtonSelectHandler(SelectEvent event) {
        fireEvent(new DeleteArgumentGroupEvent(model));
    }

    @UiHandler("label")
    void onStringValueChanged(ValueChangeEvent<String> event) {
        labelEditor.setValue(event.getValue());
        editorDriver.flush();
        if ((argumentGroupEditor != null) && (argumentGroupEditor.labelEditor() != null)) {
            argumentGroupEditor.labelEditor().setValue(event.getValue());
        }
    }

}
