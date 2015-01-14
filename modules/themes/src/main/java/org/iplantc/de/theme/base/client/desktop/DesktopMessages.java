package org.iplantc.de.theme.base.client.desktop;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/14/15.
 * @author jstroot
 */
public interface DesktopMessages extends Messages {

    @Key("about")
    String about();

    @Key("closeActiveWindow")
    String closeActiveWindow();

    @Key("contactSupport")
    String contactSupport();

    @Key("defaultOutputFolder")
    String defaultOutputFolder();

    @Key("duplicateShortCutKey")
    String duplicateShortCutKey(String key);

    @Key("forums")
    String forums();

    @Key("introduction")
    String introduction();

    @Key("keyboardShortcutMetaKey")
    String keyboardShortcutMetaKey();

    @Key("keyboardShortcuts")
    String keyboardShortcuts();

    @Key("notifyEmail")
    String notifyEmail();

    @Key("oneCharMax")
    String oneCharMax();

    @Key("openAnalysesWindow")
    String openAnalysesWindow();

    @Key("openAppsWindow")
    String openAppsWindow();

    @Key("openDataWindow")
    String openDataWindow();

    @Key("openNotificationsWindow")
    String openNotificationsWindow();

    @Key("preferences")
    String preferences();

    @Key("rememberFileSectorPath")
    String rememberFileSectorPath();

    @Key("restoreDefaults")
    String restoreDefaults();

    @Key("saveSession")
    String saveSession();
}
