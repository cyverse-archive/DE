package org.iplantc.de.theme.base.client.diskResource.toolbar;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public interface ToolbarDisplayMessages extends Messages {
    SafeHtml bulkDownloadInfoBoxHeading();

    SafeHtml bulkDownloadInfoBoxMsg();

    @Key("bulkDownloadMenuItem")
    String bulkDownloadMenuItem();

    @Key("bulkUploadFromDesktop")
    String bulkUploadFromDesktop();

    SafeHtml bulkUploadInfoBoxHeading();

    SafeHtml bulkUploadInfoBoxMsg();

    @Key("createPublicLinkMenuItem")
    String createPublicLinkMenuItem();

    @Key("download")
    String download();

    @Key("duplicateMenuItem")
    String duplicateMenuItem();

    @Key("editCommentsMenuItem")
    String editCommentsMenuItem();

    @Key("editFileMenuItem")
    String editFileMenuItem();

    @Key("editInfoTypeMenuItem")
    String editInfoTypeMenuItem();

    SafeHtml externalHyperlink(String url, String hyperlinkText);

    @Key("importFromUrlMenuItem")
    String importFromUrlMenuItem();

    @Key("manageDataLinks")
    String manageDataLinks();

    @Key("manageDataLinksHelp")
    String manageDataLinksHelp();

    @Key("metadataMenuItem")
    String metadataMenuItem();

    @Key("moveMenuItem")
    String moveMenuItem();

    @Key("moveToTrashMenuItem")
    String moveToTrashMenuItem();

    @Key("newDataWindowAtLocMenuItem")
    String newDataWindowAtLocMenuItem();

    @Key("newFolderMenuItem")
    String newFolderMenuItem();

    @Key("newPerlFileMenuItem")
    String newPerlFileMenuItem();

    @Key("newPlainTextFileMenuItem")
    String newPlainTextFileMenuItem();

    @Key("newPythonFileMenuItem")
    String newPythonFileMenuItem();

    @Key("newRFileMenuItem")
    String newRFileMenuItem();

    @Key("newShellFileMenuItem")
    String newShellFileMenuItem();

    @Key("newTabularDataFileMenuItem")
    String newTabularDataFileMenuItem();

    @Key("newWindow")
    String newWindow();

    @Key("openTrashMenuItem")
    String openTrashMenuItem();

    @Key("renameMenuItem")
    String renameMenuItem();

    @Key("restore")
    String restore();

    @Key("shareFolderLocationMenuItem")
    String shareFolderLocationMenuItem();

    @Key("shareWithCollaboratorsMenuItem")
    String shareWithCollaboratorsMenuItem();

    @Key("simpleDownloadMenuItem")
    String simpleDownloadMenuItem();

    @Key("simpleUploadFromDesktop")
    String simpleUploadFromDesktop();

    @Key("trashMenu")
    String trashMenu();

    @Key("upload")
    String upload();

    String newMdFileMenuItem();

    String copyMetadataMenuItem();

    String saveMetadataMenuItem();

    String sendToNcbiSraItem();

    String importFromCoge();

    String selectMetadataFile();

    String applyBulkMetadata();

}
