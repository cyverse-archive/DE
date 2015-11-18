package org.iplantc.de.theme.base.client.apps.toolbar;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class AppsToolbarViewDefaultAppearance implements AppsToolbarView.AppsToolbarAppearance {
    private final AppsMessages appsMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantResources iplantResources;

    public AppsToolbarViewDefaultAppearance() {
        this(GWT.<AppsMessages> create(AppsMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantResources> create(IplantResources.class));
    }

    AppsToolbarViewDefaultAppearance(final AppsMessages appsMessages,
                                     final IplantDisplayStrings iplantDisplayStrings,
                                     final IplantResources iplantResources) {
        this.appsMessages = appsMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantResources = iplantResources;
    }

    @Override
    public String appDeleteWarning() {
        return appsMessages.appDeleteWarning();
    }

    @Override
    public String failToRetrieveApp() {
        return appsMessages.failToRetrieveApp();
    }

    @Override
    public String submitForPublicUse() {
        return appsMessages.submitForPublicUse();
    }

    @Override
    public String applications() {
        return iplantDisplayStrings.applications();
    }

    @Override
    public String run() {
        return appsMessages.run();
    }

    @Override
    public ImageResource runIcon() {
        return iplantResources.run();
    }

    @Override
    public String newApp() {
        return appsMessages.newApp();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String requestTool() {
        return appsMessages.requestTool();
    }

    @Override
    public String copy() {
        return iplantDisplayStrings.copy();
    }

    @Override
    public ImageResource copyIcon() {
        return iplantResources.copy();
    }

    @Override
    public String editMenuItem() {
        return appsMessages.editMenuItem();
    }

    @Override
    public ImageResource editIcon() {
        return iplantResources.edit();
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
    public String shareMenuItem() {
        return appsMessages.shareMenuItem();
    }

    @Override
    public ImageResource submitForPublicIcon() {
        return iplantResources.submitForPublic();
    }

    @Override
    public ImageResource shareAppIcon() {
        return iplantResources.share();
    }

    @Override
    public String warning() {
        return iplantDisplayStrings.warning();
    }

    @Override
    public String workflow() {
        return appsMessages.workflow();
    }

    @Override
    public String useWf() {
        return appsMessages.useWf();
    }

    @Override
    public String searchApps() {
        return appsMessages.searchApps();
    }

    @Override
    public String share() {
        return appsMessages.share();
    }

    @Override
    public String shareCollab() {
        return appsMessages.shareCollab();
    }

    @Override
    public String sharePublic() {
        return appsMessages.sharePublic();
    }
}
