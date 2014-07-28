/**
 *
 */
package org.iplantc.de.client.notifications.views;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadAnalysis;
import org.iplantc.de.client.notifications.events.DeleteNotificationsUpdateEvent;
import org.iplantc.de.client.notifications.events.DeleteNotificationsUpdateEventHandler;
import org.iplantc.de.client.notifications.util.NotificationHelper;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.theme.base.client.listview.ListViewCustomAppearance;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

import java.util.List;

/**
 * New notifications as list
 *
 * This whole view just need to be re-written. It should listen to changes on its store, and act accordingly. When store
 * is cleared, or empty, it should put up the "no new notifications" message.
 *
 * When items are added to the store, it should remove the "no new notifications".
 * @author sriram
 * 
 */
public class NotificationListView implements IsWidget {

    private ListView<NotificationMessage, NotificationMessage> view;
    private ListStore<NotificationMessage> store;
    private int total_unseen;
    private final HorizontalPanel hyperlinkPanel;
    private boolean unseenNotificationsFetchedOnce;

    public ListStore<NotificationMessage> getStore() {
        return store;
    }

    /**
     * @return the unseenNotificationsFetchedOnce
     */
    public boolean isUnseenNotificationsFetchedOnce() {
        return unseenNotificationsFetchedOnce;
    }

    /**
     * @param unseenNotificationsFetchedOnce the unseenNotificationsFetchedOnce to set
     */
    public void setUnseenNotificationsFetchedOnce(boolean unseenNotificationsFetchedOnce) {
        this.unseenNotificationsFetchedOnce = unseenNotificationsFetchedOnce;
    }

    public static final int NEW_NOTIFICATIONS_LIMIT = 10;

    final Resources resources = GWT.create(Resources.class);
    final DeResources deRes = GWT.create(DeResources.class);
    final Style style = resources.css();
    final Renderer r = GWT.create(Renderer.class);
    private final NotificationAutoBeanFactory notificationFactory = GWT
            .create(NotificationAutoBeanFactory.class);
    private final EventBus eventBus;

    interface Renderer extends XTemplates {
        @XTemplate("<div class=\"{style.thumb}\"> {msg.message}</div>")
        public SafeHtml renderItem(NotificationMessage msg, Style style);

        @XTemplate("<div class=\"{style.thumb_highlight}\"> {msg.message}</div>")
        public SafeHtml renderItemWithHighlight(NotificationMessage msg, Style style);

    }

    interface Style extends CssResource {
        String over();

        String select();

        String thumb();

        String thumbWrap();

        String thumb_highlight();
    }

    interface Resources extends ClientBundle {
        @Source("NotificationListView.css")
        Style css();
    }

    ModelKeyProvider<NotificationMessage> kp = new ModelKeyProvider<NotificationMessage>() {
        @Override
        public String getKey(NotificationMessage item) {
            return Long.toString(item.getTimestamp());
        }
    };

    ListViewCustomAppearance<NotificationMessage> appearance = new ListViewCustomAppearance<NotificationMessage>(
            "." + style.thumbWrap(), style.over(), style.select()) {

        @Override
        public void renderEnd(SafeHtmlBuilder builder) {
            String markup = new StringBuilder("<div class=\"").append(CommonStyles.get().clear())
                    .append("\"></div>").toString();
            builder.appendHtmlConstant(markup);
        }

        @Override
        public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
            builder.appendHtmlConstant("<div class='" + style.thumbWrap() + "'>");
            builder.append(content);
            builder.appendHtmlConstant("</div>");
        }

    };
    private HorizontalPanel emptyTextPnl;

    public NotificationListView(EventBus eventBus) {
        this.eventBus = eventBus;
        resources.css().ensureInjected();
        deRes.css().ensureInjected();
        initListeners();
        hyperlinkPanel = new HorizontalPanel();
        hyperlinkPanel.setSpacing(2);
        updateNotificationLink();

    }

    private void initListeners() {
        EventBus.getInstance().addHandler(DeleteNotificationsUpdateEvent.TYPE,
                new DeleteNotificationsUpdateEventHandler() {

                    @Override
                    public void onDelete(DeleteNotificationsUpdateEvent event) {
                        if (event.getMessages() != null) {
                            for (NotificationMessage deleted : event.getMessages()) {
                                NotificationMessage nm = store.findModel(deleted);
                                if (nm != null) {
                                    store.remove(nm);
                                }
                            }
                        } else {
                            store.clear();
                        }

                        if (store.getAll().size() == 0) {
                            emptyTextPnl.setVisible(true);
                        }
                    }
                });
    }

    public void highlightNewNotifications() {
        // List<NotificationMessage> new_notifications = store.getAll();
        // TODO: implement higlight
    }

    /**
     * Process notifications
     * 
     * @param notifications list of notifications to be processed.
     */
    public void processMessages(final List<Notification> notifications) {
        // cache before removing
        // KLUDGE Apparently ListStore.getAll in GXT 3.0.1 and GWT 2.5.0 does not really create a copy of
        // the store contents.
        List<NotificationMessage> temp = Lists.newArrayList(store.getAll());

        store.clear();
        store.clearSortInfo();
        boolean displayInfo = false;
        int total_msg_popup = 0;

        for (Notification n : notifications) {
            NotificationMessage nm = n.getMessage();
            nm.setSeen(n.isSeen());
            store.add(nm);

            if (unseenNotificationsFetchedOnce
                    && !nm.isSeen()
                    && !isExist(temp, nm)
                    && total_msg_popup < NEW_NOTIFICATIONS_LIMIT) {
                displayNotificationPopup(nm);
                total_msg_popup++;
                displayInfo = true;
            }
        }
        if (total_unseen > NEW_NOTIFICATIONS_LIMIT && displayInfo) {
            NotifyInfo.display(I18N.DISPLAY.newNotificationsAlert());
        }

        if (store.getAll().size() > 0) {
            emptyTextPnl.setVisible(false);
        } else {
            emptyTextPnl.setVisible(true);
        }

        highlightNewNotifications();
    }

    private void displayNotificationPopup(NotificationMessage msg) {
        if (NotificationCategory.ANALYSIS.equals(msg.getCategory())) {
            PayloadAnalysis analysisPayload = AutoBeanCodex.decode(notificationFactory,
                   PayloadAnalysis.class, msg.getContext()).as();

            if ("Failed".equals(analysisPayload.getStatus())) { //$NON-NLS-1$
                NotifyInfo.displayWarning(msg.getMessage());
                return;
            }
        }

        NotifyInfo.display(msg.getMessage());
    }

    private boolean isExist(List<NotificationMessage> list, NotificationMessage n) {
        for (NotificationMessage noti : list) {
            if (noti.getId().equals(n.getId())) {
                return true;
            }
        }

        return false;

    }

    public void setUnseenCount(int count) {
        this.total_unseen = count;
        updateNotificationLink();
    }

    public void updateNotificationLink() {
        hyperlinkPanel.clear();
        hyperlinkPanel.add(buildNotificationHyerlink());
        if (total_unseen > 0) {
            hyperlinkPanel.add(buildAckAllHyperlink());
        }
    }

    private IPlantAnchor buildAckAllHyperlink() {
        IPlantAnchor link = new IPlantAnchor(I18N.DISPLAY.markAllasSeen(), -1, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ServicesInjector.INSTANCE.getMessageServiceFacade().markAllNotificationsSeen(new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(caught);

                    }

                    @Override
                    public void onSuccess(Void result) {
                        IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.markAllasSeenSuccess()));
                    }
                });

            }
        });

        return link;

    }

    private IPlantAnchor buildNotificationHyerlink() {
        String displayText;
        if (total_unseen > 0) {
            displayText = I18N.DISPLAY.newNotifications() + " (" + total_unseen + ")";
        } else {
            displayText = I18N.DISPLAY.allNotifications();
        }

        IPlantAnchor link = new IPlantAnchor(displayText, -1, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (total_unseen > 0) {
                    showNotificationWindow(NotificationCategory.NEW);
                } else {
                    showNotificationWindow(NotificationCategory.ALL);
                }

            }
        });
        return link;
    }

    /** Makes the notification window visible and filters by a category */
    private void showNotificationWindow(final NotificationCategory category) {
        eventBus.fireEvent(new WindowShowRequestEvent(ConfigFactory.notifyWindowConfig(category)));
    }

    private HorizontalPanel getEmptyTextPanel() {
        emptyTextPnl = new HorizontalPanel();
        if (store != null && store.getAll().size() == 0) {
            emptyTextPnl.add(new HTML("<span style='font-size:11px;'>"
 + I18N.DISPLAY.noNewNotifications() + "</span>"));
        }
        emptyTextPnl.setHeight("30px");
        return emptyTextPnl;

    }

    @Override
    public Widget asWidget() {
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.setBorders(false);

        store = new ListStore<>(kp);

        NotificationMessageProperties props = GWT.create(NotificationMessageProperties.class);
        store.addSortInfo(new StoreSortInfo<>(props.timestamp(), SortDir.DESC));
        store.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<NotificationMessage>() {
            @Override
            public void onAdd(StoreAddEvent<NotificationMessage> event) {
               // remove "no new notifications" message
            }
        });
        store.addStoreClearHandler(new StoreClearEvent.StoreClearHandler<NotificationMessage>() {
            @Override
            public void onClear(StoreClearEvent<NotificationMessage> event) {

                // put up "no new notifications" message
            }
        });
        view = new ListView<>(store,
                new IdentityValueProvider<NotificationMessage>(), appearance);
        view.setShadow(false);
        view.getSelectionModel().addSelectionHandler(new SelectionHandler<NotificationMessage>() {

            @Override
            public void onSelection(SelectionEvent<NotificationMessage> event) {
                NotificationMessage selected = event.getSelectedItem();
                if (selected != null) {
                    NotificationHelper.getInstance().view(selected);
                    view.getSelectionModel().deselect(selected);
                }
            }

        });

        view.setCell(new SimpleSafeHtmlCell<>(
                new AbstractSafeHtmlRenderer<NotificationMessage>() {

                    @Override
                    public SafeHtml render(NotificationMessage object) {
                        if (object.isSeen()) {
                            return r.renderItem(object, style);
                        } else {
                            return r.renderItemWithHighlight(object, style);
                        }

                    }
                }));
        view.setSize("250px", "220px");
        view.setBorders(false);
        container.add(getEmptyTextPanel());
        container.add(view);
        hyperlinkPanel.setHeight("30px");
        container.add(hyperlinkPanel);
        return container;
    }
}
