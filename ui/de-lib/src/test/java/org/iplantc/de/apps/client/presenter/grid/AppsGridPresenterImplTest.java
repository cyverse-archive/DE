package org.iplantc.de.apps.client.presenter.grid;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.events.selection.RunAppSelected;
import org.iplantc.de.apps.client.gin.factory.AppsGridViewFactory;
import org.iplantc.de.apps.client.presenter.callbacks.DeleteRatingCallback;
import org.iplantc.de.apps.client.presenter.callbacks.RateAppCallback;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.commons.client.comments.view.dialogs.CommentsDialog;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class AppsGridPresenterImplTest {

    @Mock AppsGridViewFactory viewFactoryMock;
    @Mock AppsGridView viewMock;

    @Mock ListStore<App> listStoreMock;
    @Mock StoreAddEvent.StoreAddHandler<App> storeAddHandlerMock;
    @Mock StoreRemoveEvent.StoreRemoveHandler<App> storeRemoveHandlerMock;
    @Mock StoreUpdateEvent.StoreUpdateHandler<App> storeUpdateHandlerMock;
    @Mock StoreClearEvent.StoreClearHandler<App> storeClearHandlerMock;
    @Mock Grid<App> gridMock;
    @Mock GridSelectionModel<App> selectionModelMock;
    @Mock AppUserServiceFacade appServiceMock;
    @Mock AppsGridView.AppsGridAppearance appearanceMock;
    @Mock UserInfo userInfoMock;
    @Mock AppUserServiceFacade appUserServiceMock;

    @Mock AsyncProviderWrapper<CommentsDialog> commentsProviderMock;
    @Captor ArgumentCaptor<AsyncCallback<CommentsDialog>> commentsDlgCaptor;

    @Captor ArgumentCaptor<AsyncCallback<String>> stringCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> voidCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<App>>> appListCallbackCaptor;
    @Mock EventBus eventBusMock;


    private AppsGridPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<ListStore<App>>any())).thenReturn(viewMock);
        when(viewMock.getGrid()).thenReturn(gridMock);
        when(gridMock.getSelectionModel()).thenReturn(selectionModelMock);
        uut = new AppsGridPresenterImpl(viewFactoryMock,
                                        listStoreMock,
                                        eventBusMock);
        uut.appService = appServiceMock;
        uut.appUserService = appUserServiceMock;
        uut.appearance = appearanceMock;
        uut.commentsDialogProvider = commentsProviderMock;
        uut.userInfo = userInfoMock;
    }

    @Test public void testConstructorEventHandlerWiring() {
        verifyConstructor();

        verifyNoMoreInteractions(viewFactoryMock,
                                 viewMock);
    }

    /**
     * Verifies that any handler registration methods are appropriately forwarded.
     */
    @Test public void verifyForwardedEventRegistration() {
        AppFavoritedEvent.AppFavoritedEventHandler eventHandlerMock = mock(AppFavoritedEvent.AppFavoritedEventHandler.class);

        /*** CALL METHOD UNDER TEST ***/
        uut.addAppFavoritedEventHandler(eventHandlerMock);
        verify(viewMock).addAppFavoritedEventHandler(eq(eventHandlerMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.addStoreAddHandler(storeAddHandlerMock);
        verify(listStoreMock).addStoreAddHandler(eq(storeAddHandlerMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.addStoreRemoveHandler(storeRemoveHandlerMock);
        verify(listStoreMock).addStoreRemoveHandler(eq(storeRemoveHandlerMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.addStoreUpdateHandler(storeUpdateHandlerMock);
        verify(listStoreMock).addStoreUpdateHandler(eq(storeUpdateHandlerMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.addStoreClearHandler(storeClearHandlerMock);
        verify(listStoreMock).addStoreClearHandler(eq(storeClearHandlerMock));
    }


    @Test public void verifyGetSelectedApp() {
        /*** CALL METHOD UNDER TEST ***/
        uut.getSelectedApp();

        verify(viewMock).getGrid();
        verify(gridMock).getSelectionModel();
        verify(selectionModelMock).getSelectedItem();

        verifyNoMoreInteractions(gridMock,
                                 selectionModelMock);
    }

    @Test public void verifyAppServiceCalled_onAppCategorySelected() {

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
                                 appearanceMock);
    }

    @Test public void doNothingIfSelectionIsEmpty_onAppCategorySelected() {

        AppCategorySelectionChangedEvent eventMock = mock(AppCategorySelectionChangedEvent.class);
        List<AppCategory> selection = Lists.newArrayList();
        when(eventMock.getAppCategorySelection()).thenReturn(selection);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCategorySelectionChanged(eventMock);

        verifyZeroInteractions(appearanceMock,
                               appServiceMock);
    }

    /**
     * Verifies that the dlg provider is called, and the proper dlg show(..) method
     * is called.
     */
    @Test public void commentsDlgShown_onAppCommentSelectedEvent(){
        AppCommentSelectedEvent eventMock = mock(AppCommentSelectedEvent.class);
        App appMock = mock(App.class);
        when(appMock.getIntegratorEmail()).thenReturn("blah@doo.com");
        when(eventMock.getApp()).thenReturn(appMock);

        when(userInfoMock.getEmail()).thenReturn("baz@foo.com");

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppCommentSelectedEvent(eventMock);

        verify(commentsProviderMock).get(commentsDlgCaptor.capture());


        /*** CALL METHOD UNDER TEST ***/
        CommentsDialog dlgMock = mock(CommentsDialog.class);
        commentsDlgCaptor.getValue().onSuccess(dlgMock);

        verify(dlgMock).show(eq(appMock),
                             eq(false),
                             any(AppMetadataServiceFacade.class));
    }

    /**
     * Verify that the service is called, and appropriate actions are taken
     * on success.
     */
    @Test public void verifyAppServiceCalled_onAppFavoriteSelected(){
        // Book-keeping for constructor
        verify(eventBusMock).addHandler(Matchers.<GwtEvent.Type<AppsGridPresenterImpl>>any(), eq(uut));
        AppFavoriteSelectedEvent eventMock = mock(AppFavoriteSelectedEvent.class);
        App appMock = mock(App.class);
        final String mockId = "mock id";
        when(appMock.getId()).thenReturn(mockId);
        when(appMock.isFavorite()).thenReturn(true);
        when(eventMock.getApp()).thenReturn(appMock);

        Widget widgetMock = mock(Widget.class);
        when(viewMock.asWidget()).thenReturn(widgetMock);
        when(userInfoMock.getWorkspaceId()).thenReturn("workspace id");

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppFavoriteSelected(eventMock);

        verify(appUserServiceMock).favoriteApp(eq(appMock),
                                               eq(false),
                                               voidCallbackCaptor.capture());

        verify(eventMock).getApp();


        /*** CALL METHOD UNDER TEST ***/
        voidCallbackCaptor.getValue().onSuccess(null);
        verify(appMock).setFavorite(false);
        verify(eventBusMock, times(2)).fireEvent(any(AppFavoritedEvent.class));
        verify(eventBusMock, times(2)).fireEvent(any(AppUpdatedEvent.class));

        verifyNoMoreInteractions(appServiceMock,
                                 listStoreMock);
    }

    @Test public void runAppEventFired_onAppNameSelected() {
        // Book-keeping for constructor
        verify(eventBusMock).addHandler(Matchers.<GwtEvent.Type<AppsGridPresenterImpl>>any(), eq(uut));
        AppNameSelectedEvent eventMock = mock(AppNameSelectedEvent.class);
        App appMock = mock(App.class);
        when(eventMock.getSelectedApp()).thenReturn(appMock);
        when(appMock.isRunnable()).thenReturn(true);
        when(appMock.isDisabled()).thenReturn(false);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppNameSelected(eventMock);

        verify(eventMock).getSelectedApp();

        verify(eventBusMock).fireEvent(any(RunAppEvent.class));

        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void runAppEventFired_onRunAppSelected() {
        // Book-keeping for constructor
        verify(eventBusMock).addHandler(Matchers.<GwtEvent.Type<AppsGridPresenterImpl>>any(), eq(uut));
        RunAppSelected eventMock = mock(RunAppSelected.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);
        when(appMock.isRunnable()).thenReturn(true);
        when(appMock.isDisabled()).thenReturn(false);

        /*** CALL METHOD UNDER TEST ***/
        uut.onRunAppSelected(eventMock);

        verify(eventMock).getApp();

        verify(eventBusMock).fireEvent(any(RunAppEvent.class));

        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verifyAppServiceCalled_onAppRatingDeselected() {
        AppRatingDeselected eventMock = mock(AppRatingDeselected.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppRatingDeselected(eventMock);

        verify(appUserServiceMock).deleteRating(eq(appMock),
                                                any(DeleteRatingCallback.class));
        verifyNoMoreInteractions(appServiceMock,
                                 appMock);
    }

    @Test public void verifyAppServiceCalled_onAppRatingSelected() {
         AppRatingSelected eventMock = mock(AppRatingSelected.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);
        int mockScore = 3;
        when(eventMock.getScore()).thenReturn(mockScore);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppRatingSelected(eventMock);

        verify(appUserServiceMock).rateApp(eq(appMock),
                                           eq(mockScore),
                                           any(RateAppCallback.class));
        verifyNoMoreInteractions(appServiceMock,
                                 appMock);
    }

    @Test public void verifyStoreClearedAndResultsAdded_onAppSearchResultLoad() {
        // Record keeping
        verifyConstructor();
        AppSearchResultLoadEvent eventMock = mock(AppSearchResultLoadEvent.class);
        List<App> results = Lists.newArrayList(mock(App.class), mock(App.class));
        String searchPatternMock = "mock search pattern";
        when(eventMock.getResults()).thenReturn(results);
        when(eventMock.getSearchPattern()).thenReturn(searchPatternMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSearchResultLoad(eventMock);

        verify(viewMock).setSearchPattern(eq(searchPatternMock));

        verify(listStoreMock).clear();
        verify(listStoreMock).addAll(eq(results));

        verifyNoMoreInteractions(listStoreMock,
                                 viewMock);

        verifyZeroInteractions(appServiceMock,
                               appUserServiceMock);
    }

    @Test public void verifyAppServiceCalled_onDeleteAppsSelected() {
        // Book-keeping for constructor
        verify(eventBusMock).addHandler(Matchers.<GwtEvent.Type<AppsGridPresenterImpl>>any(), eq(uut));
        DeleteAppsSelected eventMock = mock(DeleteAppsSelected.class);
        App mock1 = mock(App.class);
        App mock2 = mock(App.class);
        when(mock1.getId()).thenReturn("mock id 1");
        when(mock2.getId()).thenReturn("mock id 2");
        List<App> appsToBeDeleted = Lists.newArrayList(mock1,
                                                       mock2);
        when(eventMock.getAppsToBeDeleted()).thenReturn(appsToBeDeleted);

        String mockUserName = "mock user name";
        when(userInfoMock.getUsername()).thenReturn(mockUserName);
        String mockFullUserName = "mock full user name";
        when(userInfoMock.getFullUsername()).thenReturn(mockFullUserName);

        /*** CALL METHOD UNDER TEST ***/
        uut.onDeleteAppsSelected(eventMock);

        verify(eventMock).getAppsToBeDeleted();

        verify(appUserServiceMock).deleteAppsFromWorkspace(eq(appsToBeDeleted),
                                                           Matchers.<AsyncCallback<Void>>any());
        verifyNoMoreInteractions(appServiceMock,
                                 eventMock,
                                 eventBusMock,
                                 userInfoMock,
                                 mock1,
                                 mock2);
    }

    private void verifyConstructor() {
        verify(viewFactoryMock).create(eq(uut.listStore));

        // Verify view wiring
        verify(viewMock).addAppNameSelectedEventHandler(eq(uut));
        verify(viewMock).addAppRatingDeselectedHandler(eq(uut));
        verify(viewMock).addAppRatingSelectedHandler(eq(uut));
        verify(viewMock).addAppCommentSelectedEventHandlers(eq(uut));
        verify(viewMock).addAppFavoriteSelectedEventHandlers(eq(uut));
    }



}