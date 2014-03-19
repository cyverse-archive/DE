package org.iplantc.de.client.desktop.views;

import org.iplantc.de.client.desktop.widget.Desktop;
import org.iplantc.de.client.models.WindowState;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * DE Main view
 * 
 * 
 * @author sriram
 * 
 */
public interface DEView extends IsWidget {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        void doLogout();

        /**
         * Restores the windows specified by the given list of <code>WindowState</code> objects. This
         * method restores the windows in the order they are given.
         * 
         * @param windowStates
         */
        void restoreWindows(List<WindowState> windowStates);

        List<WindowState> getOrderedWindowStates();

        void doPeriodicSessionSave();

        void doWelcomeIntro();

        void cleanUp();
    }

    /**
     * set up DE main header logo and menus
     * 
     */
    void drawHeader();

    /**
     * Set the presenter for this view
     * 
     * @param presenter
     */
    void setPresenter(final Presenter presenter);

    /**
     * XXX JDS This method should not exist in the view. Eventually, all window management functionality
     * should be contained within the presenter.
     * 
     * @return
     */
    List<WindowState> getOrderedWindowStates();

    void restoreWindows(List<WindowState> windowStates);

    /**
     * Changes the value of the displayed number of unread system messages.
     * 
     * @param numUnseenSysMsgs The new number of unread system messages
     */
    void updateUnseenSystemMessageCount(long numUnseenSysMsgs);

    Desktop getDesktop();

    void cleanUp();

}
