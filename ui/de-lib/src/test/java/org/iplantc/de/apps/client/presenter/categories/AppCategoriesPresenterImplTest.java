package org.iplantc.de.apps.client.presenter.categories;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSavedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.CopyAppSelected;
import org.iplantc.de.apps.client.events.selection.CopyWorkflowSelected;
import org.iplantc.de.apps.client.gin.factory.AppCategoriesViewFactory;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GxtMockitoTestRunner.class)
public class AppCategoriesPresenterImplTest {

    @Mock EventBus eventBusMock;
    @Mock JsonUtil jsonUtilMock;
    @Mock DEProperties propsMock;
    @Mock TreeStore<AppCategory> treeStoreMock;
    @Mock AppCategoriesViewFactory viewFactoryMock;
    @Mock AppCategoriesView viewMock;
    @Mock AppServiceFacade appServiceMock;
    @Mock AppUserServiceFacade appUserServiceMock;
    @Mock AppCategoriesView.AppCategoriesAppearance appearanceMock;
    @Mock Tree<AppCategory, String> treeMock;
    @Mock TreeSelectionModel<AppCategory> selectionModelMock;

    // Event mocks
    @Mock StoreAddEvent<App> mockAddEvent;
    @Mock StoreRemoveEvent<App> mockRemoveEvent;
    @Mock StoreClearEvent<App> mockClearEvent;
    @Mock Store<App> mockStore;

    @Captor ArgumentCaptor<AsyncCallback<List<AppCategory>>> appCategoriesCaptor;
    @Captor ArgumentCaptor<AsyncCallback<App>> appCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> voidCallbackCaptor;


    @Mock AsyncProviderWrapper<AppDetailsDialog> mockDetailsProvider;
    @Captor ArgumentCaptor<AsyncCallback<AppDetailsDialog>> detailsCallbackCaptor;

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
                                             viewFactoryMock);
        uut.appUserService = appUserServiceMock;
        uut.appearance = appearanceMock;
        uut.appService = appServiceMock;
    }

    @Test public void testConstructorEventHandlerWiring() {
        verifyConstructor();
        verifyNoMoreInteractions(treeStoreMock,
                                 viewMock,
                                 viewFactoryMock,
                                 eventBusMock);
    }


    /**
     * Ensures that the view is appropriately masked and unmasked when "go(..)" is called.
     */
    @Test public void viewMaskedAndUnmaskedOnGo() {

        // Return empty list
        when(treeStoreMock.getAll()).thenReturn(Collections.<AppCategory>emptyList());
        when(appearanceMock.getAppCategoriesLoadingMask()).thenReturn("mask");

        /*** CALL METHOD UNDER TEST ***/
        uut.go(null);

        verify(viewMock).mask(anyString());
        verify(appServiceMock).getAppCategories(appCategoriesCaptor.capture());

        // Call failure with arbitrary exception
        appCategoriesCaptor.getValue().onFailure(null);
        verify(viewMock).unmask();

        appCategoriesCaptor.getValue().onSuccess(Collections.<AppCategory>emptyList());
        verify(viewMock, times(2)).unmask(); // At this point, it has been called 2 times
    }

    @Test public void testGetSelectedAppCategory() {
        /*** CALL METHOD UNDER TEST ***/
        uut.getSelectedAppCategory();

        verify(selectionModelMock).getSelectedItem();
        verifyNoMoreInteractions(selectionModelMock);
    }

    @Test public void currentAppCategoryCountsUpdated_onStoreAdd() {
        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock));
        spy.appService = appUserServiceMock;
        spy.appearance = appearanceMock;

        AppCategory appCategoryMock = mock(AppCategory.class);
        when(selectionModelMock.getSelectedItem()).thenReturn(appCategoryMock);

        // Setup event mock
        final ArrayList<App> apps = Lists.newArrayList(mock(App.class), mock(App.class));
        when(mockAddEvent.getSource()).thenReturn(mockStore);
        when(mockStore.getAll()).thenReturn(apps);


        /*** CALL METHOD UNDER TEST ***/
        spy.onAdd(mockAddEvent);
        verify(mockAddEvent).getSource();
        verify(selectionModelMock).getSelectedItem();
        verify(mockStore).getAll();
        verify(spy).updateAppCategoryAppCount(eq(appCategoryMock), eq(apps.size()));

        verifyNoMoreInteractions(mockStore,
                                 selectionModelMock);
    }

    @Test public void currentAppCategoryCountsUpdated_onStoreRemove() {
        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock));
        spy.appService = appUserServiceMock;

        AppCategory appCategoryMock = mock(AppCategory.class);
        when(selectionModelMock.getSelectedItem()).thenReturn(appCategoryMock);

        // Setup event mock
        final ArrayList<App> apps = Lists.newArrayList(mock(App.class), mock(App.class));
        when(mockRemoveEvent.getSource()).thenReturn(mockStore);
        when(mockStore.getAll()).thenReturn(apps);


        /*** CALL METHOD UNDER TEST ***/
        spy.onRemove(mockRemoveEvent);
        verify(mockRemoveEvent).getSource();
        verify(selectionModelMock).getSelectedItem();
        verify(mockStore).getAll();
        verify(spy).updateAppCategoryAppCount(eq(appCategoryMock), eq(apps.size()));
    }

    @Test public void currentAppCategoryCountsZeroed_onStoreClear() {
        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock));
        spy.appService = appUserServiceMock;

        // For book-keeping, for construction of non-spy and spy uut's
        verify(eventBusMock, times(6)).addHandler(Matchers.<GwtEvent.Type<AppCategoriesPresenterImpl>>any(), Matchers.<AppCategoriesPresenterImpl>any());

        AppCategory appCategoryMock = mock(AppCategory.class);
        when(selectionModelMock.getSelectedItem()).thenReturn(appCategoryMock);

        /*** CALL METHOD UNDER TEST ***/
        spy.onClear(mockClearEvent);
        verify(selectionModelMock).getSelectedItem();
        verify(spy).updateAppCategoryAppCount(eq(appCategoryMock), eq(0));

        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appUserServiceMock,
                               appServiceMock);
    }

    @Test public void verifyServiceCalled_onAppFavoriteSelected() {
        // For book-keeping, for construction of non-spy and spy uut's
        verify(eventBusMock, times(3)).addHandler(Matchers.<GwtEvent.Type<AppCategoriesPresenterImpl>>any(), Matchers.<AppCategoriesPresenterImpl>any());

        AppFavoriteSelectedEvent eventMock = mock(AppFavoriteSelectedEvent.class);
        App selectedAppMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(selectedAppMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppFavoriteSelected(eventMock);

        verify(eventMock).getApp();
        verify(appUserServiceMock).favoriteApp(eq(selectedAppMock), eq(true), voidCallbackCaptor.capture());


        /*** CALL METHOD UNDER TEST ***/
        voidCallbackCaptor.getValue().onSuccess(null);

        verify(selectedAppMock).setFavorite(eq(true));
        verify(eventBusMock, times(2)).fireEvent(any(AppFavoritedEvent.class));
        verify(eventBusMock, times(2)).fireEvent(any(AppUpdatedEvent.class));

        verifyNoMoreInteractions(eventBusMock,
                                 appUserServiceMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verifyServiceCalled_onAppRatingDeselected() {
        AppRatingDeselected eventMock = mock(AppRatingDeselected.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppRatingDeselected(eventMock);

        verify(appUserServiceMock).deleteRating(eq(appMock), Matchers.<AsyncCallback<AppFeedback>>any());

        verifyNoMoreInteractions(appUserServiceMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verifyServiceCalled_onAppRatingSelected() {
        AppRatingSelected eventMock = mock(AppRatingSelected.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);
        int mockScore = 3;
        when(eventMock.getScore()).thenReturn(mockScore);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppRatingSelected(eventMock);

        verify(appUserServiceMock).rateApp(eq(appMock),
                                           eq(mockScore),
                                           Matchers.<AsyncCallback<AppFeedback>>any());

        verifyNoMoreInteractions(appUserServiceMock);
        verifyZeroInteractions(appServiceMock);
    }

    /**
     * Verify that the Favorites category count is updated when
     * the current category is NOT the Favorites category.
     */
    @Test public void favoritesCategoryReselected_onAppFavorited() {
        final AppCategory mockFavoriteCategory = mock(AppCategory.class);
        when(mockFavoriteCategory.getAppCount()).thenReturn(2);
        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock){
            @Override
            AppCategory findAppCategoryByName(String name) {
                return mockFavoriteCategory;
            }
        });
        // For book-keeping, for construction of non-spy and spy uut's
        verify(eventBusMock, times(6)).addHandler(Matchers.<GwtEvent.Type<AppCategoriesPresenterImpl>>any(), Matchers.<AppCategoriesPresenterImpl>any());
        // Set Favorites category as current category
        spy.FAVORITES = "Favorites";
        AppCategory appCategoryMock = mock(AppCategory.class);
        when(appCategoryMock.getName()).thenReturn("not favorite");
        when(selectionModelMock.getSelectedItem()).thenReturn(appCategoryMock);

        AppFavoritedEvent mockEvent = mock(AppFavoritedEvent.class);
        final App appMock = mock(App.class);
        when(mockEvent.getApp()).thenReturn(appMock);


        /*** CALL METHOD UNDER TEST ***/
        spy.onAppFavorited(mockEvent);

        verify(spy).findAppCategoryByName(eq(spy.FAVORITES));
        verify(appMock).isFavorite();

        verifyNoMoreInteractions(eventBusMock);
        verifyZeroInteractions(appUserServiceMock);
    }

    /**
     * Verify that when the current category *IS NOT* the favorites category,
     * that the favorites category count is updated
     */
    @Test public void favoritesCategoryCountUpdated_onAppFavorited() {

        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock));
        // Setup fav category
        uut.FAVORITES = "Favorites";
        AppCategory favoritesCat = mock(AppCategory.class);
        when(favoritesCat.getName()).thenReturn(uut.FAVORITES);
        int startingFavCount = 4;
        when(favoritesCat.getAppCount()).thenReturn(startingFavCount);

        // Set current category as NOT the favorites category
        AppCategory appCategoryMock = mock(AppCategory.class);
        final String currentCategoryName = "OTHER CATEGORY";
        when(appCategoryMock.getName()).thenReturn(currentCategoryName);
        when(selectionModelMock.getSelectedItem()).thenReturn(appCategoryMock);

        // Set treeStore items
        when(treeStoreMock.getAll()).thenReturn(Lists.newArrayList(favoritesCat, appCategoryMock));


        AppFavoritedEvent mockEvent = mock(AppFavoritedEvent.class);
        final App mockApp = mock(App.class);
        when(mockEvent.getApp()).thenReturn(mockApp);
        when(mockApp.isFavorite()).thenReturn(true);

        /*** CALL METHOD UNDER TEST ***/
        spy.onAppFavorited(mockEvent);

        verify(spy).findAppCategoryByName(eq(uut.FAVORITES));

        // Verify count is incremented when app is favorite
        verify(spy).updateAppCategoryAppCount(eq(favoritesCat), eq(startingFavCount + 1));

        // Set app to NOT favorite, and repeat test
        when(mockApp.isFavorite()).thenReturn(false);

        /*** CALL METHOD UNDER TEST ***/
        spy.onAppFavorited(mockEvent);

        // Verify count is incremented when app is favorite
        verify(spy).updateAppCategoryAppCount(eq(favoritesCat), eq(startingFavCount - 1));
    }

    /**
     * Verify that when the current category null i.e when searching, that the favorites category count
     * is updated
     */
    @Test
    public void favoritesCategoryCountUpdated_onAppFavorited_onSearch() {

        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock));
        // Setup fav category
        uut.FAVORITES = "Favorites";
        AppCategory favoritesCat = mock(AppCategory.class);
        when(favoritesCat.getName()).thenReturn(uut.FAVORITES);
        int startingFavCount = 4;
        when(favoritesCat.getAppCount()).thenReturn(startingFavCount);

        // Set current category as null
        AppCategory appCategoryMock = null;
        when(selectionModelMock.getSelectedItem()).thenReturn(appCategoryMock);

        // Set treeStore items
        when(treeStoreMock.getAll()).thenReturn(Lists.newArrayList(favoritesCat));

        AppFavoritedEvent mockEvent = mock(AppFavoritedEvent.class);
        final App mockApp = mock(App.class);
        when(mockEvent.getApp()).thenReturn(mockApp);
        when(mockApp.isFavorite()).thenReturn(true);

        /*** CALL METHOD UNDER TEST ***/
        spy.onAppFavorited(mockEvent);

        verify(spy).findAppCategoryByName(eq(uut.FAVORITES));

        // Verify count is incremented when app is favorite
        verify(spy).updateAppCategoryAppCount(eq(favoritesCat), eq(startingFavCount + 1));

        // Set app to NOT favorite, and repeat test
        when(mockApp.isFavorite()).thenReturn(false);

        /*** CALL METHOD UNDER TEST ***/
        spy.onAppFavorited(mockEvent);

        // Verify count is incremented when app is favorite
        verify(spy).updateAppCategoryAppCount(eq(favoritesCat), eq(startingFavCount - 1));
    }

    /**
     * Verify that the {@link AppDetailsDialog} is properly constructed and shown
     * on App info selection.
     */
    @Test public void detailsDlgShown_onAppInfoSelected() {
        final AppCategoriesPresenterImpl spy = spy(new AppCategoriesPresenterImpl(treeStoreMock,
                                                                                  propsMock,
                                                                                  jsonUtilMock,
                                                                                  eventBusMock,
                                                                                  viewFactoryMock));
        spy.appDetailsDlgAsyncProvider = mockDetailsProvider;
        spy.appService = appServiceMock;
        spy.appUserService = appUserServiceMock;

        // Set up mock event
        AppInfoSelectedEvent eventMock = mock(AppInfoSelectedEvent.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);
        AppCategory catMock1 = mock(AppCategory.class);
        AppCategory catMock2 = mock(AppCategory.class);
        when(catMock1.getId()).thenReturn("id1");
        when(catMock2.getId()).thenReturn("id2");
        final List<AppCategory> appCategories = Lists.newArrayList(catMock1, catMock2);
        when(appMock.getGroups()).thenReturn(appCategories);

        // Set treeStore root items
        when(treeStoreMock.getRootItems()).thenReturn(appCategories);

        /*** CALL METHOD UNDER TEST ***/
        spy.onAppInfoSelected(eventMock);

        verify(mockDetailsProvider).get(detailsCallbackCaptor.capture());

        // Call async success
        AppDetailsDialog mockDlg = mock(AppDetailsDialog.class);
        detailsCallbackCaptor.getValue().onSuccess(mockDlg);

        verify(appUserServiceMock).getAppDetails(eq(appMock), appCallbackCaptor.capture());

        /*** CALL METHOD UNDER TEST ***/
        appCallbackCaptor.getValue().onSuccess(appMock);

        verify(spy).getGroupHierarchy(catMock1);
        verify(spy).getGroupHierarchy(catMock2);

        verify(eventMock, times(1)).getApp();
        verify(appMock, times(1)).getGroups();
        verify(mockDlg).show(eq(appMock),
                             eq(spy.searchRegexPattern),
                             Matchers.<List<List<String>>>any(),
                             eq(spy),
                             eq(spy),
                             eq(spy));

        verifyNoMoreInteractions(appMock);
    }

    @Test public void treeSelectionsClearedAndRegexStored_onAppSearchResultLoad() {
        uut.searchRegexPattern = "initial regex pattern";
        final String TEST_SEARCH_PATTERN = "test pattern";

        AppSearchResultLoadEvent eventMock = mock(AppSearchResultLoadEvent.class);
        when(eventMock.getSearchPattern()).thenReturn(TEST_SEARCH_PATTERN);


        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSearchResultLoad(eventMock);

        verify(eventMock).getSearchPattern();
        verify(selectionModelMock).deselectAll();
    }

    @Test public void userAppsCategoryReselected_onAppUpdated() {
        uut.USER_APPS_GROUP = "user apps group";
        AppCategory userGroupMock = mock(AppCategory.class);
        when(userGroupMock.getName()).thenReturn(uut.USER_APPS_GROUP);
        when(treeStoreMock.getAll()).thenReturn(Lists.newArrayList(userGroupMock));

        // Setup event
        AppSavedEvent eventMock = mock(AppSavedEvent.class);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSaved(eventMock);

        verify(selectionModelMock).deselectAll();
        verify(selectionModelMock).select(eq(userGroupMock), eq(false));
    }

    @Test public void testOnCopyAppSelected() {
        /*** CALL METHOD UNDER TEST ***/
        CopyAppSelected eventMock = mock(CopyAppSelected.class);
        final App mockApp = mock(App.class);
        when(mockApp.getId()).thenReturn("mockAppId");
        when(eventMock.getApps()).thenReturn(Lists.newArrayList(mockApp));
        uut.onCopyAppSelected(eventMock);

        verify(appUserServiceMock).copyApp(eq(mockApp), Matchers.<AsyncCallback<AppTemplate>>any());
    }

    @Test public void testOnCopyWorkflowSelected() {
        /*** CALL METHOD UNDER TEST ***/
        CopyWorkflowSelected eventMock = mock(CopyWorkflowSelected.class);
        final App mockApp = mock(App.class);
        when(mockApp.getId()).thenReturn("mockAppId");
        when(eventMock.getApps()).thenReturn(Lists.newArrayList(mockApp));
        uut.onCopyWorkflowSelected(eventMock);

        verify(appUserServiceMock).copyWorkflow(eq(mockApp.getId()), Matchers.<AsyncCallback<String>>any());
    }


    private void verifyConstructor() {
        verify(viewFactoryMock).create(eq(treeStoreMock), eq(uut));
        verify(treeStoreMock).addSortInfo(Matchers.<Store.StoreSortInfo<AppCategory>>any());

        verify(eventBusMock, times(3)).addHandler(Matchers.<GwtEvent.Type<AppCategoriesPresenterImpl>>any(), eq(uut));
    }

}