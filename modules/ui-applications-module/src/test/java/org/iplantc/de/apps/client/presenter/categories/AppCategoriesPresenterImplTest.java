package org.iplantc.de.apps.client.presenter.categories;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.gin.factory.AppCategoriesViewFactory;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

@RunWith(GwtMockitoTestRunner.class)
public class AppCategoriesPresenterImplTest {

    @Mock AppUserServiceFacade appServiceMock;
    @Mock EventBus eventBusMock;
    @Mock JsonUtil jsonUtilMock;
    @Mock DEProperties propsMock;
    @Mock TreeStore<AppCategory> treeStoreMock;
    @Mock AppCategoriesViewFactory viewFactoryMock;
    @Mock AppCategoriesView viewMock;

    private AppCategoriesPresenterImpl uut;

    @Before public void setUp() {

        when(viewFactoryMock.create(Matchers.<TreeStore<AppCategory>>any(),
                                    any(AppCategoriesView.AppCategoryHierarchyProvider.class))).thenReturn(viewMock);
        uut = new AppCategoriesPresenterImpl(treeStoreMock,
                                             propsMock,
                                             jsonUtilMock,
                                             eventBusMock,
                                             appServiceMock,
                                             viewFactoryMock){
            @Override
            void initConstants(DEProperties props, JsonUtil jsonUtil) {
                // LEAVE EMPTY FOR NOW
                // TODO Need to get the Json related parsing out of the presenter.
            }
        };
    }

    @Test public void testConstructorEventHandlerWiring() {
        verify(viewFactoryMock).create(eq(treeStoreMock), eq(uut));
        verify(treeStoreMock).addSortInfo(Matchers.<Store.StoreSortInfo<AppCategory>>any());

        verify(eventBusMock).addHandler(Matchers.<GwtEvent.Type<AppCategoriesPresenterImpl>>any(), eq(uut));

        verifyNoMoreInteractions(treeStoreMock,
                                 viewMock,
                                 viewFactoryMock,
                                 eventBusMock);
    }
}