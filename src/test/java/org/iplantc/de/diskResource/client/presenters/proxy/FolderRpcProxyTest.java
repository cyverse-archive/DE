package org.iplantc.de.diskResource.client.presenters.proxy;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.RootFolders;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderRpcProxy.GetSavedQueryTemplatesCallback;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderRpcProxy.RootFolderCallback;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderRpcProxy.SubFoldersCallback;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jstroot
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
public class FolderRpcProxyTest {

    @Mock DiskResourceServiceFacade drServiceMock;
    @Mock SearchServiceFacade searchServiceMock;
    @Mock IplantAnnouncer announcerMock;
    @Mock IsMaskable maskableMock;
    @Mock DataSearchPresenter searchPresenterMock;

    @Mock AsyncCallback<List<Folder>> folderCallbackMock;
    
    @Captor ArgumentCaptor<List<Folder>> folderListCaptor;

    private FolderRpcProxy proxyUnderTest;

    @Before public void setUp() {
        proxyUnderTest = new FolderRpcProxy(drServiceMock, searchServiceMock, announcerMock);
        proxyUnderTest.init(searchPresenterMock, maskableMock);
    }

    /**
     * Verifies functionality of load(..) method when given folder is null.
     */
    @Test public void testLoad_Case1() {

        proxyUnderTest.load(null, folderCallbackMock);

        verify(maskableMock).mask(any(String.class));

        /* Verify that the DiskResourceService getRootFolders(..) method is called */
        ArgumentCaptor<RootFolderCallback> rootFolderCallbackCaptor = ArgumentCaptor.forClass(FolderRpcProxy.RootFolderCallback.class);
        verify(drServiceMock).getRootFolders(rootFolderCallbackCaptor.capture());

        /* Verify that no other methods are called in DiskResourceService */
        verifyNoMoreInteractions(drServiceMock);
        
        /* Verify that nothing was done with initial callback */
        verifyZeroInteractions(folderCallbackMock);

        /* Verify that callback was created with intended properties */
        assertEquals(folderCallbackMock, rootFolderCallbackCaptor.getValue().callback);
        assertEquals(maskableMock, rootFolderCallbackCaptor.getValue().maskable);
    }

    /**
     * Verify functionality of load(..) method when given folder is not null, the folder.isFilter()
     * method returns true, and given callback is null.
     */
    @Test public void testLoad_Case2() {
        Folder parentFolderMock = mock(Folder.class);
        when(parentFolderMock.isFilter()).thenReturn(true);

        // Call method under test
        AsyncCallback<List<Folder>> nullCallback = null;
        proxyUnderTest.load(parentFolderMock, nullCallback);

        verify(parentFolderMock).isFilter();

        verifyNoMoreInteractions(parentFolderMock);

        verifyZeroInteractions(maskableMock, drServiceMock);
    }

    /**
     * Verify functionality of load(..) method when given folder is not null, the folder.isFilter()
     * method returns true, and given callback is not null.
     */
    @Test public void testLoad_Case3() {
        Folder parentFolderMock = mock(Folder.class);
        when(parentFolderMock.isFilter()).thenReturn(true);

        proxyUnderTest.load(parentFolderMock, folderCallbackMock);

        verify(parentFolderMock).isFilter();

        verify(folderCallbackMock).onSuccess(folderListCaptor.capture());

        /* Verify that an empty list is passed */
        assertTrue(folderListCaptor.getValue().isEmpty());

        verifyNoMoreInteractions(parentFolderMock);

        verifyZeroInteractions(maskableMock, drServiceMock);
    }

    /**
     * Verify functionality of load(..) method when given folder is not null, and the folder.isFilter()
     * method returns false.
     */
    @Test public void testLoad_Case4() {
        Folder parentFolderMock = mock(Folder.class);
        when(parentFolderMock.isFilter()).thenReturn(false);
        final String stubPath = "stubPath";
        when(parentFolderMock.getPath()).thenReturn(stubPath);

        proxyUnderTest.load(parentFolderMock, folderCallbackMock);

        verify(parentFolderMock).isFilter();

        ArgumentCaptor<SubFoldersCallback> subFoldersCallbackCaptor = ArgumentCaptor.forClass(SubFoldersCallback.class);
        verify(drServiceMock).getSubFolders(eq(parentFolderMock), subFoldersCallbackCaptor.capture());

        /* Verify that callback was created with intended properties */
        assertEquals(folderCallbackMock, subFoldersCallbackCaptor.getValue().callback);

        verifyNoMoreInteractions(parentFolderMock, drServiceMock);

    }

    /**
     * Verify functionality of load(..) method when given folder is not null, the folder.isFilter()
     * method returns false, and the folder is a {@link DiskResourceQueryTemplate}.
     */
    @Test public void testLoad_Case5() {
        DiskResourceQueryTemplate parentFolderMock = mock(DiskResourceQueryTemplate.class);
        when(parentFolderMock.isFilter()).thenReturn(false);
        final String stubPath = "stubPath";
        when(parentFolderMock.getPath()).thenReturn(stubPath);

        FolderRpcProxy spy = spy(proxyUnderTest);
        spy.load(parentFolderMock, folderCallbackMock);
        // Verify for record keeping
        verify(spy).load(any(Folder.class), eq(folderCallbackMock));

        verify(parentFolderMock).isFilter();
        verify(spy).fireEvent(any(SubmitDiskResourceQueryEvent.class));


        verifyNoMoreInteractions(parentFolderMock, spy);
        verifyZeroInteractions(drServiceMock);
    }
    
    /**
     * onSuccess callback !null
     */
    @Test public void testRootFolderCallbackOnSuccess_Case1() {
        proxyUnderTest.load(null, folderCallbackMock);

        verify(maskableMock).mask(any(String.class));

        /* Verify that the DiskResourceService getRootFolders(..) method is called */
        ArgumentCaptor<RootFolderCallback> rootFolderCallbackCaptor = ArgumentCaptor.forClass(FolderRpcProxy.RootFolderCallback.class);
        verify(drServiceMock).getRootFolders(rootFolderCallbackCaptor.capture());

        final RootFolders rootFoldersMock = mock(RootFolders.class);
        final ArrayList<Folder> newArrayList = Lists.<Folder> newArrayList(mock(Folder.class), mock(Folder.class));
        when(rootFoldersMock.getRoots()).thenReturn(newArrayList);

        rootFolderCallbackCaptor.getValue().onSuccess(rootFoldersMock);

        verify(folderCallbackMock).onSuccess(folderListCaptor.capture());

        assertEquals(newArrayList, folderListCaptor.getValue());

        ArgumentCaptor<GetSavedQueryTemplatesCallback> savedQueriesCaptor = ArgumentCaptor.forClass(GetSavedQueryTemplatesCallback.class);
        verify(searchServiceMock).getSavedQueryTemplates(savedQueriesCaptor.capture());

        verify(maskableMock).unmask();

        assertEquals(searchPresenterMock, savedQueriesCaptor.getValue().searchPresenter2);

        verifyNoMoreInteractions(drServiceMock, searchServiceMock);

        /* Verify savedQueriesCallback onSuccess */
        final List<DiskResourceQueryTemplate> qtList = Lists.newArrayList(mock(DiskResourceQueryTemplate.class), mock(DiskResourceQueryTemplate.class));
        savedQueriesCaptor.getValue().onSuccess(qtList);

        verify(searchPresenterMock).loadSavedQueries(eq(qtList));

        /* Verify savedQueriesCallback onFailure */
        savedQueriesCaptor.getValue().onFailure(null);

        verify(announcerMock).schedule(any(ErrorAnnouncementConfig.class));
    }

    /**
     * onSuccess callback null
     */
    @Test public void testRootFolderCallbackOnSuccess_Case2() {
        
        AsyncCallback<List<Folder>> nullCallback = null;
        proxyUnderTest.load(null, nullCallback);
        
        verify(maskableMock).mask(any(String.class));
        
        /* Verify that the DiskResourceService getRootFolders(..) method is called */
        ArgumentCaptor<RootFolderCallback> rootFolderCallbackCaptor = ArgumentCaptor.forClass(FolderRpcProxy.RootFolderCallback.class);
        verify(drServiceMock).getRootFolders(rootFolderCallbackCaptor.capture());
        
        final RootFolders rootFoldersMock = mock(RootFolders.class);
        verifyZeroInteractions(rootFoldersMock);
        
        rootFolderCallbackCaptor.getValue().onSuccess(rootFoldersMock);
        
        ArgumentCaptor<GetSavedQueryTemplatesCallback> savedQueriesCaptor = ArgumentCaptor.forClass(GetSavedQueryTemplatesCallback.class);
        verify(searchServiceMock).getSavedQueryTemplates(savedQueriesCaptor.capture());
        
        verify(maskableMock).unmask();
        
        assertEquals(searchPresenterMock, savedQueriesCaptor.getValue().searchPresenter2);
        
        verifyNoMoreInteractions(drServiceMock, searchServiceMock);
    }

}
