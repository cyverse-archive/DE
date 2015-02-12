package org.iplantc.de.theme.base.client.diskResource.presenter;

import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class DiskResourceViewPresenterDefaultAppearance implements DiskResourceView.Presenter.Appearance {
    private final DiskResourceMessages diskResourceMessages;
    private final IplantDisplayStrings iplantDisplayStrings;

    public DiskResourceViewPresenterDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    DiskResourceViewPresenterDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                               final IplantDisplayStrings iplantDisplayStrings) {
        this.diskResourceMessages = diskResourceMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }

    @Override
    public String createFolderLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String deleteMsg() {
        return diskResourceMessages.deleteMsg();
    }

    @Override
    public String deleteTrash() {
        return diskResourceMessages.deleteTrash();
    }

    @Override
    public String diskResourceIncompleteMove() {
        return diskResourceMessages.diskResourceIncompleteMove();
    }

    @Override
    public String emptyTrash() {
        return diskResourceMessages.emptyTrash();
    }

    @Override
    public String emptyTrashWarning() {
        return diskResourceMessages.emptyTrashWarning();
    }

    @Override
    public String loadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String moveDiskResourcesLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String permissionErrorMessage() {
        return diskResourceMessages.permissionErrorMessage();
    }

    @Override
    public String renameDiskResourcesLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String unsupportedCogeInfoType() {
        return diskResourceMessages.unsupportedCogeInfoType();
    }

    @Override
    public String unsupportedEnsemblInfoType() {
        return diskResourceMessages.unsupportedEnsemblInfoType();
    }

    @Override
    public String unsupportedTreeInfoType() {
        return diskResourceMessages.unsupportedTreeInfoType();
    }

    @Override
    public String warning() {
        return iplantDisplayStrings.warning();
    }
}
