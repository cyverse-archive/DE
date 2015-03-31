package org.iplantc.de.admin.apps.client.presenter.categories;

import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.apps.client.events.selection.AddCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.CategorizeAppSelected;
import org.iplantc.de.admin.apps.client.events.selection.DeleteCategorySelected;
import org.iplantc.de.admin.apps.client.events.selection.RenameCategorySelected;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.gin.factory.AppCategoriesViewFactory;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
@RunWith(GwtMockitoTestRunner.class)
public class AdminAppsCategoriesPresenterImplTest {

    @Mock AppServiceFacade appServiceMock;
    @Mock AppAdminServiceFacade adminAppServiceMock;

    @Mock AppCategoriesViewFactory viewFactoryMock;
    @Mock TreeStore<AppCategory> treeStoreMock;
    @Mock AdminCategoriesView.Presenter.Appearance appearanceMock;
    @Mock AppCategoriesView viewMock;
    @Mock Tree<AppCategory, String> treeMock;
    @Mock TreeSelectionModel<AppCategory> selectionModelMock;
    @Mock DEProperties propertiesMock;
    @Mock IplantAnnouncer announcerMock;

    @Captor ArgumentCaptor<AsyncCallback<List<AppCategory>>> appCatListCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<AppCategory>> appCatCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<App>> appCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> voidCallbackCaptor;

    private final String mockBetaCategoryId = "mockBetaCategoryID";
    private final String mockTrashCategoryId = "mockTrashCategoryID";


    private AdminAppsCategoriesPresenterImpl uut;

    @Before public void setUp() {
        when(viewFactoryMock.create(Matchers.<TreeStore<AppCategory>>any(),
                                    Matchers.<AppCategoriesView.AppCategoryHierarchyProvider>any())).thenReturn(viewMock);
        when(viewMock.getTree()).thenReturn(treeMock);
        when(treeMock.getSelectionModel()).thenReturn(selectionModelMock);
        when(treeStoreMock.getRootItems()).thenReturn(Lists.newArrayList(mock(AppCategory.class),
                                                                         mock(AppCategory.class),
                                                                         mock(AppCategory.class)));
        uut = new AdminAppsCategoriesPresenterImpl(viewFactoryMock,
                                                   treeStoreMock);
        uut.appearance = appearanceMock;
        uut.appService = appServiceMock;
        uut.adminAppService = adminAppServiceMock;
        uut.properties = propertiesMock;
        uut.announcer = announcerMock;
        when(propertiesMock.getDefaultTrashAppCategoryId()).thenReturn(mockTrashCategoryId);
        when(propertiesMock.getDefaultBetaCategoryId()).thenReturn(mockBetaCategoryId);
        when(appearanceMock.addCategoryPermissionError()).thenReturn("permission error");
    }

    @Test public void verifyCorrectView_getView() {
        /*** CALL METHOD UNDER TEST ***/
        final AppCategoriesView uutView = uut.getView();

        assertEquals(viewMock, uutView);

        verifyZeroInteractions(appServiceMock,
                               adminAppServiceMock);
    }

    @Test public void verifyViewMaskedAndUnmasked_go() {
        String loadingMaskMock = "mock loading mask";
        when(treeStoreMock.getAll()).thenReturn(Collections.<AppCategory>emptyList());
        when(appearanceMock.getAppCategoriesLoadingMask()).thenReturn(loadingMaskMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.go(null);

        verify(viewMock).mask(eq(loadingMaskMock));
        verify(appearanceMock).getAppCategoriesLoadingMask();
        verify(adminAppServiceMock).getPublicAppCategories(appCatListCallbackCaptor.capture(),
                                                      eq(false));

        /*** CALL METHOD UNDER TEST ***/
        appCatListCallbackCaptor.getValue().onSuccess(Collections.<AppCategory>emptyList());

        verify(viewMock).unmask();
    }


    @Test public void verifyNothingHappensTrashSelected_onAddCategorySelected() {
        AddCategorySelected eventMock = mock(AddCategorySelected.class);
        final AppCategory selectedParentCategoryMock = mock(AppCategory.class);
        when(selectedParentCategoryMock.getId()).thenReturn(mockTrashCategoryId); // Is trash category
        when(selectedParentCategoryMock.getName()).thenReturn("mock name");

        when(selectedParentCategoryMock.getAppCount()).thenReturn(0); // Category has no apps
        when(selectedParentCategoryMock.getCategories()).thenReturn(Collections.<AppCategory>emptyList()); // has no child categories

        when(eventMock.getAppCategories()).thenReturn(Lists.newArrayList(selectedParentCategoryMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAddCategorySelected(eventMock);

        verify(eventMock, times(2)).getAppCategories();

        verifyZeroInteractions(adminAppServiceMock,
                               appServiceMock,
                               treeStoreMock,
                               viewMock);
    }

    @Test public void verifyCategoriesDeselected_onAppSearchResultLoad() {
        AppSearchResultLoadEvent eventMock = mock(AppSearchResultLoadEvent.class);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppSearchResultLoad(eventMock);

        verify(viewMock).getTree();
        verify(treeMock).getSelectionModel();
        verify(selectionModelMock).deselectAll();

        verifyNoMoreInteractions(viewMock,
                                 treeMock,
                                 selectionModelMock);
        verifyZeroInteractions(adminAppServiceMock,
                               appServiceMock,
                               treeStoreMock);

    }

    @Test public void verifyNothingHappensBetaSelected_onAddCategorySelected() {
        AddCategorySelected eventMock = mock(AddCategorySelected.class);
        final AppCategory selectedParentCategoryMock = mock(AppCategory.class);
        when(selectedParentCategoryMock.getId()).thenReturn(mockBetaCategoryId); // Is beta category
        when(selectedParentCategoryMock.getName()).thenReturn("mock name");

        when(selectedParentCategoryMock.getAppCount()).thenReturn(0); // Category has no apps
        when(selectedParentCategoryMock.getCategories()).thenReturn(Collections.<AppCategory>emptyList()); // has no child categories

        when(eventMock.getAppCategories()).thenReturn(Lists.newArrayList(selectedParentCategoryMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAddCategorySelected(eventMock);

        verify(eventMock, times(2)).getAppCategories();

        verifyZeroInteractions(adminAppServiceMock,
                               appServiceMock,
                               treeStoreMock,
                               viewMock);
    }


    /**
     * Verifies that nothing happens when the category is not THE public category, category has
     * no child categories, and has apps.
     */
    @Test public void verifyNothingHappens_notPublicSelected_onAddCategorySelected() {
        AddCategorySelected eventMock = mock(AddCategorySelected.class);
        final AppCategory selectedParentCategoryMock = mock(AppCategory.class);
        when(selectedParentCategoryMock.getId()).thenReturn("someOtherId"); // Is not public
        when(selectedParentCategoryMock.getName()).thenReturn("mock name"); // Is not public

        when(selectedParentCategoryMock.getAppCount()).thenReturn(1); // Category has one app
        when(selectedParentCategoryMock.getCategories()).thenReturn(Collections.<AppCategory>emptyList()); // has no child categories

        when(eventMock.getAppCategories()).thenReturn(Lists.newArrayList(selectedParentCategoryMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAddCategorySelected(eventMock);

        verify(eventMock, times(2)).getAppCategories();

        verifyZeroInteractions(adminAppServiceMock,
                               appServiceMock,
                               treeStoreMock,
                               viewMock);
    }

    @Test public void verifyServiceCalled_onAddCategorySelected() {
        AddCategorySelected eventMock = mock(AddCategorySelected.class);
        final String mockNewCategoryName = "new category name";
        when(eventMock.getNewCategoryName()).thenReturn(mockNewCategoryName);
        final AppCategory selectedParentCategoryMock = mock(AppCategory.class);
        when(selectedParentCategoryMock.getId()).thenReturn("someOtherId"); // Is not public
        when(selectedParentCategoryMock.getName()).thenReturn("mock name"); // Is not public

        when(selectedParentCategoryMock.getAppCount()).thenReturn(0); // Category has no apps
        when(selectedParentCategoryMock.getCategories()).thenReturn(Collections.<AppCategory>emptyList()); // has no child categories

        when(eventMock.getAppCategories()).thenReturn(Lists.newArrayList(selectedParentCategoryMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onAddCategorySelected(eventMock);

        verify(eventMock, times(2)).getAppCategories();
        verify(eventMock).getNewCategoryName();
        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).addCategory(eq(mockNewCategoryName),
                                                eq(selectedParentCategoryMock),
                                                appCatCallbackCaptor.capture());

        /*** CALL METHOD UNDER TEST ***/
        AppCategory resultMock = mock(AppCategory.class);
        appCatCallbackCaptor.getValue().onSuccess(resultMock);
        verify(viewMock).unmask();
        verify(treeStoreMock).add(eq(selectedParentCategoryMock),
                                  eq(resultMock));

        verifyNoMoreInteractions(eventMock,
                                 viewMock,
                                 treeStoreMock,
                                 adminAppServiceMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verifyServiceCalled_onCategorizeAppSelected() {
        AdminAppsCategoriesPresenterImpl spy = spy(new AdminAppsCategoriesPresenterImpl(viewFactoryMock,
                                                                                        treeStoreMock){
            @Override
            void showCategorizeAppDialog(App selectedApp) {
            }
        });
        spy.appearance = appearanceMock;
        spy.appService = appServiceMock;
        spy.adminAppService = adminAppServiceMock;
        spy.properties = propertiesMock;
        spy.announcer = announcerMock;

        CategorizeAppSelected eventMock = mock(CategorizeAppSelected.class);
        App appMock = mock(App.class);
        when(eventMock.getApps()).thenReturn(Lists.newArrayList(appMock));

        /*** CALL METHOD UNDER TEST ***/
        spy.onCategorizeAppSelected(eventMock);

        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).getAppDetails(eq(appMock),
                                                  appCallbackCaptor.capture());

        App resultMock = mock(App.class);
        /*** CALL METHOD UNDER TEST ***/
        appCallbackCaptor.getValue().onSuccess(resultMock);

        verify(spy).showCategorizeAppDialog(eq(resultMock));
        verify(viewMock).unmask();
        verifyNoMoreInteractions(viewMock,
                                 adminAppServiceMock);
        verifyZeroInteractions(treeStoreMock,
                               appServiceMock);
    }

    @Test public void verifyServiceCalled_onDeleteCategorySelected() {
        DeleteCategorySelected eventMock = mock(DeleteCategorySelected.class);
        AppCategory categoryMock = mock(AppCategory.class);
        when(eventMock.getAppCategories()).thenReturn(Lists.newArrayList(categoryMock));

        /*** CALL METHOD UNDER TEST ***/
        uut.onDeleteCategorySelected(eventMock);

        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).deleteAppCategory(eq(categoryMock),
                                                      voidCallbackCaptor.capture());

        /*** CALL METHOD UNDER TEST ***/
        voidCallbackCaptor.getValue().onSuccess(null);
        verify(viewMock).unmask();
        verify(treeStoreMock).remove(eq(categoryMock));

        verifyNoMoreInteractions(adminAppServiceMock,
                                 viewMock);
        verifyZeroInteractions(appServiceMock);
    }

    @Test public void verify_onMoveCategorySelected() {
        // TODO
    }

    @Test public void verifyServiceCalled_onRenameCategorySelected() {
        RenameCategorySelected eventMock = mock(RenameCategorySelected.class);
        AppCategory appCategoryMock = mock(AppCategory.class);
        String newNameMock = "new category name";
        when(eventMock.getAppCategory()).thenReturn(appCategoryMock);
        when(eventMock.getNewCategoryName()).thenReturn(newNameMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onRenameCategorySelected(eventMock);

        verify(viewMock).mask(anyString());
        verify(adminAppServiceMock).renameAppCategory(eq(appCategoryMock),
                                                      eq(newNameMock),
                                                      appCatCallbackCaptor.capture());
        AppCategory resultMock = mock(AppCategory.class);
        when(resultMock.getName()).thenReturn(newNameMock);

        /*** CALL METHOD UNDER TEST ***/
        appCatCallbackCaptor.getValue().onSuccess(resultMock);

        verify(viewMock).unmask();
        verify(appCategoryMock).setName(eq(newNameMock));
        verify(treeStoreMock).update(eq(appCategoryMock));

        verifyNoMoreInteractions(viewMock,
                                 adminAppServiceMock);
        verifyZeroInteractions(appServiceMock);
    }


}