package org.iplantc.de.desktop.client.views.widgets;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.notifications.client.model.NotificationMessageProperties;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;
import static com.sencha.gxt.data.shared.SortDir.DESC;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * Presenter will have to listen for deletes and update the store
 * Created by jstroot on 7/24/14.
 * FIXME REFACTOR JDS Fold all strings into appearance
 * @author jstroot
 */
public class UnseenNotificationsView extends Composite implements StoreClearEvent.StoreClearHandler<NotificationMessage>,
                                                                  StoreAddEvent.StoreAddHandler<NotificationMessage>,
                                                                  StoreRemoveEvent.StoreRemoveHandler<NotificationMessage>,
                                                                  HasSelectionHandlers<NotificationMessage>,
                                                                  SelectionHandler<NotificationMessage> {
    public interface UnseenNotificationsAppearance {

        String allNotifications();

        Cell<NotificationMessage> getListViewCell();

        String newNotificationsLink(int unseenCount);

        String markAllAsSeen();

        String noNewNotifications();

        String unseenNotificationsViewWidth();

        String unseenNotificationsViewHeight();
    }

    interface UnseenNotificationsViewUiBinder extends UiBinder<VerticalLayoutContainer, UnseenNotificationsView> { }

    @UiField HTML emptyNotificationsText;
    @UiField ListView<NotificationMessage, NotificationMessage> listView;
    @UiField IPlantAnchor markAllSeenLink;
    @UiField IPlantAnchor notificationsLink;
    ListStore<NotificationMessage> store;

    @UiField(provided = true) UnseenNotificationsAppearance appearance;
    private DesktopView.UnseenNotificationsPresenter presenter;

    private static UnseenNotificationsViewUiBinder ourUiBinder = GWT.create(UnseenNotificationsViewUiBinder.class);
    int unseenNotificationCount;

    public UnseenNotificationsView(final UnseenNotificationsAppearance appearance) {
        this.appearance = appearance;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public UnseenNotificationsView() {
        this(GWT.<UnseenNotificationsAppearance>create(UnseenNotificationsAppearance.class));
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<NotificationMessage> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    public ListStore<NotificationMessage> getStore() {
        return store;
    }

    @Override
    public void onAdd(StoreAddEvent<NotificationMessage> event) {
        emptyNotificationsText.setVisible(false);
    }

    @Override
    public void onClear(StoreClearEvent<NotificationMessage> event) {
        emptyNotificationsText.setVisible(true);
    }

    @Override
    public void onRemove(StoreRemoveEvent<NotificationMessage> event) {
        if (store.size() > 0) {
            return;
        }
        emptyNotificationsText.setVisible(true);
    }

    @Override
    public void onSelection(SelectionEvent<NotificationMessage> event) {
        SelectionEvent.fire(this, event.getSelectedItem()); // Refire event
        listView.getSelectionModel().deselect(event.getSelectedItem());
    }

    public void onUnseenCountUpdated(int unseenCount) {
        this.unseenNotificationCount = unseenCount;
        markAllSeenLink.setVisible(unseenCount > 0);
        if(unseenCount > 10){
            // Update hyperlink
            notificationsLink.setText(appearance.newNotificationsLink(unseenCount));
        } else {
            // Default hyperlink text
            notificationsLink.setText(appearance.allNotifications());
        }
    }

    public void setPresenter(DesktopView.UnseenNotificationsPresenter presenter) {
        this.presenter = presenter;
    }

    @UiFactory
    ListView<NotificationMessage, NotificationMessage> createListView() {
        NotificationMessageProperties props = GWT.create(NotificationMessageProperties.class);
        store = new ListStore<>(new ModelKeyProvider<NotificationMessage>() {
            @Override
            public String getKey(NotificationMessage item) {
                return Long.toString(item.getTimestamp());
            }
        });
        store.addStoreClearHandler(this);
        store.addStoreAddHandler(this);
        store.addStoreRemoveHandler(this);
        store.addSortInfo(new Store.StoreSortInfo<>(props.timestamp(), DESC));
        ListView<NotificationMessage, NotificationMessage> lv = new ListView<>(store,
                                                                               new IdentityValueProvider<NotificationMessage>(),
                                                                               appearance.getListViewCell());
        lv.getSelectionModel().addSelectionHandler(this);
        lv.getSelectionModel().setSelectionMode(SINGLE);
        return lv;
    }

    @UiHandler("notificationsLink")
    void onSeeAllNotificationsSelected(ClickEvent event) {
        if(unseenNotificationCount > 10) {
            presenter.doSeeNewNotifications();
        } else {
            presenter.doSeeAllNotifications();
        }
    }

    @UiHandler("markAllSeenLink")
    public void onMarkAllSeenClicked(ClickEvent event) {
        presenter.doMarkAllSeen(true);
    }
}
