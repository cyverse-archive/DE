package org.iplantc.de.systemMessages.client.view;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * This is interface for a view that can announce the arrival of a new message.
 */
public interface NewMessageView extends IsWidget {

    /**
     * This is the interface of something that can present such a view
     */
    interface Presenter {
        /**
         * Handle the callback from a request to open the system messages display.
         */
        void handleDisplayMessages();
    }

}
