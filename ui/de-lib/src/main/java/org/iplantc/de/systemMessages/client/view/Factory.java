package org.iplantc.de.systemMessages.client.view;

import com.google.gwt.text.shared.Renderer;

import java.util.Date;

/**
 * A factory for making MessageView objects
 */
public interface Factory {
    /**
     * Initializes the messages display view
     * 
     * @param <M> the type of message to view
     * @param presenter the presenter for this view
     * @param messageProperties the message properties provider
     * @param activationRenderer the renderer used to render the activation time
     * 
     * @return the view
     */
    <M> MessagesView<M> makeMessagesView(MessagesView.Presenter<M> presenter, MessagesView.MessageProperties<M> messageProperties, Renderer<Date> activationRenderer);

    /**
     * Creates the new message arrived view
     * 
     * @param presenter the presenter that manages the view
     * 
     * @return the view
     */
    NewMessageView makeNewMessageView(NewMessageView.Presenter presenter);

}