package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshedEvent;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class DiskResourceServiceFacadeImplTest {

    @Mock DiscEnvApiService deServiceFacadeMock;
    @Mock DEProperties dePropertiesMock;
    @Mock DEClientConstants deConstantsMock;
    @Mock DiskResourceAutoBeanFactory drAutoBeanFactoryMock;
    @Mock UserInfo userInfoMock;
    @Mock EventBus eventBusMock;
    @Mock AsyncCallback<List<Folder>> folderListCallbackMock;


    @Before public void setup() {
    }

    @Test public void refreshFolder_onSuccess() {
        final Folder folderMock = mock(Folder.class);
        DiskResourceServiceFacadeImpl spy = spy(new DiskResourceServiceFacadeImpl(deServiceFacadeMock,
                                                                                  dePropertiesMock,
                                                                                  deConstantsMock,
                                                                                  drAutoBeanFactoryMock,
                                                                                  userInfoMock,
                                                                                  eventBusMock) {
            public Folder findModel(Folder model) {
                return folderMock;
            }
            public void removeChildren(Folder parent){

            }
            public void getSubFolders(Folder parent, AsyncCallback<List<Folder>> callback){
                callback.onSuccess(Lists.<Folder>newArrayList());
            }
        });
        spy.eventBus = eventBusMock;
        when(folderMock.getFolders()).thenReturn(null);

        // Call unit under test
        spy.refreshFolder(folderMock, folderListCallbackMock);


        ArgumentCaptor<FolderRefreshedEvent> folderRefreshedEventCaptor = ArgumentCaptor.forClass(FolderRefreshedEvent.class);
        verify(spy).removeChildren(eq(folderMock));
        verify(folderMock).setFolders(Matchers.<List<Folder>>eq(null));

        verify(folderListCallbackMock).onSuccess(Matchers.<List<Folder>>any());
        verify(eventBusMock).fireEvent(folderRefreshedEventCaptor.capture());
        assertEquals(folderMock, folderRefreshedEventCaptor.getValue().getFolder());
    }

    @Test public void refreshFolder_onFailure() {
        final Folder folderMock = mock(Folder.class);
        final Throwable throwableMock = mock(Throwable.class);
        DiskResourceServiceFacadeImpl spy = spy(new DiskResourceServiceFacadeImpl(deServiceFacadeMock,
                                                                                  dePropertiesMock,
                                                                                  deConstantsMock,
                                                                                  drAutoBeanFactoryMock,
                                                                                  userInfoMock,
                                                                                  eventBusMock) {
            public Folder findModel(Folder model) {
                return folderMock;
            }

            public void removeChildren(Folder parent){ }

            public void getSubFolders(Folder parent, AsyncCallback<List<Folder>> callback){
                callback.onFailure(throwableMock);
            }
        });
        spy.eventBus = eventBusMock;
        when(folderMock.getFolders()).thenReturn(null);

        // Call unit under test
        spy.refreshFolder(folderMock, folderListCallbackMock);

        verify(spy).removeChildren(eq(folderMock));
        verify(folderMock).setFolders(Matchers.<List<Folder>>eq(null));

        verify(folderListCallbackMock).onFailure(eq(throwableMock));
        verifyZeroInteractions(eventBusMock);
    }

    @Test public void getSubFolders_hasFoldersLoaded() {
        final Folder folderMock = mock(Folder.class);
        DiskResourceServiceFacadeImpl spy = spy(new DiskResourceServiceFacadeImpl(deServiceFacadeMock,
                                                                                  dePropertiesMock,
                                                                                  deConstantsMock,
                                                                                  drAutoBeanFactoryMock,
                                                                                  userInfoMock,
                                                                                  eventBusMock) {
            public Folder findModel(Folder model) {
                return folderMock;
            }
        });
        spy.eventBus = eventBusMock;

        final ArrayList<Folder> subFolders = Lists.newArrayList();
        when(folderMock.getFolders()).thenReturn(subFolders);

        // Call unit under test
        spy.getSubFolders(folderMock, folderListCallbackMock);

        verify(folderListCallbackMock).onSuccess(eq(subFolders));
    }

    @Test public void getSubFolders_noFoldersLoaded() {
        final Folder folderMock = mock(Folder.class);
        DiskResourceServiceFacadeImpl spy = spy(new DiskResourceServiceFacadeImpl(deServiceFacadeMock,
                                                                                  dePropertiesMock,
                                                                                  deConstantsMock,
                                                                                  drAutoBeanFactoryMock,
                                                                                  userInfoMock,
                                                                                  eventBusMock) {
            public Folder findModel(Folder model) {
                return folderMock;
            }
        });
        spy.eventBus = eventBusMock;

        when(folderMock.getFolders()).thenReturn(null);

        // Call unit under test
        spy.getSubFolders(folderMock, folderListCallbackMock);

        verify(spy).callService(any(ServiceCallWrapper.class),
                                Matchers.<AsyncCallbackConverter<String, List<Folder>>>any());
        verify(folderListCallbackMock, never()).onSuccess(Matchers.<List<Folder>>any());
    }
}