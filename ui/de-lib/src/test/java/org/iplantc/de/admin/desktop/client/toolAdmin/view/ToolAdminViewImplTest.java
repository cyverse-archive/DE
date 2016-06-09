package org.iplantc.de.admin.desktop.client.toolAdmin.view;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.AddToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.DeleteToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.SaveToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.model.ToolProperties;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.ToolAdminDetailsDialog;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * @author aramsey
 */
@RunWith(GxtMockitoTestRunner.class)
public class ToolAdminViewImplTest {

    @Mock ToolAdminView.ToolAdminViewAppearance appearanceMock;
    @Mock ToolProperties toolPropertiesMock;
    @Mock ListStore<Tool> toolListStoreMock;
    @Mock Grid<Tool> toolGridMock;
    @Mock GridSelectionModel<Tool> gridSelectionModelMock;
    @Mock Tool toolMock;
    @Mock AsyncProviderWrapper<ToolAdminDetailsDialog> toolDetailsDialogMock;

    @Captor ArgumentCaptor<AsyncCallback<ToolAdminDetailsDialog>> asyncCallbackDialogCaptor;
    @Captor ArgumentCaptor<SaveToolSelectedEvent.SaveToolSelectedEventHandler>
            saveToolSelectedEventHandlerArgumentCaptor;
    @Captor ArgumentCaptor<DeleteToolSelectedEvent.DeleteToolSelectedEventHandler>
            deleteToolSelectedEventHandlerArgumentCaptor;

    private ToolAdminViewImpl uut;

    @Before
    public void setUp() {
        when(gridSelectionModelMock.getSelectedItem()).thenReturn(toolMock);
        when(toolGridMock.getSelectionModel()).thenReturn(gridSelectionModelMock);

        /** CALL METHOD UNDER TEST **/
        uut = new ToolAdminViewImpl(appearanceMock, toolPropertiesMock, toolListStoreMock);

        uut.grid = toolGridMock;
        uut.toolDetailsDialog = toolDetailsDialogMock;

    }

    @Test
    public void testEditToolDetails_saveEvent() {

        ToolAdminViewImpl spy = spy(uut);

        /** CALL METHOD UNDER TEST **/
        spy.editToolDetails(toolMock);
        verify(toolDetailsDialogMock).get(asyncCallbackDialogCaptor.capture());

        AsyncCallback<ToolAdminDetailsDialog> asyncCallback = asyncCallbackDialogCaptor.getValue();
        ToolAdminDetailsDialog resultMock = mock(ToolAdminDetailsDialog.class);

        /** CALL METHOD UNDER TEST **/
        asyncCallback.onSuccess(resultMock);
        verify(resultMock).show(eq(toolMock));
        verify(resultMock).addSaveToolSelectedEventHandler(saveToolSelectedEventHandlerArgumentCaptor.capture());

        SaveToolSelectedEvent.SaveToolSelectedEventHandler saveHandlerMock =
                saveToolSelectedEventHandlerArgumentCaptor.getValue();
        SaveToolSelectedEvent saveEventMock = mock(SaveToolSelectedEvent.class);

        /** CALL METHOD UNDER TEST **/
        saveHandlerMock.onSaveToolSelected(saveEventMock);

        verify(spy).fireEvent(isA(SaveToolSelectedEvent.class));
        verify(resultMock).hide();
        verify(toolGridMock.getSelectionModel()).deselect(eq(toolMock));
    }

    @Test
    public void testAddButtonClicked() {
        ToolAdminViewImpl spy = spy(uut);

        SelectEvent mockEvent = mock(SelectEvent.class);

        /** CALL METHOD UNDER TEST **/
        spy.addButtonClicked(mockEvent);
        verify(toolDetailsDialogMock).get(asyncCallbackDialogCaptor.capture());

        AsyncCallback<ToolAdminDetailsDialog> asyncCallback = asyncCallbackDialogCaptor.getValue();
        ToolAdminDetailsDialog resultMock = mock(ToolAdminDetailsDialog.class);

        /** CALL METHOD UNDER TEST **/
        asyncCallback.onSuccess(resultMock);
        verify(resultMock).show();
        verify(resultMock).addSaveToolSelectedEventHandler(saveToolSelectedEventHandlerArgumentCaptor.capture());

        SaveToolSelectedEvent.SaveToolSelectedEventHandler saveHandlerMock =
                saveToolSelectedEventHandlerArgumentCaptor.getValue();
        SaveToolSelectedEvent saveEventMock = mock(SaveToolSelectedEvent.class);

        /** CALL METHOD UNDER TEST **/
        saveHandlerMock.onSaveToolSelected(saveEventMock);

        verify(spy).fireEvent(isA(AddToolSelectedEvent.class));
        verify(resultMock).hide();
    }
}
