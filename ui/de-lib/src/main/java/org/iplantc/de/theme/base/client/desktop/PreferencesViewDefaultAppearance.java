package org.iplantc.de.theme.base.client.desktop;

import org.iplantc.de.desktop.client.views.widgets.PreferencesDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class PreferencesViewDefaultAppearance implements PreferencesDialog.PreferencesViewAppearance {
    private final IplantDisplayStrings displayStrings;
    private final DesktopContextualHelpMessages help;
    private final DesktopMessages desktopMessages;

    PreferencesViewDefaultAppearance(final IplantDisplayStrings displayStrings,
                                     final DesktopContextualHelpMessages help,
                                     final DesktopMessages desktopMessages) {
        this.displayStrings = displayStrings;
        this.help = help;
        this.desktopMessages = desktopMessages;
    }

    PreferencesViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<DesktopContextualHelpMessages> create(DesktopContextualHelpMessages.class),
             GWT.<DesktopMessages> create(DesktopMessages.class));
    }

    @Override
    public String defaultOutputFolderHelp() {
        return help.defaultOutputFolderHelp();
    }

    @Override
    public String done() {
        return displayStrings.done();
    }

    @Override
    public String duplicateShortCutKey(String key) {
        return desktopMessages.duplicateShortCutKey(key);
    }

    @Override
    public String notifyEmailHelp() {
        return help.notifyEmailHelp();
    }

    @Override
    public String preferences() {
        return desktopMessages.preferences();
    }

    @Override
    public String notifyAnalysisEmail() {
        return desktopMessages.notifyAnalysisEmail();
    }

    @Override
    public String notifyImportEmail() {
        return desktopMessages.notifyImportEmail();
    }

    @Override
    public String completeRequiredFieldsError() {
        return displayStrings.completeRequiredFieldsError();
    }

    @Override
    public String rememberFileSectorPath() {
        return desktopMessages.rememberFileSectorPath();
    }

    @Override
    public String rememberFileSectorPathHelp() {
        return help.rememberFileSelectorPathHelp();
    }

    @Override
    public String restoreDefaults() {
        return desktopMessages.restoreDefaults();
    }

    @Override
    public String saveSession() {
        return desktopMessages.saveSession();
    }

    @Override
    public String defaultOutputFolder() {
        return desktopMessages.defaultOutputFolder();
    }

    @Override
    public String keyboardShortCut() {
        return desktopMessages.keyboardShortcuts();
    }

    @Override
    public String openAppsWindow() {
        return desktopMessages.openAppsWindow();
    }

    @Override
    public String kbShortcutMetaKey() {
        return desktopMessages.keyboardShortcutMetaKey();
    }

    @Override
    public String oneCharMax() {
        return desktopMessages.oneCharMax();
    }

    @Override
    public String openDataWindow() {
        return desktopMessages.openDataWindow();
    }

    @Override
    public String openAnalysesWindow() {
        return desktopMessages.openAnalysesWindow();
    }

    @Override
    public String openNotificationsWindow() {
        return desktopMessages.openNotificationsWindow();
    }

    @Override
    public String closeActiveWindow() {
        return desktopMessages.closeActiveWindow();
    }

    @Override
    public String saveSessionHelp() {
        return help.saveSessionHelp();
    }

    @Override
    public String notifyEmail() {
        return help.notifyEmail();
    }
}
