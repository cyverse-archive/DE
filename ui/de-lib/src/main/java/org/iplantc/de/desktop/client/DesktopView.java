package org.iplantc.de.desktop.client;

import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.IconButton;

import java.util.List;

/**
 * TODO JDS Change initial display time for user menu tooltips
 * TODO JDS Window layout, implement as 'one-shot' layouts. 'layout then done' no re-sizing
 *
 * Notifications, window events, layouts
 *
 * <ul>
 *     <li>Task bar buttons are controlled from the view.
 *     <li>Moved minimize functionality to IplantWindowBase. Was previously in Desktop.minimizeWindow
 *     <li>Getting rid of window managers as it previously existed. Making more use of GXT's existing
 *         WindowManager.
 * </ul>
 *
 * @author jstroot
 */
public interface DesktopView extends IsWidget {

    interface DesktopAppearance {
        interface DesktopStyles extends CssResource {
            String analyses();

            String apps();

            String data();

            String deBody();

            String desktopBackground();

            String feedback();

            String forums();

            String iplantHeader();

            String logo();

            String logoContainer();

            String notification();

            String notificationCount();

            String taskBarLayout();

            String userMenuContainer();

            String userPrefs();

            String windowBtnNav();

            String desktopBackgroundRepeat();

            String logoText();

            String userContextMenu();
        }

        IconButton.IconConfig analysisConfig();

        IconButton.IconConfig appsConfig();

        IconButton.IconConfig dataConfig();

        String feedbackAlertValidationWarning();

        IconButton.IconConfig feedbackBtnConfig();

        IconButton.IconConfig forumsConfig();

        IconButton.IconConfig notificationsConfig();

        String completeRequiredFieldsError();

        String rootApplicationTitle(int count);

        String rootApplicationTitle();

        DesktopStyles styles();

        IconButton.IconConfig userPrefsConfig();

        String notifications();

        String preferences();

        String collaborators();

        String systemMessagesLabel();

        String introduction();

        String documentation();

        String contactSupport();

        String about();

        String logout();

        String iconHomepageDataTip();

        String forums();

        String iconHomepageAnalysesTip();

        String iconHomepageAppsTip();
    }

    /**
     * This presenter is responsible for the following;
     * -- Initializing properties, user info, user settings.
     * -- Desktop window management.
     * -- Maintaining user session saving
     * -- Handling user desktop events for the desktop icons and user settings menu
     *
     *
     * TODO JDS Eventually, certain injected parameters will be injected via an AsyncProvider
     *           This will provide us with split points through injection. Only items which are not
     *           immediately necessary should be provided this way.
     */
    interface Presenter extends UserSettingsMenuPresenter, UnseenNotificationsPresenter{

        interface DesktopPresenterAppearance {

            String feedbackServiceFailure();

            String feedbackSubmitted();

            String fetchNotificationsError();

            String introWelcome();

            String loadSessionFailed();

            String loadingSession();

            String loadingSessionWaitNotice();

            String markAllAsSeenSuccess();

            String newNotificationsAlert();

            String permissionErrorMessage();

            String permissionErrorTitle();

            String saveSessionFailed();

            String saveSettings();

            String savingSession();

            String savingSessionWaitNotice();

            SafeHtml sessionRestoreCancelled();

            String systemInitializationError();

            String welcome();

            String requestHistoryError();
        }

        List<WindowState> getOrderedWindowStates();

        void go(Panel panel);

        void onAnalysesWinBtnSelect();

        void onAppsWinBtnSelect();

        void onDataWinBtnSelect();

        void onForumsBtnSelect();

        void onNotificationSelected(NotificationMessage selectedItem);

        void saveUserSettings(UserSettings value, boolean updateSilently);

        void show(final WindowConfig config);

        void show(final WindowConfig config, final boolean updateExistingWindow);

        void submitUserFeedback(Splittable splittable, IsHideable isHideable);

    }

    interface UserSettingsMenuPresenter {

        void onAboutClick();

        void onCollaboratorsClick();

        void onContactSupportClick();

        void onDocumentationClick();

        void onIntroClick();

        void doLogout(boolean sessionTimeout);

        void onSystemMessagesClick();
    }

    interface UnseenNotificationsPresenter {

        void doMarkAllSeen(boolean announce);

        void doSeeAllNotifications();

        void doSeeNewNotifications();
    }

    void ensureDebugId(String baseID);

    /**
     * @return the desktop container element used to constrain {@link org.iplantc.de.desktop.client.views.windows.IPlantWindowInterface} classes
     */
    Element getDesktopContainer();

    ListStore<NotificationMessage> getNotificationStore();

    int getUnseenNotificationCount();

    void setPresenter(Presenter presenter);

    void setUnseenNotificationCount(int count);

    void setUnseenSystemMessageCount(int count);

    void hideNotificationMenu();
}
