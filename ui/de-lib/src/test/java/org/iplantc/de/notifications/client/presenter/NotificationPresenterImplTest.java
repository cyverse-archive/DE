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

    @Mock NotificationView viewMock;
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
        when(currentCategoryMock.toString()).thenReturn("sample");
        when(viewMock.getCurrentLoadConfig()).thenReturn(mock(FilterPagingLoadConfig.class));
        when(notificationMessageMock.getId()).thenReturn("id");

        uut = new NotificationPresenterImpl(viewMock);

        uut.currentCategory = currentCategoryMock;
        uut.messageServiceFacade = messageServiceFacadeMock;
        uut.toolbar = toolbarViewMock;
        uut.eventBus = eventBusMock;
    }

    @Test
    public void testOnNotificationGridRefresh_emptyListStore() {
        when(listStoreMock.size()).thenReturn(0);
        when(viewMock.getListStore()).thenReturn(listStoreMock);

        uut.onGridRefresh();
        verify(toolbarViewMock).setDeleteAllButtonEnabled(eq(false));
    }

    @Test
    public void testOnNotificationGridRefresh_nonEmptyListStore() {
        when(listStoreMock.size()).thenReturn(5);
        when(viewMock.getListStore()).thenReturn(listStoreMock);

        uut.onGridRefresh();
        verify(toolbarViewMock).setDeleteAllButtonEnabled(eq(true));
    }

    @Test
    public void testOnNotificationSelection_emptyListStore() {
        when(listMock.size()).thenReturn(0);

        uut.onNotificationSelection(listMock);
        verify(toolbarViewMock).setDeleteButtonEnabled(eq(false));
    }

    @Test
    public void testOnNotificationSelection_nonEmptyListStore() {
        when(listMock.size()).thenReturn(5);

        uut.onNotificationSelection(listMock);
        verify(toolbarViewMock).setDeleteButtonEnabled(eq(true));
    }


    @Test
    public void testOnNotificationToolbarDeleteAllClicked() {
        uut.onDeleteAllClicked();

        verify(viewMock).mask();
        verify(messageServiceFacadeMock).deleteAll(eq(currentCategoryMock.toString()), asyncCallbackStringCaptor.capture());
        AsyncCallback<String> asyncCallback = asyncCallbackStringCaptor.getValue();

        asyncCallback.onSuccess("result");
        verify(viewMock).unmask();
        verify(viewMock).loadNotifications(eq(viewMock.getCurrentLoadConfig()));
        verify(eventBusMock).fireEvent(isA(DeleteNotificationsUpdateEvent.class));

    }

    @Test
    public void testOnNotificationToolbarDeleteClicked() {
        when(listMock.isEmpty()).thenReturn(false);
        when(listMock.size()).thenReturn(1);
        when(iteratorMock.hasNext()).thenReturn(true, false);
        when(iteratorMock.next()).thenReturn(notificationMessageMock);
        when(listMock.iterator()).thenReturn(iteratorMock);
        when(viewMock.getSelectedItems()).thenReturn(listMock);

        uut.onDeleteClicked();

        verify(messageServiceFacadeMock).deleteMessages(isA(JSONObject.class), asyncCallbackStringCaptor.capture());

        asyncCallbackStringCaptor.getValue().onSuccess("result");
        verify(eventBusMock).fireEvent(isA(DeleteNotificationsUpdateEvent.class));

    }
}
