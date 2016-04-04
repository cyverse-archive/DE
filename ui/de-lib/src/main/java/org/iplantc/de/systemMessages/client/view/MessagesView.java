package org.iplantc.de.systemMessages.client.view;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;

import java.util.Date;

/**
 * This interface describes the needed functionality of something that displays a list of system
 * messages. Implementations should allow the selection of a single message.
 * 
 * @param <M> the message type
 */
public interface MessagesView<M> extends IsWidget {

    /**
     * The properties of the messages used by the view
     */
    interface MessageProperties<M> extends PropertyAccess<M> {
        /**
         * the message id provider for providing index keys
         */
        ModelKeyProvider<M> id();

        /**
         * the message type provider
         */
        ValueProvider<M, String> type();

        /**
         * the activation time provider
         */
        ValueProvider<M, Date> activationTime();

        /**
         * the seen provider
         */
        ValueProvider<M, Boolean> seen();

        /**
         * the dismissible provider
         */
        ValueProvider<M, Boolean> dismissible();
    }

    /**
     * The interface a presenter of a message view must implement
     * 
     * @param <M> the type of message to present
     */
    interface Presenter<M> {
        /**
         * handle a user request to dismiss a message
         * 
         * @param message the message to dismiss
         */
        void handleDismissMessage(M message);

        /**
         * handle a user request to select a message
         * 
         * @param message the message to select
         */
        void handleSelectMessage(M message);

        void setViewDebugId(String debugId);
    }

    /**
     * Makes sure that the summary of the given index is visible in the summary list.
     * 
     * @param messageIndex The index of the message in the list store
     */
    void scrollIntoView(int messageIndex);

    /**
     * returns the message store backing the view
     * 
     * @return the message store
     */
    ListStore<M> getMessageStore();

    /**
     * returns the selection model backing the view
     * 
     * @return the selection model
     */
    ListViewSelectionModel<M> getSelectionModel();

    /**
     * Puts a mask over the view to disable user interaction
     * 
     * @param maskMessage the message to display while masking
     */
    void mask(String maskMessage);

    /**
     * Provides the expiration message to display for the selected system message
     * 
     * @param expiryMsg the expiration message
     */
    public void setExpiryMessage(String expiryMsg);

    /**
     * Provides the body to display for the selected message
     * 
     * @param msgBody the message body
     */
	public void setMessageBody(SafeHtml msgBody);

    /**
     * Tells the view to show the loading panel.
     */
    void showLoading();

    /**
     * Tells the view to show the messages panel.
     */
    void showMessages();

    /**
     * Tells the view to show the no messages panel.
     */
    void showNoMessages();

    /**
     * Ask the user to verify that the provided message should be dismissed.
     * 
     * @param dismiss the command to execute if the user confirms the dismissal
     */
    void verifyMessageDismissal(Command dismiss);

    /**
     * Removes a mask from the view
     */
    void unmask();

}
