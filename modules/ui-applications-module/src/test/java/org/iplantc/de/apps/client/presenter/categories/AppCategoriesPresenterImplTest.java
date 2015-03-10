package org.iplantc.de.apps.client.presenter.categories;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.gin.factory.AppCategoriesViewFactory;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.lang.String;
import java.util.Collections;
import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class AppCategoriesPresenterImplTest {

    @Mock EventBus eventBusMock;
    @Mock JsonUtil jsonUtilMock;
    @Mock DEProperties propsMock;
    @Mock TreeStore<AppCategory> treeStoreMock;
    @Mock AppCategoriesViewFactory viewFactoryMock;
    @Mock AppCategoriesView viewMock;
    @Mock AppUserServiceFacade appUserServiceMock;
    @Mock AppCategoriesView.AppCategoriesAppearance appearanceMock;
    @Mock Tree<AppCategory, String> treeMock;
    @Mock TreeSelectionModel<AppCategory> selectionModelMock;

    @Captor ArgumentCaptor<AsyncCallback<List<AppCategory>>> appCategoriesCaptor;

    private AppCategoriesPresenterImpl uut;

    @Before public void setUp() {

        when(viewFactoryMock.create(Matchers.<TreeStore<AppCategory>>any(),
                                    any(AppCategoriesView.AppCategoryHierarchyProvider.class))).thenReturn(viewMock);
        when(viewMock.getTree()).thenReturn(treeMock);
        when(treeMock.getSelectionModel()).thenReturn(selectionModelMock);
        when(treeStoreMock.getRootItems()).thenReturn(Lists.newArrayList(mock(AppCategory.class),
                                                                         mock(AppCategory.class)));
        uut = new AppCategoriesPresenterImpl(treeStoreMock,
                                             propsMock,
                                             jsonUtilMock,
                                             eventBusMock,
                                             viewFactoryMock){
            @Override
            void initConstants(DEProperties props, JsonUtil jsonUtil) {
                // LEAVE EMPTY FOR NOW
                // TODO Need to get the Json related parsing out of the presenter.
            }
        };
        uut.appService = appUserServiceMock;
        uut.appearance = appearanceMock;
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

    @Test public void testViewMaskedOnGo() {

        // Return empty list
        when(treeStoreMock.getAll()).thenReturn(Collections.<AppCategory>emptyList());
        when(appearanceMock.getAppCategoriesLoadingMask()).thenReturn("mask");

        uut.go(null);

        verify(viewMock).mask(anyString());
        verify(appUserServiceMock).getAppCategories(appCategoriesCaptor.capture());

        // Call failure with arbitrary exception
        appCategoriesCaptor.getValue().onFailure(null);
        verify(viewMock).unmask();

        appCategoriesCaptor.getValue().onSuccess(Collections.<AppCategory>emptyList());
        verify(viewMock, times(2)).unmask(); // At this point, it has been called 2 times
    }

    @Test public void getSelectedAppCategory_returnsCorrectItem() {
        uut.getSelectedAppCategory();

        verify(selectionModelMock).getSelectedItem();
        verifyNoMoreInteractions(selectionModelMock);
    }
}