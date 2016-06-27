package org.iplantc.de.theme.base.client.diskResource.metadata;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author jstroot
 */
public class MetadataViewDefaultAppearance implements MetadataView.Appearance {

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

    private final MetadataHtmlTemplates htmlTemplates;
    private final MetadataInfoTemplate infoTemplate;
    private final IplantResources iplantResources;
    private final MetadataDisplayStrings displayStrings;
    private final IplantDisplayStrings iplantDisplayStrings;


    public MetadataViewDefaultAppearance() {
        this(GWT.<MetadataHtmlTemplates> create(MetadataHtmlTemplates.class),
             GWT.<MetadataInfoTemplate> create(MetadataInfoTemplate.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<MetadataDisplayStrings> create(MetadataDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    MetadataViewDefaultAppearance(final MetadataHtmlTemplates htmlTemplates,
                                  final MetadataInfoTemplate infoTemplate,
                                  final IplantResources iplantResources,
                                  final MetadataDisplayStrings displayStrings,
                                  final IplantDisplayStrings iplantDisplayStrings) {
        this.htmlTemplates = htmlTemplates;
        this.infoTemplate = infoTemplate;
        this.iplantResources = iplantResources;
        this.displayStrings = displayStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }

    @Override
    public String attribute() {
        return displayStrings.attribute();
    }

    @Override
    public SafeHtml boldHeader(String name) {
        return htmlTemplates.boldHeader(name);
    }

    @Override
    public SafeHtml buildLabelWithDescription(String label, String description,
                                              boolean allowBlank) {
        if (label == null) {
            return null;
        }
        SafeUri infoUri = iplantResources.info().getSafeUri();
        SafeHtml infoImg = Strings.isNullOrEmpty(description) ? SafeHtmlUtils.fromString("") //$NON-NLS-1$
                               : htmlTemplates.labelInfo(infoUri, description);
        SafeHtml required = allowBlank ? SafeHtmlUtils.fromString("") : htmlTemplates.required(); //$NON-NLS-1$

        return htmlTemplates.labelHtml(infoImg, label, required);
    }

    @Override
    public String confirmAction() {
        return iplantDisplayStrings.confirmAction();
    }

    @Override
    public String metadataTemplateConfirmRemove() {
        return displayStrings.metadataTemplateConfirmRemove();
    }

    @Override
    public String metadataTemplateRemove() {
        return displayStrings.metadataTemplateRemove();
    }

    @Override
    public String metadataTemplateSelect() {
        return displayStrings.metadataTemplateSelect();
    }

    @Override
    public String newAttribute() {
        return displayStrings.newAttribute();
    }

    @Override
    public String newValue() {
        return displayStrings.newValue();
    }

    @Override
    public String newUnit() {
        return displayStrings.newUnit();
    }

    @Override
    public String paramValue() {
        return iplantDisplayStrings.paramValue();
    }

    @Override
    public SafeHtml renderComboBoxHtml(MetadataTemplateInfo object) {
        return infoTemplate.templateInfo(object.getName());
    }

    @Override
    public void renderMetadataCell(SafeHtmlBuilder sb, String value) {
        if(Strings.isNullOrEmpty(value)){
            return;
        }
        sb.append(htmlTemplates.cell(value));
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String userMetadata() {
        return displayStrings.userMetadata();
    }

    @Override
    public String add() {
        return iplantDisplayStrings.add();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return iplantResources.delete();
    }

    @Override
    public String metadataTermGuide() {
        return displayStrings.metadataTermGuide();
    }

	@Override
	public String additionalMetadata() {
		return displayStrings.additionalMetadata();
	}

	@Override
	public String paramUnit() {
		return iplantDisplayStrings.paramUnit();
	}

    @Override
    public String selectTemplate() {
        return displayStrings.selectTemplate();
    }

    @Override
    public String importMd() {
        return displayStrings.importMd();
    }

    @Override
    public String panelWidth() {
        return "575";
    }

    @Override
    public String panelHeight() {
        return "475";
    }

    @Override
    public ImageResource editIcon() {
        return iplantResources.edit();
    }

    @Override
    public String edit() {
        return iplantDisplayStrings.edit();
    }
}
