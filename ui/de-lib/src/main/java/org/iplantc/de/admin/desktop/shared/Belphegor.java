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
}
