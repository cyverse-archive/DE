package org.iplantc.de.theme.base.client.apps.details;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppSearchHighlightAppearance;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import java.util.List;

/**
 * @author jstroot
 */
public class AppDetailsDefaultAppearance implements AppDetailsView.AppDetailsAppearance {

    public interface AppDetailsAppearanceResources extends ClientBundle {
        @Source("AppDetailsStyle.css")
        AppDetailsStyle css();
    }

    private final AppsMessages appsMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final AppDetailsAppearanceResources resources;
    private final AppSearchHighlightAppearance highlightAppearance;

    public AppDetailsDefaultAppearance() {
        this(GWT.<AppsMessages> create(AppsMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<AppDetailsAppearanceResources> create(AppDetailsAppearanceResources.class),
             GWT.<AppSearchHighlightAppearance> create(AppSearchHighlightAppearance.class));
    }

    AppDetailsDefaultAppearance(final AppsMessages appsMessages,
                                final IplantDisplayStrings iplantDisplayStrings,
                                final AppDetailsAppearanceResources resources,
                                final AppSearchHighlightAppearance highlightAppearance) {
        this.appsMessages = appsMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.resources = resources;
        this.highlightAppearance = highlightAppearance;
        this.resources.css().ensureInjected();
    }

    @Override
    public String descriptionLabel() {
        return iplantDisplayStrings.description() + ": ";
    }

    @Override
    public AppDetailsStyle css() {
        return resources.css();
    }

    @Override
    public String detailsLabel() {
        return iplantDisplayStrings.details() + ": ";
    }

    @Override
    public SafeHtml getAppDocError(Throwable caught) {
        return appsMessages.getAppDocError(caught.getMessage());
    }

    @Override
    public SafeHtml getCategoriesHtml(List<List<String>> appGroupHierarchies) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        for(List<String> appGroups : appGroupHierarchies){
            final String join = Joiner.on(" >> ").join(appGroups);
            sb.appendEscaped(join);
            sb.appendHtmlConstant("<br/>");
        }
        return sb.toSafeHtml();
    }

    @Override
    public SafeHtml highlightText(String value, String searchRegexPattern) {
        SafeHtml highlightText = SafeHtmlUtils.fromTrustedString(highlightAppearance.highlightText(value, searchRegexPattern));
        return highlightText;
    }

    @Override
    public String publishedOnLabel() {
        return appsMessages.publishedOn() + ": ";
    }

    @Override
    public String integratorNameLabel() {
        return iplantDisplayStrings.integratorName() + ": ";
    }

    @Override
    public String integratorEmailLabel() {
        return iplantDisplayStrings.integratorEmail() + ": ";
    }

    @Override
    public String helpLabel() {
        return iplantDisplayStrings.help() + ": ";
    }

    @Override
    public String ratingLabel() {
        return appsMessages.rating() + ": ";
    }

    @Override
    public String categoriesLabel() {
        return iplantDisplayStrings.category() + ": ";
    }

    @Override
    public String informationTabLabel() {
        return iplantDisplayStrings.information();
    }

    @Override
    public SafeHtml saveAppDocError(Throwable caught) {
        return appsMessages.saveAppDocError(caught.getMessage());
    }

    @Override
    public String toolInformationTabLabel() {
        return iplantDisplayStrings.toolTab();
    }

    @Override
    public String toolNameLabel() {
        return iplantDisplayStrings.name() + ": ";
    }

    @Override
    public String toolPathLabel() {
        return iplantDisplayStrings.path() + ": ";
    }

    @Override
    public String toolVersionLabel() {
        return appsMessages.version() + ": ";
    }

    @Override
    public String toolAttributionLabel() {
        return iplantDisplayStrings.attribution() + ": ";
    }

    @Override
    public String userManual() {
        return iplantDisplayStrings.documentation();
    }

    @Override
    public String url() {
        return appsMessages.url();
    }

    @Override
    public String appUrl() {
        return appsMessages.appUrl();
    }

    @Override
    public String copyAppUrl() {
        return appsMessages.copyAppUrl();
    }
}
