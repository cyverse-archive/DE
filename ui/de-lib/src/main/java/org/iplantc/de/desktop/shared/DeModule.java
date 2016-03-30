package org.iplantc.de.desktop.shared;

/**
 * top level items don't need '.' prefix. so we can have gwt-debug-analysesWindow instead of
 * gwt-debug-.analysesWindow
 * 
 * @author sriram
 * @author jstroot
 * 
 */
public interface DeModule {
    interface Ids {
        String DESKTOP = "desktop";
        /**
         * sub-items
         */
        String NOTIFICATION_BUTTON = ".notificationButton";
        String USER_PREF_MENU = ".userPrefMenu";
        String FORUMS_BUTTON = ".forumsButton";

        /**
         * window tool buttons
         */
        String WIN_MAX_BTN = ".maximize";
        String WIN_RESTORE_BTN = ".restore";
        String WIN_MIN_BTN = ".minimize";
        String WIN_CLOSE_BTN = ".close";
        String WIN_LAYOUT_BTN = ".layout";


        String DATA_BTN = ".dataBtn";
        String APPS_BTN = ".appsBtn";
        String ANALYSES_BTN = ".analysesBtn";
        String FEEDBACK_BTN = ".feedbackBtn";
        String TASK_BAR = ".deTaskBar";
        String PREFERENCES_BTN = ".preferences";
        String COLLABORATORS_BTN = ".collaborators";
        String SYS_MSGS_BTN = ".systemMessages";
        String USER_MANUAL_BTN = ".userManual";
        String INTRO_BTN = ".introduction";
        String ABOUT_BTN = ".about";
        String LOGOUT_BTN = ".logout";
        String SUPPORT_BTN = ".support";
    }

    interface PreferenceIds {
        String PREFERENCES_DLG = "preferencesDlg";
        String DONE = ".done";
        String CANCEL = ".cancel";
        String DEFAULTS_BTN = ".defaults";
        String APPS_SC = ".appsShortcut";
        String DATA_SC = ".dataShortcut";
        String ANALYSES_SC = ".analysesShortcut";
        String NOTIFICATION_SC = ".notificationsShortcut";
        String CLOSE_SC = ".closeActiveShortcut";
        String EMAIL_ANALYSIS_NOTIFICATION = ".emailAnalysisNotification";
        String EMAIL_IMPORT_NOTIFICATION = ".emailImportNotification";
        String REMEMBER_LAST_PATH = ".rememberLastPath";
        String SAVE_SESSION = ".saveSession";
        String DEFAULT_OUTPUT_FOLDER = ".defaultOutputFolder";
        String BROWSE_OUTPUT_FOLDER = ".browseButton";
        String DEFAULT_OUTPUT_FIELD = ".inputField";
    }

    interface WindowIds {

        /**
         * top level items grouping
         *
         */
        String ANALYSES_WINDOW = "analysesWindow";
        String APPS_WINDOW = "appsWindow";
        String APP_EDITOR_WINDOW = "appEditorWindow";
        String DISK_RESOURCE_WINDOW = "diskResourceWindow";
        String APP_LAUNCH_WINDOW = "appLaunchWindow";
        String NOTIFICATION = "notificationWindow";
        String WORKFLOW_EDITOR = "workflowEditorWindow";
        String SIMPLE_DOWNLOAD = "simpleDownloadWindow";
        String SYSTEM_MESSAGES = "systemMessageWindow";
        String ABOUT_WINDOW = "aboutWindow";
    }
}
