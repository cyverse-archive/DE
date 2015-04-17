package org.iplantc.de.theme.base.client.diskResource.presenter;

import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceCallbackAppearance;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

import java.util.List;

/**
 * @author jstroot
 */
public class DiskResourceCallbackDefaultAppearance implements DiskResourceCallbackAppearance {
    private final DiskResourceMessages displayMessages;

    public DiskResourceCallbackDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    DiskResourceCallbackDefaultAppearance(final DiskResourceMessages displayMessages) {
        this.displayMessages = displayMessages;
    }

    @Override
    public String createDataLinksError() {
        return displayMessages.createDataLinksError();
    }

    @Override
    public String createFolderFailed() {
        return displayMessages.createFolderFailed();
    }

    @Override
    public String deleteDataLinksError() {
        return displayMessages.deleteDataLinksError();
    }

    @Override
    public String deleteFailed() {
        return displayMessages.deleteFailed();
    }

    @Override
    public String diskResourceMoveSuccess(String dest, List<String> sources) {
        return displayMessages.diskResourceMoveSuccess(dest, sources);
    }

    @Override
    public String listDataLinksError() {
        return displayMessages.listDataLinksError();
    }

    @Override
    public String metadataSuccess() {
        return displayMessages.metadataSuccess();
    }

    @Override
    public String metadataUpdateFailed() {
        return displayMessages.metadataUpdateFailed();
    }

    @Override
    public String moveFailed() {
        return displayMessages.moveFailed();
    }

    @Override
    public String partialRestore() {
        return displayMessages.partialRestore();
    }

    @Override
    public String renameFailed() {
        return displayMessages.renameFailed();
    }

    @Override
    public String restoreDefaultMsg() {
        return displayMessages.restoreDefaultMsg();
    }

    @Override
    public String restoreMsg() {
        return displayMessages.restoreMsg();
    }

    @Override
    public String ncbiCreateFolderStructureSuccess() {
        return displayMessages.ncbiCreateFolderStructureSuccess();
    }
}
