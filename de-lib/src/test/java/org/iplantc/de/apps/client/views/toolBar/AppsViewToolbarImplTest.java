package org.iplantc.de.apps.client.views.toolBar;

import org.iplantc.de.apps.client.AppsToolbarView;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppSelectionChangedEvent;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class AppsViewToolbarImplTest {

    @Mock AppAutoBeanFactory mockAppFactory;
    @Mock AppSearchAutoBeanFactory mockAppSearchFactory;
    @Mock AppServiceFacade mockAppService;
    @Mock UserInfo mockUserInfo;

    @Mock MenuItem mockAppRun;
    @Mock MenuItem mockCopyApp;
    @Mock MenuItem mockCopyWf;
    @Mock MenuItem mockCreateNewApp;
    @Mock MenuItem mockCreateWorkflow;
    @Mock MenuItem mockDeleteApp;
    @Mock MenuItem mockDeleteWf;
    @Mock MenuItem mockEditApp;
    @Mock MenuItem mockEditWf;
    @Mock MenuItem mockRequestTool;
    @Mock MenuItem mockSubmitApp;
    @Mock MenuItem mockSubmitWf;
    @Mock MenuItem mockWfRun;

    @Mock TextButton mockAppMenu;
    @Mock TextButton mockWfMenu;

    @Mock AutoBean<AppLoadConfig> mockLoadConfigAb;
    @Mock AppLoadConfig mockLoadConfig;
    @Mock AppsToolbarView.AppsToolbarAppearance mockAppearance;
    @Mock PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> mockLoader;
    @Mock List<App> currentSelectionMock;
    @Mock AppSearchField appSearchMock;

    private AppsViewToolbarImpl uut;

    @Before public void setUp() {
        when(mockAppSearchFactory.loadConfig()).thenReturn(mockLoadConfigAb);
        when(mockLoadConfigAb.as()).thenReturn(mockLoadConfig);
        uut = new AppsViewToolbarImpl(mockAppearance, mockLoader);

        setupMocks(uut);
    }

    void setupMocks(AppsViewToolbarImpl uut) {
        uut.wf_menu = mockWfMenu;
        uut.app_menu = mockAppMenu;
        uut.appRun = mockAppRun;
        uut.copyApp = mockCopyApp;
        uut.copyWf = mockCopyWf;
        uut.createNewApp = mockCreateNewApp;
        uut.createWorkflow = mockCreateWorkflow;
        uut.deleteApp = mockDeleteApp;
        uut.deleteWf = mockDeleteWf;
        uut.editApp = mockEditApp;
        uut.editWf = mockEditWf;
        uut.requestTool = mockRequestTool;
        uut.submitApp = mockSubmitApp;
        uut.submitWf = mockSubmitWf;
        uut.wfRun = mockWfRun;

        uut.appSearch = appSearchMock;

        uut.currentSelection = currentSelectionMock;
        uut.userInfo = mockUserInfo;
    }

    @Test public void emptySelection_onAppCategorySelectionChanged() {
        AppCategorySelectionChangedEvent eventMock = mock(AppCategorySelectionChangedEvent.class);
        final List<AppCategory> emptyList = Collections.emptyList();
        when(eventMock.getAppCategorySelection()).thenReturn(emptyList);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(eventMock);

        verify(appSearchMock, never()).clear();

        verifyNoMoreInteractions(appSearchMock);

        verifyZeroInteractions(mockAppRun,
                               mockCopyApp,
                               mockCopyApp,
                               mockCopyWf,
                               mockCreateNewApp,
                               mockCreateWorkflow,
                               mockDeleteApp,
                               mockDeleteWf,
                               mockEditApp,
                               mockEditWf,
                               mockRequestTool,
                               mockSubmitApp,
                               mockSubmitWf,
                               mockWfRun);
    }

    @Test public void nonEmptySelection_onAppCategorySelectionChanged() {
        AppCategorySelectionChangedEvent eventMock = mock(AppCategorySelectionChangedEvent.class);
        final List<AppCategory> selection = Lists.newArrayList(mock(AppCategory.class));
        when(eventMock.getAppCategorySelection()).thenReturn(selection);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(eventMock);

        verify(appSearchMock).clear();

        verifyNoMoreInteractions(appSearchMock);

        verifyZeroInteractions(mockAppRun,
                               mockCopyApp,
                               mockCopyApp,
                               mockCopyWf,
                               mockCreateNewApp,
                               mockCreateWorkflow,
                               mockDeleteApp,
                               mockDeleteWf,
                               mockEditApp,
                               mockEditWf,
                               mockRequestTool,
                               mockSubmitApp,
                               mockSubmitWf,
                               mockWfRun);
    }

    @Test public void emptySelection_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);
        List<App> emptySelectionMock = Collections.emptyList();
        when(eventMock.getAppSelection()).thenReturn(emptySelectionMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(emptySelectionMock));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is single step.
     */
    @Test public void singleAppSelection_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        // User does not own app
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notCurrentUser@dongle.com");
        List<App> singleAppSelection = Lists.newArrayList(appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(true);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(true);
        verify(mockAppRun).setEnabled(true);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is multi step.
     */
    @Test public void singleWfSelection_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        // User does not own app
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notCurrentUser@dongle.com");
        List<App> singleAppSelection = Lists.newArrayList(wfMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(true);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(true);
        verify(mockWfRun).setEnabled(true);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is single step and public.
     */
    @Test public void singleAppSelection_public_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isPublic()).thenReturn(true);
        // User does not own app
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notCurrentUser@dongle.com");

        List<App> singleAppSelection = Lists.newArrayList(appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(true);
        verify(mockAppRun).setEnabled(true);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }
        /**
     * Verify menu items when app is multi step and public.
     */
    @Test public void singleWfSelection_public_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(2);
        when(appMock.isPublic()).thenReturn(true);
        // User does not own app
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notCurrentUser@dongle.com");

        List<App> singleAppSelection = Lists.newArrayList(appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(true);
        verify(mockWfRun).setEnabled(true);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is single step and owner.
     */
    @Test public void singleAppSelection_owner_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        // User owns app
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("user@email.com");

        List<App> singleAppSelection = Lists.newArrayList(appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(true);
        verify(mockEditApp).setEnabled(true);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(true);
        verify(mockAppRun).setEnabled(true);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is multi step and owner.
     */
    @Test public void singleWfSelection_owner_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        // User owns app
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("user@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(true);
        verify(mockEditWf).setEnabled(true);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(true);
        verify(mockWfRun).setEnabled(true);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }
    /**
     * Verify menu items when app is single step and owner and runnable.
     */
    @Test public void singleAppSelection_ownerRunnable_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isRunnable()).thenReturn(true);
        // User owns app
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("user@email.com");

        List<App> singleAppSelection = Lists.newArrayList(appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(true);
        verify(mockEditApp).setEnabled(true);
        verify(mockSubmitApp).setEnabled(true);
        verify(mockCopyApp).setEnabled(true);
        verify(mockAppRun).setEnabled(true);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is multi step and owner and runnable.
     */
    @Test public void singleWfSelection_ownerRunnable_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        when(wfMock.isRunnable()).thenReturn(true);

        // User owns app
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("user@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(true);
        verify(mockEditWf).setEnabled(true);
        verify(mockSubmitWf).setEnabled(true);
        verify(mockCopyWf).setEnabled(true);
        verify(mockWfRun).setEnabled(true);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is single step and owner, runnable and public.
     */
    @Test public void singleAppSelection_ownerRunnablePublic_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isPublic()).thenReturn(true);
        when(appMock.isRunnable()).thenReturn(true);
        // User owns app
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("user@email.com");

        List<App> singleAppSelection = Lists.newArrayList(appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(true);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(true);
        verify(mockAppRun).setEnabled(true);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when app is multi step and owner, runnable and public.
     */
    @Test public void singleWfSelection_ownerRunnablePublic_onAppSelectionChanged() {
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        when(wfMock.isPublic()).thenReturn(true);
        when(wfMock.isRunnable()).thenReturn(true);

        // User owns app
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("user@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(true);
        verify(mockWfRun).setEnabled(true);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when multi apps are all public.
     */
    @Test public void multiAppWfSelection_allPublic_onAppSelectionChanged() {
         uut = new AppsViewToolbarImpl(mockAppearance, mockLoader){
             @Override
             boolean containsSingleStepApp(List<App> apps) {
                 return true;
             }

             @Override
             boolean containsMultiStepApp(List<App> apps) {
                 return true;
             }

             @Override
             boolean allAppsPrivate(List<App> apps) {
                 return false;
             }
         };
        setupMocks(uut);
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        when(wfMock.isPublic()).thenReturn(true);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isPublic()).thenReturn(true);

        // User does not own apps
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notUser@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock, appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when multi apps are all private.
     */
    @Test public void multiAppWfSelection_allPrivate_onAppSelectionChanged() {
         uut = new AppsViewToolbarImpl(mockAppearance, mockLoader){
             @Override
             boolean containsSingleStepApp(List<App> apps) {
                 return true;
             }

             @Override
             boolean containsMultiStepApp(List<App> apps) {
                 return true;
             }

             @Override
             boolean allAppsPrivate(List<App> apps) {
                 return true;
             }
         };
        setupMocks(uut);
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        when(wfMock.isPublic()).thenReturn(true);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isPublic()).thenReturn(true);

        // User does not own apps
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notUser@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock, appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(true);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(true);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

     /**
     * Verify menu items when multi apps are all private.
     */
    @Test public void multiAppSelection_allPrivate_onAppSelectionChanged() {
         uut = new AppsViewToolbarImpl(mockAppearance, mockLoader){
             @Override
             boolean containsSingleStepApp(List<App> apps) {
                 return true;
             }

             @Override
             boolean containsMultiStepApp(List<App> apps) {
                 return false;
             }

             @Override
             boolean allAppsPrivate(List<App> apps) {
                 return true;
             }
         };
        setupMocks(uut);
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        when(wfMock.isPublic()).thenReturn(true);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isPublic()).thenReturn(true);

        // User does not own apps
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notUser@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock, appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(true);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(false);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }

    /**
     * Verify menu items when multi apps are all private.
     */
    @Test public void multiWfSelection_allPrivate_onAppSelectionChanged() {
         uut = new AppsViewToolbarImpl(mockAppearance, mockLoader){
             @Override
             boolean containsSingleStepApp(List<App> apps) {
                 return false;
             }

             @Override
             boolean containsMultiStepApp(List<App> apps) {
                 return true;
             }

             @Override
             boolean allAppsPrivate(List<App> apps) {
                 return true;
             }
         };
        setupMocks(uut);
        AppSelectionChangedEvent eventMock = mock(AppSelectionChangedEvent.class);

        App wfMock = mock(App.class);
        when(wfMock.getStepCount()).thenReturn(2);
        when(wfMock.isPublic()).thenReturn(true);

        App appMock = mock(App.class);
        when(appMock.getStepCount()).thenReturn(1);
        when(appMock.isPublic()).thenReturn(true);

        // User does not own apps
        when(wfMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(appMock.getIntegratorEmail()).thenReturn("user@email.com");
        when(mockUserInfo.getEmail()).thenReturn("notUser@email.com");

        List<App> singleAppSelection = Lists.newArrayList(wfMock, appMock);
        when(eventMock.getAppSelection()).thenReturn(singleAppSelection);
        when(currentSelectionMock.size()).thenReturn(singleAppSelection.size());
        when(currentSelectionMock.get(eq(0))).thenReturn(singleAppSelection.get(0));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSelectionChanged(eventMock);

        verify(currentSelectionMock).clear();
        verify(currentSelectionMock).addAll(eq(singleAppSelection));

        verify(mockAppMenu).setEnabled(eq(true));
        verify(mockWfMenu).setEnabled(eq(true));

        verify(mockDeleteApp).setEnabled(false);
        verify(mockEditApp).setEnabled(false);
        verify(mockSubmitApp).setEnabled(false);
        verify(mockCopyApp).setEnabled(false);
        verify(mockAppRun).setEnabled(false);
        verify(mockDeleteWf).setEnabled(true);
        verify(mockEditWf).setEnabled(false);
        verify(mockSubmitWf).setEnabled(false);
        verify(mockCopyWf).setEnabled(false);
        verify(mockWfRun).setEnabled(false);

        verifyNoMoreInteractions(mockAppMenu,
                                 mockWfMenu,
                                 mockAppRun,
                                 mockCopyApp,
                                 mockCopyApp,
                                 mockCopyWf,
                                 mockCreateNewApp,
                                 mockCreateWorkflow,
                                 mockDeleteApp,
                                 mockDeleteWf,
                                 mockEditApp,
                                 mockEditWf,
                                 mockRequestTool,
                                 mockSubmitApp,
                                 mockSubmitWf,
                                 mockWfRun);
        verifyZeroInteractions(appSearchMock);
    }


}