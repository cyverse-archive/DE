package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.notifications.client.utils.NotifyInfo;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class GetRecentNotificationsCallbackTest {

    @Mock IplantDisplayStrings displayStringsMock;
    @Mock NotificationAutoBeanFactory factoryMock;
    @Mock DesktopView viewMock;
    @Mock NotifyInfo notifyInfoMock;
    @Mock ListStore<NotificationMessage> storeMock;
    @Mock DesktopView.Presenter.DesktopPresenterAppearance appearanceMock;

    private InitializationCallbacks.GetInitialNotificationsCallback uut;

    @Before public void setUp()  {
        uut = new InitializationCallbacks.GetInitialNotificationsCallback(viewMock, appearanceMock, notifyInfoMock);
        when(viewMock.getNotificationStore()).thenReturn(storeMock);
    }

    @Test public void notificationPopupShownForUnseenMessages()  {
        // Test all messages except analysis messages

        Notification allMock = mock(Notification.class);
        Notification systemMock = mock(Notification.class);
        Notification dataMock = mock(Notification.class);
        Notification toolRequestMock = mock(Notification.class);
        Notification newMock = mock(Notification.class);
        // Set categories
        when(allMock.getCategory()).thenReturn(NotificationCategory.ALL.toString());
        when(systemMock.getCategory()).thenReturn(String.valueOf(NotificationCategory.SYSTEM));
        when(dataMock.getCategory()).thenReturn(String.valueOf(NotificationCategory.DATA));
        when(toolRequestMock.getCategory()).thenReturn(String.valueOf(NotificationCategory.TOOLREQUEST));
        when(newMock.getCategory()).thenReturn(String.valueOf(NotificationCategory.NEW));

        final NotificationMessage notificationMessageMock = mock(NotificationMessage.class);
        when(notificationMessageMock.getTimestamp()).thenReturn(1l);

        when(allMock.getMessage()).thenReturn(notificationMessageMock);
        when(systemMock.getMessage()).thenReturn(notificationMessageMock);
        when(dataMock.getMessage()).thenReturn(notificationMessageMock);
        when(toolRequestMock.getMessage()).thenReturn(notificationMessageMock);
        when(newMock.getMessage()).thenReturn(notificationMessageMock);

        List<Notification> mockResults = Lists.newArrayList();
        mockResults.add(allMock);
        mockResults.add(systemMock);
        mockResults.add(dataMock);
        mockResults.add(toolRequestMock);
        mockResults.add(newMock);

        NotificationList list = mock(NotificationList.class);

        when(list.getNotifications()).thenReturn(mockResults);
        when(list.getUnseenTotal()).thenReturn("5");


        uut.onSuccess(list);
        verify(viewMock).setUnseenNotificationCount(eq(5));

    }

    @Ignore("Need to implement")
    @Test public void notificationPopupShownForUnseenMessages_analysis() {

    }

    @Ignore("Need to implement")
    @Test public void errorNotificationPopupShownForFailedAnalysis() {

    }

}