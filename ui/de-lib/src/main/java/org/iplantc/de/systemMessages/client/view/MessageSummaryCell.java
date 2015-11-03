package org.iplantc.de.systemMessages.client.view;

import org.iplantc.de.systemMessages.client.events.DismissMessageEvent;
import org.iplantc.de.systemMessages.client.view.MessagesView.MessageProperties;
import org.iplantc.de.resources.client.SystemMessagesResources;
import org.iplantc.de.resources.client.SystemMessagesResources.Style;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Event;

import com.sencha.gxt.cell.core.client.AbstractEventCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.event.XEvent;

import java.util.Date;

/**
 * This is the cell used to render message summaries.
 * 
 * @param <M> the type of message
 */
final class MessageSummaryCell<M> extends AbstractEventCell<M> {
	
	interface Templates extends XTemplates {
        @XTemplate(source = "MessageSummary.html")
        SafeHtml make(String type, boolean seen, boolean dismissible, String activationTimeMsg, Style style);
	}
	
	private static final Style CSS;
    private static final Templates FACTORY;

    static {
        FACTORY = GWT.create(Templates.class);
        CSS = GWT.<SystemMessagesResources> create(SystemMessagesResources.class).style();
    	CSS.ensureInjected();
    }

    private final MessageProperties<M> messageProperties;
    private final Renderer<Date> activationRenderer;

    /**
     * the constructor
     * 
     * @param messageProperties the properties provider for a message
     * @param activationRenderer the renderer for rendering activation times
     */
    MessageSummaryCell(final MessageProperties<M> messageProperties, final Renderer<Date> activationRenderer) {
        super(BrowserEvents.CLICK);
        this.messageProperties = messageProperties;
        this.activationRenderer = activationRenderer;
	}

    /**
     * @see AbstractEventCell<T>#onBrowserEvent(Context, Element, T, NativeEvent, ValueUpdater)
     */
    @Override
    public void onBrowserEvent(final Context context, final Element parent, final M message, final NativeEvent nativeEvent, final ValueUpdater<M> updater) {
		final XEvent event = nativeEvent.<XEvent>cast();
        if (event.getTypeInt() == Event.ONCLICK) {
            if (event.getEventTargetEl().hasClassName(CSS.dismiss())) {
                fireEvent(new DismissMessageEvent(messageProperties.id().getKey(message)));
            }
        }
    }
	
    /**
     * @see AbstractEventCell<T>#render(com.google.gwt.cell.client.Cell.Context, T, SafeHtmlBuilder)
     */
	@Override
    public void render(final Context context, final M message, final SafeHtmlBuilder builder) {
        final String type = messageProperties.type().getValue(message);
        final boolean seen = messageProperties.seen().getValue(message);
        final boolean dissmissible = messageProperties.dismissible().getValue(message);
        final Date actTime = messageProperties.activationTime().getValue(message);
        final String actMsg = activationRenderer.render(actTime);
        builder.append(FACTORY.make(type, seen, dissmissible, actMsg, CSS));
    }

}
