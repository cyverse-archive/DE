package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.presenter.impl.DataSearchPresenterImpl;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author psarando, jstroot
 * 
 */
@SuppressWarnings("nls")
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcesEventHandlerTest {
    @Mock
    UpdateSavedSearchesEvent eventMock;

    @Mock
    DiskResourceView.Presenter presenterMock;

    @Mock
    DiskResourceView viewMock;

    @Mock
    TreeStore<Folder> treeStoreMock;

    private DiskResourcesEventHandler drHandler;

    @Before
    public void setUp() {
        when(presenterMock.getView()).thenReturn(viewMock);
        when(viewMock.getTreeStore()).thenReturn(treeStoreMock);
        drHandler = new DiskResourcesEventHandler(presenterMock);
    }

    /**
     * Verifies that a template will be added to the tree store if it is not already there.
     * 
     * @see DataSearchPresenterImpl#updateDataNavigationWindow(List, TreeStore)
     */
    @Test
    public void testUpdateSavedSearches_Case1() {
        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock3 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getId()).thenReturn("qtMock1Id");
        when(mock2.getId()).thenReturn("qtMock2Id");
        when(mock3.getId()).thenReturn("qtMock3Id");

        // Set up Folder root tree store items
        List<Folder> toReturn = createTreeStoreRootFolderList();
        // Add 2 mocks to tree store root items
        toReturn.add(mock1);
        toReturn.add(mock2);
        when(treeStoreMock.getRootItems()).thenReturn(toReturn);
        when(treeStoreMock.findModelWithKey("qtMock1Id")).thenReturn(mock1);
        when(treeStoreMock.findModelWithKey("qtMock2Id")).thenReturn(mock2);

        // Create list to pass which contains all 3 query template mocks
        List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList(mock1, mock2, mock3);
        when(eventMock.getSavedSearches()).thenReturn(queryTemplates);

        // Call method under test
        drHandler.onUpdateSavedSearches(eventMock);

        verify(treeStoreMock, times(queryTemplates.size())).findModelWithKey(anyString());

        /* Verify that nothing is removed from the store */
        verify(treeStoreMock, never()).remove(any(Folder.class));

        /* Verify that no item is updated in the store */
        verify(treeStoreMock, never()).update(any(Folder.class));

        /* Verify that the mock not previously contained in the tree store is added */
        verify(treeStoreMock).add(eq(mock3));
    }

    /**
     * Verifies that an item which is dirty and already in the tree store will be updated.
     * 
     * @see DataSearchPresenterImpl#updateDataNavigationWindow(List, TreeStore)
     */
    @Test
    public void testUpdateSavedSearches_Case2() {
        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getId()).thenReturn("qtMock1Id");
        when(mock1.isDirty()).thenReturn(true);
        when(mock2.getId()).thenReturn("qtMock2Id");

        // Set up Folder root tree store items
        List<Folder> toReturn = createTreeStoreRootFolderList();
        // Add 2 mocks to tree store root items
        toReturn.add(mock1);
        toReturn.add(mock2);
        when(treeStoreMock.getRootItems()).thenReturn(toReturn);
        when(treeStoreMock.findModelWithKey("qtMock1Id")).thenReturn(mock1);
        when(treeStoreMock.findModelWithKey("qtMock2Id")).thenReturn(mock2);

        // Create list to pass which contains all 3 query template mocks
        List<DiskResourceQueryTemplate> queryTemplates = Lists.newArrayList(mock1, mock2);
        when(eventMock.getSavedSearches()).thenReturn(queryTemplates);

        // Call method under test
        drHandler.onUpdateSavedSearches(eventMock);

        /* Verify that nothing is removed from the store */
        verify(treeStoreMock, never()).remove(any(Folder.class));

        /* Verify that the tree store is updated with the dirty query template */
        verify(treeStoreMock).update(eq(mock1));

        /* Verify that nothing is added to the store */
        verify(treeStoreMock, never()).add(any(Folder.class));
    }

    /**
     * Verifies that templates in the RemovedSearches list of an UpdateSavedSearchesEvent will be removed
     * from the tree store.
     */
    @Test
    public void testUpdateSavedSearches_Case3() {
        final ArrayList<DiskResourceQueryTemplate> newArrayList = Lists.newArrayList(
                mock(DiskResourceQueryTemplate.class), mock(DiskResourceQueryTemplate.class));
        when(eventMock.getRemovedSearches()).thenReturn(newArrayList);

        // Call method under test
        drHandler.onUpdateSavedSearches(eventMock);

        // Verify for record keeping
        verify(treeStoreMock).remove(eq(newArrayList.get(0)));
        verify(treeStoreMock).remove(eq(newArrayList.get(1)));

        verifyNoMoreInteractions(treeStoreMock);
    }

    List<Folder> createTreeStoreRootFolderList() {
        // Set up Folder root tree store items
        Folder root1 = mock(Folder.class);
        Folder root2 = mock(Folder.class);
        Folder root3 = mock(Folder.class);
        Folder root4 = mock(Folder.class);
        when(root1.getId()).thenReturn("root1Id");
        when(root2.getId()).thenReturn("root2Id");
        when(root3.getId()).thenReturn("root3Id");
        when(root4.getId()).thenReturn("root4Id");
        List<Folder> toReturn = Lists.newArrayList(root1, root2, root3, root4);
        return toReturn;
    }
}
