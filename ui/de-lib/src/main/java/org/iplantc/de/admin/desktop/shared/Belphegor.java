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
    }
}
