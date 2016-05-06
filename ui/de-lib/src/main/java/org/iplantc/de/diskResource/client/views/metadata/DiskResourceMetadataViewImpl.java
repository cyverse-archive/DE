package org.iplantc.de.diskResource.client.views.metadata;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.model.DiskResourceMetadataProperties;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.SelectMetadataTemplateView;
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
    private final DateTimeFormat timestampFormat;

    private ContentPanel userMetadataPanel;
    private ContentPanel additionalMetadataPanel;

    private final DiskResourceAutoBeanFactory autoBeanFactory =
            GWT.create(DiskResourceAutoBeanFactory.class);
 //   private final FastMap<DiskResourceMetadata> templateAttrAvuMap = new FastMap<>();
//    private final FastMap<Field<?>> templateAttrFieldMap = new FastMap<>();
    private final boolean writable;

    private ListStore<DiskResourceMetadata> additionalMdListStore;
    private Grid<DiskResourceMetadata> additionalMdgrid;
    private GridInlineEditing<DiskResourceMetadata> gridInlineEditing;

    private ListStore<DiskResourceMetadata> userMdListStore;
    private Grid<DiskResourceMetadata> userMdGrid;

    private List<MetadataTemplateInfo> templates;

    private int unique_avu_id;
    private boolean valid;
    private MetadataView.Presenter presenter;
    private String baseId;

    public DiskResourceMetadataViewImpl(boolean isEditable) {

        alc = new AccordionLayoutContainer();
        centerPanel = new VerticalLayoutContainer();
        timestampFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

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

/*    public IPlantAnchor buildHelpLink(final List<MetadataTemplateAttribute> attributes) {
        IPlantAnchor helpLink =
                new IPlantAnchor(appearance.metadataTermGuide(), 150, new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        VerticalLayoutContainer helpVlc = new VerticalLayoutContainer();
                        helpVlc.setScrollMode(ScrollMode.AUTOY);
                        for (MetadataTemplateAttribute mta : attributes) {
                            HTML l = new HTML("<b>" + mta.getName() + ":</b> <br/>");
                            HTML helpText = new HTML("<p>" + mta.getDescription() + "</p><br/>");
                            helpVlc.add(l, new VerticalLayoutData(.25, -1));
                            helpVlc.add(helpText, new VerticalLayoutData(.90, -1));
                        }
                        Dialog w = new Dialog();
                        w.setHideOnButtonClick(true);
                        w.setSize("350", "400");
                        w.setPredefinedButtons(PredefinedButton.OK);
                        w.setHeadingText(selectedTemplate.getName());
                        w.setBodyStyle("background: #fff;");
                        w.setWidget(helpVlc);
                        w.show();

                    }
                });

        return helpLink;
    }*/

    @Override
    public void populateTemplates(List<MetadataTemplateInfo> templates) {
        this.templates = templates;
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
        SelectMetadataTemplateView view = new SelectMetadataTemplateView(templates);
        view.setModal(false);
        view.setSize("400px", "200px");
        view.setHeadingText("Select Template");
        view.show();

    }

/*    private CheckBox buildBooleanField(MetadataTemplateAttribute attribute) {
        CheckBox cb = new CheckBox();

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null && !Strings.isNullOrEmpty(avu.getValue())) {
            cb.setValue(Boolean.valueOf(avu.getValue()));
        }

        // CheckBox fields can still be (un)checked when setReadOnly is set to true.
        cb.setEnabled(writable);

        return cb;
    }

    private DateField buildDateField(MetadataTemplateAttribute attribute) {
        final DateField tf = new DateField(new DateTimePropertyEditor(timestampFormat));
        tf.setAllowBlank(!attribute.isRequired());
        if (writable) {
            tf.setEmptyText(timestampFormat.format(new Date(0)));
        }

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null && !Strings.isNullOrEmpty(avu.getValue())) {
            try {
                tf.setValue(timestampFormat.parse(avu.getValue()));
            } catch (Exception e) {
                GWT.log(avu.getValue(), e);
            }
        }

        return tf;
    }

    private FieldLabel buildFieldLabel(IsWidget widget,
                                       String lbl,
                                       String description,
                                       boolean allowBlank) {
        FieldLabel fl = new FieldLabel(widget);
        if (!(widget instanceof CheckBox)) {
            fl.setHTML(appearance.buildLabelWithDescription(lbl, description, allowBlank));
        } else {
            // always set allow blank to true for checkbox
            fl.setHTML(appearance.buildLabelWithDescription(lbl, description, true));
        }
        new QuickTip(fl);
        fl.setLabelAlign(LabelAlign.TOP);
        return fl;
    }

    private NumberField<Integer> buildIntegerField(MetadataTemplateAttribute attribute) {
        NumberField<Integer> nf = new NumberField<>(new IntegerPropertyEditor());
        nf.setAllowBlank(!attribute.isRequired());
        nf.setAllowDecimals(false);
        nf.setAllowNegative(true);

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null && !Strings.isNullOrEmpty(avu.getValue())) {
            nf.setValue(new Integer(avu.getValue()));
        }

        return nf;
    }

    private NumberField<Double> buildNumberField(MetadataTemplateAttribute attribute) {
        NumberField<Double> nf = new NumberField<>(new DoublePropertyEditor());
        nf.setAllowBlank(!attribute.isRequired());
        nf.setAllowDecimals(true);
        nf.setAllowNegative(true);

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null && !Strings.isNullOrEmpty(avu.getValue())) {
            nf.setValue(new Double(avu.getValue()));
        }

        return nf;
    }*/



    private void setUserMetadataDebugIds() {
        userMetadataPanel.ensureDebugId(baseId + MetadataIds.USER_METADATA);
        getCollapseBtn(userMetadataPanel).ensureDebugId(
                baseId + MetadataIds.USER_METADATA + MetadataIds.USER_METADATA_COLLAPSE);
    }
/*
    private TextArea buildTextArea(MetadataTemplateAttribute attribute) {
        TextArea area = new TextArea();
        area.setAllowBlank(!attribute.isRequired());
        area.setHeight(200);

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null) {
            area.setValue(avu.getValue());
        }

        return area;
    }

    private TextField buildTextField(MetadataTemplateAttribute attribute) {
        TextField fld = new TextField();
        fld.setAllowBlank(!attribute.isRequired());

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null) {
            fld.setValue(avu.getValue());
        }

        return fld;
    }

    private TextField buildURLField(MetadataTemplateAttribute attribute) {
        TextField tf = buildTextField(attribute);
        tf.addValidator(new UrlValidator());
        if (writable) {
            tf.setEmptyText("Valid URL");
        }
        return tf;
    }*/


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

    /**
   //  * @param attribute the template attribute
   //  * @return Field based on MetadataTemplateAttribute type.
     */
  /* private Field<?> getAttributeValueWidget(MetadataTemplateAttribute attribute) {
        String type = attribute.getType();
        if (type.equalsIgnoreCase("timestamp")) { //$NON-NLS-1$
            return buildDateField(attribute);
        } else if (type.equalsIgnoreCase("boolean")) { //$NON-NLS-1$
            return buildBooleanField(attribute);
        } else if (type.equalsIgnoreCase("number")) { //$NON-NLS-1$
            return buildNumberField(attribute);
        } else if (type.equalsIgnoreCase("integer")) { //$NON-NLS-1$
            return buildIntegerField(attribute);
        } else if (type.equalsIgnoreCase("string")) { //$NON-NLS-1$
            return buildTextField(attribute);
        } else if (type.equalsIgnoreCase("multiline text")) { //$NON-NLS-1$
            return buildTextArea(attribute);
        } else if (type.equalsIgnoreCase("URL/URI")) { //$NON-NLS-1$
            return buildURLField(attribute);
        } else if (type.equalsIgnoreCase("Enum")) {
            return buildListField(attribute);
        } else {
            return null;
        }
    }*/

/*    private ComboBox<TemplateAttributeSelectionItem> buildListField(MetadataTemplateAttribute attribute) {
        ListStore<TemplateAttributeSelectionItem> store =
                new ListStore<>(new ModelKeyProvider<TemplateAttributeSelectionItem>() {

                    @Override
                    public String getKey(TemplateAttributeSelectionItem item) {
                        return item.getId();
                    }
                });
        store.addAll(attribute.getValues());
        ComboBox<TemplateAttributeSelectionItem> combo =
                new ComboBox<>(store, new StringLabelProvider<TemplateAttributeSelectionItem>() {
                    @Override
                    public String getLabel(TemplateAttributeSelectionItem item) {
                        return item.getValue();
                    }
                });
        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null) {
            String val = avu.getValue();
            for (TemplateAttributeSelectionItem item : attribute.getValues()) {
                if (item.getValue().equals(val)) {
                    combo.setValue(item);
                    break;
                }
            }

        } else {
            for (TemplateAttributeSelectionItem item : attribute.getValues()) {
                if (item.isDefaultValue()) {
                    combo.setValue(item);
                    break;
                }
            }
        }
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setAllowBlank(!attribute.isRequired());
        return combo;

    }*/

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

        additionalMdgrid = new Grid<>(createAdditionalListStore(), createColumnModel());
        additionalMetadataPanel.add(additionalMdgrid);
        alc.add(additionalMetadataPanel);

        if (writable) {
            initEditor();
        }
        additionalMdgrid.getSelectionModel().addSelectionChangedHandler(new MetadataSelectionChangedListener());
        new QuickTip(additionalMdgrid);


        userMdGrid = new Grid<>(createUserListStore(), createColumnModel());
        userMetadataPanel.add(userMdGrid);
        alc.add(userMetadataPanel);


        userMdGrid.getSelectionModel().addSelectionChangedHandler(new MetadataSelectionChangedListener());
        new QuickTip(additionalMdgrid);

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
