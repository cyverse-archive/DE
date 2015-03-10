package org.iplantc.de.apps.client.presenter.grid;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.gin.factory.AppsGridViewFactory;
import org.iplantc.de.client.models.apps.App;

import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

@RunWith(GwtMockitoTestRunner.class)
public class AppsGridPresenterImplTest {

    @Mock AppsGridViewFactory viewFactoryMock;
    @Mock AppsGridView viewMock;

    private AppsGridPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<ListStore<App>>any())).thenReturn(viewMock);
        uut = new AppsGridPresenterImpl(viewFactoryMock);
    }

    @Test public void testConstructorEventHandlerWiring() {
        verify(viewFactoryMock).create(eq(uut.listStore));

        // Verify view wiring
        verify(viewMock).addAppNameSelectedEventHandler(eq(uut));
        verify(viewMock).addAppRatingDeselectedHandler(eq(uut));
        verify(viewMock).addAppRatingSelectedHandler(eq(uut));
        verify(viewMock).addAppCommentSelectedEventHandlers(eq(uut));
        verify(viewMock).addAppFavoriteSelectedEventHandlers(eq(uut));


        verifyNoMoreInteractions(viewFactoryMock,
                                 viewMock);
    }
}