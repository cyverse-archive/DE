package org.iplantc.de.theme.base.client.diskResource.presenter;

import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

import java.util.ArrayList;

/**
 * @author jstroot
 */
public class DiskResourceViewPresenterDefaultAppearance implements DiskResourceView.Presenter.Appearance {
    private final DiskResourceMessages diskResourceMessages;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantErrorStrings iplantErrorStrings;

    public DiskResourceViewPresenterDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class));
    }

    DiskResourceViewPresenterDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                               final IplantDisplayStrings iplantDisplayStrings,
                                               final IplantErrorStrings iplantErrorStrings) {
        this.diskResourceMessages = diskResourceMessages;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
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
    public String duplicateCheckFailed() {
        return diskResourceMessages.duplicateCheckFailed();
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
    public String fileName() {
        return diskResourceMessages.fileName();
    }

    @Override
    public String fileUploadSuccess(String filename) {
        return iplantDisplayStrings.fileUploadSuccess(filename);
    }

    @Override
    public String fileUploadsFailed(ArrayList<String> files) {
        return iplantErrorStrings.fileUploadsFailed(files);
    }

    @Override
    public String folderName() {
        return diskResourceMessages.folderName();
    }

    @Override
    public String idParentInvalid() {
        return diskResourceMessages.idParentInvalid();
    }

    @Override
    public String importFailed(String sourceUrl) {
        return iplantErrorStrings.importFailed(sourceUrl);
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
    public String rename() {
        return iplantDisplayStrings.rename();
    }

    @Override
    public String renameDiskResourcesLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String folderRefreshFailed(String folder) {
        return iplantErrorStrings.folderRefreshFailed(folder);
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

    @Override
    public String details() {
        return iplantDisplayStrings.details();
    }
}
