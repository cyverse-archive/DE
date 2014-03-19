package org.iplantc.de.apps.integration.client.view.propertyEditors.widgets;

import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.ArgumentValidatorType;
import org.iplantc.de.client.util.AppTemplateUtils;
import org.iplantc.de.resources.client.uiapps.widgets.ArgumentValidatorMessages;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import java.util.List;
import java.util.Set;

/**
 * 
 * FIXME JDS Need to create simple test to validate Number-based validators with simplified range
 * testing. Should inform user that the newest validator conflicts with existing validators.
 * 
 * TODO CORE-4806 Refactor. Remove field label from this class, move it down.
 * 
 * @author jstroot
 * 
 */
public class ArgumentValidatorEditor extends Composite implements ValueAwareEditor<Argument>, HasValueChangeHandlers<List<ArgumentValidator>> {
    
    interface MyUiBinder extends UiBinder<Widget, ArgumentValidatorEditor> {}
    private final class AddValidatorCancelBtnSelectHandler implements SelectHandler {
        private final ArgumentValidator selectedItem;
        private final int selectedItemIndex;
    
        private AddValidatorCancelBtnSelectHandler(int selectedItemIndex, ArgumentValidator selectedItem) {
            this.selectedItemIndex = selectedItemIndex;
            this.selectedItem = selectedItem;
        }
    
        @Override
        public void onSelect(SelectEvent event) {
            // JDS If we have cancelled, re-add the item at its prev index.
            validators.getStore().add(selectedItemIndex, selectedItem);
        }
    }

    private final class AddValidatorOkBtnSelectHndlr implements SelectHandler {
        private final AddValidatorDialog dlg;
    
        public AddValidatorOkBtnSelectHndlr(final AddValidatorDialog dlg) {
            this.dlg = dlg;
        }
    
        @Override
        public void onSelect(SelectEvent event) {
            // When ok button is selected, fetch the current argument validator and add it to the store.
            ArgumentValidator arg = dlg.getArgumentValidator();
            validators.getStore().add(arg);
            ValueChangeEvent.fire(ArgumentValidatorEditor.this, validators.getStore().getAll());
        }
    }

    private final class ValidatorValueProvider implements ValueProvider<ArgumentValidator, String> {
        @Override
        public String getPath() {
            return "";
        }
    
        @Override
        public String getValue(ArgumentValidator object) {
            String retVal = "";
            switch (object.getType()) {
                case Regex:
                    // FIXME: CORE-4632
                    Splittable regexSplittable = object.getParams().get(0);
                    String regex;
                    if (regexSplittable.isNumber()) {
                        Double asNumber = regexSplittable.asNumber();
                        regex = String.valueOf(asNumber.intValue());
                    } else {
                        regex = regexSplittable.asString();
                    }
                    retVal = avMessages.regex(SafeHtmlUtils.fromString(regex).asString());
                    break;
                case CharacterLimit:
                    int charLimit = Double.valueOf(object.getParams().get(0).asNumber()).intValue();
                    retVal = avMessages.characterLimit(charLimit);
                    break;

                case IntAbove:
                    int intAbove = Double.valueOf(object.getParams().get(0).asNumber()).intValue();
                    retVal = avMessages.intAbove(intAbove);
                    break;

                case IntBelow:
                    int intBelow = Double.valueOf(object.getParams().get(0).asNumber()).intValue();
                    retVal = avMessages.intBelow(intBelow);
                    break;
                case IntRange:
                    int intRangeAbove = Double.valueOf(object.getParams().get(0).asNumber()).intValue();
                    int intRangeBelow = Double.valueOf(object.getParams().get(1).asNumber()).intValue();
                    retVal = avMessages.intRange(intRangeAbove, intRangeBelow);
                    break;

                case DoubleAbove:
                    double dblAbove = Double.valueOf(object.getParams().get(0).asNumber());
                    retVal = avMessages.dblAbove(dblAbove);
                    break;
                case DoubleBelow:
                    double dblBelow = Double.valueOf(object.getParams().get(0).asNumber());
                    retVal = avMessages.dblBelow(dblBelow);
                    break;

                case DoubleRange:
                    double dblRangeAbove = Double.valueOf(object.getParams().get(0).asNumber());
                    double dblRangeBelow = Double.valueOf(object.getParams().get(1).asNumber());
                    retVal = avMessages.dblRange(dblRangeAbove, dblRangeBelow);
                    break;

                default:
                    retVal = object.getType().name();
                    break;
            }
            return retVal;
        }
    
        @Override
        public void setValue(ArgumentValidator object, String value) {/* Do Nothing */}
    }

    private static MyUiBinder BINDER = GWT.create(MyUiBinder.class);

    @Ignore
    @UiField
    TextButton add;

    @Ignore
    @UiField
    TextButton delete;

    @Ignore
    @UiField
    TextButton edit;

    @UiField
    Grid<ArgumentValidator> grid;

    @UiField
    FieldLabel validatorEditorLabel;

    // The Editor for Argument.getValidators()
    ListStoreEditor<ArgumentValidator> validators;

    @UiField
    ListStore<ArgumentValidator> validatorStore;


    private final ArgumentValidatorMessages avMessages;

    private Argument model;

    private final Set<ArgumentValidatorType> supportedValidatorTypes;

    @Inject
    public ArgumentValidatorEditor(AppTemplateWizardAppearance appearance, ArgumentValidatorMessages avMessages) {
        this.avMessages = avMessages;
        initWidget(BINDER.createAndBindUi(this));
        grid.setHeight(300);

        validatorEditorLabel.setHTML(appearance.createContextualHelpLabel(appearance.getPropertyPanelLabels().validatorRulesLabel(), appearance.getContextHelpMessages().textInputValidationRules()));
        // Add selection handler to grid to control enabled state of "edit" and "delete" buttons.
        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<ArgumentValidator>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<ArgumentValidator> event) {
                if ((event.getSelection() == null) || event.getSelection().isEmpty()) {
                    edit.setEnabled(false);
                    delete.setEnabled(false);
                } else if ((event.getSelection() != null) && (event.getSelection().size() == 1)) {
                    edit.setEnabled(true);
                    delete.setEnabled(true);
                } else if ((event.getSelection() != null) && (event.getSelection().size() > 1)) {
                    edit.setEnabled(false);
                    delete.setEnabled(true);
                }
            }
        });
        validators = new ListStoreEditor<ArgumentValidator>(validatorStore);
        supportedValidatorTypes = Sets.newHashSet();
        QuickTip quickTip = new QuickTip(validatorEditorLabel);
        quickTip.getToolTipConfig().setDismissDelay(0);

    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ArgumentValidator>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void flush() {
        // JDS Shouldn't have to flush here, but we will see.
    }

    @Override
    public void onPropertyChange(String... arg0) {/* Do Nothing */}

    @Override
    public void setDelegate(EditorDelegate<Argument> arg0) {/* Do Nothing */}

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.editor.client.ValueAwareEditor#setValue(java.lang.Object)
     * 
     * The purpose of this method is:
     * 1. To determine if this control should be shown at all
     * 2. If it is to be shown, to create the list of available validators for
     * the 'add validator' popup.
     */
    @Override
    public void setValue(Argument value) {
        if ((value == null) || !AppTemplateUtils.typeSupportsValidators(value.getType())) {
            return;
        }

        if (model == null) {
            if (value.getValidators() == null) {
                value.setValidators(Lists.<ArgumentValidator> newArrayList());
            }
            // Selectively instantiate the sub editor based on argument type
            // Set supported validator types here.
            switch (value.getType()) {
                case Double:
                    supportedValidatorTypes.add(ArgumentValidatorType.DoubleAbove);
                    supportedValidatorTypes.add(ArgumentValidatorType.DoubleBelow);
                    supportedValidatorTypes.add(ArgumentValidatorType.DoubleRange);
                    break;

                case Integer:
                    supportedValidatorTypes.add(ArgumentValidatorType.IntAbove);
                    supportedValidatorTypes.add(ArgumentValidatorType.IntBelow);
                    supportedValidatorTypes.add(ArgumentValidatorType.IntRange);
                    break;

                case Text:
                    supportedValidatorTypes.add(ArgumentValidatorType.CharacterLimit);
                    supportedValidatorTypes.add(ArgumentValidatorType.Regex);
                    break;

                default:
                    // The current argument is not a valid type for this control.
                    // So, disable and hide ourself so the user doesn't see it.
                    validatorEditorLabel.disable();
                    validatorEditorLabel.setVisible(false);
                    break;
            }
        }

        this.model = value;
    }

    @UiFactory
    ColumnModel<ArgumentValidator> createColumnModel() {
        ColumnConfig<ArgumentValidator, String> nameCol = new ColumnConfig<ArgumentValidator, String>(new ValidatorValueProvider(), 50, "Validation Rules");
        List<ColumnConfig<ArgumentValidator, ?>> list = Lists.newArrayList();
        list.add(nameCol);
        return new ColumnModel<ArgumentValidator>(list);
    }

    @UiFactory
    ListStore<ArgumentValidator> createListStore() {
        return new ListStore<ArgumentValidator>(new ModelKeyProvider<ArgumentValidator>() {
            @Override
            public String getKey(ArgumentValidator item) {
                return item.getId();
            }
        });
    }

    @UiHandler("add")
    void onAddButtonSelected(@SuppressWarnings("unused") SelectEvent event) {
       final  AddValidatorDialog dlg = new AddValidatorDialog(supportedValidatorTypes, avMessages);
        dlg.addOkButtonSelectHandler(new AddValidatorOkBtnSelectHndlr(dlg));
        dlg.addCancelButtonSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                //do nothing. just hide
                dlg.hide();
            }
        });
        dlg.show();
    }

    @UiHandler("delete")
    void onDeleteButtonSelected(@SuppressWarnings("unused") SelectEvent event) {
        List<ArgumentValidator> selection = grid.getSelectionModel().getSelection();
        if (selection == null) {
            return;
        }
        for (ArgumentValidator av : selection) {
            validatorStore.remove(av);
        }
        ValueChangeEvent.fire(this, selection);
    }

    @UiHandler("edit")
    void onEditButtonSelected(@SuppressWarnings("unused") SelectEvent event) {
        final ArgumentValidator selectedItem = grid.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        // JDS Remove
        final int selectedItemIndex = validators.getStore().indexOf(selectedItem);
        validators.getStore().remove(selectedItem);
        ValueChangeEvent.fire(this, Lists.newArrayList(selectedItem));
        AddValidatorDialog dlg = new AddValidatorDialog(supportedValidatorTypes, avMessages);
        dlg.addOkButtonSelectHandler(new AddValidatorOkBtnSelectHndlr(dlg));
        dlg.addCancelButtonSelectHandler(new AddValidatorCancelBtnSelectHandler(selectedItemIndex, selectedItem));

        dlg.setArgumentValidator(selectedItem);
        dlg.show();
    }

}
