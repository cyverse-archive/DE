package org.iplantc.de.diskResource.client.presenters.callbacks;

import java.util.List;

/**
 * Created by jstroot on 2/10/15.
 * @author jstroot
 */
public interface DiskResourceCallbackAppearance {
    String createDataLinksError();

    String createFolderFailed();

    String deleteDataLinksError();

    String deleteFailed();

    String diskResourceMoveSuccess(String dest, List<String> sources);

    String listDataLinksError();

    String metadataSuccess();

    String metadataUpdateFailed();

    String moveFailed();

    String partialRestore();

    String renameFailed();

    String restoreDefaultMsg();

    String restoreMsg();

    String ncbiCreateFolderStructureSuccess();
}
