package org.iplantc.de.theme.base.client.diskResource.details;

import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author jstroot
 */
public class DetailsViewDefaultAppearance implements DetailsView.Appearance {

    public interface Resources extends ClientBundle {
        @Source("DetailsViewStyle.css")
        DetailsViewStyle css();
    }

    private final IplantDisplayStrings iplantDisplayStrings;
    private final DetailsViewDisplayStrings displayStrings;
    private final DiskResourceMessages diskResourceMessages;
    private final IplantErrorStrings iplantErrorStrings;
    private final IplantResources iplantResources;
    private final DetailsViewStyle style;

    public DetailsViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<DetailsViewDisplayStrings> create(DetailsViewDisplayStrings.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class),
             GWT.<IplantResources> create(IplantResources.class),
             GWT.<Resources> create(Resources.class));
    }
    public DetailsViewDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                        final DetailsViewDisplayStrings displayStrings,
                                        final DiskResourceMessages diskResourceMessages,
                                        final IplantErrorStrings iplantErrorStrings,
                                        final IplantResources iplantResources,
                                        final Resources resources) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
        this.diskResourceMessages = diskResourceMessages;
        this.iplantErrorStrings = iplantErrorStrings;
        this.iplantResources = iplantResources;
        this.style = resources.css();

        this.style.ensureInjected();
    }


    @Override
    public String coge() {
        return iplantDisplayStrings.coge();
    }

    @Override
    public String createdDate() {
        return displayStrings.createdDate();
    }

    @Override
    public String delete() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public String ensembl() {
        return displayStrings.ensembl();
    }

    @Override
    public String files() {
        return displayStrings.files();
    }

    @Override
    public String folders() {
        return displayStrings.folders();
    }

    @Override
    public String infoTypeDisabled() {
        return displayStrings.sharingDisabled();
    }

    @Override
    public String lastModified() {
        return iplantDisplayStrings.lastModified();
    }

    @Override
    public String noDetails() {
        return displayStrings.noDetails();
    }

    @Override
    public String beginSharing() {
        return displayStrings.noSharing();
    }

    @Override
    public String permissions() {
        return iplantDisplayStrings.permissions();
    }

    @Override
    public String selectInfoType() {
        return displayStrings.selectInfoType();
    }

    @Override
    public String sendTo() {
        return displayStrings.sendTo();
    }

    @Override
    public String share() {
        return displayStrings.share();
    }

    @Override
    public String sharingDisabled() {
        return displayStrings.sharingDisabled();
    }

    @Override
    public String size() {
        return diskResourceMessages.size();
    }

    @Override
    public String treeViewer() {
        return displayStrings.treeViewer();
    }

    @Override
    public DetailsViewStyle css() {
        return style;
    }

    @Override
    public String viewersDisabled() {
        return displayStrings.sharingDisabled();
    }

    @Override
    public ImageResource deselectInfoTypeIcon() {
        return iplantResources.deleteIcon();
    }
}
