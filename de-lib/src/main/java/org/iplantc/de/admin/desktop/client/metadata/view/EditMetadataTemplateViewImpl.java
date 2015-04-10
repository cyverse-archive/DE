package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView.Presenter;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.TemplateAttributeSelectionItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMetadataTemplateViewImpl implements IsWidget, EditMetadataTemplateView {

    private static EditMetadataTemplateViewImplUiBinder uiBinder = GWT.create(EditMetadataTemplateViewImplUiBinder.class);

    interface EditMetadataTemplateViewImplUiBinder extends
                                                  UiBinder<Widget, EditMetadataTemplateViewImpl> {
    }

    private final Widget widget;
    @UiField
    TextField tempName;
    @UiField
    TextButton addBtn, delBtn;
    @UiField
    Grid<MetadataTemplateAttribute> grid;
    @UiField
    ListStore<MetadataTemplateAttribute> store;
    @UiField
    ColumnModel<MetadataTemplateAttribute> cm;
    @UiField
    VerticalLayoutContainer con;
    private final MetadataTemplateAttributeProperties mta_props;
    private Presenter presenter;
    private final DiskResourceAutoBeanFactory drFac;
    private GridEditing<MetadataTemplateAttribute> editing;

    @Inject
    public EditMetadataTemplateViewImpl(final MetadataTemplateAttributeProperties mta_props,
                                        final DiskResourceAutoBeanFactory factory) {
        this.mta_props = mta_props;
        this.drFac = factory;
        widget = uiBinder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiFactory
    ListStore<MetadataTemplateAttribute> createListStore() {
        final ListStore<MetadataTemplateAttribute> listStore = new ListStore<>(new ModelKeyProvider<MetadataTemplateAttribute>() {

            @Override
            public String getKey(MetadataTemplateAttribute item) {
                return item.getName();
            }
        });
        listStore.setEnableFilters(true);
        return listStore;
    }

    @UiFactory
    ColumnModel<MetadataTemplateAttribute> createColumnModel() {
        ColumnConfig<MetadataTemplateAttribute, String> nameCol = new ColumnConfig<>(mta_props.name(),
                                                                                     100,
                                                                                     "Name");
        ColumnConfig<MetadataTemplateAttribute, Boolean> reqCol = new ColumnConfig<>(mta_props.required(),
                                                                                     100,
                                                                                     "Required");
        ColumnConfig<MetadataTemplateAttribute, String> descCol = new ColumnConfig<>(mta_props.description(),
                                                                                     100,
                                                                                     "Description");
        ColumnConfig<MetadataTemplateAttribute, String> typeCol = new ColumnConfig<>(mta_props.type(),
                                                                                     100,
                                                                                     "Type");

        ColumnConfig<MetadataTemplateAttribute, String> valuesCol = new ColumnConfig<>(new ValueProvider<MetadataTemplateAttribute, String>() {

                                                                                           @Override
                                                                                           public String
                                                                                                   getValue(MetadataTemplateAttribute object) {
                                                                                               if (object != null) {
                                                                                                   StringBuilder sb = new StringBuilder();
                                                                                                   List<TemplateAttributeSelectionItem> values = object.getValues();
                                                                                                   if (values != null) {
                                                                                                       for (TemplateAttributeSelectionItem si : values) {
                                                                                                           sb.append(si.getValue());
                                                                                                           sb.append(",");
                                                                                                       }
                                                                                                       if (sb.charAt(sb.length()) == (',')) {
                                                                                                           sb.deleteCharAt(sb.length());
                                                                                                       }

                                                                                                       return sb.toString();
                                                                                                   } else {
                                                                                                       return null;
                                                                                                   }
                                                                                               } else {
                                                                                                   return null;
                                                                                               }

                                                                                           }

                                                                                           @Override
                                                                                           public void
                                                                                                   setValue(MetadataTemplateAttribute object,
                                                                                                            String value) {
                                                                                               if (object != null) {
                                                                                                   String[] tokens = value.split(",");
                                                                                                   List<TemplateAttributeSelectionItem> itemList = new ArrayList<>();
                                                                                                   if (itemList != null) {
                                                                                                       for (String t : tokens) {
                                                                                                           TemplateAttributeSelectionItem si = drFac.templateAttributeSelectionItem()
                                                                                                                                                    .as();
                                                                                                           si.setId("");
                                                                                                           si.setValue(t);
                                                                                                           itemList.add(si);

                                                                                                       }
                                                                                                       object.setValues(itemList);
                                                                                                   }
                                                                                               }

                                                                                           }

                                                                                           @Override
                                                                                           public String
                                                                                                   getPath() {
                                                                                               // TODO
                                                                                               // Auto-generated
                                                                                               // method
                                                                                               // stub
                                                                                               return null;
                                                                                           }
                                                                                       },
                                                                                       100,
                                                                                       "Value(s)");

        List<ColumnConfig<MetadataTemplateAttribute, ?>> columns = new ArrayList<ColumnConfig<MetadataTemplateAttribute, ?>>();
        columns.add(nameCol);
        columns.add(descCol);
        columns.add(reqCol);
        columns.add(typeCol);
        columns.add(valuesCol);
        editing = createGridEditing();
        editing.addEditor(nameCol, new TextField());
        editing.addEditor(descCol, new TextArea());
        editing.addEditor(reqCol, new CheckBox());
        editing.addEditor(typeCol, setUpTypeEditing());
        editing.addEditor(valuesCol, new TextField());

        return new ColumnModel<>(columns);
    }

    protected GridEditing<MetadataTemplateAttribute> createGridEditing() {
        GridRowEditing<MetadataTemplateAttribute> rowEditing = new GridRowEditing<MetadataTemplateAttribute>(grid);
        return rowEditing;
    }

    private SimpleComboBox<String> setUpTypeEditing() {
        SimpleComboBox<String> typeCombo = new SimpleComboBox<String>(new StringLabelProvider<String>());
        typeCombo.setClearValueOnParseError(true);
        typeCombo.setPropertyEditor(new PropertyEditor<String>() {
            @Override
            public String parse(CharSequence text) throws ParseException {
                return text.toString();
            }

            @Override
            public String render(String object) {
                return object.toString();
            }
        });
        typeCombo.setTriggerAction(TriggerAction.ALL);
        typeCombo.add(Arrays.asList("String",
                                    "Timestamp",
                                    "Boolean",
                                    "Number",
                                    "Integer",
                                    "Multiline text",
                                    "URL/URI",
                                    "Enum"));
        typeCombo.setValue("String");
        return typeCombo;

    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);

    }

    @Override
    public void unmask() {
        con.unmask();
    }

    @UiHandler("addBtn")
    void addButtonClicked(SelectEvent event) {
        MetadataTemplateAttribute mta = drFac.metadataTemplateAttribute().as();
        mta.setName("Attribute" + store.getAll().size());
        store.add(0, mta);
        int row = store.indexOf(mta);
        editing.startEditing(new GridCell(row, 0));
    }

    @UiHandler("delBtn")
    void delButtonClicked(SelectEvent event) {

    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

}
