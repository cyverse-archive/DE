package org.iplantc.de.systemMessages.client.view;

import org.iplantc.de.resources.client.SystemMessagesResources;
import org.iplantc.de.systemMessages.client.events.DismissMessageEvent;
import org.iplantc.de.systemMessages.shared.SystemMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.Date;
import java.util.List;

/**
 * This is the default implementation of the messages view.
 * 
 * @param <M> the type of message to view
 */
final class DefaultMessagesView<M> extends Composite implements MessagesView<M> {

    interface Binder extends UiBinder<Widget, DefaultMessagesView<?>> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
    private static final <M> ListView<M, M> makeMessageList(final MessageProperties<M> msgProps) {
        final ListStore<M> store = new ListStore<M>(msgProps.id());
        store.addSortInfo(new StoreSortInfo<M>(msgProps.activationTime(), SortDir.DESC));
        final IdentityValueProvider<M> prov = new IdentityValueProvider<M>();
        final SummaryListAppearance<M> appr = new SummaryListAppearance<M>();
        final ListView<M, M> list = new ListView<M, M>(store, prov, appr);
        final ListViewSelectionModel<M> sel = new ListViewSelectionModel<M>();
        sel.setSelectionMode(SelectionMode.SINGLE);
        list.setSelectionModel(sel);
        return list;
    }

	@UiField
    SystemMessagesResources res;

    @UiField
    CardLayoutContainer layout;

    @UiField
    Widget loadingPanel;

    @UiField
    Widget noMessagesPanel;

    @UiField
    ResizeContainer messagesPanel;

    @UiField
	HTML messageView;
	
    @UiField
    Label expiryView;

    @UiField(provided = true)
    final ListView<M, M> messageList;

    private final MessageSummaryCell<M> summaryCell;
    private final Presenter<M> presenter;
    private String HIDDEN_ATTRIBUTE = "hidden";

    /**
     * the constructor
     * 
     * @param presenter the corresponding presenter
     * @param messageProperties the message properties provider
     * @param activationRenderer the renderer for the activation time
     */
    DefaultMessagesView(final Presenter<M> presenter, final MessageProperties<M> messageProperties, final Renderer<Date> activationRenderer) {
        this.presenter = presenter;
        summaryCell = new MessageSummaryCell<M>(messageProperties, activationRenderer);
        messageList = makeMessageList(messageProperties);
        initWidget(binder.createAndBindUi(this));
        res.style().ensureInjected();
        messageList.setCell(summaryCell);
        initHandlers();
    }

    private void initHandlers() {
        messageList.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<M>() {
            @Override
            public void onSelectionChanged(final SelectionChangedEvent<M> event) {
                handleMessageSelection(event);
            }
        });
        summaryCell.addHandler(new DismissMessageEvent.Handler() {
            @Override
            public void handleDismiss(final DismissMessageEvent event) {
                handleMessageDismissal(event);
            }
        }, DismissMessageEvent.TYPE);
    }

    /**
     * @see MessagesView#scrollIntoView(int)
     */
    @Override
    public void scrollIntoView(final int messageIndex) {
        Scheduler.get().scheduleFinally(new ScheduledCommand() {
            @Override
            public void execute() {
                if (messageIndex < messageList.getItemCount()) {
                    messageList.getElement(messageIndex).scrollIntoView();
                }
            }
        });
    }

    /**
     * @see MessagesView#getMessageStore()
     */
    @Override
    public ListStore<M> getMessageStore() {
        return messageList.getStore();
    }

    /**
     * @see MessagesView#getSelectionModel()
     */
    @Override
    public ListViewSelectionModel<M> getSelectionModel() {
        return messageList.getSelectionModel();
    }

    /**
     * @see MessagesView#setExpiryMessage(String)
     */
	@Override
    public void setExpiryMessage(final String expiryMsg) {
        expiryView.setText(expiryMsg);
	}

    /**
     * @see MessagesView#setMessageBody(SafeHtml)
     */
	@Override
	public void setMessageBody(final SafeHtml msgBody) {
		messageView.setHTML(msgBody);
	}

    /**
     * @see MessagesView#showLoading()
     */
    @Override
    public void showLoading() {
        layout.setActiveWidget(loadingPanel);
    }

    /**
     * @see MessagesView#showMessages()
     */
    @Override
    public void showMessages() {
        layout.setActiveWidget(messagesPanel);
        messagesPanel.getElement().removeAttribute(HIDDEN_ATTRIBUTE);
        noMessagesPanel.getElement().setAttribute(HIDDEN_ATTRIBUTE, HIDDEN_ATTRIBUTE);
    }

    /**
     * @see MessagesView#showNoMessages()
     */
    @Override
    public void showNoMessages() {
        layout.setActiveWidget(noMessagesPanel);
        noMessagesPanel.getElement().removeAttribute(HIDDEN_ATTRIBUTE);
        messagesPanel.getElement().setAttribute(HIDDEN_ATTRIBUTE, HIDDEN_ATTRIBUTE);
    }

    /**
     * @see MessagesView#verifyMessageDismissal(Command)
     */
    @Override
    public void verifyMessageDismissal(final Command dismiss) {
        final DismissalDialog dlg = new DismissalDialog(dismiss);
        dlg.show();
    }

    /*
     * This method is overridden to force the message panel to be laid out a second time in case
     * the expiry message wrapped or unwrapped.
     */
    /**
     * @see Composite#onResize(int, int)
     */
    @Override
    protected void onResize(final int width, final int height) {
        super.onResize(width, height);
        if (layout.getActiveWidget() == messagesPanel) {
            Scheduler.get().scheduleFinally(new ScheduledCommand() {
                @Override
                public void execute() {
                    messagesPanel.forceLayout();
                }
            });
        }
    }

    private void handleMessageDismissal(final DismissMessageEvent event) {
        final M msg = messageList.getStore().findModelWithKey(event.getMessage());
        if (msg != null) {
            presenter.handleDismissMessage(msg);
        }
    }

    private void handleMessageSelection(final SelectionChangedEvent<M> event) {
        final List<M> selection = event.getSelection();
        if (!selection.isEmpty()) {
            presenter.handleSelectMessage(selection.get(0));
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        messagesPanel.ensureDebugId(baseID + SystemMessages.Ids.MESSAGES_PANEL);
        noMessagesPanel.ensureDebugId(baseID + SystemMessages.Ids.NO_MESSAGES_PANEL);
    }
}

