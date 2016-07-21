package org.iplantc.de.theme.base.client.diskResource.metadata;

import org.iplantc.de.diskResource.client.views.metadata.dialogs.DownloadTemplateCell;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.theme.base.client.admin.metadata.*;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * Created by sriram on 6/27/16.
 */
public class DownloadTemplateCellDefaultAppearance implements DownloadTemplateCell.DownloadTemplateCellAppearance {

    interface  MyCss extends CssResource {
        @CssResource.ClassName("download")
        String download();
    }

    interface Resources extends ClientBundle {
        @Source("DownloadTemplateCell.css")
        MyCss css();
    }

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<img name='{2}' class='{0}' title='{2}' src='{1}' />")
        SafeHtml cell(String imgClassName, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("<img name='{2}' id='{3}' class='{0}' title='{2}' src='{1}'/>")
        SafeHtml debugCell(String imgClassName, SafeUri img, String toolTip, String debugId);
    }

    private final Templates templates;
    private final Resources resources;
    private final IplantResources iplantResources;

    public DownloadTemplateCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<Resources> create(Resources.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    DownloadTemplateCellDefaultAppearance(final Templates templates,
                                          final Resources resources,
                                          final IplantResources iplantResources)  {
        this.templates = templates;
        this.resources = resources;
        this.iplantResources = iplantResources;
        this.resources.css().ensureInjected();
    }

    @Override
    public void render(SafeHtmlBuilder sb, String debugId) {
        String imgClassName, tooltip;
        imgClassName = resources.css().download();
        tooltip = download();
        final SafeUri safeUri = iplantResources.arrowDown().getSafeUri();
        if(DebugInfo.isDebugIdEnabled()){
            sb.append(templates.debugCell(imgClassName, safeUri, tooltip, debugId));
        }else {
            sb.append(templates.cell(imgClassName, safeUri, tooltip));
        }

    }

    @Override
    public String download() {
        return "Download";
    }
}
