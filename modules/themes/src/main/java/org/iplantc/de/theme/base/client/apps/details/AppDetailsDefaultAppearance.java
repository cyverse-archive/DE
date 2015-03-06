package org.iplantc.de.theme.base.client.apps.details;

import org.iplantc.de.apps.client.AppDetailsView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

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

    public AppDetailsDefaultAppearance() {
        this(GWT.<AppsMessages> create(AppsMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<AppDetailsAppearanceResources> create(AppDetailsAppearanceResources.class));
    }

    AppDetailsDefaultAppearance(final AppsMessages appsMessages,
                                final IplantDisplayStrings iplantDisplayStrings,
                                final AppDetailsAppearanceResources resources) {
        this.appsMessages = appsMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.resources = resources;
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
        return iplantDisplayStrings.toolTab();
    }

    @Override
    public String toolInformationTabLabel() {
        return iplantDisplayStrings.information();
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
        return iplantDisplayStrings.version() + ": ";
    }

    @Override
    public String toolAttributionLabel() {
        return iplantDisplayStrings.attribution() + ": ";
    }
}
