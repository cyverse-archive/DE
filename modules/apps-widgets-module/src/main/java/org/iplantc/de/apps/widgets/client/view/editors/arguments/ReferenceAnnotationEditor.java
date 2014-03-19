package org.iplantc.de.apps.widgets.client.view.editors.arguments;

import org.iplantc.de.apps.widgets.client.view.editors.ReferenceGenomeProperties;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToReferenceGenomeConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.ReferenceGenome;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.autobean.shared.Splittable;

import static com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction.ALL;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class ReferenceAnnotationEditor extends AbstractArgumentEditor implements HasValueChangeHandlers<Splittable> {
    private final ComboBox<ReferenceGenome> comboBox;
    private final ArgumentEditorConverter<ReferenceGenome> editorAdapter;

    public ReferenceAnnotationEditor(AppTemplateWizardAppearance appearance, ListStore<ReferenceGenome> refGenomeStore, ReferenceGenomeProperties props) {
        super(appearance);
        comboBox = new ComboBox<ReferenceGenome>(refGenomeStore, props.name());
        comboBox.setTriggerAction(ALL);
        comboBox.setEmptyText(I18N.APPS_MESSAGES.emptyListSelectionText());
        comboBox.setMinChars(1);
        ClearComboBoxSelectionKeyDownHandler handler = new ClearComboBoxSelectionKeyDownHandler(comboBox);
        comboBox.addKeyDownHandler(handler);

        editorAdapter = new ArgumentEditorConverter<ReferenceGenome>(comboBox, new SplittableToReferenceGenomeConverter());

        argumentLabel.setWidget(editorAdapter);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Splittable> handler) {
        return editorAdapter.addValueChangeHandler(handler);
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return editorAdapter;
    }

}
