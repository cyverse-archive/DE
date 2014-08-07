package org.iplantc.de.shared;

/**
 * top level items don't need '.' prefix. so we can have gwt-debug-analysesWindow instead of
 * gwt-debug-.analysesWindow
 * 
 * @author sriram/stroot
 * 
 */
public interface DeModule {
    interface Ids {
        /**
         * top level items grouping
         * 
         */
        String ANALYSES_WINDOW = "analysesWindow";
        String APPS_WINDOW = "appsWindow";
        String APP_EDITOR_WINDOW = "appEditorWindow";
        String DESKTOP = "desktop";
        /**
         * sub-items
         */
        String NOTIFICATION_BUTTON = ".notificationButton";
        String USER_PREF_MENU = ".userPrefMenu";
        String FORUMS_BUTTON = ".forumsButton";

        String DISK_RESOURCE_WINDOW = "diskResourceWindow";

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
}
