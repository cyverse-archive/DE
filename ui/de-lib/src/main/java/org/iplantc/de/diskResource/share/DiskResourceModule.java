package org.iplantc.de.diskResource.share;

/**
 * @author jstroot
 */
public interface DiskResourceModule {
    interface Ids {

        String DISK_RESOURCE_VIEW = ".diskResourceView";


        String MENU_BAR = ".menuBar";
        String GRID = ".grid";
        String NAVIGATION = ".navigation";
        String FILE_MENU = ".fileMenu";
        String UPLOAD_MENU = ".uploadMenu";
        String EDIT_MENU = ".editMenu";
        String DOWNLOAD_MENU = ".downloadMenu";
        String REFRESH_BUTTON = ".refreshButton";
        String SHARE_MENU = ".shareMenu";
        String TRASH_MENU = ".trashMenu";
        String SEARCH_FIELD = ".searchField";
        String MENU_ITEM_SIMPLE_UPLOAD = ".simpleUpload";
        String MENU_ITEM_BULK_UPLOAD = ".bulkUpload";
        String MENU_ITEM_IMPORT_FROM_URL = ".importFromUrl";
        String MENU_ITEM_NEW_WINDOW = ".newWindow";
        String MENU_ITEM_NEW_WINDOW_AT_LOC = ".newWindowLoc";
        String MENU_ITEM_NEW_FOLDER = ".newFolder";
        String MENU_ITEM_DUPLICATE = ".duplicate";
        String MENU_ITEM_NEW_PLAIN_TEXT = ".newPlainText";
        String MENU_ITEM_NEW_TABULAR_DATA = ".newTabularData";
        String MENU_ITEM_NEW_R_DATA = ".newRData";
        String MENU_ITEM_NEW_PYTHON_DATA = ".newPythonData";
        String MENU_ITEM_NEW_PERL_DATA = ".newPerlData";
        String MENU_ITEM_NEW_SHELL_DATA = ".newShellData";
        String MENU_ITEM_NEW_MD_DATA = ".newMdData";
        String MENU_ITEM_NEW_PATH_LIST = ".newPathList";
        String MENU_ITEM_MOVE_TO_TRASH = ".moveToTrash";
        String MENU_ITEM_RENAME = ".rename";
        String MENU_ITEM_MOVE = ".move";
        String MENU_ITEM_DELETE = ".delete";
        String MENU_ITEM_EDIT_FILE = ".editFile";
        String MENU_ITEM_EDIT_INFO_TYPE = ".editInfoType";
        String METADATA_MENU = ".metadata";
        String MENU_ITEM_SIMPLE_DOWNLOAD = ".simpleDownload";
        String MENU_ITEM_BULK_DOWNLOAD = ".bulkDownload";
        String MENU_ITEM_SHARE_WITH_COLLABORATORS = ".shareWithCollaborators";
        String MENU_ITEM_CREATE_PUBLIC_LINK = ".createPublicLink";
        String MENU_ITEM_SHARE_FOLDER_LOCATION = ".shareFolderLocation";
        String MENU_ITEM_SEND_TO_COGE = ".sendToCoge";
        String MENU_ITEM_SEND_TO_ENSEMBL = ".sendToEnsembl";
        String MENU_ITEM_SEND_TO_TREE_VIEWER = ".sendToTreeViewer";
        String MENU_ITEM_OPEN_TRASH = ".openTrash";
        String MENU_ITEM_RESTORE = ".restore";
        String MENU_ITEM_EMPTY_TRASH = ".emptyTrash";


        String ACTION_CELL_DATA_LINK_ADD = ".actionCellDataLinkAdd";
        String ACTION_CELL_DATA_LINK = ".actionCellDataLink";
        String ACTION_CELL_SHARE = ".actionCellShare";
        String ACTION_CELL_METADATA = ".actionCellMetadata";
        String ACTION_CELL_FAVORITE = ".favorite";
        String ACTION_CELL_COMMENTS = ".comments";
        String NAME_CELL = ".nameCell";
        String PATH_CELL = ".pathCell";
        String DETAILS = ".details";
        String NAV_TREE = ".tree";
        String TREE_COLLAPSE = ".collapse";

        String MENU_ITEM_METADATA_COPY = ".mcopy";
        String MENU_ITEM_METADATA_SAVE = ".msave";

        String MENU_ITEM_NCBI_SRA = ".ncbisra";

        String MENU_ITEM_IMPORT_FROM_COGE = ".CogeImport";

        String MENU_ITEM_BULK_METADATA = ".bulkmetadata";
        String MENU_ITEM_REQUEST_DOI = ".requestdoi";
        String MENU_ITEM_SELECTFILE = ".selectfile";
    }

    interface MetadataIds {

        String METADATA_WINDOW = "metadataWindow";
        String METADATA_VIEW = ".metadataView";
        String ADD_METADATA = ".addMetadata";
        String DELETE_METADATA = ".deleteMetadata";
        String TEMPLATES = ".templates";

        String USER_METADATA = ".userMetadata";
        String USER_METADATA_COLLAPSE = ".collapseBtn";
        String METADATA_TEMPLATE = ".metadataTemplate";
        String METADATA_TEMPLATE_COLLAPSE = ".collapseBtn";
    }
}
