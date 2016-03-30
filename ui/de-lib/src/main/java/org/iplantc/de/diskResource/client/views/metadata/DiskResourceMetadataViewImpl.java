package org.iplantc.de.diskResource.client.views.metadata;

import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.models.diskResources.TemplateAttributeSelectionItem;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.model.DiskResourceMetadataProperties;
import org.iplantc.de.diskResource.share.DiskResourceModule.MetadataIds;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.ExpandMode;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel.LabelAlign;
import com.sencha.gxt.widget.core.client.form.FormPanelHelper;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.IntegerPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextArea;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FIXME REFACTOR Segregate programmatic view construction to a different UIBinder, class, etc
 * FIXME REFACTOR Factor out an appearance for this class
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

    private final class MetadataSelectionChangedListener implements SelectionChangedHandler<DiskResourceMetadata> {
        @Override
        public void onSelectionChanged(SelectionChangedEvent<DiskResourceMetadata> event) {
            deleteMetadataButton.setEnabled(event.getSelection().size() > 0 && writable);
            if (gridInlineEditing != null) {
                gridInlineEditing.completeEditing();
            }
        }
    }

    private final class RemoveTemplateHandlerImpl implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            ConfirmMessageBox cmb = new ConfirmMessageBox(appearance.confirmAction(),
                                                          appearance.metadataTemplateConfirmRemove());
            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        alc.remove(templateForm);
                        templateCombo.setEnabled(true);
                        expandUserMetadataPanel();
                        templateCombo.setValue(null);
                        selectedTemplate = null;
                        valid = true;
                    }
                }
            });
            cmb.show();
        }
    }

    private final class TemplateInfoLabelProvider implements LabelProvider<MetadataTemplateInfo> {
        @Override
        public String getLabel(MetadataTemplateInfo item) {
            return item.getName();
        }
    }

    private final class TemplateInfoModelKeyProvider implements ModelKeyProvider<MetadataTemplateInfo> {
        @Override
        public String getKey(MetadataTemplateInfo item) {
            return item.getId();
        }
    }

    private final class TemplateInfoSelectionHandler implements SelectionHandler<MetadataTemplateInfo> {
        @Override
        public void onSelection(SelectionEvent<MetadataTemplateInfo> event) {
            selectedTemplate = event.getSelectedItem();
            templateCombo.setValue(selectedTemplate, true);
            onTemplateSelected(selectedTemplate);
        }
    }

    @UiTemplate("DiskResourceMetadataEditorPanel.ui.xml")
    interface DiskResourceMetadataEditorPanelUiBinder extends UiBinder<Widget, DiskResourceMetadataViewImpl> {
    }

    @UiField TextButton addMetadataButton;
    @UiField BorderLayoutContainer con;
    @UiField TextButton deleteMetadataButton;
    @UiField ComboBox<MetadataTemplateInfo> templateCombo;
    @UiField ToolBar toolbar;

    private final AccordionLayoutContainer alc;
    private final AccordionLayoutAppearance accordionLayoutAppearance = GWT.create(AccordionLayoutAppearance.class);
    private final VerticalLayoutContainer centerPanel;
    private final DateTimeFormat timestampFormat;
    private Grid<DiskResourceMetadata> grid;
    private GridInlineEditing<DiskResourceMetadata> gridInlineEditing;
    private MetadataTemplateInfo selectedTemplate;
    private VerticalLayoutContainer templateContainer;
    private ContentPanel templateForm;
    private ContentPanel userMetadataPanel;

    private static final String AVU_BEAN_TAG_MODEL_KEY = "model-key"; //$NON-NLS-1$
    private static final DiskResourceMetadataEditorPanelUiBinder uiBinder = GWT.create(DiskResourceMetadataEditorPanelUiBinder.class);
    @UiField(provided = true) final MetadataView.Appearance appearance = GWT.create(MetadataView.Appearance.class);

    private final DiskResourceAutoBeanFactory autoBeanFactory = GWT.create(DiskResourceAutoBeanFactory.class);
    private final FastMap<DiskResourceMetadata> templateAttrAvuMap = new FastMap<>();
    private final FastMap<Field<?>> templateAttrFieldMap = new FastMap<>();
    private final boolean writable;
    private ListStore<DiskResourceMetadata> listStore;
    private final ListStore<MetadataTemplateInfo> templateStore;
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
        templateStore = new ListStore<>(new TemplateInfoModelKeyProvider());

        initWidget(uiBinder.createAndBindUi(this));
        con.setCenterWidget(centerPanel);
        initGrid();
        addMetadataButton.setEnabled(writable);
        deleteMetadataButton.disable();
        templateCombo.setEnabled(writable);
    }

    @Override
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
                    value = timestampFormat.format(((DateField) field).getValue());
                } else if(field instanceof ComboBox<?>) {
                    @SuppressWarnings("unchecked")
                    ComboBox<TemplateAttributeSelectionItem> temp = (ComboBox<TemplateAttributeSelectionItem>)field;
                    value = temp.getValue().getValue();
                }

                avu.setValue(value);
            }

            avus.add(avu);
        }
        metadataTemplate.setAvus(avus);

        return metadataTemplate;
    }

    @Override
    public List<DiskResourceMetadata> getAvus() {
        return listStore.getAll();
    }

    @Override
    public boolean isValid() {
        if (selectedTemplate != null && templateForm != null) {
            List<IsField<?>> fields = FormPanelHelper.getFields(templateForm);
            for (IsField<?> f : fields) {
                if (!f.isValid(false)) {
                    valid = false;
                }
            }

        }
        return valid;
    }

    @Override
    public void loadMetadata(final List<DiskResourceMetadata> metadataList) {
        for (DiskResourceMetadata avu : metadataList) {
            setAvuModelKey(avu);
        }

        listStore.clear();
        listStore.commitChanges();
        listStore.addAll(metadataList);

        grid.getStore().setEnableFilters(true);
    }

    @Override
    public void loadMetadataTemplate(DiskResourceMetadataTemplate metadataTemplate) {
        templateAttrAvuMap.clear();

        if (metadataTemplate == null) {
            selectedTemplate = null;
        } else {
            for (DiskResourceMetadata avu : metadataTemplate.getAvus()) {
                String attribute = avu.getAttribute();
                templateAttrAvuMap.put(attribute, avu);
            }

            selectedTemplate = templateStore.findModelWithKey(metadataTemplate.getId());
            templateCombo.setValue(selectedTemplate);
            onTemplateSelected(selectedTemplate);
        }
    }

    @Override
    public void loadTemplateAttributes(List<MetadataTemplateAttribute> attributes) {
        templateAttrFieldMap.clear();
        IPlantAnchor removeLink = buildRemoveTemplateLink();
        IPlantAnchor helpLink = buildHelpLink(attributes);
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);
        hp.add(removeLink);
        hp.add(helpLink);
        templateContainer.add(hp, new VerticalLayoutData(1, -1));
        for (MetadataTemplateAttribute attribute : attributes) {
            Field<?> field = getAttributeValueWidget(attribute);
            if (field != null) {
                field.setReadOnly(!writable);
                templateAttrFieldMap.put(attribute.getName(), field);
                templateContainer.add(buildFieldLabel(field, attribute.getName(), attribute.getDescription(), !attribute.isRequired()), new VerticalLayoutData(.90, -1));
            }
        }
        alc.forceLayout();
        alc.unmask();
    }

    public IPlantAnchor buildHelpLink(final List<MetadataTemplateAttribute> attributes) {
        IPlantAnchor helpLink = new IPlantAnchor(appearance.metadataTermGuide(),
                                                 150,
                                                 new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                VerticalLayoutContainer helpVlc = new VerticalLayoutContainer();
                helpVlc.setScrollMode(ScrollMode.AUTOY);
                for (MetadataTemplateAttribute mta : attributes) {
                    HTML l = new HTML("<b>" + mta.getName() + ":</b> <br/>");
                                                             HTML helpText = new HTML("<p>"
                                                                     + mta.getDescription()
                                                                     + "</p><br/>");
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
    }

    @Override
    public void populateTemplates(List<MetadataTemplateInfo> templates) {
        templateStore.clear();
        templateStore.addAll(templates);
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
        templateCombo.ensureDebugId(baseID + MetadataIds.TEMPLATES);

        setUserMetadataDebugIds();
    }

    @UiFactory
    ComboBox<MetadataTemplateInfo> buildTemplateCombo() {

        final AbstractSafeHtmlRenderer<MetadataTemplateInfo> htmlRenderer = new AbstractSafeHtmlRenderer<MetadataTemplateInfo>() {
            @Override
            public SafeHtml render(MetadataTemplateInfo object) {
                return appearance.renderComboBoxHtml(object);
            }
        };
        ComboBox<MetadataTemplateInfo> comboBox = new ComboBox<>(templateStore,
                                                                 new TemplateInfoLabelProvider(),
                                                                 htmlRenderer);
        comboBox.setEditable(false);
        comboBox.setWidth(250);
        comboBox.setEmptyText(appearance.metadataTemplateSelect());
        comboBox.setTypeAhead(true);
        comboBox.addSelectionHandler(new TemplateInfoSelectionHandler());
        return comboBox;
    }

    @UiHandler("addMetadataButton")
    void onAddMetadataSelected(SelectEvent event) {
        expandUserMetadataPanel();
        String attr = getUniqueAttrName(appearance.newAttribute(), 0);
        DiskResourceMetadata md = newMetadata(attr, appearance.newValue(), ""); //$NON-NLS-1$
        setAvuModelKey(md);
        listStore.add(0, md);
        gridInlineEditing.startEditing(new GridCell(0, 0));
        gridInlineEditing.getEditor(grid.getColumnModel().getColumn(0)).validate(false);
    }

    @UiHandler("deleteMetadataButton")
    void onDeleteMetadataSelected(SelectEvent event) {
        expandUserMetadataPanel();
        for (DiskResourceMetadata md : grid.getSelectionModel().getSelectedItems()) {
            listStore.remove(md);
        }
    }

    private CheckBox buildBooleanField(MetadataTemplateAttribute attribute) {
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

    private FieldLabel buildFieldLabel(IsWidget widget, String lbl, String description,
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
    }

    private IPlantAnchor buildRemoveTemplateLink() {
        return new IPlantAnchor(appearance.metadataTemplateRemove(),
                                200,
                                new RemoveTemplateHandlerImpl());
    }

    private void buildTemplateContainer() {
        centerPanel.clear();
        alc.clear();
        alc.setExpandMode(ExpandMode.SINGLE);
        buildTemplatePanel();
        buildUserMetadataPanel();
        // must re add the grid
        userMetadataPanel.add(grid);
        alc.add(templateForm);
        alc.add(userMetadataPanel);
        alc.setActiveWidget(templateForm);
        centerPanel.add(alc, new VerticalLayoutData(1, -1));

        setTemplateFormDebugIds();
        setUserMetadataDebugIds();
    }

    private void setUserMetadataDebugIds() {
        userMetadataPanel.ensureDebugId(baseId + MetadataIds.USER_METADATA);
        getCollapseBtn(userMetadataPanel).ensureDebugId(baseId + MetadataIds.USER_METADATA + MetadataIds.USER_METADATA_COLLAPSE);
    }

    private void setTemplateFormDebugIds() {
        templateForm.ensureDebugId(baseId + MetadataIds.METADATA_TEMPLATE);
        getCollapseBtn(templateForm).ensureDebugId(baseId + MetadataIds.METADATA_TEMPLATE + MetadataIds.METADATA_TEMPLATE_COLLAPSE);
    }

    private void buildTemplatePanel() {
        templateForm = new ContentPanel(accordionLayoutAppearance);
        templateForm.setBodyStyle("background-color: #fff; padding: 5px"); //$NON-NLS-1$
        templateForm.setSize("575", "275"); //$NON-NLS-1$ //$NON-NLS-2$
        templateForm.setHeadingHtml(appearance.boldHeader(templateCombo.getCurrentValue().getName()));
        templateForm.getHeader().addStyleName(ThemeStyles.get().style().borderTop());
        templateContainer = new VerticalLayoutContainer();
        templateContainer.setScrollMode(ScrollMode.AUTOY);
        templateForm.add(templateContainer);
        // need this to be set manually to avoid renderer assertion error
        templateForm.setCollapsible(true);
        // end temp fix
    }

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
    }

    private void buildUserMetadataPanel() {
        userMetadataPanel = new ContentPanel(accordionLayoutAppearance);
        userMetadataPanel.setSize("575", "275"); //$NON-NLS-1$ //$NON-NLS-2$
        userMetadataPanel.setCollapsible(true);
        userMetadataPanel.getHeader().addStyleName(ThemeStyles.get().style().borderTop());

        userMetadataPanel.setHeadingHtml(appearance.boldHeader(appearance.userMetadata()));
    }

    private ColumnModel<DiskResourceMetadata> createColumnModel() {
        List<ColumnConfig<DiskResourceMetadata, ?>> columns = Lists.newArrayList();
        DiskResourceMetadataProperties props = GWT.create(DiskResourceMetadataProperties.class);
        ColumnConfig<DiskResourceMetadata, String> attributeColumn = new ColumnConfig<>(props.attribute(), 150, appearance.attribute());
        ColumnConfig<DiskResourceMetadata, String> valueColumn = new ColumnConfig<>(props.value(), 150, appearance.paramValue());

        MetadataCell metadataCell = new MetadataCell();
        attributeColumn.setCell(metadataCell);
        valueColumn.setCell(metadataCell);
        columns.add(attributeColumn);
        columns.add(valueColumn);

        return new ColumnModel<>(columns);
    }

    private ListStore<DiskResourceMetadata> createListStore() {
        listStore = new ListStore<>(new ModelKeyProvider<DiskResourceMetadata>() {
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

        return listStore;
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
     * @param attribute the template attribute
     * @return Field based on MetadataTemplateAttribute type.
     */
    private Field<?> getAttributeValueWidget(MetadataTemplateAttribute attribute) {
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
    }

    private ComboBox<TemplateAttributeSelectionItem> buildListField(MetadataTemplateAttribute attribute) {
        ListStore<TemplateAttributeSelectionItem> store = new ListStore<>(new ModelKeyProvider<TemplateAttributeSelectionItem>() {

            @Override
            public String getKey(TemplateAttributeSelectionItem item) {
                return item.getId();
            }
        });
        store.addAll(attribute.getValues());
        ComboBox<TemplateAttributeSelectionItem> combo = new ComboBox<>(store,
                                                                        new StringLabelProvider<TemplateAttributeSelectionItem>() {
                                                                            @Override
                                                                            public String
                                                                                    getLabel(TemplateAttributeSelectionItem item) {
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

    }

    private String getUniqueAttrName(String attrName, int i) {
        String retName = i > 0 ? attrName + "_(" + i + ")" : attrName; //$NON-NLS-1$ //$NON-NLS-2$
        for (DiskResourceMetadata md : listStore.getAll()) {
            if (md.getAttribute().equals(retName)) {
                return getUniqueAttrName(attrName, ++i);
            }
        }
        return retName;
    }

    private void initEditor() {
        gridInlineEditing = new GridInlineEditing<>(grid);
        gridInlineEditing.setClicksToEdit(ClicksToEdit.TWO);
        ColumnConfig<DiskResourceMetadata, String> column1 = grid.getColumnModel().getColumn(0);
        ColumnConfig<DiskResourceMetadata, String> column2 = grid.getColumnModel().getColumn(1);

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
                listStore.commitChanges();
            }
        });
    }

    private void initGrid() {
        buildUserMetadataPanel();
        grid = new Grid<>(createListStore(), createColumnModel());
        userMetadataPanel.add(grid);
        centerPanel.add(userMetadataPanel, new VerticalLayoutData(1, -1));

        if (writable) {
            initEditor();
        }
        grid.getSelectionModel().addSelectionChangedHandler(new MetadataSelectionChangedListener());
        new QuickTip(grid);
    }

    private DiskResourceMetadata newMetadata(String attr, String value, String unit) {
        // FIXME Move to presenter. Autobean factory doesn't belong in view.
        DiskResourceMetadata avu = autoBeanFactory.metadata().as();

        avu.setAttribute(attr);
        avu.setValue(value);
        avu.setUnit(unit);

        return avu;
    }

    private void onTemplateSelected(MetadataTemplateInfo templateInfo) {
        templateCombo.setEnabled(false);
        presenter.onTemplateSelected(templateInfo.getId());
        buildTemplateContainer();
        alc.mask(appearance.templateSelectedLoadingMask());
    }

    private Widget getCollapseBtn(ContentPanel panel) {
        return panel.getHeader().getTool(0);
    }

}
