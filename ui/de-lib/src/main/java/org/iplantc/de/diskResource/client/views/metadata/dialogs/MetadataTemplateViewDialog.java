package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.MetadataTemplateAttribute;
import org.iplantc.de.client.models.diskResources.TemplateAttributeSelectionItem;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.MetadataView;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import java.util.Date;
import java.util.List;

/**
 * Created by sriram on 5/9/16.
 */
public class MetadataTemplateViewDialog extends IPlantDialog {
   final MetadataView.Appearance appearance = GWT.create(MetadataView.Appearance.class);
    private VerticalLayoutContainer widget;
    private final DateTimeFormat timestampFormat;
    private boolean writable;
    private final FastMap<DiskResourceMetadata> templateAttrAvuMap = new FastMap<>();
    private final FastMap<Field<?>> templateAttrFieldMap = new FastMap<>();
    private List<MetadataTemplateAttribute> attributes;
    private List<DiskResourceMetadata> templateMd;

    public MetadataTemplateViewDialog(List<DiskResourceMetadata> templateMd, boolean writable,
                                      List<MetadataTemplateAttribute> attributes) {
        timestampFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
        this.writable = writable;
        this.attributes = attributes;
        this.templateMd = templateMd;

        widget = new VerticalLayoutContainer();
        widget.setScrollMode(ScrollSupport.ScrollMode.AUTOY);
        buildAvuMap();
        loadTemplateAttributes();
        add(widget);
    }
    
    private void buildAvuMap() {
    	for (MetadataTemplateAttribute attribute: attributes) {
    		DiskResourceMetadata md = findMetadataForAttribute(attribute.getName());
    		templateAttrAvuMap.put(attribute.getName(),md);
    	}
    }
    
    private DiskResourceMetadata findMetadataForAttribute(String attribute) {
    	for(DiskResourceMetadata md: templateMd) {
    		if(md.getAttribute().equals(attribute)) {
    			return md;
    		}
    	}
		return null;
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
        fl.setLabelAlign(FormPanel.LabelAlign.TOP);
        return fl;
    }

    private NumberField<Integer> buildIntegerField(MetadataTemplateAttribute attribute) {
        NumberField<Integer> nf = new NumberField<>(new NumberPropertyEditor.IntegerPropertyEditor());
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
        NumberField<Double> nf = new NumberField<>(new NumberPropertyEditor.DoublePropertyEditor());
        nf.setAllowBlank(!attribute.isRequired());
        nf.setAllowDecimals(true);
        nf.setAllowNegative(true);

        DiskResourceMetadata avu = templateAttrAvuMap.get(attribute.getName());
        if (avu != null && !Strings.isNullOrEmpty(avu.getValue())) {
            nf.setValue(new Double(avu.getValue()));
        }

        return nf;
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


    private void loadTemplateAttributes() {
        templateAttrFieldMap.clear();
        IPlantAnchor helpLink = buildHelpLink(attributes);
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);
        hp.add(helpLink);
        widget.add(hp, new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        for (MetadataTemplateAttribute attribute : attributes) {
            Field<?> field = getAttributeValueWidget(attribute);
            if (field != null) {
                field.setReadOnly(!writable);
                templateAttrFieldMap.put(attribute.getName(), field);
                widget.add(buildFieldLabel(field,
                                           attribute.getName(),
                                           attribute.getDescription(),
                                           !attribute.isRequired()),
                           new VerticalLayoutContainer.VerticalLayoutData(.90, -1));
            }
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
        combo.setTriggerAction(ComboBoxCell.TriggerAction.ALL);
        combo.setAllowBlank(!attribute.isRequired());
        return combo;

    }

    public IPlantAnchor buildHelpLink(final List<MetadataTemplateAttribute> attributes) {
        IPlantAnchor helpLink =
                new IPlantAnchor(appearance.metadataTermGuide(), 150, new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        VerticalLayoutContainer helpVlc = new VerticalLayoutContainer();
                        helpVlc.setScrollMode(ScrollSupport.ScrollMode.AUTOY);
                        for (MetadataTemplateAttribute mta : attributes) {
                            HTML l = new HTML("<b>" + mta.getName() + ":</b> <br/>");
                            HTML helpText = new HTML("<p>" + mta.getDescription() + "</p><br/>");
                            helpVlc.add(l, new VerticalLayoutContainer.VerticalLayoutData(.25, -1));
                            helpVlc.add(helpText,
                                        new VerticalLayoutContainer.VerticalLayoutData(.90, -1));
                        }
                        Dialog w = new Dialog();
                        w.setHideOnButtonClick(true);
                        w.setSize("350", "400");
                        w.setPredefinedButtons(PredefinedButton.OK);
                        w.setHeadingText(MetadataTemplateViewDialog.this.getHeader().getText());
                        w.setBodyStyle("background: #fff;");
                        w.setWidget(helpVlc);
                        w.show();

                    }
                });

        return helpLink;
    }


}
