package org.iplantc.de.theme.base.client.diskResource.toolbar;

import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class ToolbarViewDefaultAppearance implements ToolbarView.Appearance {
    private final IplantResources iplantResources;
    private final IplantDisplayStrings iplantDisplayStrings;
    private final ToolbarDisplayMessages displayMessages;
    private final Resources resources;
    private final CommonUiConstants commonUiConstants;
    private final DiskResourceMessages diskResourceMessages;

    public interface Resources extends ClientBundle {

        @Source("../file_delete.gif")
        ImageResource deleteIcon();

        @Source("../file_download.gif")
        ImageResource downloadIcon();

        @Source("../bin_empty.png")
        ImageResource emptyTrashIcon();

        @Source("../import.gif")
        ImageResource importDataIcon();

        @Source("../comments.png")
        ImageResource metadataIcon();

        @Source("../list-ingredients-16.png")
        ImageResource newPathListIcon();

        @Source("../bin.png")
        ImageResource trashIcon();
    }

    public ToolbarViewDefaultAppearance() {
        this(GWT.<IplantResources> create(IplantResources.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<ToolbarDisplayMessages> create(ToolbarDisplayMessages.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<Resources> create(Resources.class),
             GWT.<CommonUiConstants> create(CommonUiConstants.class));
    }

    ToolbarViewDefaultAppearance(final IplantResources iplantResources,
                                 final IplantDisplayStrings iplantDisplayStrings,
                                 final ToolbarDisplayMessages displayMessages,
                                 final DiskResourceMessages diskResourceMessages,
                                 final Resources resources,
                                 final CommonUiConstants commonUiConstants) {
        this.iplantResources = iplantResources;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayMessages = displayMessages;
        this.diskResourceMessages = diskResourceMessages;
        this.resources = resources;
        this.commonUiConstants = commonUiConstants;
    }

    @Override
    public SafeHtml bulkDownloadInfoBoxHeading() {
        return displayMessages.bulkDownloadInfoBoxHeading();
    }

    @Override
    public SafeHtml bulkDownloadInfoBoxMsg() {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append(displayMessages.bulkDownloadInfoBoxMsg());
        sb.append(SafeHtmlUtils.fromString(" "));
        sb.append(displayMessages.externalHyperlink(commonUiConstants.iDropDesktopClientInstructionsUrl(), "here."));
        return sb.toSafeHtml();
    }

    @Override
    public SafeHtml bulkUploadInfoBoxHeading() {
        return displayMessages.bulkUploadInfoBoxHeading();
    }

    @Override
    public SafeHtml bulkUploadInfoBoxMsg() {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        sb.append(displayMessages.bulkUploadInfoBoxMsg());
        sb.append(SafeHtmlUtils.fromString(" "));
        sb.append(displayMessages.externalHyperlink(commonUiConstants.iDropDesktopClientInstructionsUrl(), "here."));
        return sb.toSafeHtml();
    }

    @Override
    public String newPathListMenuText() {
        return diskResourceMessages.newPathListMenuText();
    }

    @Override
    public ImageResource newPathListMenuIcon() {
        return resources.newPathListIcon();
    }

    @Override
    public ImageResource trashIcon() {
        return resources.trashIcon();
    }

    @Override
    public String moveToTrashMenuItem() {
        return displayMessages.moveToTrashMenuItem();
    }

    @Override
    public ImageResource newMdFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public ImageResource newShellFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public ImageResource newPythonFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public ImageResource newPerlFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public ImageResource newRFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public ImageResource newDelimitedFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public String newTabularDataFileMenuItem() {
        return displayMessages.newTabularDataFileMenuItem();
    }

    @Override
    public ImageResource newPlainTexFileIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public String newPlainTextFileMenuItem() {
        return displayMessages.newPlainTextFileMenuItem();
    }

    @Override
    public ImageResource newFileMenuIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public String newFileMenu() {
        return iplantDisplayStrings.create();
    }

    @Override
    public String duplicateMenuItem() {
        return displayMessages.duplicateMenuItem();
    }

    @Override
    public ImageResource newFolderIcon() {
        return iplantResources.folderAdd();
    }

    @Override
    public String newFolderMenuItem() {
        return displayMessages.newFolderMenuItem();
    }

    @Override
    public String newDataWindowAtLocMenuItem() {
        return displayMessages.newDataWindowAtLocMenuItem();
    }

    @Override
    public ImageResource addIcon() {
        return iplantResources.add();
    }

    @Override
    public String newWindow() {
        return displayMessages.newWindow();
    }

    @Override
    public ImageResource importDataIcon() {
        return resources.importDataIcon();
    }

    @Override
    public String importFromUrlMenuItem() {
        return displayMessages.importFromUrlMenuItem();
    }

    @Override
    public String bulkUploadFromDesktop() {
        return displayMessages.bulkUploadFromDesktop();
    }

    @Override
    public String simpleUploadFromDesktop() {
        return displayMessages.simpleUploadFromDesktop();
    }

    @Override
    public String uploadMenu() {
        return displayMessages.upload();
    }

    @Override
    public String editMenu() {
        return iplantDisplayStrings.edit();
    }

    @Override
    public String renameMenuItem() {
        return displayMessages.renameMenuItem();
    }

    @Override
    public ImageResource fileRenameIcon() {
        return iplantResources.fileRename();
    }

    @Override
    public String editFileMenuItem() {
        return displayMessages.editFileMenuItem();
    }

    @Override
    public String editCommentsMenuItem() {
        return displayMessages.editCommentsMenuItem();
    }

    @Override
    public ImageResource userCommentIcon() {
        return iplantResources.userComment();
    }

    @Override
    public String editInfoTypeMenuItem() {
        return displayMessages.editInfoTypeMenuItem();
    }

    @Override
    public ImageResource infoIcon() {
        return iplantResources.info();
    }

    @Override
    public String metadataMenuItem() {
        return displayMessages.metadataMenuItem();
    }

    @Override
    public ImageResource metadataIcon() {
        return resources.metadataIcon();
    }

    @Override
    public String moveMenuItem() {
        return displayMessages.moveMenuItem();
    }

    @Override
    public ImageResource editIcon() {
        return iplantResources.edit();
    }

    @Override
    public String downloadMenu() {
        return displayMessages.download();
    }

    @Override
    public String simpleDownloadMenuItem() {
        return displayMessages.simpleDownloadMenuItem();
    }

    @Override
    public ImageResource downloadIcon() {
        return resources.downloadIcon();
    }

    @Override
    public String bulkDownloadMenuItem() {
        return displayMessages.bulkDownloadMenuItem();
    }

    @Override
    public String shareMenu() {
        return diskResourceMessages.share();
    }

    @Override
    public String shareWithCollaboratorsMenuItem() {
        return displayMessages.shareWithCollaboratorsMenuItem();
    }

    @Override
    public ImageResource shareWithCollaboratorsIcon() {
        return iplantResources.share();
    }

    @Override
    public String createPublicLinkMenuItem() {
        return displayMessages.createPublicLinkMenuItem();
    }

    @Override
    public ImageResource linkAddIcon() {
        return iplantResources.arrowUp();
    }

    @Override
    public String shareFolderLocationMenuItem() {
        return displayMessages.shareFolderLocationMenuItem();
    }

    @Override
    public ImageResource shareFolderLocationIcon() {
        return iplantResources.folder();
    }

    @Override
    public String sendToCogeMenuItem() {
        return iplantDisplayStrings.sendToCogeMenuItem();
    }

    @Override
    public ImageResource sendToCogeIcon() {
        return iplantResources.arrowUp();
    }

    @Override
    public String sendToEnsemblMenuItem() {
        return iplantDisplayStrings.sendToEnsemblMenuItem();
    }

    @Override
    public ImageResource sendToEnsemblIcon() {
        return iplantResources.arrowUp();
    }

    @Override
    public String sendToTreeViewerMenuItem() {
        return iplantDisplayStrings.sendToTreeViewerMenuItem();
    }

    @Override
    public ImageResource sendToTreeViewerIcon() {
        return iplantResources.arrowUp();
    }

    @Override
    public ImageResource sendNcbiSraIcon() {
        return iplantResources.arrowUp();
    }

    @Override
    public String refresh() {
        return iplantDisplayStrings.refresh();
    }

    @Override
    public String trashMenu() {
        return displayMessages.trashMenu();
    }

    @Override
    public String openTrashMenuItem() {
        return displayMessages.openTrashMenuItem();
    }

    @Override
    public ImageResource openTrashIcon() {
        return resources.trashIcon();
    }

    @Override
    public String restore() {
        return displayMessages.restore();
    }

    @Override
    public String emptyTrashMenuItem() {
        return diskResourceMessages.emptyTrash();
    }

    @Override
    public ImageResource emptyTrashIcon() {
        return resources.emptyTrashIcon();
    }

    @Override
    public String deleteMenuItem() {
        return iplantDisplayStrings.delete();
    }

    @Override
    public ImageResource deleteIcon() {
        return resources.deleteIcon();
    }

    @Override
    public ImageResource refreshIcon() {
        return iplantResources.refresh();
    }

    @Override
    public String newRFileMenuItem() {
        return displayMessages.newRFileMenuItem();
    }

    @Override
    public String newPerlFileMenuItem() {
        return displayMessages.newPerlFileMenuItem();
    }

    @Override
    public String newPythonFileMenuItem() {
        return displayMessages.newPythonFileMenuItem();
    }

    @Override
    public String newShellFileMenuItem() {
        return displayMessages.newShellFileMenuItem();
    }

    @Override
    public String newMdFileMenuItem() {
        return displayMessages.newMdFileMenuItem();
    }

    @Override
    public String copyMetadataMenuItem() {
        return displayMessages.copyMetadataMenuItem();
    }

    @Override
    public String saveMetadataMenuItem() {
        return displayMessages.saveMetadataMenuItem();
    }

    @Override
    public String sendToNcbiSraItem() {
        return displayMessages.sendToNcbiSraItem();
    }

    @Override
    public String importFromCoge() {
        return displayMessages.importFromCoge();
    }

    @Override
    public String applyBulkMetadata() {
        return displayMessages.applyBulkMetadata();
    }

    @Override
    public String selectMetadata() {
        return displayMessages.selectMetadataFile();
    }

    @Override
    public String requestDOI() {
        return displayMessages.requestDOI();
    }

    @Override
    public String doiLinkMsg() {
        return displayMessages.doiLinkMsg();
    }

    @Override
    public String needDOI() {
        return displayMessages.needDOI();
    }
}
