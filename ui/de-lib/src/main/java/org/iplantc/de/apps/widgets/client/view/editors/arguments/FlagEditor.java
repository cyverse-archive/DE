package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToBooleanConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.apps.integration.Argument;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class FlagEditor extends AbstractArgumentEditor {
    private CheckBoxAdapter checkBox;
    private ArgumentEditorConverter<Boolean> editorAdapter;
    private LabelLeafEditor<String> cbDescriptionEditor;
    private LabelLeafEditor<String> cbLabelLeafEditor;

    public FlagEditor(AppTemplateWizardAppearance appearance) {
        super(appearance);
    }

    @Override
    public void setValue(Argument value) {
        super.setValue(value);
        argumentLabel.setHTML("");
        argumentLabel.setLabelSeparator("");
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

    @Override
    protected void init() {
        checkBox = new CheckBoxAdapter();
        cbDescriptionEditor = new LabelLeafEditor<String>(checkBox, this, appearance);
        cbLabelLeafEditor = new LabelLeafEditor<String>(checkBox, this, appearance);
        editorAdapter = new ArgumentEditorConverter<Boolean>(checkBox, new SplittableToBooleanConverter());
        checkBox.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent arg0) {
                FlagEditor.this.fireEvent(new ArgumentSelectedEvent(model));
            }
        }, ClickEvent.getType());
        initWidget(editorAdapter);
    }

    @Override
    public LeafValueEditor<String> descriptionEditor() {
        return cbDescriptionEditor;
    }

    @Override
    public LeafValueEditor<String> labelEditor() {
        return cbLabelLeafEditor;
    }

}
