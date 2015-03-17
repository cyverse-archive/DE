package org.iplantc.de.theme.base.client.apps.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppInfoCell;
import org.iplantc.de.resources.client.IplantResources;
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
 * @author jstroot
 */
public class AppInfoCellDefaultAppearance implements AppInfoCell.AppInfoCellAppearance {

    interface MyCss extends CssResource {
        @CssResource.ClassName("app_info")
        String appRun();
    }

    interface Resources extends ClientBundle {
        @Source("AppInfoCell.css")
        MyCss css();
    }

    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<img class='{0}' qtip='{2}' src='{1}'/>")
        SafeHtml cell(String imgClassName, SafeUri img, String toolTip);

        @SafeHtmlTemplates.Template("<img id='{3}' class='{0}' qtip='{2}' src='{1}'/>")
        SafeHtml debugCell(String imgClassName, SafeUri img, String toolTip, String debugId);
    }

    private final Templates templates;
    private final Resources resources;
    private final AppsMessages appsMessages;
    private final IplantResources iplantResources;


    public AppInfoCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<Resources> create(Resources.class),
             GWT.<AppsMessages> create(AppsMessages.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    AppInfoCellDefaultAppearance(final Templates templates,
                                 final Resources resources,
                                 final AppsMessages appsMessages,
                                 final IplantResources iplantResources) {
        this.templates = templates;
        this.resources = resources;
        this.appsMessages = appsMessages;
        this.iplantResources = iplantResources;
        this.resources.css().ensureInjected();
    }

    @Override
    public void render(SafeHtmlBuilder sb,
                       String debugId) {
        String imgClassName, tooltip;
        imgClassName = resources.css().appRun();
        tooltip = appsMessages.clickAppInfo();
        final SafeUri safeUri = iplantResources.info().getSafeUri();
          if(DebugInfo.isDebugIdEnabled()){
            sb.append(templates.debugCell(imgClassName, safeUri, tooltip, debugId));
        }else {
            sb.append(templates.cell(imgClassName, safeUri, tooltip));
        }
    }
}
