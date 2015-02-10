package org.iplantc.de.diskResource.client.views.metadata;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.model.DiskResourceMetadataProperties;
import org.iplantc.de.diskResource.share.DiskResourceModule.MetadataIds;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
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
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.event.StartEditEvent.StartEditHandler;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            if (!Strings.isNullOrEmpty(value)) {
                sb.append(htmlTemplates.cell(value));
            }
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
            ConfirmMessageBox cmb = new ConfirmMessageBox(displayStrings.confirmAction(), displayStrings.metadataTemplateConfirmRemove());
            cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (PredefinedButton.YES.equals(event.getHideButton())) {
                        alc.remove(templateForm);
                        templateCombo.setEnabled(true);
                        expandUserMetadataPanel();
                        templateCombo.setValue(null);
                        selectedTemplate = null;
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

    interface MetadataHtmlTemplates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<b>{0}</b>")
        SafeHtml boldHeader(String headerText);

        @SafeHtmlTemplates.Template("<span qtip=\"{0}\">{0}</span>")
        SafeHtml cell(String value);

        @SafeHtmlTemplates.Template("<span> {0}&nbsp;{2}&nbsp;{1}</span>")
        SafeHtml labelHtml(SafeHtml info, String label, SafeHtml required);

        @SafeHtmlTemplates.Template("<img style='cursor:pointer;' qtip=\"{1}\" src=\"{0}\"/>")
        SafeHtml labelInfo(SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("<span style='color:red; top:-5px;'>*</span>")
        SafeHtml required();
    }

    interface MetadataInfoTemplate extends XTemplates {
        @XTemplate("<div style='text-overflow:ellipsis;overflow:hidden;white-space:nowrap;border:none;' qtip='{name}' >{name}</div>")
        SafeHtml templateInfo(String name);
    }

    @UiField TextButton addMetadataButton;
    @UiField BorderLayoutContainer con;
    @UiField TextButton deleteMetadataButton;
    @UiField ComboBox<MetadataTemplateInfo> templateCombo;
    @UiField ToolBar toolbar;

    private static final String METADATA_COMPLETE = "Metadata complete"; //$NON-NLS-1$
    private static final DiskResourceMetadataEditorPanelUiBinder uiBinder = GWT.create(DiskResourceMetadataEditorPanelUiBinder.class);
    private final AccordionLayoutContainer alc;
    private final AccordionLayoutAppearance appearance;
    private final DiskResourceAutoBeanFactory autoBeanFactory;
    private final VerticalLayoutContainer centerPanel;
    private final DiskResourceAutoBeanFactory diskResourceAutoBeanFactory;
    private final IplantDisplayStrings displayStrings;
    private final MetadataHtmlTemplates htmlTemplates;
    private final IplantResources iplantResources;
    private final FastMap<DiskResourceMetadata> templateAttrAvuMap = new FastMap<>();
    private final FastMap<Field<?>> templateAttrFieldMap = new FastMap<>();
    private final DateTimeFormat timestampFormat;
    private final Set<DiskResourceMetadata> toBeDeleted = Sets.newHashSet();
    private final boolean writable;
    private DiskResourceMetadataTemplate currentMetadataTemplate;
    private Grid<DiskResourceMetadata> grid;
    private GridInlineEditing<DiskResourceMetadata> gridInlineEditing;
    private CheckBox isCompleteCbx;
    private ListStore<DiskResourceMetadata> listStore;
    private Presenter presenter;
    private MetadataTemplateInfo selectedTemplate;
    private VerticalLayoutContainer templateContainer;
    private ContentPanel templateForm;
    private ListStore<MetadataTemplateInfo> templateStore;
    private int unique_avu_id;
    private ContentPanel userMetadataPanel;
    private boolean valid;

    public DiskResourceMetadataViewImpl(final DiskResource dr) {
        htmlTemplates = GWT.create(MetadataHtmlTemplates.class);
        autoBeanFactory = GWT.create(DiskResourceAutoBeanFactory.class);
        timestampFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
        displayStrings = I18N.DISPLAY;
        iplantResources = IplantResources.RESOURCES;
        diskResourceAutoBeanFactory = GWT.create(DiskResourceAutoBeanFactory.class);
        appearance = GWT.create(AccordionLayoutAppearance.class);
        writable = DiskResourceUtil.getInstance().isWritable(dr);
        alc = new AccordionLayoutContainer();
        centerPanel = new VerticalLayoutContainer();
        valid = true;

        initWidget(uiBinder.createAndBindUi(this));
        con.setCenterWidget(centerPanel);
        initGrid();
        addMetadataButton.setEnabled(writable);
        deleteMetadataButton.disable();
        templateCombo.setEnabled(writable);
        ensureDebugId(MetadataIds.METADATA_VIEW);
    }

    @Override
    public DiskResourceMetadataTemplate getMetadataTemplateToAdd() {
        if (selectedTemplate == null) {
            return null;
        }

        DiskResourceMetadataTemplate metadataTemplate = autoBeanFactory.templateAvus().as();
        metadataTemplate.setId(selectedTemplate.getId());

        ArrayList<DiskResourceMetadata> avus = Lists.newArrayList();
        metadataTemplate.setAvus(avus);

        for (String attr : templateAttrFieldMap.keySet()) {
            DiskResourceMetadata avu = templateAttrAvuMap.get(attr);
            if (avu == null) {
                avu = newMetadata(attr, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
                templateAttrAvuMap.put(attr, avu);
            }
            if (Strings.isNullOrEmpty(avu.getId())) {
                avu.setId(null);
            }

            Field<?> field = templateAttrFieldMap.get(attr);
            if (field.getValue() != null) {
                String value = field.getValue().toString();
                if ((field instanceof DateField) && !Strings.isNullOrEmpty(value)) {
                    value = timestampFormat.format(((DateField) field).getValue());
                }

                avu.setValue(value);
            }

            avus.add(avu);
        }

        return metadataTemplate;
    }

    @Override
    public DiskResourceMetadataTemplate getMetadataTemplateToDelete() {
        if (selectedTemplate == null) {
            return currentMetadataTemplate;
        }

        return null;
    }

    @Override
    public Set<DiskResourceMetadata> getMetadataToAdd() {
        HashSet<DiskResourceMetadata> metaDataToAdd = Sets.newHashSet();
        metaDataToAdd.addAll(listStore.getAll());

        return metaDataToAdd;
    }

    @Override
    public Set<DiskResourceMetadata> getMetadataToDelete() {
        return toBeDeleted;
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
    public void loadMetadata(DiskResourceMetadataList metadataList) {
        List<DiskResourceMetadata> metadata = metadataList.getMetadata();

        for (DiskResourceMetadata avu : metadata) {
            avu.setId(unique_avu_id++ + ""); //$NON-NLS-1$
        }

        listStore.clear();
        listStore.commitChanges();
        listStore.addAll(metadata);

        grid.getStore().setEnableFilters(true);
    }

    @Override
    public void loadMetadataTemplate(DiskResourceMetadataTemplate metadataTemplate) {
        currentMetadataTemplate = metadataTemplate;
        templateAttrAvuMap.clear();

        if (metadataTemplate != null) {
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
        templateContainer.add(removeLink, new VerticalLayoutData(.25, -1));
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
    public boolean shouldValidate() {
        if (isCompleteCbx != null) {
            return isCompleteCbx.getValue();
        }

        // validate by default
        return true;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        addMetadataButton.ensureDebugId(baseID + MetadataIds.ADD_METADATA);
        deleteMetadataButton.ensureDebugId(baseID + MetadataIds.DELETE_METADATA);
        templateCombo.ensureDebugId(baseID + MetadataIds.TEMPLATES);
    }

    @UiFactory
    ComboBox<MetadataTemplateInfo> buildTemplateCombo() {
        templateStore = new ListStore<>(new TemplateInfoModelKeyProvider());

        ComboBox<MetadataTemplateInfo> comboBox = new ComboBox<>(templateStore, new TemplateInfoLabelProvider(), new AbstractSafeHtmlRenderer<MetadataTemplateInfo>() {

            @Override
            public SafeHtml render(MetadataTemplateInfo object) {
                final MetadataInfoTemplate xtemp = GWT.create(MetadataInfoTemplate.class);
                return xtemp.templateInfo(object.getName());
            }
        });
        comboBox.setEditable(false);
        comboBox.setWidth(250);
        comboBox.setEmptyText(displayStrings.metadataTemplateSelect());
        comboBox.setTypeAhead(true);
        comboBox.addSelectionHandler(new TemplateInfoSelectionHandler());
        return comboBox;
    }

    @UiHandler("addMetadataButton")
    void onAddMetadataSelected(SelectEvent event) {
        expandUserMetadataPanel();
        String attr = getUniqueAttrName(displayStrings.newAttribute(), 0);
        DiskResourceMetadata md = newMetadata(attr, displayStrings.newValue(), ""); //$NON-NLS-1$
        md.setId(unique_avu_id++ + ""); //$NON-NLS-1$
        listStore.add(0, md);
        gridInlineEditing.startEditing(new GridCell(0, 0));
        gridInlineEditing.getEditor(grid.getColumnModel().getColumn(0)).validate(false);
    }

    @UiHandler("deleteMetadataButton")
    void onDeleteMetadataSelected(SelectEvent event) {
        expandUserMetadataPanel();
        for (DiskResourceMetadata md : grid.getSelectionModel().getSelectedItems()) {
            toBeDeleted.add(md);
            listStore.remove(md);
        }
    }

    private CheckBox buildBooleanField(MetadataTemplateAttribute attribute) {
        CheckBox cb = new CheckBox();

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null && !Strings.isNullOrEmpty(avu.getValue())) {
            cb.setValue(Boolean.valueOf(avu.getValue()));
        }

        if (attribute.getName().equalsIgnoreCase(METADATA_COMPLETE)) {
            isCompleteCbx = cb;
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
            fl.setHTML(buildLabelWithDescription(lbl, description, allowBlank));
        } else {
            // always set allow blank to true for checkbox
            fl.setHTML(buildLabelWithDescription(lbl, description, true));
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

    private String buildLabelWithDescription(final String label, final String description,
                                             boolean allowBlank) {
        if (label == null) {
            return null;
        }
        SafeUri infoUri = iplantResources.info().getSafeUri();
        SafeHtml infoImg = Strings.isNullOrEmpty(description) ? SafeHtmlUtils.fromString("") //$NON-NLS-1$
                               : htmlTemplates.labelInfo(infoUri, description);
        SafeHtml required = allowBlank ? SafeHtmlUtils.fromString("") : htmlTemplates.required(); //$NON-NLS-1$

        return htmlTemplates.labelHtml(infoImg, label, required).asString();
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
        return new IPlantAnchor(displayStrings.metadataTemplateRemove(), 575, new RemoveTemplateHandlerImpl());
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

    }

    private void buildTemplatePanel() {
        templateForm = new ContentPanel(appearance);
        templateForm.setBodyStyle("background-color: #fff; padding: 5px"); //$NON-NLS-1$
        templateForm.setSize("575", "275"); //$NON-NLS-1$ //$NON-NLS-2$
        templateForm.setHeadingHtml(htmlTemplates.boldHeader(templateCombo.getCurrentValue().getName()));
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
        userMetadataPanel = new ContentPanel(appearance);
        userMetadataPanel.setSize("575", "275"); //$NON-NLS-1$ //$NON-NLS-2$
        userMetadataPanel.setCollapsible(true);
        userMetadataPanel.getHeader().addStyleName(ThemeStyles.get().style().borderTop());

        userMetadataPanel.setHeadingHtml(htmlTemplates.boldHeader(displayStrings.userMetadata()));
    }

    private DiskResourceMetadata copy(DiskResourceMetadata drm) {
        DiskResourceMetadata temp = AutoBeanCodex.decode(diskResourceAutoBeanFactory, DiskResourceMetadata.class, "{}").as(); //$NON-NLS-1$
        temp.setAttribute(drm.getAttribute());
        temp.setValue(drm.getValue());
        temp.setUnit(drm.getUnit());
        temp.setId(drm.getId());
        return temp;
    }

    private ColumnModel<DiskResourceMetadata> createColumnModel() {
        List<ColumnConfig<DiskResourceMetadata, ?>> columns = Lists.newArrayList();
        DiskResourceMetadataProperties props = GWT.create(DiskResourceMetadataProperties.class);
        ColumnConfig<DiskResourceMetadata, String> attributeColumn = new ColumnConfig<>(props.attribute(), 150, displayStrings.attribute());
        ColumnConfig<DiskResourceMetadata, String> valueColumn = new ColumnConfig<>(props.value(), 150, displayStrings.paramValue());

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
                    return item.getId();
                } else {
                    return ""; //$NON-NLS-1$
                }
            }
        });

        return listStore;
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
        } else {
            return null;
        }
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
        gridInlineEditing.addStartEditHandler(new StartEditHandler<DiskResourceMetadata>() {

            @Override
            public void onStartEdit(StartEditEvent<DiskResourceMetadata> event) {
                int r = event.getEditCell().getRow();
                DiskResourceMetadata drm = listStore.get(r);
                DiskResourceMetadata temp = copy(drm);
                toBeDeleted.add(temp);

            }
        });
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
        alc.mask(displayStrings.loadingMask());
    }

}
