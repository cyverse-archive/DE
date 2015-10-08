package org.iplantc.de.diskResource.client.presenters.toolbar;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.genomes.GenomeAutoBeanFactory;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.de.diskResource.client.events.selection.SimpleDownloadSelected;
import org.iplantc.de.diskResource.client.gin.factory.BulkMetadataDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.ToolbarViewFactory;
import org.iplantc.de.diskResource.client.views.dialogs.GenomeSearchDialog;

import com.google.gwtmockito.GwtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class ToolbarViewPresenterImplTest {

    @Mock ToolbarViewFactory viewFactoryMock;
    @Mock DiskResourceView.Presenter parentPresenterMock;
    @Mock EventBus eventBusMock;
    @Mock ToolbarView viewMock;
    @Mock
    GenomeSearchDialog genomeSearchView;
    @Mock
    GenomeAutoBeanFactory gFactory;
    @Mock
    BulkMetadataDialogFactory bulkMetadataViewFactor;

    private ToolbarViewPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<ToolbarView.Presenter>any())).thenReturn(viewMock);
        uut = new ToolbarViewPresenterImpl(viewFactoryMock,
                                           genomeSearchView,
                                           bulkMetadataViewFactor,
                                           gFactory,
                                           parentPresenterMock);
        uut.eventBus = eventBusMock;
    }

    @Test public void onSimpleDownloadSelected_firesEvent() {
        SimpleDownloadSelected eventMock = mock(SimpleDownloadSelected.class);
        List<DiskResource> selectedResourcesMock = Collections.emptyList();
        Folder selectedFolderMock = mock(Folder.class);
        when(eventMock.getSelectedDiskResources()).thenReturn(selectedResourcesMock);
        when(eventMock.getSelectedFolder()).thenReturn(selectedFolderMock);


        /** CALL METHOD UNDER TEST **/
        uut.onSimpleDownloadSelected(eventMock);

        ArgumentCaptor<RequestSimpleDownloadEvent> captor = ArgumentCaptor.forClass(RequestSimpleDownloadEvent.class);
        verify(eventBusMock).fireEvent(captor.capture());

        assertEquals(selectedResourcesMock, captor.getValue().getRequestedResources());
        assertEquals(selectedFolderMock, captor.getValue().getCurrentFolder());

        verifyNoMoreInteractions(eventBusMock);
    }

}