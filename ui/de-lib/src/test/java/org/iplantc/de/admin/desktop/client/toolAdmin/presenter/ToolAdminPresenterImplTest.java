package org.iplantc.de.admin.desktop.client.toolAdmin.presenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.AddToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.DeleteToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.SaveToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.ToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.gin.factory.ToolAdminViewFactory;
import org.iplantc.de.admin.desktop.client.toolAdmin.model.ToolProperties;
import org.iplantc.de.admin.desktop.client.toolAdmin.service.ToolAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.DeleteToolDialog;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.OverwriteToolDialog;
import org.iplantc.de.client.models.errorHandling.ServiceErrorCode;
import org.iplantc.de.client.models.errorHandling.SimpleServiceError;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolList;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.List;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class ToolAdminPresenterImplTest {

    @Mock ToolAdminViewFactory toolAdminViewFactoryMock;
    @Mock ToolAdminServiceFacade toolAdminServiceFacadeMock;
    @Mock ToolAutoBeanFactory toolAutoBeanFactoryMock;
    @Mock ToolProperties toolPropertiesMock;
    @Mock ToolAdminView.ToolAdminViewAppearance toolAdminViewAppearanceMock;
    @Mock ToolAdminView viewMock;
    @Mock ModelKeyProvider<Tool> idMock;
    @Mock AutoBean<ToolList> toolListAutoBeanMock;
    @Mock List<Tool> listToolMock;
    @Mock ListStore<Tool> listStoreToolMock;
    @Mock IplantAnnouncer iplantAnnouncerMock;
    @Mock AsyncProviderWrapper<OverwriteToolDialog> overwriteAppDialogMock;
    @Mock AsyncProviderWrapper<DeleteToolDialog> deleteAppDialogMock;

    @Captor ArgumentCaptor<AsyncCallback<Tool>> asyncCallbackToolCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<Tool>>> asyncCallbackToolListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> asyncCallbackVoidCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OverwriteToolDialog>> asyncCallbackOverwriteDlgCaptor;
    @Captor ArgumentCaptor<AsyncCallback<DeleteToolDialog>> asyncCallbackDeleteDlgCaptor;
    @Captor ArgumentCaptor<SelectEvent.SelectHandler> selectHandlerArgumentCaptor;

    private ToolAdminPresenterImpl uut;

    @Before
    public void setUp() {
        when(toolAdminViewFactoryMock.create(Matchers.<ListStore<Tool>>any())).thenReturn(viewMock);
        when(toolPropertiesMock.id()).thenReturn(idMock);

        when(toolAdminViewAppearanceMock.addToolSuccessText()).thenReturn("sample");
        when(toolAdminViewAppearanceMock.deleteToolSuccessText()).thenReturn("sample");
        when(toolAdminViewAppearanceMock.updateToolSuccessText()).thenReturn("sample");
        uut = new ToolAdminPresenterImpl(toolAdminViewFactoryMock,
                                         toolAdminServiceFacadeMock,
                                         toolAutoBeanFactoryMock,
                                         toolPropertiesMock,
                                         toolAdminViewAppearanceMock);
        uut.announcer = iplantAnnouncerMock;
        uut.overwriteAppDialog = overwriteAppDialogMock;
        uut.deleteAppDialog = deleteAppDialogMock;

        verifyConstructor(uut);
    }

    private void verifyConstructor(ToolAdminPresenterImpl uut) {
        verify(toolAdminViewFactoryMock).create(Matchers.<ListStore<Tool>>any());
        verify(viewMock).addAddToolSelectedEventHandler(eq(uut));
        verify(viewMock).addSaveToolSelectedEventHandler(eq(uut));
        verify(viewMock).addToolSelectedEventHandler(eq(uut));
        verify(viewMock).addDeleteToolSelectedEventHandler(eq(uut));

        verifyNoMoreInteractions(toolAdminViewFactoryMock);
    }

    @Test
    public void testOnAddToolSelected() {
        AddToolSelectedEvent addToolSelectedEventMock = mock(AddToolSelectedEvent.class);

        Tool toolMock = mock(Tool.class);
        when(addToolSelectedEventMock.getTool()).thenReturn(toolMock);

        ToolList toolListMock = mock(ToolList.class);
        when(toolAutoBeanFactoryMock.getToolList()).thenReturn(toolListAutoBeanMock);
        when(toolListAutoBeanMock.as()).thenReturn(toolListMock);

        /** CALL METHOD UNDER TEST **/
        uut.onAddToolSelected(addToolSelectedEventMock);

        verify(toolAdminServiceFacadeMock).addTool(eq(toolListMock),
                                                      asyncCallbackVoidCaptor.capture());

    }

    @Test
    public void testOnAddToolSelected_callbackSuccess() {
        AddToolSelectedEvent addToolSelectedEventMock = mock(AddToolSelectedEvent.class);

        Tool toolMock = mock(Tool.class);
        when(addToolSelectedEventMock.getTool()).thenReturn(toolMock);

        ToolList toolListMock = mock(ToolList.class);
        when(toolAutoBeanFactoryMock.getToolList()).thenReturn(toolListAutoBeanMock);
        when(toolListAutoBeanMock.as()).thenReturn(toolListMock);

        /** CALL METHOD UNDER TEST **/
        uut.onAddToolSelected(addToolSelectedEventMock);

        verify(toolAdminServiceFacadeMock).addTool(eq(toolListMock),
                                                   asyncCallbackVoidCaptor.capture());

        /** CALL METHOD UNDER TEST **/
        asyncCallbackVoidCaptor.getValue().onSuccess(null);
        uut.updateView();
    }

    @Test
    public void testOnDeleteToolSelected_success() {
        DeleteToolSelectedEvent deleteToolSelectedEventMock = mock(DeleteToolSelectedEvent.class);
        Tool toolMock = mock(Tool.class);
        String idMock = "toolIdMock";

        when(toolMock.getId()).thenReturn(idMock);
        when(deleteToolSelectedEventMock.getTool()).thenReturn(toolMock);
        when(listStoreToolMock.findModelWithKey(anyString())).thenReturn(toolMock);

        /** CALL METHOD UNDER TEST **/
        uut.onDeleteToolSelected(deleteToolSelectedEventMock);

        verify(toolAdminServiceFacadeMock).deleteTool(eq(idMock), asyncCallbackVoidCaptor.capture());

        /** CALL METHOD UNDER TEST **/
        asyncCallbackVoidCaptor.getValue().onSuccess(null);
        uut.updateView();

    }

    @Test
    public void testOnDeleteToolSelected_fail() {
        DeleteToolSelectedEvent deleteToolSelectedEventMock = mock(DeleteToolSelectedEvent.class);
        Tool toolMock = mock(Tool.class);
        String idMock = "toolIdMock";

        when(toolMock.getId()).thenReturn(idMock);
        when(deleteToolSelectedEventMock.getTool()).thenReturn(toolMock);
        when(listStoreToolMock.findModelWithKey(anyString())).thenReturn(toolMock);

        ToolAdminPresenterImpl uuti = new ToolAdminPresenterImpl(toolAdminViewFactoryMock,
                                                                 toolAdminServiceFacadeMock,
                                                                 toolAutoBeanFactoryMock,
                                                                 toolPropertiesMock,
                                                                 toolAdminViewAppearanceMock) {
            @Override
            String getServiceError(Throwable caught) {
                return "ERR_NOT_WRITEABLE";
            }
        };
        uuti.deleteAppDialog = deleteAppDialogMock;

        /** CALL METHOD UNDER TEST **/
        uuti.onDeleteToolSelected(deleteToolSelectedEventMock);
        verify(toolAdminServiceFacadeMock).deleteTool(eq(idMock), asyncCallbackVoidCaptor.capture());

        String serviceError = uuti.getServiceError(mock(Throwable.class));
        Throwable caughtMock = mock(Throwable.class);

        /** CALL METHOD UNDER TEST **/
        asyncCallbackVoidCaptor.getValue().onFailure(caughtMock);
        assertEquals(serviceError, ServiceErrorCode.ERR_NOT_WRITEABLE.toString());
        verify(deleteAppDialogMock).get(asyncCallbackDeleteDlgCaptor.capture());

        DeleteToolDialog dialog = mock(DeleteToolDialog.class);

        /** CALL METHOD UNDER TEST **/
        asyncCallbackDeleteDlgCaptor.getValue().onSuccess(dialog);
        verify(dialog).setText(eq(caughtMock));
        verify(dialog).show();

        verifyNoMoreInteractions(dialog, toolAdminServiceFacadeMock);
    }

    @Test
    public void onToolSelected() {
        Tool toolMock = mock(Tool.class);
        String idMock = "mockToolId";
        ToolSelectedEvent eventMock = mock(ToolSelectedEvent.class);
        when(toolMock.getId()).thenReturn(idMock);
        when(eventMock.getTool()).thenReturn(toolMock);

        /** CALL METHOD UNDER TEST **/
        uut.onToolSelected(eventMock);

        verify(toolAdminServiceFacadeMock).getToolDetails(eq(idMock),
                                                          Matchers.<AsyncCallback<Tool>>any());
        verifyNoMoreInteractions(toolAdminServiceFacadeMock, viewMock);
    }

    @Test public void onToolSelected_callbackSuccess() {
        Tool toolMock = mock(Tool.class);
        String idMock = "mockToolId";
        ToolSelectedEvent eventMock = mock(ToolSelectedEvent.class);
        when(toolMock.getId()).thenReturn(idMock);
        when(eventMock.getTool()).thenReturn(toolMock);

        /** CALL METHOD UNDER TEST **/
        uut.onToolSelected(eventMock);

        verify(toolAdminServiceFacadeMock).getToolDetails(eq(idMock),
                                                          asyncCallbackToolCaptor.capture());
        AsyncCallback<Tool> value = asyncCallbackToolCaptor.getValue();

        /** CALL METHOD UNDER TEST **/
        value.onSuccess(toolMock);
        verify(viewMock).editToolDetails(eq(toolMock));

        verifyNoMoreInteractions(toolAdminServiceFacadeMock, viewMock);
    }

    @Test public void testOnSaveToolSelected() {
        Tool toolMock = mock(Tool.class);
        SaveToolSelectedEvent saveToolSelectedEventMock = mock(SaveToolSelectedEvent.class);
        when(saveToolSelectedEventMock.getTool()).thenReturn(toolMock);

        /** CALL METHOD UNDER TEST **/
        uut.onSaveToolSelected(saveToolSelectedEventMock);

        uut.updateTool(eq(toolMock), eq(anyBoolean()));
    }

    @Test public void testUpdateTool_overwriteFalse() {
        Tool toolMock = mock(Tool.class);
        String idMock = "sample";
        when(toolMock.getId()).thenReturn(idMock);


        /** CALL METHOD UNDER TEST **/
        uut.updateTool(eq(toolMock), eq(anyBoolean()));

        verify(toolAdminServiceFacadeMock).updateTool(Matchers.<Tool>any(),
                                                      anyBoolean(),
                                                      asyncCallbackVoidCaptor.capture());

        asyncCallbackVoidCaptor.getValue().onSuccess(null);
        uut.updateView();
    }

    @Test public void testUpdateTool_overwriteTrue() {
        Tool toolMock = mock(Tool.class);
        String idMock = "sample";
        when(toolMock.getId()).thenReturn(idMock);

        SimpleServiceError simpleServiceErrorMock = mock(SimpleServiceError.class);
        when(simpleServiceErrorMock.getErrorCode()).thenReturn("ERR_NOT_WRITEABLE");

        ToolAdminPresenterImpl uuti = new ToolAdminPresenterImpl(toolAdminViewFactoryMock,
                                         toolAdminServiceFacadeMock,
                                         toolAutoBeanFactoryMock,
                                         toolPropertiesMock,
                                         toolAdminViewAppearanceMock) {
            @Override
            String getServiceError(Throwable caught) {
                return "ERR_NOT_WRITEABLE";
            }
        };
        uuti.overwriteAppDialog = overwriteAppDialogMock;

        /** CALL METHOD UNDER TEST **/
        uuti.updateTool(eq(toolMock), eq(anyBoolean()));

        verify(toolAdminServiceFacadeMock).updateTool(Matchers.<Tool>any(),
                                                      anyBoolean(),
                                                      asyncCallbackVoidCaptor.capture());


        String serviceError = uuti.getServiceError(mock(Throwable.class));
        Throwable caughtMock = mock(Throwable.class);

        /** CALL METHOD UNDER TEST **/
        asyncCallbackVoidCaptor.getValue().onFailure(caughtMock);
        assertEquals(serviceError, ServiceErrorCode.ERR_NOT_WRITEABLE.toString());
        verify(overwriteAppDialogMock).get(asyncCallbackOverwriteDlgCaptor.capture());

        OverwriteToolDialog dialog = mock(OverwriteToolDialog.class);

        /** CALL METHOD UNDER TEST **/
        asyncCallbackOverwriteDlgCaptor.getValue().onSuccess(dialog);
        verify(dialog).setText(eq(caughtMock));
        verify(dialog).show();
        verify(dialog).addOkButtonSelectHandler(selectHandlerArgumentCaptor.capture());

        selectHandlerArgumentCaptor.getValue().onSelect(mock(SelectEvent.class));

    }

    @Test public void testUpdateView() {
        final ToolAdminPresenterImpl uuti = new ToolAdminPresenterImpl(toolAdminViewFactoryMock,
                                                                 toolAdminServiceFacadeMock,
                                                                 toolAutoBeanFactoryMock,
                                                                 toolPropertiesMock,
                                                                 toolAdminViewAppearanceMock) {
            @Override
            ListStore<Tool> createListStore(ToolProperties toolProps) {
                return listStoreToolMock;
            }
        };

        String searchTermMock = "searchTermMock";

        /** CALL METHOD UNDER TEST **/
        uuti.updateView(searchTermMock);
        verify(toolAdminServiceFacadeMock).getTools(eq(searchTermMock), asyncCallbackToolListCaptor.capture());

        AsyncCallback<List<Tool>> listAsyncCallback = asyncCallbackToolListCaptor.getValue();

        List<Tool> toolListMock = Lists.newArrayList(mock(Tool.class));


        /** CALL METHOD UNDER TEST **/
        listAsyncCallback.onSuccess(toolListMock);
        verify(listStoreToolMock).replaceAll(eq(toolListMock));
        verifyNoMoreInteractions(listStoreToolMock, toolAdminServiceFacadeMock);
    }

}
