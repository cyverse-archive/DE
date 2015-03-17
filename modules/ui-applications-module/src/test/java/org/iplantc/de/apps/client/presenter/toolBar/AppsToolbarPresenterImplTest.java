package org.iplantc.de.apps.client.presenter.toolBar;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent.AppSearchResultLoadEventHandler;
import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewWorkflowEvent;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.selection.CreateNewAppSelected;
import org.iplantc.de.apps.client.events.selection.CreateNewWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.EditAppSelected;
import org.iplantc.de.apps.client.events.selection.EditWorkflowSelected;
import org.iplantc.de.apps.client.events.selection.RequestToolSelected;
import org.iplantc.de.apps.client.gin.factory.AppsToolbarViewFactory;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.tools.requests.client.views.dialogs.NewToolRequestDialog;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;

import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class AppsToolbarPresenterImplTest {

    @Mock AppUserServiceFacade appServiceMock;
    @Mock AppsToolbarViewFactory viewFactoryMock;
    @Mock AppsToolbarView viewMock;
    @Mock UserInfo userInfoMock;

    @Mock BeforeLoadEvent.BeforeLoadHandler<FilterPagingLoadConfig> beforeLoadHandlerMock;
    @Mock PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loaderMock;
    @Mock EventBus eventBusMock;

    @Captor ArgumentCaptor<AsyncCallback<String>> stringCallbackCaptor;
    @Mock Provider<NewToolRequestDialog> requestToolDlgProviderMock;

    private AppsToolbarPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>>any())).thenReturn(viewMock);
        uut = new AppsToolbarPresenterImpl(appServiceMock,
                                           viewFactoryMock);
        uut.eventBus = eventBusMock;
        uut.userInfo = userInfoMock;
        uut.newToolRequestDialogProvider = requestToolDlgProviderMock;
    }

    @Test public void testConstructorEventHandlerWiring() {
        verify(viewFactoryMock).create(eq(uut.loader));

        verify(viewMock).addCreateNewAppSelectedHandler(eq(uut));
        verify(viewMock).addCreateNewWorkflowSelectedHandler(eq(uut));
        verify(viewMock).addEditAppSelectedHandler(eq(uut));
        verify(viewMock).addRequestToolSelectedHandler(eq(uut));
        verify(viewMock).addEditWorkflowSelectedHandler(eq(uut));

        verifyNoMoreInteractions(viewFactoryMock,
                                 viewMock);
    }

    @Test public void verifyForwardedEventRegistration() {
        uut.loader = loaderMock;
        AppSearchResultLoadEventHandler resultLoadEventHandler = mock(AppSearchResultLoadEventHandler.class);

        /*** CALL METHOD UNDER TEST ***/
        uut.addAppSearchResultLoadEventHandler(resultLoadEventHandler);
        verify(viewMock).addAppSearchResultLoadEventHandler(eq(resultLoadEventHandler));

        /*** CALL METHOD UNDER TEST ***/
        uut.addBeforeLoadHandler(beforeLoadHandlerMock);
        verify(loaderMock).addBeforeLoadHandler(eq(beforeLoadHandlerMock));
        verifyZeroInteractions(appServiceMock,
                               eventBusMock);
    }

    @Test public void verifyEventFired_onCreateNewAppSelected() {
        CreateNewAppSelected eventMock = mock(CreateNewAppSelected.class);

        /*** CALL METHOD UNDER TEST ***/
        uut.onCreateNewAppSelected(eventMock);

        verify(eventBusMock).fireEvent(any(CreateNewAppEvent.class));
        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verifyEventFired_onCreateNewWorkflowSelected() {
        CreateNewWorkflowSelected eventMock = mock(CreateNewWorkflowSelected.class);

        /*** CALL METHOD UNDER TEST ***/
        uut.onCreateNewWorkflowSelected(eventMock);

        verify(eventBusMock).fireEvent(any(CreateNewWorkflowEvent.class));
        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appServiceMock);
    }


    @Test public void verifyEventFired_onEditAppSelected() {
        final String emailMock = "user@email.com";

        EditAppSelected eventMock = mock(EditAppSelected.class);
        App appMock = mock(App.class);
        when(appMock.isPublic()).thenReturn(true);
        when(eventMock.getApp()).thenReturn(appMock);

        // Set app to current user
        when(appMock.getIntegratorEmail()).thenReturn(emailMock);
        when(userInfoMock.getEmail()).thenReturn(emailMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onEditAppSelected(eventMock);

        ArgumentCaptor<EditAppEvent> editAppEventCaptor = ArgumentCaptor.forClass(EditAppEvent.class);
        verify(eventBusMock).fireEvent(editAppEventCaptor.capture());

        assertEquals(appMock, editAppEventCaptor.getValue().getAppToEdit());
        assertEquals(true, editAppEventCaptor.getValue().isUserIntegratorAndAppPublic());

        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appServiceMock);
    }


    @Test public void verifyServiceCalled_onEditWorkflowSelected() {
        EditWorkflowSelected eventMock = mock(EditWorkflowSelected.class);
        App workflowMock = mock(App.class);
        when(workflowMock.getId()).thenReturn("mock id");
        when(eventMock.getWorkFlow()).thenReturn(workflowMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onEditWorkflowSelected(eventMock);

        verify(appServiceMock).editWorkflow(eq(workflowMock), stringCallbackCaptor.capture());

        /*** CALL METHOD UNDER TEST ***/
        stringCallbackCaptor.getValue().onSuccess("{}");
        ArgumentCaptor<EditWorkflowEvent> editWorkflowCaptor = ArgumentCaptor.forClass(EditWorkflowEvent.class);
        verify(eventBusMock).fireEvent(editWorkflowCaptor.capture());

        assertEquals(workflowMock, editWorkflowCaptor.getValue().getWorkflowToEdit());

        verifyNoMoreInteractions(appServiceMock,
                                 eventBusMock);
    }

    @Test public void verifyToolRequestDlgShown_onRequestToolSelected() {
        RequestToolSelected eventMock = mock(RequestToolSelected.class);
        NewToolRequestDialog dlgMock = mock(NewToolRequestDialog.class);
        when(requestToolDlgProviderMock.get()).thenReturn(dlgMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onRequestToolSelected(eventMock);

        verify(requestToolDlgProviderMock).get();
        verify(dlgMock).show();

        verifyNoMoreInteractions(dlgMock,
                                 requestToolDlgProviderMock,
                                 eventMock);

        verifyZeroInteractions(eventBusMock,
                               appServiceMock);
    }

}