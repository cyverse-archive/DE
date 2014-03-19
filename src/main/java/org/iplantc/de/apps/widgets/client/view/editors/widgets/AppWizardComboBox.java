package org.iplantc.de.apps.widgets.client.view.editors.widgets;

import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.editors.SelectionItemProperties;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.AbstractArgumentEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.ClearComboBoxSelectionKeyDownHandler;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.SplittableToSelectionArgConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.uiapps.widgets.AppsWidgetsDisplayMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import java.util.List;

/**
 * @author jstroot
 * 
 */
public class AppWizardComboBox extends AbstractArgumentEditor implements HasValueChangeHandlers<Splittable> {

    private final AppsWidgetsDisplayMessages appsWidgetsMessages = I18N.APPS_MESSAGES;

    private final AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
    private final ListStore<SelectionItem> listStore;

    private final SelectionItemProperties props = GWT.<SelectionItemProperties> create(SelectionItemProperties.class);

    private final ComboBox<SelectionItem> selectionItemsEditor;

    private final ListStoreEditor<SelectionItem> selectionItemsStoreBinder;
    private final ArgumentEditorConverter<SelectionItem> valueEditor;

    public AppWizardComboBox(AppTemplateWizardAppearance appearance) {
        super(appearance);
        // JDS Initialize list store, and its editor
        listStore = new ListStore<SelectionItem>(props.id());
        selectionItemsStoreBinder = new ListStoreEditor<SelectionItem>(listStore);

        // JDS Initialize combobox and its editor converter
        selectionItemsEditor = new ComboBox<SelectionItem>(listStore, props.displayLabel());
        selectionItemsEditor.setEmptyText(appsWidgetsMessages.emptyListSelectionText());
        selectionItemsEditor.setMinChars(1);
        selectionItemsEditor.setTriggerAction(TriggerAction.ALL);
        ClearComboBoxSelectionKeyDownHandler handler = new ClearComboBoxSelectionKeyDownHandler(selectionItemsEditor);
        selectionItemsEditor.addKeyDownHandler(handler);
        valueEditor = new ArgumentEditorConverter<SelectionItem>(selectionItemsEditor, new SplittableToSelectionArgConverter());

        argumentLabel.setWidget(valueEditor);
    }

    @Override
    public HandlerRegistration addArgumentSelectedEventHandler(ArgumentSelectedEvent.ArgumentSelectedEventHandler handler) {
        return addHandler(handler, ArgumentSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Splittable> handler) {
        return valueEditor.addValueChangeHandler(handler);
    }

    @Override
    public void flush() {
        selectionItemsEditor.flush();
        selectionItemsEditor.validate(false);
        SelectionItem currSi = selectionItemsEditor.getCurrentValue();
        if (currSi == null) {
            return;
        }
        Splittable currSiSplittable = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(currSi));

        // JDS Set value, if the current value payload does not equal the model's value payload
        if ((model.getValue() == null) || ((model.getValue() != null) && !model.getValue().getPayload().equals(currSiSplittable.getPayload()))) {
            currSiSplittable = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(currSi));
            model.setValue(currSiSplittable);
            model.setDefaultValue(currSiSplittable);
        }
    }

    @Override
    public ValueAwareEditor<List<SelectionItem>> selectionItemsEditor() {
        return selectionItemsStoreBinder;
    }

    public void setRequired(boolean required) {
        selectionItemsEditor.setAllowBlank(!required);
    }

    @Override
    public void setValue(final Argument value) {
        super.setValue(value);
        if (AppTemplateUtils.isSelectionArgumentType(value.getType()) || value.getType().equals(ArgumentType.TreeSelection)) {
            return;
        }

        if (model.getSelectionItems() != null) {
            selectionItemsStoreBinder.setValue(model.getSelectionItems());
        }
        if (model.getValue() != null) {
            if (!model.getValue().isUndefined("id")) {
                SelectionItem modelValue = AutoBeanCodex.decode(factory, SelectionItem.class, model.getValue()).as();
                String id = model.getValue().get("id").asString();
                SelectionItem si = listStore.findModelWithKey(id);
                if (si != null) {
                    doSetAndSelect(si);
                } else {
                    for (final SelectionItem item : listStore.getAll()) {
                        boolean valuesEqual = modelValue.getValue().equals(item.getValue());
                        boolean namesEqual = modelValue.getName().equals(item.getName());
                        if (namesEqual && valuesEqual) {
                            doSetAndSelect(item);
                        }
                    }
                }
            }

        }
    }

    @Override
    public ArgumentEditorConverter<?> valueEditor() {
        return valueEditor;
    }

    private void doSetAndSelect(final SelectionItem item) {
        selectionItemsEditor.setValue(item);
    }

}
