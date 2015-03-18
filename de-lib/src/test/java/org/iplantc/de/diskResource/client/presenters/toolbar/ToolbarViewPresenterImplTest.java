package org.iplantc.de.diskResource.client.presenters.toolbar;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.diskResource.client.events.selection.SimpleUploadSelected;
import org.iplantc.de.diskResource.client.gin.factory.ToolbarViewFactory;

import com.google.gwtmockito.GwtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class ToolbarViewPresenterImplTest {

    @Mock ToolbarViewFactory viewFactoryMock;
    @Mock DiskResourceView.Presenter parentPresenterMock;
    @Mock EventBus eventBusMock;
    @Mock ToolbarView viewMock;

    private ToolbarViewPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<ToolbarView.Presenter>any())).thenReturn(viewMock);
        uut = new ToolbarViewPresenterImpl(viewFactoryMock,
                                           parentPresenterMock);
        uut.eventBus = eventBusMock;
    }

    @Test public void verifyHomePathSetWhenFolderNull_onSimpleUploadSelected() {
        SimpleUploadSelected eventMock = mock(SimpleUploadSelected.class);
        when(eventMock.getSelectedFolder()).thenReturn(null);
        Folder homeFolderMock = mock(Folder.class);
        when(homeFolderMock.getPath()).thenReturn("/mock/home/path");
        when(parentPresenterMock.getHomeFolder()).thenReturn(homeFolderMock);

        /** CALL METHOD UNDER TEST **/
        uut.onSimpleUploadSelected(eventMock);

        verify(eventMock).getSelectedFolder();
        verify(parentPresenterMock).getHomeFolder();

        ArgumentCaptor<RequestSimpleUploadEvent> captor = ArgumentCaptor.forClass(RequestSimpleUploadEvent.class);
        verify(eventBusMock).fireEvent(captor.capture());

        assertEquals(homeFolderMock, captor.getValue().getDestinationFolder());
    }
}