package org.iplantc.de.apps.integration.shared;

/**
 * @author jstroot
 */
public interface AppIntegrationModule {

    interface Ids {
        String APP_EDITOR_VIEW = ".appEditorView";
        String PALETTE = ".pallete";
        String PALETTE_HELP = ".help";
        String TOOLBAR = ".menuBar";
        String CMD_LINE_PREVIEW = ".cmdLinePreview";

        String ENV_VARIABLE = ".environmentVariable";
        String FILE_INPUT = ".fileInput";
        String FLAG = ".flag";
        String INTEGER_INPUT = ".integerInput";
        String MULTI_FILE_SELECTOR = ".multiFileSelector";
        String MULTI_LINE_TEXT = ".multiLineText";
        String TEXT = ".text";
        String SINGLE_SELECT = ".singleSelect";
        String TREE_SELECTION = ".treeSelection";
        String INFO = ".info";
        String FOLDER_INPUT = ".folderInput";
        String INTEGER_SELECTION = ".integerSelection";
        String DOUBLE_SELECTION = ".doubleSelection";
        String DOUBLE_INPUT = ".doubleInput";
        String FILE_OUTPUT = ".fileOutput";
        String FOLDER_OUTPUT = ".folderOutput";
        String MULTI_FILE_OUTPUT = ".multiFileOutput";
        String REFERENCE_GENOME = ".referenceGenome";
        String REFERENCE_ANNOTATION = ".referenceAnnotation";
        String REFERENCE_SEQUENCE = ".referenceSequence";

        String GROUP = ".group";
        String PROPERTY_EDITOR = ".propertyEditor";
        String ARG_ORDER = ".argumentOrder";
        String PREVIEW = ".preview";
        String PREVIEW_UI = ".UI";
        String PREVIEW_JSON = ".JSON";
        String SAVE = ".save";
        String TOOL = ".tool";
        String APP_NAME = ".appName";
        String APP_DESCRIPTION = ".appDescription";

        String SEARCH = ".search";
        String NEW_TOOL_REQUEST = ".newToolRequest";
        String INSTALLED_TOOLS_DLG = ".installedToolsDlg";
        String OK = ".ok";
        String CANCEL = ".cancel";
        String FILE_FOLDER_PANEL = ".fileFolderPanel";
        String TEXT_NUMERICAL_PANEL = ".textNumericalPanel";
        String LIST_PANEL = ".listPanel";
        String OUTPUT_PANEL = ".outputPanel";
        String REFERENCE_GENOME_PANEL = ".referenceGenomePanel";
        String HELP_BTN = ".help";
        String COLLAPSE_BTN = ".collapseBtn";
    }

    interface PropertyPanelIds {
        String LABEL = ".label";
        String ARGUMENT_OPTION = ".argumentOption";
        String DEFAULT_VALUE = ".defaultValue";
        String DEFAULT_VALUE_LABEL = ".defaultValueLabel";
        String DO_NOT_DISPLAY = ".doNotDisplay";
        String REQUIRED = ".required";
        String OMIT_IF_BLANK = ".omitIfBlank";
        String TOOL_TIP = ".toolTip";
        String VALIDATOR_RULES = ".validatorRules";
        String EDIT_LIST = ".editList";
        String FILE_INFO_TYPE = ".fileInfoType";
        String DATA_SOURCE = ".dataSource";
        String NAME = ".name";
        String DELETE = ".delete";
        String CHECKED_OPTION = ".checkedOption";
        String CHECKED_VALUE = ".checkedValue";
        String UNCHECKED_OPTION = ".unCheckedOption";
        String UNCHECKED_VALUE = ".unCheckedValue";
        String COMMAND_LINE = ".commandLine";
    }
}
