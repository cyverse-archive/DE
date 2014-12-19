package org.iplantc.de.desktop.client.views.widgets;

import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GxtMockitoTestRunner.class)
public class DesktopNotifications_UnseenNotificationsTest {
    @Test public void seeNotificationsHyperlinkUpdatedWhenUnseenCountIsGreaterThan10() {
        final UnseenNotificationsView.UnseenNotificationsAppearance mockAppearance = mock(UnseenNotificationsView.UnseenNotificationsAppearance.class);
        final String testLinkValue = "Link was updated";
        when(mockAppearance.newNotificationsLink(anyInt())).thenReturn(testLinkValue);
        UnseenNotificationsView uut = new UnseenNotificationsView(mockAppearance);
        uut.notificationsLink = mock(IPlantAnchor.class);

        uut.onUnseenCountUpdated(11);
        verify(mockAppearance).newNotificationsLink(anyInt());
        verify(uut.notificationsLink).setText(eq(testLinkValue));
        verifyNoMoreInteractions(uut.notificationsLink, mockAppearance);
    }

    @Test public void seeNotificationsHyperlinkDefaultWhenUnseenCountIsLessThan10() {
        final UnseenNotificationsView.UnseenNotificationsAppearance mockAppearance = mock(UnseenNotificationsView.UnseenNotificationsAppearance.class);
        final String testLinkValue = "Link was default";
        when(mockAppearance.allNotifications()).thenReturn(testLinkValue);
        UnseenNotificationsView uut = new UnseenNotificationsView(mockAppearance);
        uut.notificationsLink = mock(IPlantAnchor.class);

        uut.onUnseenCountUpdated(1);
        verify(mockAppearance).allNotifications();
        verify(uut.notificationsLink).setText(eq(testLinkValue));
        verifyNoMoreInteractions(uut.notificationsLink, mockAppearance);
    }

    @Test public void newNotificationsAreShownWhenUnseenCountGreaterThan10() {
        final UnseenNotificationsView.UnseenNotificationsAppearance mockAppearance = mock(UnseenNotificationsView.UnseenNotificationsAppearance.class);
        UnseenNotificationsView uut = new UnseenNotificationsView(mockAppearance);
        uut.notificationsLink = mock(IPlantAnchor.class);
        DesktopView.UnseenNotificationsPresenter mockPresenter = mock(DesktopView.UnseenNotificationsPresenter.class);
        uut.setPresenter(mockPresenter);
        uut.unseenNotificationCount = 11;

        uut.onSeeAllNotificationsSelected(mock(ClickEvent.class));
        verify(mockPresenter).doSeeNewNotifications();
        verifyNoMoreInteractions(mockPresenter);

        verifyNoMoreInteractions(mockPresenter);
    }

    @Test public void allNotificationsAreShownWhenUnseenCountLessThan10() {
        final UnseenNotificationsView.UnseenNotificationsAppearance mockAppearance = mock(UnseenNotificationsView.UnseenNotificationsAppearance.class);
        UnseenNotificationsView uut = new UnseenNotificationsView(mockAppearance);
        uut.notificationsLink = mock(IPlantAnchor.class);
        DesktopView.UnseenNotificationsPresenter mockPresenter = mock(DesktopView.UnseenNotificationsPresenter.class);
        uut.setPresenter(mockPresenter);
        uut.unseenNotificationCount = 3;

        uut.onSeeAllNotificationsSelected(mock(ClickEvent.class));
        verify(mockPresenter).doSeeAllNotifications();
        verifyNoMoreInteractions(mockPresenter);
    }

}
