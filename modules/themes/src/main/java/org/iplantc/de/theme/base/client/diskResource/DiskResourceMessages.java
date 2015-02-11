package org.iplantc.de.theme.base.client.diskResource;

import com.google.gwt.i18n.client.Messages;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourceMessages extends Messages {

    String collapseAll();

    String createDataLinksError();

    String createFolderFailed();

    String createIn(String path);

    String deleteDataLinksError();

    String deleteFailed();

    @DefaultMessage("Selected items moved to {0}.")
    @AlternateMessage({"=1", "Selected item moved to {0}."})
    @Key("diskResourceMoveSuccess")
    String diskResourceMoveSuccess(String destPath, @Optional @PluralCount List<String> srcPaths);

    @Key("fileFolderDialogHeaderText")
    String fileFolderDialogHeaderText();

    @Key("fileFolderSelectorFieldEmptyText")
    String fileFolderSelectorFieldEmptyText();

    @Key("fileSelectDialogHeaderText")
    String fileSelectDialogHeaderText();

    String folderName();

    String listDataLinksError();

    String metadataSuccess();

    String metadataUpdateFailed();

    String moveFailed();

    String newFolder();

    String partialRestore();

    String renameFailed();

    String restoreDefaultMsg();

    String restoreMsg();

    @Key("selectedFile")
    String selectedFile();

    @Key("selectedFolder")
    String selectedFolder();

    @Key("folderSelectDialogHeaderText")
    String folderSelectDialogHeaderText();

    @Key("newPathListMenuText")
    String newPathListMenuText();

    @Key("selectedItem")
    String selectedItem();

    @Key("share")
    String share();

    @Key("size")
    String size();

    @Key("unsupportedCogeInfoType")
    String unsupportedCogeInfoType();

    @Key("unsupportedEnsemblInfoType")
    String unsupportedEnsemblInfoType();

    @Key("unsupportedTreeInfoType")
    String unsupportedTreeInfoType();
}
