package org.iplantc.de.theme.base.client.diskResource.metadata;

import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.TemplateNameCell;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * Created by sriram on 7/7/16.
 */
public class TemplateNameCellDefaultAppearance implements TemplateNameCell.TemplateNameCellAppearance {

    interface  MyCss extends CssResource {
        @CssResource.ClassName("info")
        String info();

        String background();

    }

    interface Resources extends ClientBundle {
        @Source("TemplateNameCell.css")
        MyCss css();
    }

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<img class='{0}' title='{2}' src='{1}'>{2}</img>")
        SafeHtml cell(String imgClassName, SafeUri img, String templateName);

        @SafeHtmlTemplates.Template("<img id='{3}' class='{0}' title='{2}' src='{1}'>{2}</img>")
        SafeHtml debugCell(String imgClassName, SafeUri img, String templateName, String debugId);
    }

    private final Templates templates;
    private final Resources resources;
    private final IplantResources iplantResources;


    public TemplateNameCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<Resources> create(Resources.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    TemplateNameCellDefaultAppearance(final Templates templates,
                                          final Resources resources,
                                          final IplantResources iplantResources)  {
        this.templates = templates;
        this.resources = resources;
        this.iplantResources = iplantResources;
        this.resources.css().ensureInjected();
    }

    @Override
    public void render(SafeHtmlBuilder sb, MetadataTemplateInfo value) {
        String imgClassName, tooltip;
        imgClassName = resources.css().info();
        tooltip = "info";
        final SafeUri safeUri = iplantResources.info().getSafeUri();
        if(DebugInfo.isDebugIdEnabled()){
            sb.append(templates.debugCell(imgClassName, safeUri, value.getName(), value.getId() + "-info"));
        } else {
            sb.append(templates.cell(imgClassName, safeUri, value.getName()));
        }

    }

    @Override
    public String Description() {
        return "Description";
    }

    @Override
    public String background() {
        return resources.css().background();
    }
}
