package org.iplantc.de.client.newDesktop;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

import com.sencha.gxt.widget.core.client.button.IconButton;

/**
 * @author jstroot
 */
public interface NewDesktopView extends IsWidget {

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

        IconButton.IconConfig feedbackBtnConfig();

        IconButton.IconConfig forumsConfig();

        IconButton.IconConfig notificationsConfig();

        DesktopStyles styles();

        IconButton.IconConfig userPrefsConfig();
    }

    /**
     * -- The presenter is responsible for window management.
     */
    interface Presenter extends UserSettingsMenuPresenter {

        /**
         * <ul>
         *     <li>Initialize keyboard shortcuts
         *     <li>Init Save session
         *     <li>Initialize DE Properties
         *     <li>Do initial fetch of unseen notifications
         * </ul>
         * @param panel
         */
        void go(Panel panel);

        void onAnalysesWinBtnSelect();

        void onAppsWinBtnSelect();

        void onDataWinBtnSelect();

        void onForumsBtnSelect();

    }

    interface UserSettingsMenuPresenter {

        void onAboutClick();

        void onCollaboratorsClick();

        void onContactSupportClick();

        void onDocumentationClick();

        void onIntroClick();

        void onLogoutClick();

        void onPreferencesClick();

        void onSystemMessagesClick();
    }

    void ensureDebugId(String baseID);

    void setPresenter(Presenter presenter);

    void setUnseenNotificationCount(int count);
}
