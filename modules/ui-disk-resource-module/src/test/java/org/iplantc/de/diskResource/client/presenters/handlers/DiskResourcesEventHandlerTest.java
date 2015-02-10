package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.search.UpdateSavedSearchesEvent;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * @author psarando, jstroot
 * 
 */
@SuppressWarnings("nls")
@RunWith(GxtMockitoTestRunner.class)
public class DiskResourcesEventHandlerTest {
    @Mock UpdateSavedSearchesEvent eventMock;
    @Mock NavigationView.Presenter presenterMock;
    @Mock DiskResourceView viewMock;
    @Mock TreeStore<Folder> treeStoreMock;
    private DiskResourcesEventHandler drHandler;

    // TODO:SS complete tests for metadata
    @Before public void setUp() {
        drHandler = new DiskResourcesEventHandler(presenterMock);
    }

    @Ignore
    @Test public void testOnDiskResourcesMoved(){

    }

}
