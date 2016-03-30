package org.iplantc.de.admin.desktop.client.metadata.view;

import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView.Presenter;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.apps.widgets.client.view.editors.widgets.CheckBoxAdapter;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.TemplateAttributeSelectionItem;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.AbstractGridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMetadataTemplateViewImpl extends Composite implements IsWidget, EditMetadataTemplateView {

    private static EditMetadataTemplateViewImplUiBinder uiBinder = GWT.create(EditMetadataTemplateViewImplUiBinder.class);

    interface EditMetadataTemplateViewImplUiBinder extends
                                                  UiBinder<Widget, EditMetadataTemplateViewImpl> {
    }

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
    @UiField CheckBoxAdapter chkDeleted;

    private final MetadataTemplateAttributeProperties mta_props;
    private Presenter presenter;
    private final DiskResourceAutoBeanFactory drFac;
    private GridEditing<MetadataTemplateAttribute> editing;
    private String templateId; // cache id when editing existing template

    ColumnConfig<MetadataTemplateAttribute, String> nameCol;
    ColumnConfig<MetadataTemplateAttribute, Boolean> reqCol;
    ColumnConfig<MetadataTemplateAttribute, String> descCol;
    ColumnConfig<MetadataTemplateAttribute, String> typeCol;
    ColumnConfig<MetadataTemplateAttribute, String> valuesCol;
    private final TemplateAttributeSelectionItemProperties tasi_props;
    @UiField(provided = true)
    EditMetadataTemplateViewAppearance appearance;

    @Inject
    public EditMetadataTemplateViewImpl(final MetadataTemplateAttributeProperties mta_props,
                                        final TemplateAttributeSelectionItemProperties tasi_props,
                                        final DiskResourceAutoBeanFactory factory,
                                        final EditMetadataTemplateViewAppearance appearance) {
        this.mta_props = mta_props;
        this.tasi_props = tasi_props;
        this.drFac = factory;
        this.appearance = appearance;
        initWidget(uiBinder.createAndBindUi(this));
        createGridEditing();
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        chkDeleted.setText("Mark As Deleted");
        new GridDragSource<>(grid);
        GridDropTarget<MetadataTemplateAttribute> tar = new GridDropTarget<>(grid);
        tar.setAllowSelfAsSource(true);
        tar.setFeedback(Feedback.INSERT);
        tar.setOperation(Operation.MOVE);

        ensureDebugId(Belphegor.MetadataIds.EDIT_DIALOG + Belphegor.MetadataIds.VIEW);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        tempName.setId(baseID + Belphegor.MetadataIds.TEMPLATE_NAME);
        addBtn.ensureDebugId(baseID + Belphegor.MetadataIds.ADD);
        delBtn.ensureDebugId(baseID + Belphegor.MetadataIds.DELETE);
        grid.ensureDebugId(baseID + Belphegor.MetadataIds.GRID);
        chkDeleted.getCheckBox().ensureDebugId(baseID + Belphegor.MetadataIds.CHECK_DELETED);
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
        nameCol = new ColumnConfig<>(mta_props.name(), 100, "Name");
        reqCol = new ColumnConfig<>(mta_props.required(), 100, "Required");
        descCol = new ColumnConfig<>(mta_props.description(), 100, "Description");
        typeCol = new ColumnConfig<>(mta_props.type(), 100, "Type");

        valuesCol = new ColumnConfig<>(new ValueProvider<MetadataTemplateAttribute, String>() {

            @Override
            public String getValue(MetadataTemplateAttribute object) {
                return "Enum value(s)";
            }

            @Override
            public void setValue(MetadataTemplateAttribute object, String value) {
                // do nothing intentionally
            }

            @Override
            public String getPath() {
                return "values";
            }
        }, 100, "Value(s)");

        SafeStyles btnPaddingStyle = SafeStylesUtils.fromTrustedString("padding: 1px 3px 0;");
        TextButtonCell button = buildValueEditButtonCell();
        valuesCol.setCell(button);
        valuesCol.setColumnTextClassName(CommonStyles.get().inlineBlock());
        valuesCol.setColumnTextStyle(btnPaddingStyle);

        reqCol.setCell(new SimpleSafeHtmlCell<Boolean>(new AbstractSafeHtmlRenderer<Boolean>() {
            @Override
            public SafeHtml render(Boolean object) {
                return SafeHtmlUtils.fromString(object ? "true" : "false");
            }
        }));

        List<ColumnConfig<MetadataTemplateAttribute, ?>> columns = new ArrayList<ColumnConfig<MetadataTemplateAttribute, ?>>();
        columns.add(nameCol);
        columns.add(descCol);
        columns.add(reqCol);
        columns.add(typeCol);
        columns.add(valuesCol);

        return new ColumnModel<>(columns);
    }

    private TextButtonCell buildValueEditButtonCell() {
        TextButtonCell button = new TextButtonCell();
        button.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Context c = event.getContext();
                int row = c.getIndex();
                MetadataTemplateAttribute mta = store.get(row);
                editEnumValues(mta);
            }
        });
        return button;
    }

    private void editEnumValues(final MetadataTemplateAttribute mta) {
        final EnumValuesEditor eve = new EnumValuesEditor(mta);
        final IPlantDialog ipd = new IPlantDialog();
        ipd.setSize("500px", "300px");
        ipd.add(eve.asWidget());
        ipd.setHeadingText("Edit Enum Values");
        ipd.show();
        ipd.setHideOnButtonClick(false);
        ipd.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (eve.validateEnumValues()) {
                    mta.setValues(eve.get());
                    ipd.hide();
                } else {
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.enumError()));
                }
            }
        });
        ipd.addCancelButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                ipd.hide();
            }
        });

    }

    protected void createGridEditing() {
        editing = new GridRowEditing<MetadataTemplateAttribute>(grid);
        ((AbstractGridEditing<MetadataTemplateAttribute>)editing).setClicksToEdit(ClicksToEdit.TWO);
        editing.addEditor(nameCol, new TextField());
        editing.addEditor(descCol, new TextArea());
        editing.addEditor(reqCol, new CheckBox());
        editing.addEditor(typeCol, setUpTypeEditing());
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
        typeCombo.setEditable(false);
        typeCombo.setAllowBlank(false);
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
        mta.setName("Attribute" + Math.random());
        mta.setRequired(false);
        mta.setDescription("Test");
        mta.setType("String");

        editing.cancelEditing();
        store.add(0, mta);
        int row = store.indexOf(mta);
        editing.startEditing(new GridCell(row, 0));
    }

    @UiHandler("delBtn")
    void delButtonClicked(SelectEvent event) {
        store.remove(grid.getSelectionModel().getSelectedItem());
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @Override
    public MetadataTemplate getTemplate() {
        MetadataTemplate mt = drFac.metadataTemplate().as();
        if (!Strings.isNullOrEmpty(templateId)) {
            mt.setId(templateId);
        }
        mt.setName(tempName.getValue());
        mt.setAttributes(store.getAll());
        mt.setDeleted(chkDeleted.getValue());
        return mt;
    }

    @Override
    public boolean validate() {
        store.commitChanges();
        boolean valid = tempName.validate();
        for (MetadataTemplateAttribute mta : store.getAll()) {
            if (Strings.isNullOrEmpty(mta.getName()) || Strings.isNullOrEmpty(mta.getType())) {
                return false;
            }
            List<TemplateAttributeSelectionItem> items = mta.getValues();
            if (mta.getType().equals("Enum")) {
                if (items == null || items.size() == 0) {
                    return false;
                }
            } else {
                // allow values only for enum type
                if (items != null && items.size() > 0) {
                    return false;
                }
            }

        }
        return valid;
    }

    @Override
    public void edit(MetadataTemplate result) {
        tempName.setValue(result.getName());
        templateId = result.getId();
        store.addAll(result.getAttributes());
        chkDeleted.setValue(result.isDeleted());
    }

    @Override
    public void reset() {
        tempName.clear();
        store.clear();
        templateId = null;
    }

    private class EnumValuesEditor implements IsWidget {

        private final MetadataTemplateAttribute mta;
        private ListStore<TemplateAttributeSelectionItem> enum_store;
        private ColumnModel<TemplateAttributeSelectionItem> enum_cm;
        private GridRowEditing<TemplateAttributeSelectionItem> editEnumVal;
        private Grid<TemplateAttributeSelectionItem> enum_grid;
        private ToolBar toolBar;
        private VerticalLayoutContainer verticalLayoutContainer;

        EnumValuesEditor(MetadataTemplateAttribute mta) {
            this.mta = mta;
            build();
        }

        private void init() {
            enum_store = new ListStore<>(new ModelKeyProvider<TemplateAttributeSelectionItem>() {

                @Override
                public String getKey(TemplateAttributeSelectionItem item) {
                    return item.getValue();
                }
            });
            if (mta != null && mta.getValues() != null && mta.getValues().size() > 0) {
                enum_store.addAll(mta.getValues());
            }

            ColumnConfig<TemplateAttributeSelectionItem, String> valCol = new ColumnConfig<>(tasi_props.value(),
                                                                                             250,
                                                                                             appearance.valColumn());
            ColumnConfig<TemplateAttributeSelectionItem, Boolean> defCol = new ColumnConfig<>(tasi_props.defaultValue(),
                                                                                              100,
                                                                                              appearance.defColumn());
            ArrayList<ColumnConfig<TemplateAttributeSelectionItem, ?>> cmList = new ArrayList<>();
            cmList.add(valCol);
            cmList.add(defCol);
            defCol.setCell(new SimpleSafeHtmlCell<Boolean>(new AbstractSafeHtmlRenderer<Boolean>() {
                @Override
                public SafeHtml render(Boolean object) {
                    return SafeHtmlUtils.fromString(object ? "true" : "false");
                }
            }));
            enum_cm = new ColumnModel<TemplateAttributeSelectionItem>(cmList);
            enum_grid = new Grid<>(enum_store, enum_cm);
            editEnumVal = new GridRowEditing<>(enum_grid);
            editEnumVal.setClicksToEdit(ClicksToEdit.TWO);
            editEnumVal.addEditor(valCol, new TextField());
            editEnumVal.addEditor(defCol, new CheckBox());
        }

        private void buildToolbar() {
            toolBar = new ToolBar();
            TextButton addButton = new TextButton(appearance.addBtn());
            addButton.addSelectHandler(new SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    TemplateAttributeSelectionItem item = drFac.templateAttributeSelectionItem().as();
                    item.setValue("value" + Math.random());
                    item.setDefaultValue(false);
                    editEnumVal.cancelEditing();
                    enum_store.add(0, item);
                    int row = enum_store.indexOf(item);
                    editEnumVal.startEditing(new GridCell(row, 0));
                }
            });
            TextButton delButton = new TextButton(appearance.delBtn());
            delButton.addSelectHandler(new SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    enum_store.remove(enum_store.indexOf(enum_grid.getSelectionModel().getSelectedItem()));
                }
            });
            toolBar.add(addButton);
            toolBar.add(delButton);
        }

        private void build() {
            init();
            buildToolbar();
            verticalLayoutContainer = new VerticalLayoutContainer();
            verticalLayoutContainer.setBorders(true);
            verticalLayoutContainer.add(toolBar, new VerticalLayoutData(1, -1));
            verticalLayoutContainer.add(enum_grid, new VerticalLayoutData(1, 1));
        }

        @Override
        public Widget asWidget() {
            return verticalLayoutContainer;
        }

        public boolean validateEnumValues() {
            enum_store.commitChanges();
            boolean found = false;
            for (TemplateAttributeSelectionItem item : enum_store.getAll()) {
                if (item.isDefaultValue()) {
                    if (found) {
                        return false;
                    } else {
                        found = true;
                    }
                }
            }

            return true;
        }

        public List<TemplateAttributeSelectionItem> get() {
            return enum_store.getAll();
        }

    }

}
