package org.iplantc.de.diskResource.client.views.toolbar;

import static org.iplantc.de.client.models.diskResources.PermissionValue.*;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class DiskResourceViewToolbar_onFolderSelectedTest {

    // File Menu
    @Mock public MenuItem mockNewWindow,
            mockNewWindowAtLoc,
            mockNewFolder,
            mockDuplicate,
            mockNewFileMi,
            mockMoveToTrash;
    // Trash Menu
    @Mock public MenuItem mockOpenTrash,
            mockRestore,
            mockEmptyTrash;
    // Edit Menu
    @Mock public MenuItem mockRename,
            mockMove,
            mockDelete,
            mockEditFile,
            mockEditComments,
            mockEditInfoType;

    // Share Menu
    @Mock public MenuItem mockShareWithCollabs,
            mockCreatePublicLink,
            mockSendToCoge,
            mockSendToEnsembl,
            mockSendToTreeViewer;
    // Download Menu
    @Mock public MenuItem mockSimpleDownload,
            mockBulkDownload;
    // Upload Menu
    @Mock public MenuItem mockSimpleUpload,
            mockBulkUpload,
            mockImportFromUrl;
    @Mock public TextButton mockUploadMenu,
            mockFileMenu,
            mockEditMenu,
            mockDownloadMenu,
            mockShareMenu,
            mockRefreshButton,
            mockTrashMenu;
    @Mock DiskResourceSearchField searchFieldMock;
    private final boolean containsFile = false;
    private final boolean isReadable = true;
    private boolean isSelectionInTrash = false;
    private final boolean isSelectionOwner = true;
    private DiskResourceViewToolbarImpl uut;
    @Mock ToolbarView.Appearance mockAppearance;
    @Mock ToolbarView.Presenter mockPresenter;

    @Before public void setup() {
        uut = new DiskResourceViewToolbarImpl(searchFieldMock, mock(UserInfo.class), mockAppearance, mockPresenter){
            @Override
            boolean containsFile(List<DiskResource> selection) {
                return containsFile;
            }

            @Override
            boolean isOwnerList(List<DiskResource> selection) {
                return isSelectionOwner;
            }

            @Override
            boolean isReadable(DiskResource item) {
                return isReadable;
            }

            @Override
            boolean isSelectionInTrash(List<DiskResource> selection) {
                return isSelectionInTrash;
            }

            @Override
            boolean canUploadTo(DiskResource folder) {
                return (folder != null)
                               && (write.equals(folder.getPermission()) || own.equals(folder.getPermission()))
                               && !isSelectionInTrash
                               && (folder instanceof Folder)
                               && !(folder instanceof DiskResourceQueryTemplate);
            }
        };
        mockMenuItems(uut);
    }

    /**
     * Selection is empty
     */
    @Test public void testOnFolderSelected_emptySelection(){
        this.isSelectionInTrash = false;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        when(mockEvent.getSelectedFolder()).thenReturn(null);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(true);
        verify(mockBulkUpload).setEnabled(true);
        verify(mockImportFromUrl).setEnabled(true);

        // File Menu Items
        verify(mockNewFolder).setEnabled(true);
        verify(mockNewFileMi).setEnabled(true);

        verify(mockRefreshButton).setEnabled(false);
    }

    /**
     * Selection contains one folder:
     * with read perms,
     */
    @Test public void testOnFolderSelected_folder_read() {
        this.isSelectionInTrash = false;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getPermission()).thenReturn(read);
        when(mockEvent.getSelectedFolder()).thenReturn(mockFolder);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(false);
        verify(mockBulkUpload).setEnabled(false);
        verify(mockImportFromUrl).setEnabled(false);

        // File Menu Items
        verify(mockNewFolder).setEnabled(false);
        verify(mockNewFileMi).setEnabled(false);

        verify(mockRefreshButton).setEnabled(true);
    }

    /**
     * Selection contains one folder:
     * with write perms,
     */
    @Test public void testOnFolderSelected_folder_write() {
        this.isSelectionInTrash = false;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getPermission()).thenReturn(write);
        when(mockEvent.getSelectedFolder()).thenReturn(mockFolder);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(true);
        verify(mockBulkUpload).setEnabled(true);
        verify(mockImportFromUrl).setEnabled(true);

        // File Menu Items
        verify(mockNewFolder).setEnabled(true);
        verify(mockNewFileMi).setEnabled(true);

        verify(mockRefreshButton).setEnabled(true);
    }

    /**
     * Selection contains one folder:
     * with own perms,
     */
    @Test public void testOnFolderSelected_folder_own() {
        this.isSelectionInTrash = false;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getPermission()).thenReturn(own);
        when(mockEvent.getSelectedFolder()).thenReturn(mockFolder);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(true);
        verify(mockBulkUpload).setEnabled(true);
        verify(mockImportFromUrl).setEnabled(true);

        // File Menu Items
        verify(mockNewFolder).setEnabled(true);
        verify(mockNewFileMi).setEnabled(true);

        verify(mockRefreshButton).setEnabled(true);
    }

    /**
     * Selection contains one folder:
     * in trash with read perms,
     */
    @Test public void testOnFolderSelected_folder_inTrash_read() {
        this.isSelectionInTrash = true;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getPermission()).thenReturn(read);
        when(mockEvent.getSelectedFolder()).thenReturn(mockFolder);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(false);
        verify(mockBulkUpload).setEnabled(false);
        verify(mockImportFromUrl).setEnabled(false);

        // File Menu Items
        verify(mockNewFolder).setEnabled(false);
        verify(mockNewFileMi).setEnabled(false);
        verify(mockRefreshButton).setEnabled(true);
    }

    /**
     * Selection contains one folder:
     * in trash with write perms,
     */
    @Test public void testOnFolderSelected_folder_inTrash_write() {
        this.isSelectionInTrash = true;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getPermission()).thenReturn(write);
        when(mockEvent.getSelectedFolder()).thenReturn(mockFolder);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(false);
        verify(mockBulkUpload).setEnabled(false);
        verify(mockImportFromUrl).setEnabled(false);

        // File Menu Items
        verify(mockNewFolder).setEnabled(false);
        verify(mockNewFileMi).setEnabled(false);

        verify(mockRefreshButton).setEnabled(true);
    }

    /**
     * Selection contains one folder:
     * in trash with own perms,
     */
    @Test public void testOnFolderSelected_folder_inTrash_own() {
        this.isSelectionInTrash = true;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        Folder mockFolder = mock(Folder.class);
        when(mockFolder.getPermission()).thenReturn(own);
        when(mockEvent.getSelectedFolder()).thenReturn(mockFolder);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(false);
        verify(mockBulkUpload).setEnabled(false);
        verify(mockImportFromUrl).setEnabled(false);

        // File Menu Items
        verify(mockNewFolder).setEnabled(false);
        verify(mockNewFileMi).setEnabled(false);
        verify(mockRefreshButton).setEnabled(true);
    }

    /**
     * Selection contains one query template:
     * with own perms,
     */
    @Test public void testOnFolderSelected_queryTemplate_own() {
        this.isSelectionInTrash = false;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        DiskResourceQueryTemplate mockQt = mock(DiskResourceQueryTemplate.class);
        when(mockQt.getPermission()).thenReturn(own);
        when(mockEvent.getSelectedFolder()).thenReturn(mockQt);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(false);
        verify(mockBulkUpload).setEnabled(false);
        verify(mockImportFromUrl).setEnabled(false);

        // File Menu Items
        verify(mockNewFolder).setEnabled(false);
        verify(mockNewFileMi).setEnabled(false);

        verify(mockRefreshButton).setEnabled(true);
    }
     /**
     * Selection contains one query template:
     * with read perms,
     */
    @Test public void testOnFolderSelected_queryTemplate_read() {
        this.isSelectionInTrash = false;

        // Setup mock event
        FolderSelectionEvent mockEvent = mock(FolderSelectionEvent.class);
        DiskResourceQueryTemplate mockQt = mock(DiskResourceQueryTemplate.class);
        when(mockQt.getPermission()).thenReturn(read);
        when(mockEvent.getSelectedFolder()).thenReturn(mockQt);
        uut.onFolderSelected(mockEvent);
        verifyOnFolderSelectedNeverUsedItems();

        // Upload Menu Items
        verify(mockSimpleUpload).setEnabled(false);
        verify(mockBulkUpload).setEnabled(false);
        verify(mockImportFromUrl).setEnabled(false);

        // File Menu Items
        verify(mockNewFolder).setEnabled(false);
        verify(mockNewFileMi).setEnabled(false);

        verify(mockRefreshButton).setEnabled(true);
    }

    void verifyOnFolderSelectedNeverUsedItems(){
        // Top level menu items
        verify(mockUploadMenu, never()).setEnabled(anyBoolean());
        verify(mockFileMenu, never()).setEnabled(anyBoolean());
        verify(mockEditMenu, never()).setEnabled(anyBoolean());
        verify(mockDownloadMenu, never()).setEnabled(anyBoolean());
        verify(mockShareMenu, never()).setEnabled(anyBoolean());
        verify(mockTrashMenu, never()).setEnabled(anyBoolean());

        // File Menu Items
        verify(mockNewWindow, never()).setEnabled(anyBoolean());
        verify(mockNewWindowAtLoc, never()).setEnabled(anyBoolean());
        verify(mockMoveToTrash, never()).setEnabled(anyBoolean());

        // Edit Menu Items
        verify(mockRename, never()).setEnabled(anyBoolean());
        verify(mockMove, never()).setEnabled(anyBoolean());
        verify(mockDelete, never()).setEnabled(anyBoolean());
        verify(mockEditFile, never()).setEnabled(anyBoolean());
        verify(mockEditComments, never()).setEnabled(anyBoolean());
        verify(mockEditInfoType, never()).setEnabled(anyBoolean());

        // Download Menu Items
        verify(mockSimpleDownload, never()).setEnabled(anyBoolean());
        verify(mockBulkDownload, never()).setEnabled(anyBoolean());

        // Share Menu Items
        verify(mockShareWithCollabs, never()).setEnabled(anyBoolean());
        verify(mockCreatePublicLink, never()).setEnabled(anyBoolean());
        verify(mockSendToCoge, never()).setEnabled(anyBoolean());
        verify(mockSendToEnsembl, never()).setEnabled(anyBoolean());
        verify(mockSendToTreeViewer, never()).setEnabled(anyBoolean());

        // Trash Menu Items
        verify(mockOpenTrash, never()).setEnabled(anyBoolean());
        verify(mockRestore, never()).setEnabled(anyBoolean());
        verify(mockEmptyTrash, never()).setEnabled(anyBoolean());
    }

    private void mockMenuItems(DiskResourceViewToolbarImpl uut) {
        // Upload Menu
        uut.uploadMenu = mockUploadMenu;
        uut.simpleUploadMi = mockSimpleUpload;
        uut.bulkUploadMi = mockBulkUpload;
        uut.importFromUrlMi = mockImportFromUrl;

        // File Menu
        uut.fileMenu = mockFileMenu;
        uut.newWindowMi = mockNewWindow;
        uut.newWindowAtLocMi = mockNewWindowAtLoc;
        uut.newFolderMi = mockNewFolder;
        uut.duplicateMi = mockDuplicate;
        uut.newFileMi = mockNewFileMi;
        uut.moveToTrashMi = mockMoveToTrash;

        // Edit Menu
        uut.editMenu = mockEditMenu;
        uut.renameMi = mockRename;
        uut.moveMi = mockMove;
        uut.deleteMi = mockDelete;
        uut.editFileMi = mockEditFile;
        uut.editCommentsMi = mockEditComments;
        uut.editInfoTypeMi = mockEditInfoType;

        // Download Menu
        uut.downloadMenu = mockDownloadMenu;
        uut.simpleDownloadMi = mockSimpleDownload;
        uut.bulkDownloadMi = mockBulkDownload;

        // Share Menu
        uut.shareMenu = mockShareMenu;
        uut.shareWithCollaboratorsMi = mockShareWithCollabs;
        uut.createPublicLinkMi = mockCreatePublicLink;
        uut.sendToCogeMi = mockSendToCoge;
        uut.sendToEnsemblMi = mockSendToEnsembl;
        uut.sendToTreeViewerMi = mockSendToTreeViewer;

        // Refresh
        uut.refreshButton = mockRefreshButton;

        // Trash Menu
        uut.trashMenu = mockTrashMenu;
        uut.openTrashMi = mockOpenTrash;
        uut.restoreMi = mockRestore;
        uut.emptyTrashMi = mockEmptyTrash;
    }

}
