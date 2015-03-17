package org.iplantc.de.admin.apps.client.views.toolbar;

import org.iplantc.de.admin.apps.client.AdminAppsToolbarView;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collections;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class AdminAppsToolbarViewImplTest {
    @Mock AppAutoBeanFactory mockAppFactory;
    @Mock AppSearchAutoBeanFactory mockAppSearchFactory;
    @Mock AppServiceFacade mockAppService;

    @Mock TextButton mockAddCategory;
    @Mock TextButton mockCategorizeApp;
    @Mock TextButton mockDeleteCat;
    @Mock TextButton mockRenameCategory;
    @Mock TextButton mockRestoreApp;
    @Mock TextButton mockDeleteApp;
    @Mock TextButton mockMoveCategory;

    @Mock AppSelectionChangedEvent mockAppSelectionChangedEvent;
    @Mock AppCategorySelectionChangedEvent mockAppGrpSelectionChangedEvent;
    @Mock PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> mockLoader;
    @Mock AdminAppsToolbarView.ToolbarAppearance appearanceMock;

    private AdminAppsToolbarViewImpl uut;

    @Before public void setUp(){
        uut = new AdminAppsToolbarViewImpl(appearanceMock,
                                           mockLoader);
        mockMenuItems(uut);
    }

    private void mockMenuItems(AdminAppsToolbarViewImpl uut){
        uut.addCategory = mockAddCategory;
        uut.categorizeApp = mockCategorizeApp;
        uut.deleteCat = mockDeleteCat;
        uut.deleteApp = mockDeleteApp;
        uut.renameCategory = mockRenameCategory;
        uut.restoreApp = mockRestoreApp;
        uut.moveCategory = mockMoveCategory;
    }

    @Test public void testOnAppSelectionChanged_zeroSelected() {
        when(mockAppSelectionChangedEvent.getAppSelection()).thenReturn(Collections.<App>emptyList());

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(mockAppSelectionChangedEvent);

        verify(mockAppSelectionChangedEvent).getAppSelection();

        verify(mockRestoreApp).setEnabled(eq(false));
        verify(mockDeleteApp).setEnabled(eq(false));
        verify(mockCategorizeApp).setEnabled(eq(false));

        verify(mockAddCategory).setEnabled(eq(false));
        verify(mockRenameCategory).setEnabled(eq(false));

        verifyNoMoreInteractions(mockAppSelectionChangedEvent,
                                 mockRestoreApp,
                                 mockDeleteApp,
                                 mockCategorizeApp,
                                 mockAddCategory,
                                 mockRenameCategory);
    }

    @Test
    public void testOnAppSelectionChanged_oneSelected_notDeleted() {
        final App mock = mock(App.class);
        when(mock.isDeleted()).thenReturn(false);
        when(mockAppSelectionChangedEvent.getAppSelection()).thenReturn(Lists.newArrayList(mock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(mockAppSelectionChangedEvent);

        verify(mockAppSelectionChangedEvent).getAppSelection();
        verify(mock).isDeleted();

        verify(mockRestoreApp).setEnabled(eq(false));
        verify(mockDeleteApp).setEnabled(eq(true));
        verify(mockCategorizeApp).setEnabled(eq(true));

        verify(mockAddCategory).setEnabled(eq(false));
        verify(mockRenameCategory).setEnabled(eq(false));

        verifyNoMoreInteractions(mockAppSelectionChangedEvent,
                                 mockRestoreApp,
                                 mockDeleteApp,
                                 mockCategorizeApp,
                                 mockAddCategory,
                                 mockRenameCategory,
                                 mock);
    }

    @Test public void testOnAppSelectionChanged_oneSelected_isDeleted() {
        final App mock = mock(App.class);
        when(mock.isDeleted()).thenReturn(true);
        when(mockAppSelectionChangedEvent.getAppSelection()).thenReturn(Lists.newArrayList(mock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(mockAppSelectionChangedEvent);

        verify(mockAppSelectionChangedEvent).getAppSelection();
        verify(mock).isDeleted();

        verify(mockRestoreApp).setEnabled(eq(true));
        verify(mockDeleteApp).setEnabled(eq(false));
        verify(mockCategorizeApp).setEnabled(eq(false));

        verify(mockAddCategory).setEnabled(eq(false));
        verify(mockRenameCategory).setEnabled(eq(false));

        verifyNoMoreInteractions(mockAppSelectionChangedEvent,
                                 mockRestoreApp,
                                 mockDeleteApp,
                                 mockCategorizeApp,
                                 mockAddCategory,
                                 mockRenameCategory,
                                 mock);

        verifyZeroInteractions(mockDeleteCat,
                               mockRenameCategory,
                               mockMoveCategory);
    }

    @Test public void testOnAppCategorySelectionChanged_zeroSelected() {
        when(mockAppGrpSelectionChangedEvent.getAppCategorySelection()).thenReturn(Collections.<AppCategory>emptyList());

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(mockAppGrpSelectionChangedEvent);

        verify(mockAppGrpSelectionChangedEvent).getAppCategorySelection();

        verify(mockAddCategory).setEnabled(eq(true));
        verify(mockDeleteCat).setEnabled(eq(false));
        verify(mockRenameCategory).setEnabled(eq(false));
        verify(mockMoveCategory).setEnabled(eq(false));

        verifyNoMoreInteractions(mockAppGrpSelectionChangedEvent,
                                 mockRestoreApp,
                                 mockDeleteApp,
                                 mockCategorizeApp);

        verifyZeroInteractions(mockRestoreApp,
                               mockDeleteApp,
                               mockCategorizeApp);
    }

    @Test public void testOnAppCategorySelectionChanged_oneSelected() {
        when(mockAppGrpSelectionChangedEvent.getAppCategorySelection()).thenReturn(Lists.newArrayList(mock(AppCategory.class)));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(mockAppGrpSelectionChangedEvent);

        verify(mockAppGrpSelectionChangedEvent).getAppCategorySelection();

        verify(mockAddCategory).setEnabled(eq(true));
        verify(mockDeleteCat).setEnabled(eq(true));
        verify(mockRenameCategory).setEnabled(eq(true));
        verify(mockMoveCategory).setEnabled(eq(true));

        verifyNoMoreInteractions(mockAppGrpSelectionChangedEvent,
                                 mockRestoreApp,
                                 mockDeleteApp,
                                 mockCategorizeApp);

        verifyZeroInteractions(mockRestoreApp,
                               mockDeleteApp,
                               mockCategorizeApp);
    }

    @Test public void testOnAppCategorySelectionChanged_oneSelected_hasApps() {
        final AppCategory appCategoryMock = mock(AppCategory.class);
        when(appCategoryMock.getAppCount()).thenReturn(1);
        when(mockAppGrpSelectionChangedEvent.getAppCategorySelection()).thenReturn(Lists.newArrayList(appCategoryMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(mockAppGrpSelectionChangedEvent);

        verify(mockAppGrpSelectionChangedEvent).getAppCategorySelection();

        verify(mockDeleteCat).setEnabled(eq(true));
        verify(mockRenameCategory).setEnabled(eq(true));
        verify(mockMoveCategory).setEnabled(eq(true));
        verify(mockAddCategory).setEnabled(eq(true));

        verifyNoMoreInteractions(mockAppGrpSelectionChangedEvent,
                                 mockRestoreApp,
                                 mockDeleteApp,
                                 mockCategorizeApp);

        verifyZeroInteractions(mockRestoreApp,
                               mockDeleteApp,
                               mockCategorizeApp);
    }
}