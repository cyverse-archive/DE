package org.iplantc.de.diskResource.client.views.metadata;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.model.DiskResourceMetadataProperties;
import org.iplantc.de.diskResource.client.presenters.metadata.MetadataPresenterImpl;
import org.iplantc.de.diskResource.share.DiskResourceModule.MetadataIds;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * FIXME REFACTOR Segregate programmatic view construction to a different UIBinder, class, etc FIXME
 * REFACTOR Factor out an appearance for this class
 *
 * @author jstroot sriram
 */
public class DiskResourceMetadataViewImpl extends Composite implements MetadataView {

    private final class AttributeValidationHandler implements ValidHandler, InvalidHandler {
        public AttributeValidationHandler() {
        }

        @Override
        public void onInvalid(InvalidEvent event) {
            valid = false;
        }

        @Override
        public void onValid(ValidEvent event) {
            valid = true;
        }
    }

    private final class MetadataCell extends AbstractCell<String> {
        @Override
        public void render(Context context, String value, SafeHtmlBuilder sb) {
            appearance.renderMetadataCell(sb, value);
        }
    }

    @UiTemplate("DiskResourceMetadataEditorPanel.ui.xml")
    interface DiskResourceMetadataEditorPanelUiBinder
            extends UiBinder<Widget, DiskResourceMetadataViewImpl> {
    }

    private static final DiskResourceMetadataEditorPanelUiBinder uiBinder =
            GWT.create(DiskResourceMetadataEditorPanelUiBinder.class);

    private final AccordionLayoutAppearance accordionLayoutAppearance =
            GWT.create(AccordionLayoutAppearance.class);

    private boolean dirty;

    @UiField(provided = true)
    final MetadataView.Appearance appearance = GWT.create(MetadataView.Appearance.class);
    @UiField
    TextButton addMetadataButton;
    @UiField
    BorderLayoutContainer con;
    @UiField
    TextButton deleteMetadataButton;
    @UiField
    ToolBar toolbar;
    @UiField
    TextButton selectButton;
    @UiField
    TextButton importButton;
    @UiField
    TextButton editMetadataButton;
    @UiField
    AccordionLayoutContainer alc;
    @UiField(provided = true)
    ContentPanel userMetadataPanel;
    @UiField(provided = true)
    ContentPanel additionalMetadataPanel;
    @UiField
    ListStore<Avu> userMdListStore;
    @UiField
    ListStore<Avu> additionalMdListStore;
    @UiField
    Grid<Avu> additionalMdgrid;
    @UiField
    Grid<Avu> userMdGrid;
    @UiField
    GridView<Avu> uview;
    @UiField
    GridView<Avu> aview;
    @UiField(provided = true)
    ColumnModel<Avu> ucm;
    @UiField(provided = true)
    ColumnModel<Avu> acm;


    private HashSet<Avu> selectedSet;
    private GridRowEditing<Avu> userGridRowEditing;
    private final boolean writable;
    private boolean valid;
    private MetadataView.Presenter presenter;
    private String baseId;
    private CellSelectionModel<Avu> userChxBoxModel;
    private CheckBoxSelectionModel<Avu> addChxBoxModel;

    public DiskResourceMetadataViewImpl(boolean isEditable) {
        selectedSet = new HashSet<>();
        writable = isEditable;
        valid = true;
        userChxBoxModel = new CellSelectionModel<>();
        addChxBoxModel = new CheckBoxSelectionModel<Avu>();
        init();
        initWidget(uiBinder.createAndBindUi(this));
        alc.setActiveWidget(userMetadataPanel);
        importButton.setToolTip(appearance.importMdTooltip());
        addMetadataButton.setEnabled(writable);
        deleteMetadataButton.disable();
        if (writable) {
            initUserMdGridEditor();
            new QuickTip(userMdGrid);
        }
        userMdGrid.setSelectionModel(userChxBoxModel);
        additionalMdgrid.setSelectionModel(addChxBoxModel);
        additionalMdgrid.getSelectionModel()
                        .addSelectionChangedHandler(new DiskResourceAdditionalMetadataSelectionChangedHandler());
    }

    @Override
    public void mask() {
        con.mask(appearance.loadingMask());
    }

    @Override
    public void unmask() {
        con.unmask();
    }


    @Override
    public List<Avu> getAvus() {
        return additionalMdListStore.getAll();
    }

    @Override
    public List<Avu> getUserMetadata() {
        return userMdListStore.getAll();
    }


    @Override
    public void loadMetadata(final List<Avu> metadataList) {
        additionalMdListStore.clear();
        additionalMdListStore.commitChanges();
        for (Avu avu : metadataList) {
            additionalMdListStore.add(presenter.setAvuModelKey(avu));
        }

        additionalMdgrid.getStore().setEnableFilters(true);
    }

    @Override
    public void loadUserMetadata(final List<Avu> metadataList) {
        userMdListStore.clear();
        userMdListStore.commitChanges();

        for (Avu avu : metadataList) {
            userMdListStore.add(presenter.setAvuModelKey(avu));
        }

        userMdGrid.getStore().setEnableFilters(true);
    }

    @Override
    public void setPresenter(Presenter p) {
        this.presenter = p;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        this.baseId = baseID;
        addMetadataButton.ensureDebugId(baseID + MetadataIds.ADD_METADATA);
        editMetadataButton.ensureDebugId(baseID + MetadataIds.EDIT_METADATA);
        deleteMetadataButton.ensureDebugId(baseID + MetadataIds.DELETE_METADATA);
        selectButton.ensureDebugId(baseID + MetadataIds.TEMPLATES);

        setUserMetadataDebugIds();
    }


    @UiHandler("addMetadataButton")
    void onAddMetadataSelected(SelectEvent event) {
        String attr = getUniqueAttrName(appearance.newAttribute(), 0);
        Avu md =
                MetadataPresenterImpl.newMetadata(attr, appearance.newValue(), appearance.newUnit());
        presenter.setAvuModelKey(md);
        userMdListStore.add(0, md);
        userGridRowEditing.startEditing(new GridCell(0, 1));
        userGridRowEditing.getEditor(userMdGrid.getColumnModel().getColumn(1)).validate(false);
    }

    @UiHandler("deleteMetadataButton")
    void onDeleteMetadataSelected(SelectEvent event) {
        for (Avu md : selectedSet) {
          userMdListStore.remove(md);
        }

        //remove deleted items
        selectedSet.clear();
    }
    @UiHandler("editMetadataButton")
    void onEditMetadataSelected(SelectEvent event) {
        int row = uview.findRowIndex(uview.getRow(selectedSet.iterator().next()));
        userGridRowEditing.startEditing(new GridCell(row,1));
        userGridRowEditing.getEditor(userMdGrid.getColumnModel().getColumn(1)).validate(false);
    }

    @UiHandler("selectButton")
    void onSelectButtonSelected(SelectEvent event) {
        presenter.onSelectTemplate();

    }

    @UiHandler("importButton")
    void onImportSelected(SelectEvent event) {
        presenter.onImport(additionalMdgrid.getSelectionModel().getSelectedItems());
    }

    private void setUserMetadataDebugIds() {
        userMetadataPanel.ensureDebugId(baseId + MetadataIds.USER_METADATA);
        getCollapseBtn(userMetadataPanel).ensureDebugId(
                baseId + MetadataIds.USER_METADATA + MetadataIds.USER_METADATA_COLLAPSE);
    }

    void buildUserMetadataPanel() {
        userMetadataPanel = new ContentPanel(accordionLayoutAppearance);
        userMetadataPanel.setSize(appearance.panelWidth(), appearance.panelHeight());
        userMetadataPanel.setCollapsible(true);
        userMetadataPanel.getHeader().addStyleName(ThemeStyles.get().style().borderTop());
        userMetadataPanel.setHeadingHtml(appearance.boldHeader(appearance.userMetadata()));
    }

    void buildAdditionalMetadataPanel() {
        additionalMetadataPanel = new ContentPanel(accordionLayoutAppearance);
        additionalMetadataPanel.setSize(appearance.panelWidth(), appearance.panelHeight());
        additionalMetadataPanel.setCollapsible(true);
        additionalMetadataPanel.getHeader().addStyleName(ThemeStyles.get().style().borderTop());
        additionalMetadataPanel.setHeadingHtml(appearance.boldHeader(appearance.additionalMetadata()));
    }


    void createColumnModel() {
        List<ColumnConfig<Avu, ?>> columns = Lists.newArrayList();
        DiskResourceMetadataProperties props = GWT.create(DiskResourceMetadataProperties.class);

        ColumnConfig<Avu, Boolean> selectColumn = new ColumnConfig<>(new ValueProvider<Avu, Boolean>() {
           @Override
            public Boolean getValue(Avu object) {
                return selectedSet.contains(object);
            }

            @Override
            public void setValue(Avu object, Boolean value) {
               //do nothing
            }

            @Override
            public String getPath() {
                return "SelectCheckBox";
            }
        },30,"");
        ColumnConfig<Avu, String> attributeColumn =
                new ColumnConfig<>(props.attribute(), 150, appearance.attribute());
        ColumnConfig<Avu, String> valueColumn =
                new ColumnConfig<>(props.value(), 150, appearance.paramValue());
        ColumnConfig<Avu, String> unitColumn =
                new ColumnConfig<>(props.unit(), 150, appearance.paramUnit());


        CheckBoxCell checkBoxCell = getCheckBoxCell();

        selectColumn.setCell(checkBoxCell);
        selectColumn.setFixed(true);
        selectColumn.setHideable(false);
        selectColumn.setMenuDisabled(true);
        selectColumn.setResizable(false);
        selectColumn.setSortable(false);

        MetadataCell metadataCell = new MetadataCell();
        attributeColumn.setCell(metadataCell);
        valueColumn.setCell(metadataCell);

        columns.add(selectColumn);
        columns.add(attributeColumn);
        columns.add(valueColumn);
        columns.add(unitColumn);

        List<ColumnConfig<Avu, ?>> userMdCols = Lists.newArrayList();
        userMdCols.addAll(columns);
        ucm = new ColumnModel<>(userMdCols);

        List<ColumnConfig<Avu, ?>> addMdCols = Lists.newArrayList();
        addMdCols.add(addChxBoxModel.getColumn());
        addMdCols.addAll(columns);
        acm = new ColumnModel<>(addMdCols);

    }

    private CheckBoxCell getCheckBoxCell() {
        return new CheckBoxCell() {
            @Override
            protected void onClick(XElement parent, final NativeEvent event) {
                super.onClick(parent, event);
                Avu avu =  userChxBoxModel.getSelectedItem();
                if (!selectedSet.remove(avu)) {
                   selectedSet.add(avu);
                }
                setButtonState();
            }
        };
    }

    @UiFactory
    ListStore<Avu> createAdditionalListStore() {
        return new ListStore<>(new ModelKeyProvider<Avu>() {
            @Override
            public String getKey(Avu item) {
                if (item != null) {
                    final AutoBean<Object> metadataBean = AutoBeanUtils.getAutoBean(item);
                    return metadataBean.getTag(presenter.AVU_BEAN_TAG_MODEL_KEY);
                } else {
                    return ""; //$NON-NLS-1$
                }
            }
        });


    }

    private void setButtonState() {
        boolean deleteEnabled = (selectedSet.size() > 0) && writable;
        boolean editEnabled = (selectedSet.size() == 1) && writable;
        deleteMetadataButton.setEnabled(deleteEnabled);
        editMetadataButton.setEnabled(editEnabled);
    }

    private void expandUserMetadataPanel() {
        if (userMetadataPanel.isCollapsed()) {
            userMetadataPanel.expand();
        }
    }

    private String getUniqueAttrName(String attrName, int i) {
        String retName = i > 0 ? attrName + "_(" + i + ")" : attrName;
        for (Avu md : additionalMdListStore.getAll()) {
            if (md.getAttribute().equals(retName)) {
                return getUniqueAttrName(attrName, ++i);
            }
        }
        return retName;
    }

    private void initUserMdGridEditor() {
        userGridRowEditing = new GridRowEditing<>(userMdGrid);
        userGridRowEditing.setClicksToEdit(ClicksToEdit.TWO);

        ColumnConfig<Avu,Boolean> column0 = userMdGrid.getColumnModel().getColumn(0);
        ColumnConfig<Avu, String> column1 = userMdGrid.getColumnModel().getColumn(1);
        ColumnConfig<Avu, String> column2 = userMdGrid.getColumnModel().getColumn(2);
        ColumnConfig<Avu, String> column3 = userMdGrid.getColumnModel().getColumn(3);

        CheckBox cxb = new CheckBox();
        cxb.setEnabled(false);

        TextField field1 = new TextField();
        TextField field2 = new TextField();
        TextField field3 = new TextField();

        field1.setAutoValidate(true);
        field2.setAutoValidate(true);
        field3.setAutoValidate(true);

        field1.setAllowBlank(false);
        field2.setAllowBlank(false);
        field3.setAllowBlank(true);

        AttributeValidationHandler validationHandler = new AttributeValidationHandler();
        field1.addInvalidHandler(validationHandler);
        field1.addValidHandler(validationHandler);

        userGridRowEditing.addEditor(column0, cxb);
        userGridRowEditing.addEditor(column1, field1);
        userGridRowEditing.addEditor(column2, field2);
        userGridRowEditing.addEditor(column3, field3);
        userGridRowEditing.addCompleteEditHandler(new CompleteEditHandler<Avu>() {

            @Override
            public void onCompleteEdit(CompleteEditEvent<Avu> event) {
                dirty = true;
                userMdListStore.commitChanges();
            }
        });
    }

    private void init() {
        buildAdditionalMetadataPanel();
        buildUserMetadataPanel();
        createColumnModel();
    }

    private Widget getCollapseBtn(ContentPanel panel) {
        return panel.getHeader().getTool(0);
    }

    @Override
    public void updateMetadataFromTemplateView(List<Avu> metadataList) {
        userMdGrid.mask();
        if(userMdListStore.size() == 0) {
           userMdListStore.addAll(metadataList);
           userMdGrid.unmask();
           return;
        }
        List<Avu> itemsToAdd = new ArrayList<>();
        for (Avu md : metadataList) {
            Avu model = userMdListStore.findModel(md);
            if (model != null) {
                model.setValue(md.getValue());
                userMdListStore.update(model);
            } else {
                itemsToAdd.add(presenter.setAvuModelKey(md));
            }
        }
        userMdListStore.addAll(itemsToAdd);
        userMdGrid.unmask();
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void addToUserMetadata(List<Avu> umd) {
        userMdListStore.addAll(umd);
    }

    @Override
    public void removeImportedMetadataFromStore(List<Avu> umd) {
        for (Avu md : umd) {
            additionalMdListStore.remove(md);
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    private class DiskResourceAdditionalMetadataSelectionChangedHandler
            implements SelectionChangedHandler<Avu> {
        @Override
        public void onSelectionChanged(SelectionChangedEvent<Avu> event) {
            if (event.getSelection() != null && event.getSelection().size() > 0) {
                importButton.enable();
            } else {
                importButton.disable();
            }

        }
    }
}
