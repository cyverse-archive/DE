package org.iplantc.de.apps.integration.client.view.propertyEditors;

import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsContextualHelpMessages;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsPropertyPanelLabels;
import org.iplantc.de.resources.client.uiapps.widgets.argumentTypes.InfoTypeLabels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class InfoPropertyEditor extends AbstractArgumentPropertyEditor {

    interface EditorDriver extends SimpleBeanEditorDriver<Argument, InfoPropertyEditor> {}
    interface InfoPropertyEditorUiBinder extends UiBinder<Widget, InfoPropertyEditor> {}

    private static InfoPropertyEditorUiBinder uiBinder = GWT.create(InfoPropertyEditorUiBinder.class);

    @UiField
    FieldLabel argLabelLabel;

    @UiField(provided = true)
    InfoTypeLabels infoLabels;
    @UiField
    TextArea label;

    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);

    @Inject
    public InfoPropertyEditor(AppTemplateWizardAppearance appearance, AppsWidgetsPropertyPanelLabels appLabels, AppsWidgetsContextualHelpMessages help) {
        super(appearance);
        this.infoLabels = appLabels;
        initWidget(uiBinder.createAndBindUi(this));
        argLabelLabel.setHTML(appearance.createContextualHelpLabel(infoLabels.infoLabel(), help.infoLabelHelp()));
        editorDriver.initialize(this);
        editorDriver.accept(new InitializeTwoWayBinding(this));
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
    protected void initLabelOnlyEditMode(boolean isLabelOnlyEditMode) {
        // Do nothing
    }

}
