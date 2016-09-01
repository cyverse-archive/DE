package org.iplantc.de.admin.desktop.shared;

/**
 * @author aramsey
 */
public interface Belphegor {

    interface Ids {
        String BELPHEGOR = "belphegorViewport";
        String MENU_BUTTON = ".menuButton";
        String APPS = ".apps";
        String METADATA = ".metadata";
        String PERMID = ".permId";
        String REFERENCE_GENOME = ".referenceGenome";
        String SYSTEM_MESSAGE = ".systemMessage";
        String TOOL_ADMIN = ".toolAdmin";
        String TOOL_REQUEST = ".toolRequest";
        String CATALOG = ".catalog";
        String WORKSHOP_ADMIN = ".workshopAdmin";
        String CATALOG_TAB = ".catalogTab";
        String REFERENCE_GENOME_TAB = ".referenceGenomeTab";
        String TOOL_REQUEST_TAB = ".toolRequestTab";
        String TOOL_ADMIN_TAB = ".toolAdminTab";
        String SYSTEM_MESSAGE_TAB = ".systemMessageTab";
        String METADATA_TAB = ".metadataTab";
        String PERMID_TAB = ".permIdTab";
        String WORKSHOP_ADMIN_TAB = ".workshopAdminTab";
    }

    interface AppIds {

        String VIEW = ".view";
        String CATEGORIES = ".categories";
        String TOOLBAR = ".toolbar";
        String GRID = ".grid";
        String GRID_VIEW = ".gridView";
        String TOOLBAR_ADD = ".addBtn";
        String TOOLBAR_CATEGORIZE = ".categorizeBtn";
        String TOOLBAR_DELETEAPP = ".deleteAppBtn";
        String TOOLBAR_DELETECAT = ".deleteCatBtn";
        String TOOLBAR_MOVE = ".moveBtn";
        String TOOLBAR_RENAME = ".renameBtn";
        String TOOLBAR_RESTORE = ".restoreBtn";
        String TOOLBAR_SEARCH = ".search";
        String APP_EDITOR = "appEditor";
        String EDITOR_WINDOW = ".window";
        String NAME = ".name";
        String NAME_LABEL = ".nameLabel";
        String INTEGRATOR_NAME = ".integratorName";
        String INTEGRATOR_NAME_LABEL = ".integratorNameLabel";
        String INTEGRATOR_EMAIL = ".integratorEmail";
        String INTEGRATOR_EMAIL_LABEL = ".integratorEmailLabel";
        String DISABLED = ".disabled";
        String DISABLED_LABEL = ".disabledLabel";
        String DESCRIPTION = ".description";
        String DESCRIPTION_LABEL = ".descriptionLabel";
        String WIKI_URL = ".wikiUrl";
        String WIKI_URL_LABEL = ".wikiUrlLabel";
        String SAVE = ".saveBtn";
        String CANCEL = ".cancelBtn";
        String APP_DOC = ".appDoc";
        String APP_DOC_LABEL = ".appDocLabel";
        String DOC_HELP = ".docHelp";
        String TEMPLATE_LINK = ".templateLink";
        String CLOSE_BTN = ".closeBtn";
        String BETA = ".beta";
        String COL_HEADER = ".colHeader";
    }

    interface RefGenomeIds {

        String VIEW = ".view";
        String ADD = ".addBtn";
        String GRID = ".grid";
        String NAME_FILTER = ".nameFilter";
        String NAME_LABEL = ".nameLabel";
        String PATH_LABEL = ".pathLabel";
        String NAME = ".name";
        String PATH = ".path";
        String CREATED_BY = ".createdBy";
        String LAST_MODIFIED_BY = ".lastModifiedBy";
        String CREATED_DATE = ".createdDate";
        String LAST_MODIFIED_DATE = ".lastModifiedDate";
        String DELETED = ".deleted";
        String EDITOR_VIEW = ".view";
        String GENOME_EDITOR = "genomeEditorWindow";
        String SAVE_BTN = ".saveBtn";
        Object NAME_CELL = ".nameCell";
        String COL_HEADER = ".colHeader";
    }

    interface ToolRequestIds {
        String VIEW = ".view";
        String UPDATE = ".updateBtn";
        String GRID = ".grid";
        String DETAILS_PANEL = ".detailsPanel";
        String ADDITIONAL_DATA_FILE = ".additionalData";
        String ADDITIONAL_INFO = ".additionalInfo";
        String ARCHITECTURE = ".architecture";
        String CMD_LINE = ".cmdLine";
        String DOC_URL = ".docUrl";
        String MULTI_THREAD = ".multiThread";
        String PHONE = ".phone";
        String SOURCE_URL = ".sourceUrl";
        String SUBMITTED_BY = ".submittedBy";
        String TEST_DATA = ".testData";
        String VERSION = ".version";
        String CURRENT_STATUS = ".currentStatus";
        String STATUS_COMBO = ".statusCombo";
        String COMMENTS = ".comments";
        String STATUS = ".status";
        String TOOL_REQUEST_DIALOG = "toolRequestDialog";
        String SUBMIT_BTN = ".submitBtn";
        String DIALOG_VIEW = ".view";
        String ATTRIBUTION = ".attribution";
    }

    interface ToolAdminIds {
        String VIEW = ".view";
        String ADD = ".addBtn";
        String DELETE = ".deleteBtn";
        String GRID = ".grid";
        String TOOL_ADMIN_DIALOG = "toolAdminDialog";
        String FILTER = ".filter";
        String DETAILS_VIEW = ".view";
        String SAVE = ".saveBtn";
        String TOOL_NAME_LABEL = ".toolNameLabel";
        String TOOL_TYPE_LABEL = ".toolTypeLabel";
        String TOOL_LOCATION_LABEL = ".toolLocationLabel";
        String TOOL_DESCRIPTION = ".toolDescription";
        String TOOL_NAME = ".toolName";
        String TOOL_TYPE = ".toolType";
        String TOOL_ATTRIBUTION = ".toolAttribution";
        String TOOL_VERSION = ".toolVersion";
        String TOOL_LOCATION = ".toolLocation";
        String IMPLEMENTOR_LABEL = ".implementorLabel";
        String IMPLEMENTOR_EMAIL_LABEL = ".implementorEmailLabel";
        String IMPLEMENTOR = ".implementor";
        String IMPLEMENTOR_EMAIL = ".implementorEmail";
        String INPUT_FILES_LABEL = ".inputFilesLabel";
        String ADD_INPUT = ".inputFilesAddBtn";
        String DELETE_INPUT = ".inputFilesDeleteBtn";
        String OUTPUT_FILES_LABEL = ".outputFilesLabel";
        String ADD_OUTPUT = ".outputFilesAddBtn";
        String DELETE_OUTPUT = ".outputFilesDeleteBtn";
        String INPUT_FILES_GRID = ".grid";
        String OUTPUT_FILES_GRID = ".grid";
        String CONTAINER_NAME = ".name";
        String CONTAINER_WORKING_DIR = ".workingDir";
        String CONTAINER_ENTRY_POINT = ".entryPoint";
        String CONTAINER_MEMORY = ".memory";
        String CONTAINER_CPU = ".cpu";
        String CONTAINER_NETWORK_MODE = ".networkMode";
        String CONTAINER_DEVICES_LABEL = ".devicesLabel";
        String CONTAINER_DEVICES_ADD = ".devicesAddBtn";
        String CONTAINER_DEVICES_DELETE = ".devicesDeleteBtn";
        String CONTAINER_VOLUMES_LABEL = ".volumesLabel";
        String CONTAINER_VOLUMES_ADD = ".volumesAddBtn";
        String CONTAINER_VOLUMES_DELETE = ".volumesDeleteBtn";
        String CONTAINER_VOLUMES_FROM_LABEL = ".volumesFromLabel";
        String CONTAINER_VOLUMES_FROM_ADD = ".volumesFromAddBtn";
        String CONTAINER_VOLUMES_FROM_DELETE = ".volumesFromDeleteBtn";
        String CONTAINER_DEVICES_GRID = ".devicesGrid";
        String CONTAINER_VOLUMES_GRID = ".volumesGrid";
        String CONTAINER_VOLUMES_FROM_GRID = ".volumesFromGrid";
        String IMAGE_NAME_LABEL = ".nameLabel";
        String IMAGE_NAME = ".name";
        String IMAGE_TAG = ".tag";
        String IMAGE_URL = ".url";
        String TOOL_IMPLEMENTATION = ".toolImplementation";
        String TOOL_CONTAINER = ".toolContainer";
        String TOOL_DEVICES = ".toolDevices";
        String CONTAINER_VOLUMES = ".containerVolumes";
        String CONTAINER_VOLUMES_FROM = ".containerVolumesFrom";
        String TOOL_IMAGE = ".toolImage";
        String TEST_DATA = ".testData";
        String INPUT_FILES = ".inputFiles";
        String OUTPUT_FILES = ".outputFiles";
        String DELETE_TOOL_DIALOG = "deleteToolDialog";
        String OVERWRITE_TOOL_DIALOG = "overwriteToolDialog";
        String OKBTN = ".okBtn";
        String PUBLIC_APPS = ".publicApps";
        String CONFIRM_DELETE = "confirmToolDelete";
        String YES = ".yesBtn";
        String NO = ".noBtn";
        String NAME_CELL = ".nameCell";
        String COL_HEADER = ".colHeader";
    }

    interface WorkshopAdminIds {
        String VIEW = ".view";
        String USER_SEARCH = ".userSearch";
        String DELETE_BTN = ".deleteBtn";
        String SAVE_BTN = ".saveBtn";
        String REFRESH_BTN = ".refreshBtn";
    }

    interface SystemMessageIds {
        String VIEW = ".view";
        String ADD = ".addBtn";
        String DELETE = ".deleteBtn";
        String GRID = ".grid";
        String EDIT_DIALOG = "systemMsgDialog";
        String SUBMIT = ".submitBtn";
        String ACTIVATION_DATE = ".activationDate";
        String ACTIVATION_TIME = ".activationTime";
        String DEACTIVATION_DATE = ".deactivationDate";
        String DEACTIVATION_TIME = ".deactivationTime";
        String DISMISSABLE = ".dismissable";
        String LOGINS_DISABLED = ".loginsDisabled";
        String MESSAGE = ".message";
        String TYPE = ".type";
        String NAME_CELL = ".nameCell";
    }

    interface MetadataIds {
        String VIEW = ".view";
        String ADD = ".addBtn";
        String EDIT = ".editBtn";
        String DELETE = ".deleteBtn";
        String GRID = ".grid";
        String EDIT_DIALOG = "metadataTemplateDialog";
        String OK = ".okBtn";
        String TEMPLATE_NAME = ".templateName";
        String CHECK_DELETED = ".checkDeleted";
        String DELETE_MSG_BOX = "deleteMetadataMsgBox";
        String YES = ".yesBtn";
        String NO = ".noBtn";
        String TEMPLATE_DESCRIPTION = ".description";
        String COL_HEADER = ".colHeader";
    }

    interface PermIds {
        String VIEW = ".view";
        String UPDATE = ".updateBtn";
        String METADATA = ".metadataBtn";
        String DOI = ".createDOIBtn";
        String GRID = ".grid";
        String CREATE_DOI_MSG = "createDOIMsg";
        String YES = ".yesBtn";
        String NO = ".noBtn";
        String METADATA_DIALOG = "editMetadataDialog";
        String OK = ".okBtn";
        String UPDATE_PERMID_DIALOG = "updatePermIdDialog";
        String CURRENT_STATUS = ".currentStatus";
        String USER_EMAIL = ".userEmail";
        String STATUS_COMBO = ".statusCombo";
        String COMMENTS = ".comments";
        String CANCEL = ".cancelBtn";
        String COL_HEADER = ".colHeader";
    }

    interface CatalogIds {
        String VIEW = ".view";
        String ADD_ONTOLOGY_BTN = ".addOntologyBtn";
        String DELETE_ONTOLOGY_BTN = ".deleteOntologyBtn";
        String ONTOLOGY_DROP_DOWN = ".ontologyDropDown";
        String SAVE_HIERARCHY_BTN = ".saveHierarchyBtn";
        String DELETE_HIERARCHY_BTN = ".deleteHierarchyBtn";
        String CATEGORIZE_BTN = ".categorizeBtn";
        String DELETE_APP_BTN = ".deleteAppBtn";
        String APP_SEARCH = ".appSearch";
        String RESTORE_APP_BTN = ".restoreAppBtn";
        String EDITOR_PANEL = ".editorPanel";
        String EDITOR_TREE_PANEL = ".editorTreePanel";
        String NO_TREE_PANEL = ".noTreePanel";
        String EMPTY_TREE_PANEL = ".emptyTreePanel";
        String EDITOR_TREE = ".editorTree";
        String EDITOR_GRID = ".editorGrid";
        String PUBLISH_BTN = ".publishBtn";
        String PREVIEW_PANEL = ".previewPanel";
        String PREVIEW_TREE_PANEL = ".previewTreePanel";
        String PREVIEW_TREE = ".previewTree";
        String PREVIEW_GRID = ".previewGrid";
        String REFRESH_PREVIEW_BTN = ".refreshPreviewBtn";
        String HEADER = ".header";
        String TREE_NODE = ".treeNode";
    }
}
