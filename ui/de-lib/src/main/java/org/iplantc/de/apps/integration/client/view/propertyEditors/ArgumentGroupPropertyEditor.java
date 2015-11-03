package org.iplantc.de.apps.integration.client.view.propertyEditors;


import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.Ids;
import static org.iplantc.de.apps.integration.shared.AppIntegrationModule.PropertyPanelIds;
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
import com.google.gwt.editor.client.adapters.SimpleEditor;
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

    interface ArgumentGroupPropertyEditorUiBinder extends UiBinder<Widget, ArgumentGroupPropertyEditor> { }

    interface EditorDriver extends SimpleBeanEditorDriver<ArgumentGroup, ArgumentGroupPropertyEditor> { }

    final PrefixedHasTextEditor labelEditor;

    @UiField ContentPanel cp;
    @UiField @Ignore TextButton deleteButton;
    @UiField TextField label;
    SimpleEditor<String> name;
    private static ArgumentGroupPropertyEditorUiBinder BINDER = GWT.create(ArgumentGroupPropertyEditorUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private String absoluteEditorPath;
    private ArgumentGroupEditor argumentGroupEditor;
    private boolean labelOnlyEditMode = false;
    private ArgumentGroup model;

    @Inject
    public ArgumentGroupPropertyEditor(AppTemplateWizardAppearance appearance) {
        initWidget(BINDER.createAndBindUi(this));
        name = SimpleEditor.of();
        labelEditor = new PrefixedHasTextEditor(cp.getHeader(), appearance);
        editorDriver.initialize(this);
        ensureDebugId(Ids.PROPERTY_EDITOR + Ids.GROUP);
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

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {
        this.labelOnlyEditMode = labelOnlyEditMode;
        if (labelOnlyEditMode) {
            // Perform labelOnlyEdit actions
            deleteButton.disable();
        }
    }

    public void setBoundArgumentGroupEditor(ArgumentGroupEditor argumentGroupEditor) {
        this.argumentGroupEditor = argumentGroupEditor;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        label.ensureDebugId(baseID + PropertyPanelIds.LABEL);
        deleteButton.ensureDebugId(baseID + PropertyPanelIds.DELETE);
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
        name.setValue(event.getValue());
        editorDriver.flush();
        if ((argumentGroupEditor != null) && (argumentGroupEditor.labelEditor() != null)) {
            argumentGroupEditor.labelEditor().setValue(event.getValue());
        }
    }

}
