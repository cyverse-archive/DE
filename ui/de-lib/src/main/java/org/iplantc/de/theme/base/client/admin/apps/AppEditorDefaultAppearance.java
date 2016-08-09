package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.theme.base.client.admin.BelphegorConstants;
import org.iplantc.de.admin.apps.client.views.editor.AppEditor;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorErrorStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AppEditorDefaultAppearance implements AppEditor.AppEditorAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final BelphegorDisplayStrings displayStrings;
    private final BelphegorErrorStrings errorStrings;
    private final BelphegorConstants constants;
    private final CommonUiConstants uiConstants;

    public AppEditorDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<BelphegorErrorStrings> create(BelphegorErrorStrings.class),
             GWT.<BelphegorConstants> create(BelphegorConstants.class),
             GWT.<CommonUiConstants> create(CommonUiConstants.class));
    }

    AppEditorDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                               final BelphegorDisplayStrings displayStrings,
                               final BelphegorErrorStrings errorStrings,
                               final BelphegorConstants constants,
                               final CommonUiConstants uiConstants) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.constants = constants;
        this.uiConstants = uiConstants;
    }

    @Override
    public String appDocumentationLabel() {
        return displayStrings.appDocumentationLabel();
    }

    @Override
    public String appEditorWidth() {
        return "595";
    }

    @Override
    public String appName() {
        return iplantDisplayStrings.name();
    }

    @Override
    public String appNameRestrictedChars() {
        return uiConstants.appNameRestrictedChars();
    }

    @Override
    public String appNameRestrictedStartingChars() {
        return uiConstants.appNameRestrictedStartingChars();
    }

    @Override
    public String docHelpHtml() {
        return displayStrings.docHelpHtml();
    }

    @Override
    public String integratorName() {
        return iplantDisplayStrings.integratorName();
    }

    @Override
    public String integratorEmail() {
        return iplantDisplayStrings.integratorEmail();
    }

    @Override
    public String invalidAppNameMsg(String badStartChars, String badChars) {
        return errorStrings.invalidAppNameMsg(badStartChars, badChars);
    }

    @Override
    public String tempDisable() {
        return displayStrings.tempDisable();
    }

    @Override
    public String appDisabled() {
        return iplantDisplayStrings.appDisabled();
    }

    @Override
    public String appDescription() {
        return displayStrings.appDescription();
    }

    @Override
    public String templateLinkPopupHeading() {
        return displayStrings.templateLinkPopupHeading();
    }

    @Override
    public String templateLinkTitle() {
        return displayStrings.templateLinkTitle();
    }

    @Override
    public String validDocError() {
        return errorStrings.validDocError();
    }

    @Override
    public SafeHtml wikiUrlFieldLabel() {
        return SafeHtmlUtils.fromTrustedString(iplantDisplayStrings.wikiUrlLabel(constants.publishDocumentationUrl()));
    }

    @Override
    public String betaLabel() {
        return displayStrings.betaLabel();
    }

    @Override
    public String betaCheckBoxText() {
        return displayStrings.betaCheckBoxText();
    }
}
