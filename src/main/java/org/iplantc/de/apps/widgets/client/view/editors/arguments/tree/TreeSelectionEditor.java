package org.iplantc.de.apps.widgets.client.view.editors.arguments.tree;

import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent;
import org.iplantc.de.apps.widgets.client.events.ArgumentRequiredChangedEvent.ArgumentRequiredChangedEventHandler;
import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent;
import org.iplantc.de.apps.widgets.client.view.AppTemplateForm;
import org.iplantc.de.apps.widgets.client.view.editors.SelectionItemProperties;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.LabelLeafEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.VisibilityEditor;
import org.iplantc.de.apps.widgets.client.view.editors.arguments.converters.ArgumentEditorConverter;
import org.iplantc.de.apps.widgets.client.view.editors.style.AppTemplateWizardAppearance;
import org.iplantc.de.apps.widgets.client.view.util.SelectionItemTreeStoreEditor;
import org.iplantc.de.client.models.apps.integration.AppTemplateAutoBeanFactory;
import org.iplantc.de.client.models.apps.integration.Argument;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.apps.integration.ArgumentValidator;
import org.iplantc.de.client.models.apps.integration.SelectionItem;
import org.iplantc.de.client.models.apps.integration.SelectionItemGroup;
import org.iplantc.de.client.models.apps.integration.SelectionItemList;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import static com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign.TOP;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckCascade;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;

import java.util.List;

/**
 * A Panel that contains a SelectionItemTree and a filtering field that filters the tree items.
 *
 * @author psarando
 * 
 */
public class TreeSelectionEditor extends Composite implements AppTemplateForm.ArgumentEditor, HasValueChangeHandlers<List<SelectionItem>> {

    /**
     * Restores the CheckState of the tree, and expand any Group that is checked or has checked children.
     * This handler must be added after the store is added to the tree, since the tree adds its own
     * handlers that must trigger before this one.
     * 
     * @author jstroot
     * 
     */
    private final class MyStoreFilterHandler implements StoreFilterHandler<SelectionItem> {
        @Override
        public void onFilter(StoreFilterEvent<SelectionItem> event) {
            TreeStore<SelectionItem> treeStore = tree.getStore();

            for (SelectionItem ruleArg : treeStore.getAll()) {
                if (ruleArg.isDefault()) {
                    tree.setChecked(ruleArg, CheckState.CHECKED);
                }
            }

            boolean filtered = treeStore.isFiltered();

            for (SelectionItem ruleArg : treeStore.getAll()) {
                // Ensure any groups with filtered-out children still display the group icon.
                if (ruleArg instanceof SelectionItemGroup) {
                    tree.setLeaf(ruleArg, false);
                    tree.refresh(ruleArg);
                }

                if (!filtered && ruleArg.isDefault()) {
                    // Restore the tree's expanded state when the filter is cleared.
                    tree.setExpanded(ruleArg, true);
                }
            }
        }
    }

    private final class MyTreeStoreEditor extends SelectionItemTreeStoreEditor {

        private MyTreeStoreEditor(TreeStore<SelectionItem> store, HasValueChangeHandlers<List<SelectionItem>> valueChangeTarget) {
            super(store, valueChangeTarget);
        }

        @Override
        protected CheckCascade getCheckStyle() {
            return tree.getCheckStyle();
        }

        @Override
        protected boolean getSingleSelect() {
            return tree.getSelectionModel().getSelectionMode().equals(SelectionMode.SINGLE);
        }

        @Override
        protected void setCheckStyle(CheckCascade treeCheckCascade) {
            if (treeCheckCascade == null) {
                return;
            }

            tree.setCheckStyle(CheckCascade.valueOf(treeCheckCascade.name()));
        }

        @Override
        protected void setItems(SelectionItemGroup root) {
            tree.setItems(root);
        }

        @Override
        protected void setSingleSelect(boolean singleSelect) {
            if (singleSelect) {
                tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            } else {
                tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
            }
        }

        @Override
        protected boolean shouldFlush() {
            return true;
        }
    }

    private final class TreeValueChangeHandler implements ValueChangeHandler<List<SelectionItem>> {
        @Override
        public void onValueChange(ValueChangeEvent<List<SelectionItem>> event) {
            if (!selectionItemsEditor.isSuppressEvent()) {
                ValueChangeEvent.fire(TreeSelectionEditor.this, Lists.<SelectionItem> newArrayList(selectionItemsEditor.getCurrentTree()));
            }
        }
    }


    private final FieldLabel argumentLabel;

    private EditorDelegate<Argument> delegate;

    private final LabelLeafEditor<String> descriptionEditor;

    private boolean disableOnNotVisible = false;

    private final SimpleEditor<String> idEditor;

    private final LabelLeafEditor<String> labelLeafEditor;


    private boolean labelOnlyEditMode = false;

    private Argument model;

    private final LabelLeafEditor<Boolean> requiredEditor;

    private final SelectionItemTreeStoreEditor selectionItemsEditor;

    private final SelectionItemTree tree;

    private final SimpleEditor<ArgumentType> typeEditor;

    private final VisibilityEditor visibilityEditor;

    public TreeSelectionEditor(AppTemplateWizardAppearance appearance, SelectionItemProperties props) {
        TreeStore<SelectionItem> store = new TreeStore<SelectionItem>(props.id());

        tree = new SelectionItemTree(store, props.display());
//        if (presenter.isEditingMode()) {
//            /*
//             * KLUDGE CORE-4653 JDS Set selection model to locked. This is to prevent the user from
//             * making any selections due to the issue described in CORE-4653.
//             */
//            tree.getSelectionModel().setLocked(true);
//            tree.setCore4653Kludge();
//        }
        tree.setHeight(appearance.getDefaultTreeSelectionHeight());

        tree.addValueChangeHandler(new TreeValueChangeHandler());
        selectionItemsEditor = new MyTreeStoreEditor(store, this);

        // This handler must be added after the store is added to the tree, since the tree adds its own
        // handlers that must trigger before this one.
        // Restore the tree's Checked state from each item's isDefault field after it's filtered.
        store.addStoreFilterHandler(new MyStoreFilterHandler());

        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        vlc.add(buildFilter(store), new VerticalLayoutData(1, -1));
        vlc.add(tree);

        argumentLabel = new FieldLabel(vlc);
        argumentLabel.setLabelAlign(TOP);
        argumentLabel.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                TreeSelectionEditor.this.fireEvent(new ArgumentSelectedEvent(model));
            }
        }, ClickEvent.getType());

        labelLeafEditor = new LabelLeafEditor<String>(argumentLabel, this, appearance);
        idEditor = SimpleEditor.<String> of();
        typeEditor = SimpleEditor.<ArgumentType> of();
        requiredEditor = new LabelLeafEditor<Boolean>(argumentLabel, this, appearance);
        descriptionEditor = new LabelLeafEditor<String>(argumentLabel, this, appearance);

        initWidget(argumentLabel);
        visibilityEditor = new VisibilityEditor(this);
    }

    @Override
    public HandlerRegistration addArgumentRequiredChangedEventHandler(ArgumentRequiredChangedEventHandler handler) {
        return addHandler(handler, ArgumentRequiredChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addArgumentSelectedEventHandler(ArgumentSelectedEvent.ArgumentSelectedEventHandler handler) {
        return addHandler(handler, ArgumentSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<SelectionItem>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public LeafValueEditor<String> descriptionEditor() {
        return descriptionEditor;
    }

    @Override
    public void disableOnNotVisible() {
        this.disableOnNotVisible = true;
    }

    @Override
    public void disableValidations() {
        // Tree selector does not currently support validations
    }

    @Override
    public void flush() {
        selectionItemsEditor.flush();
    }

    @Override
    public EditorDelegate<Argument> getEditorDelegate() {
        return delegate;
    }

    @Override
    public LeafValueEditor<String> idEditor() {
        return idEditor;
    }

    @Override
    public boolean isDisabledOnNotVisible() {
        return disableOnNotVisible;
    }

    @Override
    public boolean isLabelOnlyEditMode() {
        return labelOnlyEditMode;
    }

    @Override
    public boolean isValidationDisabled() {
        return false;
    }

    @Override
    public LeafValueEditor<String> labelEditor() {
        return labelLeafEditor;
    }

    @Override
    public void onPropertyChange(String... paths) {/* Do Nothing */}

    @Override
    public LeafValueEditor<Boolean> requiredEditor() {
        return requiredEditor;
    }

    @Override
    public ValueAwareEditor<List<SelectionItem>> selectionItemsEditor() {
        return selectionItemsEditor;
    }

    @Override
    public void setDelegate(EditorDelegate<Argument> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tree.setEnabled(enabled);
        tree.getSelectionModel().setLocked(!enabled);
    }

    @Override
    public void setLabelOnlyEditMode(boolean labelOnlyEditMode) {
        this.labelOnlyEditMode = labelOnlyEditMode;
    }

    /**
     * @param selection a splittable which this function assumes is a list of {@link SelectionItem}s
     */
    public void setSelection(Splittable selection) {
        selectionItemsEditor.setSuppressEvent(true);
        AppTemplateAutoBeanFactory factory = GWT.create(AppTemplateAutoBeanFactory.class);
        SelectionItemList siList = AutoBeanCodex.decode(factory, SelectionItemList.class, "{\"selectionItems\": " + selection.getPayload() + "}").as();
        List<SelectionItem> items = siList.getSelectionItems();
        tree.setSelection(items);
        selectionItemsEditor.setSuppressEvent(false);
    }

    @Override
    public void setValue(Argument value) {
        if ((value == null) || !value.getType().equals(ArgumentType.TreeSelection)) {
            return;
        }
        List<SelectionItem> toBeRemoved = AutoBeanUtils.getAutoBean(value).getTag(SelectionItem.TO_BE_REMOVED);
        if (toBeRemoved != null) {
            selectionItemsEditor.setSuppressEvent(true);
            for (SelectionItem si : toBeRemoved) {
                tree.getStore().remove(si);
            }
            selectionItemsEditor.setSuppressEvent(false);
        }
        AutoBeanUtils.getAutoBean(value).setTag(SelectionItem.TO_BE_REMOVED, null);
        this.model = value;
        boolean restoreSelectionsFromDefaultValue = (value.getDefaultValue() != null) && (value.getDefaultValue().isIndexed()) && (value.getDefaultValue().size() > 0);
        tree.setRestoreCheckedSelectionFromTree(!restoreSelectionsFromDefaultValue);
        selectionItemsEditor.setValue(value.getSelectionItems());
        if (restoreSelectionsFromDefaultValue) {
            /*
             * JDS If we're not in editing mode, and there is a list of items in the defaultValue field.
             * This should only happen when an app is being re-launched from the Analysis window, and
             * there were values selected for the Tree.
             */
            Splittable defValCopy = value.getDefaultValue().deepCopy();
            setSelection(defValCopy);
            /*
             * JDS Clear default value so future selections can be made (since they are made through
             * value.getSelectionItems() and not value.defaultValue()
             */
            value.setDefaultValue(null);
        }
    }

    @Override
    public LeafValueEditor<ArgumentType> typeEditor() {
        return typeEditor;
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
        return visibilityEditor;
    }

    private StoreFilterField<SelectionItem> buildFilter(TreeStore<SelectionItem> store) {
        StoreFilterField<SelectionItem> treeFilter = new StoreFilterField<SelectionItem>() {
            @Override
            protected boolean doSelect(Store<SelectionItem> store, SelectionItem parent, SelectionItem item, String filter) {
                String itemDisplay = item.getDisplay().toLowerCase();
                String searchTerm = filter.toLowerCase();

                return (itemDisplay.indexOf(searchTerm) >= 0);
            }

            @Override
            protected void onFilter() {
                super.onFilter();

                tree.unmask();
            }

            @Override
            protected void onKeyUp(Event event) {
                tree.mask();

                super.onKeyUp(event);
            }
        };

        treeFilter.bind(store);
        treeFilter.setEmptyText(I18N.DISPLAY.treeSelectorFilterEmptyText());
        treeFilter.setWidth(250);
        treeFilter.setValidationDelay(750);

        return treeFilter;
    }

}
