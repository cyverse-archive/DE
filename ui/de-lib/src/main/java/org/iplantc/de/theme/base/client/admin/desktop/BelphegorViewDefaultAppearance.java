package org.iplantc.de.theme.base.client.admin.desktop;

import org.iplantc.de.theme.base.client.admin.BelphegorConstants;
import org.iplantc.de.admin.desktop.client.views.BelphegorView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;

import com.sencha.gxt.core.client.XTemplates;

/**
 * @author jstroot
 */
public class BelphegorViewDefaultAppearance implements BelphegorView.BelphegorViewAppearance{

    public interface MyTemplate extends XTemplates {
        @XTemplates.XTemplate("<div class='{style.iplantcHeader}'>" + "<table><tbody><tr>"
                + "<td role='presentation' align='LEFT' valign='TOP'><a style='outline-style: none;' href='{iplantHome}' target='_blank'><div class='{style.iplantcLogo}'></div></a></td>"
                + "<td role='presentation' align='LEFT' valign='TOP'><div class='{style.headerMenu}'></div>"
                + "</td></tr></tbody></table></div>")
        SafeHtml getTemplate(BelphegorView.BelphegorStyle style, SafeUri iplantHome);
    }

    interface Resources extends ClientBundle {
        @Source("BelphegorStyle.css")
        BelphegorView.BelphegorStyle style();

        @Source("headerlogo.png")
        ImageResource headerLogo();

        @Source("headerlogo-fill.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource headerLogoFill();
    }

    private final BelphegorConstants constants;
    private final BelphegorDisplayStrings belphegorDisplayStrings;
    private final IplantDisplayStrings displayStrings;
    private final BelphegorView.BelphegorStyle style;
    private final MyTemplate template;

    public BelphegorViewDefaultAppearance() {
        this(GWT.<BelphegorConstants> create(BelphegorConstants.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<MyTemplate> create(MyTemplate.class),
             GWT.<Resources> create(Resources.class));
    }
    BelphegorViewDefaultAppearance(final BelphegorConstants constants,
                                   final BelphegorDisplayStrings belphegorDisplayStrings,
                                   final IplantDisplayStrings displayStrings,
                                   final MyTemplate template,
                                   final Resources resources) {
        this.constants = constants;
        this.belphegorDisplayStrings = belphegorDisplayStrings;
        this.displayStrings = displayStrings;
        this.style = resources.style();
        this.template = template;
        this.style.ensureInjected();
    }

    @Override
    public String applications() {
        return displayStrings.applications();
    }

    @Override
    public String logout() {
        return displayStrings.logout();
    }

    @Override
    public String logoutWindowUrl() {
        return GWT.getHostPageBaseURL() + constants.logoutUrl();
    }

    @Override
    public SafeHtml renderNorthContainer() {
        return template.getTemplate(style(), UriUtils.fromSafeConstant(constants.cyverseHome()));
    }

    @Override
    public BelphegorView.BelphegorStyle style() {
        return style;
    }

    @Override
    public SafeHtml nsfProjectText() {
        return displayStrings.nsfProjectText();
    }

    @Override
    public SafeHtml projectCopyrightStatement() {
        return displayStrings.projectCopyrightStatement();
    }

    @Override
    public String referenceGenomes() {
        return belphegorDisplayStrings.referenceGenomes();
    }

    @Override
    public String toolRequests() {
        return belphegorDisplayStrings.toolRequests();
    }

    @Override
    public String toolAdmin(){
        return belphegorDisplayStrings.toolAdmin();
    }

    @Override
    public String ontologies() {
        return belphegorDisplayStrings.ontologies();
    }

    @Override
    public String systemMessages() {
        return belphegorDisplayStrings.systemMessages();
    }
}
