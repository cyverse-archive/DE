package org.iplantc.de.theme.base.client.diskResource.grid.presenter;

import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;
import org.iplantc.de.theme.base.client.diskResource.grid.GridViewDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class GridViewPresenterDefaultAppearance implements GridView.Presenter.Appearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantErrorStrings iplantErrorStrings;
    private final GridViewDisplayStrings displayStrings;
    private final DiskResourceMessages diskResourceMessages;

    public GridViewPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class),
             GWT.<GridViewDisplayStrings> create(GridViewDisplayStrings.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    GridViewPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                       final IplantErrorStrings iplantErrorStrings,
                                       final GridViewDisplayStrings displayStrings,
                                       final DiskResourceMessages diskResourceMessages) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
        this.displayStrings = displayStrings;
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String comments() {
        return iplantDisplayStrings.comments();
    }

    @Override
    public String commentsDialogHeight() {
        return "450px";
    }

    @Override
    public String commentsDialogWidth() {
        return "600px";
    }

    @Override
    public String copy() {
        return iplantDisplayStrings.copy();
    }

    @Override
    public String copyPasteInstructions() {
        return iplantDisplayStrings.copyPasteInstructions();
    }

    @Override
    public String createDataLinksError() {
        return diskResourceMessages.createDataLinksError();
    }

    @Override
    public String markFavoriteError() {
        return displayStrings.markFavoriteError();
    }

    @Override
    public String metadata() {
        return displayStrings.metadata();
    }

    @Override
    public String metadataDialogHeight() {
        return "400";
    }

    @Override
    public String metadataDialogWidth() {
        return "600";
    }

    @Override
    public String metadataFormInvalid() {
        return displayStrings.metadataFormInvalid();
    }

    @Override
    public String metadataHelp() {
        return displayStrings.metadataHelp();
    }

    @Override
    public String removeFavoriteError() {
        return displayStrings.removeFavoriteError();
    }

    @Override
    public String retrieveStatFailed() {
        return displayStrings.retrieveStatError();
    }

    @Override
    public String shareLinkDialogHeight() {
        return "130";
    }

    @Override
    public int shareLinkDialogTextBoxWidth() {
        return 500;
    }

    @Override
    public String shareLinkDialogWidth() {
        return "535";
    }
}
