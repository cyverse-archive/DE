package org.iplantc.de.theme.base.client.apps.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppNameCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppSearchHighlightAppearance;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AppNameCellDefaultAppearance implements AppNameCell.AppNameCellAppearance {

    public interface MyCss extends CssResource {
        String appHyperlinkName();

        String appName();

        String appDisabled();

        String appBeta();
    }

    public interface Resources extends ClientBundle {
        @Source("AppNameCell.css")
        MyCss css();
    }

    public interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<span name='{3}' class='{0}' qtip='{2}'>{1}</span>")
        SafeHtml cell(String textClassName, SafeHtml name, String textToolTip, String elementName);

        @SafeHtmlTemplates.Template("<span id='{4}' name='{3}' class='{0}' qtip='{2}'>{1}</span>")
        SafeHtml debugCell(String textClassName, SafeHtml name, String textToolTip, String elementName, String debugId);
    }

    private final Templates templates;
    protected final Resources resources;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final AppsMessages appsMessages;
    private final AppSearchHighlightAppearance highlightAppearance;

    public AppNameCellDefaultAppearance() {
        this(GWT.<Templates> create(Templates.class),
             GWT.<Resources> create(Resources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<AppsMessages> create(AppsMessages.class),
             GWT.<AppSearchHighlightAppearance> create(AppSearchHighlightAppearance.class));
    }

    AppNameCellDefaultAppearance(final Templates templates,
                                 final Resources resources,
                                 final IplantDisplayStrings iplantDisplayStrings,
                                 final AppsMessages appsMessages,
                                 final AppSearchHighlightAppearance highlightAppearance) {
        this.templates = templates;
        this.resources = resources;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.appsMessages = appsMessages;
        this.highlightAppearance = highlightAppearance;
        this.resources.css().ensureInjected();
    }

    @Override
    public String appDisabledClass() {
        return resources.css().appDisabled();
    }

    @Override
    public String appHyperlinkNameClass() {
        return resources.css().appHyperlinkName();
    }

    @Override
    public String appUnavailable() {
        return iplantDisplayStrings.appUnavailable();
    }

    @Override
    public String appBeta() {
        return appsMessages.betaToolTip();
    }

    @Override
    public String appBetaNameClass() {
        return resources.css().appBeta();
    }

    @Override
    public void render(final SafeHtmlBuilder sb,
                       final App value,
                       final String textClassName,
                       final String pattern,
                       final String textToolTip,
                       final String debugId) {
        SafeHtml highlightText = SafeHtmlUtils.fromTrustedString(highlightAppearance.highlightText(value.getName(), pattern));

        sb.appendHtmlConstant("&nbsp;");
        if(DebugInfo.isDebugIdEnabled()
               && !Strings.isNullOrEmpty(debugId)){
            sb.append(templates.debugCell(textClassName, highlightText, textToolTip, ELEMENT_NAME, debugId));
        }else {
            sb.append(templates.cell(textClassName, highlightText, textToolTip, ELEMENT_NAME));
        }
    }

    @Override
    public String run() {
        return appsMessages.run();
    }
}
