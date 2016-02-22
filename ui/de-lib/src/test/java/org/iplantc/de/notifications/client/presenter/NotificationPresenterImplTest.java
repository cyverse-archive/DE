package org.iplantc.de.notifications.client.presenter;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.notifications.client.events.DeleteNotificationsUpdateEvent;
import org.iplantc.de.notifications.client.events.NotificationGridRefreshEvent;
import org.iplantc.de.notifications.client.events.NotificationSelectionEvent;
import org.iplantc.de.notifications.client.events.NotificationToolbarDeleteAllClickedEvent;
import org.iplantc.de.notifications.client.events.NotificationToolbarDeleteClickedEvent;
import org.iplantc.de.notifications.client.gin.factory.NotificationViewFactory;
import org.iplantc.de.notifications.client.model.NotificationMessageProperties;
import org.iplantc.de.notifications.client.views.NotificationToolbarView;
import org.iplantc.de.notifications.client.views.NotificationView;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.List;

/**
 * @author aramsey
 */
@RunWith(GxtMockitoTestRunner.class)
public class NotificationPresenterImplTest {

    @Mock NotificationViewFactory viewFactoryMock;
    @Mock NotificationView.NotificationViewAppearance appearanceMock;
    @Mock NotificationView viewMock;
    @Mock NotificationMessageProperties messagePropertiesMock;
    @Mock MessageServiceFacade messageServiceFacadeMock;
    @Mock NotificationToolbarView toolbarViewMock;
    @Mock EventBus eventBusMock;
    @Mock NotificationCategory currentCategoryMock;
    @Mock ListStore<NotificationMessage> listStoreMock;
    @Mock List<NotificationMessage> listMock;
    @Mock NotificationMessage notificationMessageMock;
    @Mock Iterator<NotificationMessage> iteratorMock;

    @Captor ArgumentCaptor<AsyncCallback<String>> asyncCallbackStringCaptor;

    private NotificationPresenterImpl uut;

    @Before
    public void setUp() {
        when(viewFactoryMock.create(listStoreMock)).thenReturn(viewMock);
        when(currentCategoryMock.toString()).thenReturn("sample");
        when(viewMock.getCurrentLoadConfig()).thenReturn(mock(FilterPagingLoadConfig.class));
        when(notificationMessageMock.getId()).thenReturn("id");

        uut = new NotificationPresenterImpl(viewFactoryMock,
                                            appearanceMock,
                                            toolbarViewMock,
                                            messagePropertiesMock) {
            @Override
            ListStore<NotificationMessage> createListStore(NotificationMessageProperties messageProperties) {
                return listStoreMock;
            }
        };
        uut.currentCategory = currentCategoryMock;
        uut.messageServiceFacade = messageServiceFacadeMock;
        uut.eventBus = eventBusMock;
    }

    @Test
    public void testOnNotificationGridRefresh_emptyListStore() {
        NotificationGridRefreshEvent eventMock = mock(NotificationGridRefreshEvent.class);
        when(listStoreMock.size()).thenReturn(0);

        uut.onNotificationGridRefresh(eventMock);
        verify(toolbarViewMock).setDeleteAllButtonEnabled(eq(false));
    }

    @Test
    public void testOnNotificationGridRefresh_nonEmptyListStore() {
        NotificationGridRefreshEvent eventMock = mock(NotificationGridRefreshEvent.class);
        when(listStoreMock.size()).thenReturn(5);

        uut.onNotificationGridRefresh(eventMock);

        verify(toolbarViewMock).setDeleteAllButtonEnabled(eq(true));
    }

    @Test
    public void testOnNotificationSelection_emptyListStore() {
        NotificationSelectionEvent eventMock = mock(NotificationSelectionEvent.class);
        when(eventMock.getNotifications()).thenReturn(listMock);
        when(listMock.size()).thenReturn(0);

        uut.onNotificationSelection(eventMock);

        verify(toolbarViewMock).setDeleteButtonEnabled(eq(false));
    }

    @Test
    public void testOnNotificationSelection_nonEmptyListStore() {
        NotificationSelectionEvent eventMock = mock(NotificationSelectionEvent.class);
        when(eventMock.getNotifications()).thenReturn(listMock);
        when(listMock.size()).thenReturn(5);

        uut.onNotificationSelection(eventMock);

        verify(toolbarViewMock).setDeleteButtonEnabled(eq(true));
    }


    @Test
    public void testOnNotificationToolbarDeleteAllClicked() {
        NotificationToolbarDeleteAllClickedEvent eventMock = mock(NotificationToolbarDeleteAllClickedEvent.class);
        uut.onNotificationToolbarDeleteAllClicked(eventMock);

        verify(viewMock).mask();
        verify(messageServiceFacadeMock).deleteAll(eq(currentCategoryMock), asyncCallbackStringCaptor.capture());

        AsyncCallback<String> asyncCallback = asyncCallbackStringCaptor.getValue();

        asyncCallback.onSuccess("result");
        verify(viewMock).unmask();
        verify(viewMock).loadNotifications(eq(viewMock.getCurrentLoadConfig()));
        verify(eventBusMock).fireEvent(isA(DeleteNotificationsUpdateEvent.class));

    }

    @Test
    public void testOnNotificationToolbarDeleteClicked() {
        NotificationToolbarDeleteClickedEvent eventMock = mock(NotificationToolbarDeleteClickedEvent.class);

        when(listMock.isEmpty()).thenReturn(false);
        when(listMock.size()).thenReturn(1);
        when(iteratorMock.hasNext()).thenReturn(true, false);
        when(iteratorMock.next()).thenReturn(notificationMessageMock);
        when(listMock.iterator()).thenReturn(iteratorMock);
        when(viewMock.getSelectedItems()).thenReturn(listMock);

        uut.onNotificationToolbarDeleteClicked(eventMock);


        verify(messageServiceFacadeMock).deleteMessages(isA(JSONObject.class), asyncCallbackStringCaptor.capture());

        asyncCallbackStringCaptor.getValue().onSuccess("result");
        verify(eventBusMock).fireEvent(isA(DeleteNotificationsUpdateEvent.class));

    }
}
