package org.iplantc.de.theme.base.client.diskResource;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

import java.util.List;

/**
 * @author jstroot
 */
public interface DiskResourceMessages extends Messages {

    String browse();

    String collapseAll();

    String createDataLinksError();

    String createFolderFailed();

    String createIn(String path);

    SafeHtml dataDragDropStatusText(int size);

    String deleteDataLinksError();

    String deleteFailed();

    String deleteMsg();

    String deleteTrash();

    String diskResourceDoesNotExist(String diskResourcePath);

    String diskResourceIncompleteMove();

    @DefaultMessage("Selected items moved to {0}.")
    @AlternateMessage({"=1", "Selected item moved to {0}."})
    @Key("diskResourceMoveSuccess")
    String diskResourceMoveSuccess(String destPath, @Optional @PluralCount List<String> srcPaths);

    String duplicateCheckFailed();

    String emptyTrash();

    String emptyTrashWarning();

    @Key("fileFolderDialogHeaderText")
    String fileFolderDialogHeaderText();

    @Key("fileFolderSelectorFieldEmptyText")
    String fileFolderSelectorFieldEmptyText();

    String fileName();

    @Key("fileSelectDialogHeaderText")
    String fileSelectDialogHeaderText();

    String fileUploadMaxSizeWarning();

    String folderName();

    String idParentInvalid();

    String importLabel();

    String listDataLinksError();

    String metadataSuccess();

    String metadataUpdateFailed();

    String moveFailed();

    String newFolder();

    String nonDefaultFolderWarning();

    String partialRestore();

    String permissionErrorMessage();

    String permissionSelectErrorMessage();

    String permissions();

    String renameFailed();

    String requiredField();

    String reset();

    String restoreDefaultMsg();

    String restoreMsg();

    String selectAFile();

    String selectAFolder();

    String selectMultipleInputs();

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

    String tabFileConfigDialogCommaRadioLabel();

    String tabFileConfigDialogHeading();

    String tabFileConfigDialogTabRadioLabel();

    @Key("unsupportedCogeInfoType")
    String unsupportedCogeInfoType();

    @Key("unsupportedEnsemblInfoType")
    String unsupportedEnsemblInfoType();

    @Key("unsupportedTreeInfoType")
    String unsupportedTreeInfoType();

    String uploadingToFolder(String path);

    String urlImport();

    String urlPrompt();

    String projectName();

    String numberOfBioSamples();

    String numberOfLib();

    String ncbiSraProject();

    String ncbiCreateFolderStructureSuccess();

    /**
     * Genome Import Dialog strings
     */
    String heading();

    String loading();

    String importText();

    String searchGenome();

    String organismName();

    String version();

    String chromosomeCount();

    String sequenceType();

    String cogeSearchError();

    String cogeImportGenomeError();

    String cogeImportGenomeSucess();

    String importFromCoge();

    String bulkMetadataHeading();

    String selectMetadataFile();

    String selectTemplate();

    String applyBulkMetadata();

    String uploadMetadata();

    String templatesError();

    String bulkMetadataSuccess();

    String bulkMetadataError();

    String overWiteMetadata();

    String norecords();

}
