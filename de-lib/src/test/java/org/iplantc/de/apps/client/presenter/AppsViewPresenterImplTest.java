package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.gin.factory.AppsViewFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gwtmockito.GwtMockitoTestRunner;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

@RunWith(GwtMockitoTestRunner.class)
public class AppsViewPresenterImplTest {

    private AppsViewPresenterImpl uut;
    @Mock AppCategoriesView categoriesViewMock;
    @Mock AppCategoriesView.Presenter categoriesPresenterMock;

    @Mock AppsGridView gridViewMock;
    @Mock AppsGridView.Presenter gridPresenterMock;

    @Mock AppsToolbarView toolbarViewMock;
    @Mock AppsToolbarView.Presenter toolbarPresenterMock;

    @Mock AppsViewFactory viewFactoryMock;

    @Before public void setUp() {
        when(categoriesPresenterMock.getView()).thenReturn(categoriesViewMock);
        when(gridPresenterMock.getView()).thenReturn(gridViewMock);
        when(toolbarPresenterMock.getView()).thenReturn(toolbarViewMock);
        uut = new AppsViewPresenterImpl(viewFactoryMock,
                                        categoriesPresenterMock,
                                        gridPresenterMock,
                                        toolbarPresenterMock);
    }

    @Test public void testConstructorEventHandlerWiring() {
        verify(viewFactoryMock).create(eq(categoriesPresenterMock),
                                       eq(gridPresenterMock),
                                       eq(toolbarPresenterMock));

        // Verify categories wiring
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(gridPresenterMock));
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(gridViewMock));
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(toolbarViewMock));

        // Verify grid wiring
        verify(gridPresenterMock).addStoreAddHandler(categoriesPresenterMock);
        verify(gridPresenterMock).addStoreRemoveHandler(categoriesPresenterMock);
        verify(gridPresenterMock).addStoreClearHandler(categoriesPresenterMock);
        verify(gridPresenterMock).addAppFavoritedEventHandler(categoriesPresenterMock);
        verify(gridViewMock).addAppSelectionChangedEventHandler(toolbarViewMock);
        verify(gridViewMock).addAppInfoSelectedEventHandler(categoriesPresenterMock);

        // Verify toolbar wiring
        verify(toolbarViewMock).addDeleteAppsSelectedHandler(gridPresenterMock);
        verify(toolbarViewMock).addCopyAppSelectedHandler(categoriesPresenterMock);
        verify(toolbarViewMock).addCopyWorkflowSelectedHandler(categoriesPresenterMock);
        verify(toolbarViewMock).addRunAppSelectedHandler(gridPresenterMock);
        verify(toolbarViewMock).addAppSearchResultLoadEventHandler(categoriesPresenterMock);
        verify(toolbarViewMock).addAppSearchResultLoadEventHandler(gridPresenterMock);
        verify(toolbarViewMock).addBeforeAppSearchEventHandler(gridViewMock);
        verify(toolbarViewMock).addAppSearchResultLoadEventHandler(gridViewMock);

        verify(categoriesPresenterMock, times(3)).getView();
        verify(gridPresenterMock, times(5)).getView();
        verify(toolbarPresenterMock, times(10)).getView();


        verifyNoMoreInteractions(viewFactoryMock,
                                 categoriesPresenterMock,
                                 gridPresenterMock,
                                 toolbarPresenterMock,
                                 categoriesViewMock,
                                 gridViewMock,
                                 toolbarViewMock);

        final List<String> dirStack = Lists.newArrayList();
        final List<String> output = Lists.newArrayList();
        for(String s : Splitter.on("/").split("/foo/bar/baz")){
            dirStack.add(s);
            output.add(Joiner.on("/").join(dirStack));
        }
        System.out.println(output);
    }



}