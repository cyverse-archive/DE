package org.iplantc.de.client.models;

import org.iplantc.de.client.KeyBoardShortcutConstants;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

/**
 * 
 * A singleton hold user general settings
 * 
 * @author sriram
 * 
 */
public class UserSettings {

    private final KeyBoardShortcutConstants SHORTCUTS = GWT.create(KeyBoardShortcutConstants.class);
    private boolean enableAnalysisEmailNotification;
    private boolean enableImportEmailNotification;
    private String defaultFileSelectorPath;
    private boolean rememberLastPath;
    private boolean saveSession;
    private Folder defaultOutputFolder;
    private String dataShortCut;
    private String appShortCut;
    private String analysesShortCut;
    private String notifyShortCut;
    private String closeShortCut;
    private Folder systemDefaultOutputFolder;
    private String lastPath;
    
    
    public static final String EMAIL_ANALYSIS_NOTIFCATOIN = "enableAnalysisEmailNotification";
    public static final String EMAIL_IMPORT_NOTIFICATION = "enableImportEmailNotification";
    public static final String DEFAULT_FILE_SELECTOR_PATH = "defaultFileSelectorPath";
    public static final String REMEMBER_LAST_PATH = "rememberLastPath";
    public static final String SAVE_SESSION = "saveSession";
    public static final String DEFAULT_OUTPUT_FOLDER = "defaultOutputFolder";
    public static final String DATA_KB_SHORTCUT = "dataKBShortcut";
    public static final String APPS_KB_SHORTCUT = "appsKBShortcut";
    public static final String ANALYSIS_KB_SHORTCUT = "analysisKBShortcut";
    public static final String NOTIFICATION_KB_SHORTCUT = "notificationKBShortcut";
    public static final String CLOSE_KB_SHORTCUT_STRING = "closeKBShortcut";
    public static final String SYSTEM_DEFAULT_OUTPUT_DIR = "systemDefaultOutputDir";
    public static final String LAST_PATH = "lastPathId";


    private static UserSettings instance;

    public UserSettings(final Splittable userSettingsSplit){
        setValues(userSettingsSplit);
    }

    private UserSettings() {
        this.enableAnalysisEmailNotification = false;
        this.rememberLastPath = false;
        this.saveSession = true;
    }

    public static UserSettings getInstance() {
        if (instance == null) {
            instance = new UserSettings();
        }

        return instance;
    }

    public void setValues(JSONObject obj) {
        if (obj == null) {
            return;
        }

        setValues(StringQuoter.split(obj.toString()));
    }

    public void setValues(Splittable split) {
        if ((split == null) || (split == Splittable.NULL)) {
            return;
        }

        if (split.get(EMAIL_ANALYSIS_NOTIFCATOIN) != null) {
            setEnableAnalysisEmailNotification(split.get(EMAIL_ANALYSIS_NOTIFCATOIN).asBoolean());
        } else {
            setEnableAnalysisEmailNotification(true);
        }
        if (split.get(EMAIL_IMPORT_NOTIFICATION) != null) {
            setEnableImportEmailNotification(split.get(EMAIL_IMPORT_NOTIFICATION).asBoolean());
        } else {
            setEnableImportEmailNotification(true);
        }

        if (split.get(DEFAULT_FILE_SELECTOR_PATH) != null) {
            setDefaultFileSelectorPath(split.get(DEFAULT_FILE_SELECTOR_PATH).asString());
        }
        if (split.get(REMEMBER_LAST_PATH) != null) {
            setRememberLastPath(split.get(REMEMBER_LAST_PATH).asBoolean());
        } else {
            setRememberLastPath(true);
        }

        if (split.get(SAVE_SESSION) != null) {
            setSaveSession(split.get(SAVE_SESSION).asBoolean());
        } else {
            setSaveSession(true);
        }

        if (split.get(DEFAULT_OUTPUT_FOLDER) != null) {
            setDefaultOutputFolder(buildFolder(split.get(DEFAULT_OUTPUT_FOLDER)));
        }

        if (split.get(SYSTEM_DEFAULT_OUTPUT_DIR) != null) {
            setSystemDefaultOutputFolder(buildFolder(split.get(SYSTEM_DEFAULT_OUTPUT_DIR)));
        }

        if (split.get(LAST_PATH) != null) {
            setLastPath(split.get(LAST_PATH).asString());
        }

        parseKeyboardShortcuts(split);
    }

    private void parseKeyboardShortcuts(Splittable split) {
        if ((split == null) || (split == Splittable.NULL)) {
            setDataShortCut(SHORTCUTS.dataKeyShortCut());
            setAppsShortCut(SHORTCUTS.appsKeyShortCut());
            setAnalysesShortCut(SHORTCUTS.analysisKeyShortCut());
            setNotifyShortCut(SHORTCUTS.notifyKeyShortCut());
            setCloseShortCut(SHORTCUTS.closeKeyShortCut());
            return;
        }

        final Splittable dataShortcutSplit = split.get(DATA_KB_SHORTCUT);
        if ((dataShortcutSplit == null) || !dataShortcutSplit.isString()) {
            setDataShortCut(SHORTCUTS.dataKeyShortCut());
        } else {
            setDataShortCut(dataShortcutSplit.asString());
        }

        final Splittable appsShortcutSplit = split.get(APPS_KB_SHORTCUT);
        if ((appsShortcutSplit == null) || !appsShortcutSplit.isString()) {
            setAppsShortCut(SHORTCUTS.appsKeyShortCut());
        } else {
            setAppsShortCut(appsShortcutSplit.asString());
        }

        final Splittable analysesShortcutSplit = split.get(ANALYSIS_KB_SHORTCUT);
        if ((analysesShortcutSplit == null) || !analysesShortcutSplit.isString()) {
            setAnalysesShortCut(SHORTCUTS.analysisKeyShortCut());
        } else {
            setAnalysesShortCut(analysesShortcutSplit.asString());
        }

        final Splittable notifyShortcutSplit = split.get(NOTIFICATION_KB_SHORTCUT);
        if ((notifyShortcutSplit == null) || !notifyShortcutSplit.isString()) {
            setNotifyShortCut(SHORTCUTS.notifyKeyShortCut());
        } else {
            setNotifyShortCut(notifyShortcutSplit.asString());
        }

        final Splittable closeShortcutSplit = split.get(CLOSE_KB_SHORTCUT_STRING);
        if ((closeShortcutSplit == null) || !closeShortcutSplit.isString()) {
            setCloseShortCut(SHORTCUTS.closeKeyShortCut());
        } else {
            setCloseShortCut(closeShortcutSplit.asString());
        }
    }

    public void setDataShortCut(String c) {
        this.dataShortCut = c;
        
    }

    public String getDataShortCut() {
        return dataShortCut;
    }

    public void setAppsShortCut(String c) {
        this.appShortCut = c;
    }

    public String getAppsShortCut() {
        return appShortCut;
    }

    public void setAnalysesShortCut(String c) {
        this.analysesShortCut = c;
    }

    public String getAnalysesShortCut() {
        return analysesShortCut;
    }

    public void setNotifyShortCut(String c) {
        this.notifyShortCut = c;
    }

    public String getNotifyShortCut() {
        return notifyShortCut;
    }
    /**
     * @param enableAnalysisEmailNotification the enableAnalysisEmailNotification to set
     */
    public void setEnableAnalysisEmailNotification(boolean enableAnalysisEmailNotification) {
        this.enableAnalysisEmailNotification = enableAnalysisEmailNotification;
    }

    /**
     * @return the enableAnalysisEmailNotification
     */
    public boolean isEnableAnalysisEmailNotification() {
        return enableAnalysisEmailNotification;
    }

    /**
     * @param defaultFileSelectorPath the defaultFileSelectorPath to set
     */
    public void setDefaultFileSelectorPath(String defaultFileSelectorPath) {
        this.defaultFileSelectorPath = defaultFileSelectorPath;
    }

    /**
     * @return the defaultFileSelectorPath
     */
    public String getDefaultFileSelectorPath() {
        return (defaultFileSelectorPath == null) ? "" : defaultFileSelectorPath;
    }

    /**
     * Get Splittable representation
     * 
     * @return
     */
    public Splittable asSplittable() {
        Splittable ret = StringQuoter.createSplittable();
        StringQuoter.create(isEnableAnalysisEmailNotification()).assign(ret, EMAIL_ANALYSIS_NOTIFCATOIN);
        StringQuoter.create(isEnableImportEmailNotification()).assign(ret, EMAIL_IMPORT_NOTIFICATION);
        StringQuoter.create(getDefaultFileSelectorPath()).assign(ret, DEFAULT_FILE_SELECTOR_PATH);
        StringQuoter.create(isRememberLastPath()).assign(ret, REMEMBER_LAST_PATH);
        StringQuoter.create(isSaveSession()).assign(ret, SAVE_SESSION);
        AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(getDefaultOutputFolder())).assign(ret, DEFAULT_OUTPUT_FOLDER);
        StringQuoter.create(getAppsShortCut()).assign(ret, APPS_KB_SHORTCUT);
        StringQuoter.create(getAnalysesShortCut()).assign(ret, ANALYSIS_KB_SHORTCUT);
        StringQuoter.create(getDataShortCut()).assign(ret, DATA_KB_SHORTCUT);
        StringQuoter.create(getNotifyShortCut()).assign(ret, NOTIFICATION_KB_SHORTCUT);
        StringQuoter.create(getCloseShortCut()).assign(ret, CLOSE_KB_SHORTCUT_STRING);
        Splittable sysDefFolder = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(getSystemDefaultOutputFolder()));
        sysDefFolder.assign(ret, SYSTEM_DEFAULT_OUTPUT_DIR);
        StringQuoter.create(getLastPath()).assign(ret, LAST_PATH);

        return ret;
    }

    /**
     * @param rememberLastPath the rememberLastPath to set
     */
    public void setRememberLastPath(boolean rememberLastPath) {
        this.rememberLastPath = rememberLastPath;
    }

    /**
     * @return the rememberLastPath
     */
    public boolean isRememberLastPath() {
        return rememberLastPath;
    }

    /**
     * 
     * 
     * @param saveSession
     */
    public void setSaveSession(boolean saveSession) {
        this.saveSession = saveSession;
    }

    public boolean isSaveSession() {
        return saveSession;
    }

    /**
     * @param defaultOutputFolder the new default output folder.
     */
    public void setDefaultOutputFolder(Folder defaultOutputFolder) {
        this.defaultOutputFolder = defaultOutputFolder;
    }

    private Folder buildFolder(Splittable defaultOutputFolder) {
        DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
        AutoBean<Folder> FolderBean = factory.folder();
        Folder folder = FolderBean.as();
        folder.setId(defaultOutputFolder.get("id").asString());
        folder.setPath(defaultOutputFolder.get("path").asString());
        return folder;
    }

    /**
     * @return the default output folder.
     */
    public Folder getDefaultOutputFolder() {
        return defaultOutputFolder;
    }

    /**
     * @return the closeShortCut
     */
    public String getCloseShortCut() {
        return closeShortCut;
    }

    /**
     * @param closeShortCut the closeShortCut to set
     */
    public void setCloseShortCut(String closeShortCut) {
        this.closeShortCut = closeShortCut;
    }

    /**
     * @return the systemDefaultOutputFolder
     */
    public Folder getSystemDefaultOutputFolder() {
        return systemDefaultOutputFolder;
    }

    /**
     * @param systemDefaultOutputFolder the systemDefaultOutputFolder to set
     */
    public void setSystemDefaultOutputFolder(Folder systemDefaultOutputFolder) {
        this.systemDefaultOutputFolder = systemDefaultOutputFolder;
        GWT.log("System Default Output folder set: path = " + systemDefaultOutputFolder.getPath());
    }

    /**
     * @return the lastPath
     */
    public String getLastPath() {
        return lastPath;
    }

    /**
     * @param lastPath the lastPath to set
     */
    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    public boolean isEnableImportEmailNotification() {
        return enableImportEmailNotification;
    }

    public void setEnableImportEmailNotification(boolean enableImportEmailNotification) {
        this.enableImportEmailNotification = enableImportEmailNotification;
    }
}
