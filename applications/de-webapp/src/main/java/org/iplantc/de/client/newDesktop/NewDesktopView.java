package org.iplantc.de.client.newDesktop;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;

import com.sencha.gxt.widget.core.client.button.IconButton;

/**
 * TODO Opacity for tool button hover styles
 *
 * Created by jstroot on 7/9/14.
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
    interface Presenter {

        /**
         * <ul>
         *     <li>Initialize keyboard shortcuts
         *     <li>Init Save session
         *     <li>Initialize DE Properties
         * </ul>
         * @param panel
         */
        void go(Panel panel);

        void onAnalysesWinBtnSelect();

        void onAppsWinBtnSelect();

        void onDataWinBtnSelect();

        void onForumsBtnSelect();

        void onNotificationsBtnSelect();

        void onUserPrefsBtnSelect();

    }

    void ensureDebugId(String baseID);

    void setPresenter(Presenter presenter);
}
