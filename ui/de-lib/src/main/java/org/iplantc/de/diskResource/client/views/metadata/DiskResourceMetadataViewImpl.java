package org.iplantc.de.diskResource.client.views.metadata;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.model.DiskResourceMetadataProperties;
import org.iplantc.de.diskResource.share.DiskResourceModule.MetadataIds;

import com.google.common.collect.Lists;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.List;

/**
 * FIXME REFACTOR Segregate programmatic view construction to a different UIBinder, class, etc FIXME
 * REFACTOR Factor out an appearance for this class
 *
 * @author jstroot
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

    private final class MetadataSelectionChangedListener
            implements SelectionChangedHandler<DiskResourceMetadata> {
        @Override
        public void onSelectionChanged(SelectionChangedEvent<DiskResourceMetadata> event) {
            deleteMetadataButton.setEnabled(event.getSelection().size() > 0 && writable);
            if (gridInlineEditing != null) {
                gridInlineEditing.completeEditing();
            }
        }
    }



    @UiTemplate("DiskResourceMetadataEditorPanel.ui.xml")
    interface DiskResourceMetadataEditorPanelUiBinder
            extends UiBinder<Widget, DiskResourceMetadataViewImpl> {
    }

    private static final DiskResourceMetadataEditorPanelUiBinder uiBinder =
            GWT.create(DiskResourceMetadataEditorPanelUiBinder.class);

    private static final String AVU_BEAN_TAG_MODEL_KEY = "model-key"; //$NON-NLS-1$

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

    private final AccordionLayoutContainer alc;
    private final AccordionLayoutAppearance accordionLayoutAppearance =
            GWT.create(AccordionLayoutAppearance.class);
    private final VerticalLayoutContainer centerPanel;


    private ContentPanel userMetadataPanel;
    private ContentPanel additionalMetadataPanel;

    private final DiskResourceAutoBeanFactory autoBeanFactory =
            GWT.create(DiskResourceAutoBeanFactory.class);

    private final boolean writable;
    private ListStore<DiskResourceMetadata> additionalMdListStore;
    private Grid<DiskResourceMetadata> additionalMdgrid;
    private GridInlineEditing<DiskResourceMetadata> gridInlineEditing;

    private ListStore<DiskResourceMetadata> userMdListStore;
    private Grid<DiskResourceMetadata> userMdGrid;

    private int unique_avu_id;
    private boolean valid;
    private MetadataView.Presenter presenter;
    private String baseId;

    public DiskResourceMetadataViewImpl(boolean isEditable) {
        alc = new AccordionLayoutContainer();
        centerPanel = new VerticalLayoutContainer();


        writable = isEditable;
        valid = true;

        initWidget(uiBinder.createAndBindUi(this));
        con.setCenterWidget(centerPanel);
        initGrid();
        addMetadataButton.setEnabled(writable);
        deleteMetadataButton.disable();
    }

/*    @Override
    public DiskResourceMetadataTemplate getMetadataTemplate() {
        if (selectedTemplate == null) {
            return null;
        }

        DiskResourceMetadataTemplate metadataTemplate = autoBeanFactory.templateAvus().as();
        metadataTemplate.setId(selectedTemplate.getId());

        ArrayList<DiskResourceMetadata> avus = Lists.newArrayList();

        for (String attr : templateAttrFieldMap.keySet()) {
            DiskResourceMetadata avu = templateAttrAvuMap.get(attr);
            if (avu == null) {
                avu = newMetadata(attr, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
                templateAttrAvuMap.put(attr, avu);
            }

            Field<?> field = templateAttrFieldMap.get(attr);
            if (field.getValue() != null) {
                String value = field.getValue().toString();
                if ((field instanceof DateField) && !Strings.isNullOrEmpty(value)) {
                    value = timestampFormat.format(((DateField)field).getValue());
                } else if (field instanceof ComboBox<?>) {
                    @SuppressWarnings("unchecked") ComboBox<TemplateAttributeSelectionItem> temp =
                            (ComboBox<TemplateAttributeSelectionItem>)field;
                    value = temp.getValue().getValue();
                }

                avu.setValue(value);
            }

            avus.add(avu);
        }
        metadataTemplate.setAvus(avus);

        return metadataTemplate;
    }*/

    @Override
    public List<DiskResourceMetadata> getAvus() {
        return additionalMdListStore.getAll();
    }

    @Override
    public boolean isValid() {
/*        if (selectedTemplate != null && templateForm != null) {
            List<IsField<?>> fields = FormPanelHelper.getFields(templateForm);
            for (IsField<?> f : fields) {
                if (!f.isValid(false)) {
                    valid = false;
                }
            }

        }*/
        return false;
    }

    @Override
    public void loadMetadata(final List<DiskResourceMetadata> metadataList) {
        for (DiskResourceMetadata avu : metadataList) {
            setAvuModelKey(avu);
        }

        additionalMdListStore.clear();
        additionalMdListStore.commitChanges();
        additionalMdListStore.addAll(metadataList);

        additionalMdgrid.getStore().setEnableFilters(true);
    }

    @Override
    public void loadUserMetadata(final List<DiskResourceMetadata> metadataList) {
        for (DiskResourceMetadata avu : metadataList) {
            setAvuModelKey(avu);
        }

        userMdListStore.clear();
        userMdListStore.commitChanges();
        userMdListStore.addAll(metadataList);

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
        deleteMetadataButton.ensureDebugId(baseID + MetadataIds.DELETE_METADATA);
        selectButton.ensureDebugId(baseID + MetadataIds.TEMPLATES);

        setUserMetadataDebugIds();
    }


    @UiHandler("addMetadataButton")
    void onAddMetadataSelected(SelectEvent event) {
        expandUserMetadataPanel();
        String attr = getUniqueAttrName(appearance.newAttribute(), 0);
        DiskResourceMetadata md = newMetadata(attr, appearance.newValue(), ""); //$NON-NLS-1$
        setAvuModelKey(md);
        additionalMdListStore.add(0, md);
        gridInlineEditing.startEditing(new GridCell(0, 0));
        gridInlineEditing.getEditor(additionalMdgrid.getColumnModel().getColumn(0)).validate(false);
    }

    @UiHandler("deleteMetadataButton")
    void onDeleteMetadataSelected(SelectEvent event) {
        expandUserMetadataPanel();
        for (DiskResourceMetadata md : additionalMdgrid.getSelectionModel().getSelectedItems()) {
            additionalMdListStore.remove(md);
        }
    }

    @UiHandler("selectButton")
    void onSelectButtonSelected(SelectEvent event) {
        presenter.onSelectTemplate();

    }

    private void setUserMetadataDebugIds() {
        userMetadataPanel.ensureDebugId(baseId + MetadataIds.USER_METADATA);
        getCollapseBtn(userMetadataPanel).ensureDebugId(
                baseId + MetadataIds.USER_METADATA + MetadataIds.USER_METADATA_COLLAPSE);
    }

    private void buildUserMetadataPanel() {
        userMetadataPanel = new ContentPanel(accordionLayoutAppearance);
        userMetadataPanel.setSize("575", "275"); //$NON-NLS-1$ //$NON-NLS-2$
        userMetadataPanel.setCollapsible(true);
        userMetadataPanel.getHeader().addStyleName(ThemeStyles.get().style().borderTop());
        userMetadataPanel.setHeadingHtml(appearance.boldHeader(appearance.userMetadata()));
    }

    private void buildAdditionalMetadataPanel() {
        additionalMetadataPanel = new ContentPanel(accordionLayoutAppearance);
        additionalMetadataPanel.setSize("575", "275"); //$NON-NLS-1$ //$NON-NLS-2$
        additionalMetadataPanel.setCollapsible(true);
        additionalMetadataPanel.getHeader().addStyleName(ThemeStyles.get().style().borderTop());
        additionalMetadataPanel.setHeadingHtml(appearance.boldHeader("Additional Metadata"));
    }

    private ColumnModel<DiskResourceMetadata> createColumnModel() {
        List<ColumnConfig<DiskResourceMetadata, ?>> columns = Lists.newArrayList();
        DiskResourceMetadataProperties props = GWT.create(DiskResourceMetadataProperties.class);
        ColumnConfig<DiskResourceMetadata, String> attributeColumn =
                new ColumnConfig<>(props.attribute(), 150, appearance.attribute());
        ColumnConfig<DiskResourceMetadata, String> valueColumn =
                new ColumnConfig<>(props.value(), 150, appearance.paramValue());

        MetadataCell metadataCell = new MetadataCell();
        attributeColumn.setCell(metadataCell);
        valueColumn.setCell(metadataCell);
        columns.add(attributeColumn);
        columns.add(valueColumn);

        return new ColumnModel<>(columns);
    }

    private ListStore<DiskResourceMetadata> createAdditionalListStore() {
        additionalMdListStore = new ListStore<>(new ModelKeyProvider<DiskResourceMetadata>() {
            @Override
            public String getKey(DiskResourceMetadata item) {
                if (item != null) {
                    final AutoBean<Object> metadataBean = AutoBeanUtils.getAutoBean(item);
                    return metadataBean.getTag(AVU_BEAN_TAG_MODEL_KEY);
                } else {
                    return ""; //$NON-NLS-1$
                }
            }
        });

        return additionalMdListStore;
    }

    private ListStore<DiskResourceMetadata> createUserListStore() {
        userMdListStore = new ListStore<>(new ModelKeyProvider<DiskResourceMetadata>() {
            @Override
            public String getKey(DiskResourceMetadata item) {
                if (item != null) {
                    final AutoBean<Object> metadataBean = AutoBeanUtils.getAutoBean(item);
                    return metadataBean.getTag(AVU_BEAN_TAG_MODEL_KEY);
                } else {
                    return ""; //$NON-NLS-1$
                }
            }
        });

        return userMdListStore;
    }

    private void setAvuModelKey(DiskResourceMetadata avu) {
        if (avu != null) {
            final AutoBean<DiskResourceMetadata> avuBean = AutoBeanUtils.getAutoBean(avu);
            avuBean.setTag(AVU_BEAN_TAG_MODEL_KEY, String.valueOf(unique_avu_id++));
        }
    }

    private void expandUserMetadataPanel() {
        if (userMetadataPanel.isCollapsed()) {
            userMetadataPanel.expand();
        }
    }

    private String getUniqueAttrName(String attrName, int i) {
        String retName = i > 0 ? attrName + "_(" + i + ")" : attrName; //$NON-NLS-1$ //$NON-NLS-2$
        for (DiskResourceMetadata md : additionalMdListStore.getAll()) {
            if (md.getAttribute().equals(retName)) {
                return getUniqueAttrName(attrName, ++i);
            }
        }
        return retName;
    }

    private void initEditor() {
        gridInlineEditing = new GridInlineEditing<>(additionalMdgrid);
        gridInlineEditing.setClicksToEdit(ClicksToEdit.TWO);
        ColumnConfig<DiskResourceMetadata, String> column1 = additionalMdgrid.getColumnModel().getColumn(0);
        ColumnConfig<DiskResourceMetadata, String> column2 = additionalMdgrid.getColumnModel().getColumn(1);

        TextField field1 = new TextField();
        TextField field2 = new TextField();

        field1.setAutoValidate(true);
        field2.setAutoValidate(true);

        field1.setAllowBlank(false);
        field2.setAllowBlank(false);

        AttributeValidationHandler validationHandler = new AttributeValidationHandler();
        field1.addInvalidHandler(validationHandler);
        field1.addValidHandler(validationHandler);

        gridInlineEditing.addEditor(column1, field1);
        gridInlineEditing.addEditor(column2, field2);
        gridInlineEditing.addCompleteEditHandler(new CompleteEditHandler<DiskResourceMetadata>() {

            @Override
            public void onCompleteEdit(CompleteEditEvent<DiskResourceMetadata> event) {
                additionalMdListStore.commitChanges();
            }
        });
    }

    private void initGrid() {
        buildAdditionalMetadataPanel();
        buildUserMetadataPanel();

        userMdGrid = new Grid<>(createUserListStore(), createColumnModel());
        userMetadataPanel.add(userMdGrid);
        alc.add(userMetadataPanel);

        userMdGrid.getSelectionModel().addSelectionChangedHandler(new MetadataSelectionChangedListener());
        new QuickTip(additionalMdgrid);

        additionalMdgrid = new Grid<>(createAdditionalListStore(), createColumnModel());
        additionalMetadataPanel.add(additionalMdgrid);
        alc.add(additionalMetadataPanel);

        if (writable) {
            initEditor();
        }
        additionalMdgrid.getSelectionModel().addSelectionChangedHandler(new MetadataSelectionChangedListener());
        new QuickTip(additionalMdgrid);

       alc.setActiveWidget(userMetadataPanel);
       centerPanel.add(alc,new VerticalLayoutData(1,-1));

    }

    private DiskResourceMetadata newMetadata(String attr, String value, String unit) {
        // FIXME Move to presenter. Autobean factory doesn't belong in view.
        DiskResourceMetadata avu = autoBeanFactory.metadata().as();

        avu.setAttribute(attr);
        avu.setValue(value);
        avu.setUnit(unit);

        return avu;
    }

    private Widget getCollapseBtn(ContentPanel panel) {
        return panel.getHeader().getTool(0);
    }

}
