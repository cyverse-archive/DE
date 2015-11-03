package org.iplantc.de.admin.apps.client.presenter.grid;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.events.selection.RestoreAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.SaveAppSelected;
import org.iplantc.de.admin.apps.client.gin.factory.AdminAppsGridViewFactory;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class AdminAppsGridPresenterImplTest {

    @Mock AdminAppsGridViewFactory viewFactoryMock;
    @Mock AppAdminServiceFacade adminAppServiceMock;
    @Mock AdminAppsGridView viewMock;
    @Mock AppServiceFacade appServiceMock;
    @Mock ListStore<App> listStoreMock;
    @Mock AdminAppsGridView.Presenter.Appearance appearanceMock;
    @Mock StoreRemoveEvent.StoreRemoveHandler<App> storeRemoveHandlerMock;
    @Mock Grid<App> gridMock;
    @Mock GridSelectionModel<App> selectionModelMock;
    @Mock IplantAnnouncer announcerMock;

    @Captor ArgumentCaptor<AsyncCallback<List<App>>> appListCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<App>> appCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> voidCallbackCaptor;

    private AdminAppsGridPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<ListStore<App>>any())).thenReturn(viewMock);
        when(viewMock.getGrid()).thenReturn(gridMock);
        when(gridMock.getSelectionModel()).thenReturn(selectionModelMock);
        uut = new AdminAppsGridPresenterImpl(viewFactoryMock, listStoreMock);
        uut.adminAppService = adminAppServiceMock;
        uut.appService = appServiceMock;
        uut.appearance = appearanceMock;
        uut.announcer = announcerMock;
    }

    @Test public void verifyForwardedEventHandlerRegistration() {
        /*** CALL METHOD UNDER TEST ***/
        uut.addStoreRemoveHandler(storeRemoveHandlerMock);

        verify(listStoreMock).addStoreRemoveHandler(eq(storeRemoveHandlerMock));
        verifyNoMoreInteractions(listStoreMock);
        verifyZeroInteractions(appServiceMock,
                               adminAppServiceMock);
    }

    @Test public void verifyCorrectView_getView() {
        /*** CALL METHOD UNDER TEST ***/
        final AdminAppsGridView uutView = uut.getView();

        assertEquals(viewMock, uutView);
    }

    @Test public void verifyAppServiceCalled_onAppCategorySelectionChanged() {
        AppCategorySelectionChangedEvent eventMock = mock(AppCategorySelectionChangedEvent.class);
        final AppCategory appCategoryMock = mock(AppCategory.class);
        when(appCategoryMock.getId()).thenReturn("mock category id");
        List<AppCategory> selection = Lists.newArrayList(appCategoryMock);
        when(eventMock.getAppCategorySelection()).thenReturn(selection);

        when(appearanceMock.getAppsLoadingMask()).thenReturn("loading mask");

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(eventMock);

        verify(viewMock).mask(anyString());
        verify(appearanceMock).getAppsLoadingMask();
        verify(appServiceMock).getApps(eq(appCategoryMock), appListCallbackCaptor.capture());

        List<App> resultList = Lists.newArrayList(mock(App.class));

        /*** CALL METHOD UNDER TEST ***/
        appListCallbackCaptor.getValue().onSuccess(resultList);

        verify(listStoreMock).clear();
        verify(listStoreMock).addAll(eq(resultList));
        verify(viewMock).unmask();

        verifyNoMoreInteractions(appServiceMock,
                                 appearanceMock,
                                 listStoreMock);
    }

    @Test public void verifyServiceCalled_onAppNameSelected() {
        // Record keeping
        verify(viewMock).addAppNameSelectedEventHandler(Matchers.<AppNameSelectedEvent.AppNameSelectedEventHandler>any());

        AppNameSelectedEvent eventMock = mock(AppNameSelectedEvent.class);
        App appMock = mock(App.class);
        when(eventMock.getSelectedApp()).thenReturn(appMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppNameSelected(eventMock);

        verify(adminAppServiceMock).getAppDoc(eq(appMock), Matchers.<AsyncCallback<AppDoc>>any());

        verifyZeroInteractions(viewMock,
                               appServiceMock,
                               listStoreMock);
    }

    @Test public void verifyListStoreUpdated_onAppSearchResultLoad() {
        AppSearchResultLoadEvent eventMock = mock(AppSearchResultLoadEvent.class);
        final ArrayList<App> resultsMock = Lists.newArrayList(mock(App.class),
                                                    mock(App.class));
        when(eventMock.getResults()).thenReturn(resultsMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSearchResultLoad(eventMock);

        verify(listStoreMock).clear();
        verify(listStoreMock).addAll(eq(resultsMock));

        verifyNoMoreInteractions(listStoreMock);
        verifyZeroInteractions(adminAppServiceMock,
                               appServiceMock);
    }

    @Test public void verifyAppServiceCalled_onDeleteAppsSelected() {
        // Record keeping
        verify(viewMock).addAppNameSelectedEventHandler(Matchers.<AppNameSelectedEvent.AppNameSelectedEventHandler>any());

        DeleteAppsSelected eventMock = mock(DeleteAppsSelected.class);
        final App appMock = mock(App.class);
        final ArrayList<App> appsToBeDeletedMock = Lists.newArrayList(appMock);
        when(eventMock.getAppsToBeDeleted()).thenReturn(appsToBeDeletedMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onDeleteAppsSelected(eventMock);

        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).deleteApp(eq(appMock), voidCallbackCaptor.capture());

        /*** CALL METHOD UNDER TEST ***/
        voidCallbackCaptor.getValue().onSuccess(null);

        verify(viewMock).unmask();
        verify(viewMock).getGrid();
        verify(gridMock).getSelectionModel();
        verify(selectionModelMock).deselectAll();
        verify(listStoreMock).remove(eq(appMock));

        verifyNoMoreInteractions(viewMock,
                                 adminAppServiceMock,
                                 listStoreMock,
                                 gridMock,
                                 viewMock);

        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verifyServiceCalled_onRestoreAppSelected() {
        // Record keeping
        verify(viewMock).addAppNameSelectedEventHandler(Matchers.<AppNameSelectedEvent.AppNameSelectedEventHandler>any());

        RestoreAppSelected eventMock = mock(RestoreAppSelected.class);
        final App appMock = mock(App.class);
        when(appMock.isDeleted()).thenReturn(true);
        when(eventMock.getApps()).thenReturn(Lists.newArrayList(appMock));


        /*** CALL METHOD UNDER TEST ***/
        uut.onRestoreAppSelected(eventMock);

        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).restoreApp(eq(appMock),
                                               appCallbackCaptor.capture());

        App resultMock = mock(App.class);
        /*** CALL METHOD UNDER TEST ***/
        appCallbackCaptor.getValue().onSuccess(resultMock);

        verify(viewMock).unmask();

        verifyNoMoreInteractions(viewMock);
    }

    @Test public void verifyDocSaved_onSaveAppSelected() {
        // Record keeping
        verify(viewMock).addAppNameSelectedEventHandler(Matchers.<AppNameSelectedEvent.AppNameSelectedEventHandler>any());

        uut.isDocUpdate = false;
        SaveAppSelected eventMock = mock(SaveAppSelected.class);
        App appMock = mock(App.class);
        AppDoc docMock = mock(AppDoc.class);
        when(appMock.getName()).thenReturn("mock name");
        when(docMock.getDocumentation()).thenReturn("mock documentation");
        when(eventMock.getApp()).thenReturn(appMock);
        when(eventMock.getDoc()).thenReturn(docMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onSaveAppSelected(eventMock);

        verify(appMock).getName();
        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).updateApp(eq(appMock), Matchers.<AsyncCallback<App>>any());

        verify(docMock).getDocumentation();
        verify(adminAppServiceMock).saveAppDoc(eq(appMock),
                                               eq(docMock),
                                               Matchers.<AsyncCallback<AppDoc>>any());

        verifyNoMoreInteractions(viewMock,
                                 adminAppServiceMock,
                                 appMock,
                                 docMock);
        verifyZeroInteractions(appServiceMock);
    }


    @Test public void verifyDocUpdated_onSaveAppSelected() {
        // Record keeping
        verify(viewMock).addAppNameSelectedEventHandler(Matchers.<AppNameSelectedEvent.AppNameSelectedEventHandler>any());

        uut.isDocUpdate = true;
        SaveAppSelected eventMock = mock(SaveAppSelected.class);
        App appMock = mock(App.class);
        AppDoc docMock = mock(AppDoc.class);
        when(appMock.getName()).thenReturn("mock name");
        when(docMock.getDocumentation()).thenReturn("mock documentation");
        when(eventMock.getApp()).thenReturn(appMock);
        when(eventMock.getDoc()).thenReturn(docMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onSaveAppSelected(eventMock);

        verify(appMock).getName();
        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).updateApp(eq(appMock), Matchers.<AsyncCallback<App>>any());

        verify(docMock).getDocumentation();
        verify(adminAppServiceMock).updateAppDoc(eq(appMock),
                                                 eq(docMock),
                                                 Matchers.<AsyncCallback<AppDoc>>any());

        verifyNoMoreInteractions(viewMock,
                                 adminAppServiceMock,
                                 appMock,
                                 docMock);
        verifyZeroInteractions(appServiceMock);
    }

}