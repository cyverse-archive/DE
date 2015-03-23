package org.iplantc.de.diskResource.client.presenters.grid;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewFactory;
import org.iplantc.de.diskResource.client.views.grid.DiskResourceColumnModel;

import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class GridViewPresenterImplTest {

    @Mock FolderContentsRpcProxyFactory folderContentsProxyFactoryMock;
    @Mock GridViewFactory gridViewFactoryMock;
    @Mock List<InfoType> infoTypeFiltersMock;
    @Mock NavigationView.Presenter navigationPresenterMock;
    @Mock TYPE entityTypeMock;
    @Mock GridView viewMock;
    @Mock GridView.FolderContentsRpcProxy folderContentsProxyMock;
    @Mock DiskResourceColumnModel columnModelMock;
    @Mock GridView.Presenter.Appearance appearanceMock;

    private GridViewPresenterImpl uut;

    @Before public void setUp() {
        when(folderContentsProxyFactoryMock.createWithEntityType(eq(infoTypeFiltersMock), eq(entityTypeMock))).thenReturn(folderContentsProxyMock);
        when(gridViewFactoryMock.create(any(GridView.Presenter.class), Matchers.<ListStore<DiskResource>>any(), eq(folderContentsProxyMock))).thenReturn(viewMock);
        when(viewMock.getColumnModel()).thenReturn(columnModelMock);
        uut = new GridViewPresenterImpl(gridViewFactoryMock, folderContentsProxyFactoryMock, appearanceMock, navigationPresenterMock, infoTypeFiltersMock, entityTypeMock);
    }

    @Test public void placeHolderTest() {

    }

}
