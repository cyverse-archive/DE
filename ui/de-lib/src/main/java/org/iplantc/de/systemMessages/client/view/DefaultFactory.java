package org.iplantc.de.systemMessages.client.view;

import org.iplantc.de.systemMessages.client.view.MessagesView.MessageProperties;

import com.google.gwt.text.shared.Renderer;

import java.util.Date;

/**
 * This the factory for creating the default system message related views.
 */
public final class DefaultFactory implements Factory {

    /**
     * @see Factory#makeMessagesView(MessagesView.Presenter, MessageProperties, Renderer<Date>)
     */
    @Override
    public <M> MessagesView<M> makeMessagesView(final MessagesView.Presenter<M> presenter, final MessageProperties<M> messageProperties, final Renderer<Date> activationRenderer) {
        return new DefaultMessagesView<M>(presenter, messageProperties, activationRenderer);
    }

    /**
     * @see Factory#makeNewMessageView(NewMessageView.Presenter)
     */
    @Override
    public NewMessageView makeNewMessageView(final NewMessageView.Presenter presenter) {
        return new DefaultNewMessageView(presenter);
    }

}
