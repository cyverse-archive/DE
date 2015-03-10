package org.iplantc.de.apps.client.presenter.toolBar;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.gin.factory.AppsToolbarViewFactory;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppUserServiceFacade;

import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

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
public class AppsToolbarPresenterImplTest {

    @Mock AppAutoBeanFactory appFactory;
    @Mock AppSearchAutoBeanFactory appSearchFactory;
    @Mock AppUserServiceFacade appServiceMock;
    @Mock AppsToolbarView.AppsToolbarAppearance appearanceMock;
    @Mock AppsToolbarViewFactory viewFactoryMock;
    @Mock AppsToolbarView viewMock;

    private AppsToolbarPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>>any())).thenReturn(viewMock);
        uut = new AppsToolbarPresenterImpl(appServiceMock,
                                           appSearchFactory,
                                           appFactory,
                                           appearanceMock,
                                           viewFactoryMock);
    }

    @Test public void testConstructorEventHandlerWiring() {
        verify(viewFactoryMock).create(eq(uut.loader));

        verify(viewMock).addCreateNewAppSelectedHandler(eq(uut));
        verify(viewMock).addCreateNewWorkflowSelectedHandler(eq(uut));
        verify(viewMock).addEditAppSelectedHandler(eq(uut));
        verify(viewMock).addRequestToolSelectedHandler(eq(uut));

        verifyNoMoreInteractions(viewFactoryMock,
                                 viewMock);
    }
}