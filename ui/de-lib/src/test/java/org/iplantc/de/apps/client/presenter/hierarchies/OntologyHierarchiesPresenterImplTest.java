package org.iplantc.de.apps.client.presenter.hierarchies;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.DetailsCategoryClicked;
import org.iplantc.de.apps.client.events.selection.DetailsHierarchyClicked;
import org.iplantc.de.apps.client.gin.factory.OntologyHierarchiesViewFactory;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.OntologyServiceFacade;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.widgets.DETabPanel;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class OntologyHierarchiesPresenterImplTest {

    @Mock IplantAnnouncer announcerMock;
    @Mock OntologyUtil ontologyUtilMock;
    @Mock AsyncProvider<AppDetailsDialog> appDetailsDialogProviderMock;
    @Mock AppUserServiceFacade appUserServiceMock;
    @Mock DETabPanel tabPanelMock;
    @Mock OntologyServiceFacade ontologyServiceMock;
    @Mock OntologyHierarchiesView.OntologyHierarchiesAppearance appearanceMock;
    @Mock EventBus eventBusMock;
    @Mock AvuAutoBeanFactory avuFactoryMock;
    @Mock HandlerManager handlerManagerMock;
    @Mock Map<String, List<OntologyHierarchy>> iriToHierarchyMapMock;
    @Mock OntologyHierarchiesViewFactory factoryMock;
    @Mock OntologyHierarchy hierarchyMock;
    @Mock List<OntologyHierarchy> hierarchyListMock;
    @Mock App appMock;
    @Mock Avu avuMock;
    @Mock AppDetailsDialog appDetailsDialogMock;
    @Mock List<Avu> avuListMock;
    @Mock Iterator<Avu> avuIteratorMock;
    @Mock Iterator<OntologyHierarchy> hierarchyListIterator;
    @Mock List<List<String>> pathListMock;
    @Mock TreeStore<OntologyHierarchy> hierarchyTreeStoreMock;
    @Mock TreeStore<AppCategory> categoryTreeStoreMock;
    @Mock OntologyHierarchiesView viewMock;
    @Mock Tree<OntologyHierarchy, String> hierarchyTreeMock;
    @Mock Tree<AppCategory, String> categoryTreeMock;
    @Mock Iterator<Widget> tabPanelIteratorMock;
    @Mock Widget randomWidgetMock;
    @Mock TreeSelectionModel<OntologyHierarchy> treeSelectionModelMock;
    @Mock TreeSelectionModel<AppCategory> categoryTreeSelectionModelMock;
    @Mock AppCategory appCategoryMock;
    @Mock List<AppCategory> appCategoryListMock;
    @Mock List<OntologyHierarchy> unclassifiedHierarchiesMock;

    @Captor ArgumentCaptor<AsyncCallback<List<OntologyHierarchy>>> hierarchyListCallback;
    @Captor ArgumentCaptor<AsyncCallback<AppDetailsDialog>> appDetailsDialogCallback;
    @Captor ArgumentCaptor<AsyncCallback<App>> appDetailsCallback;
    @Captor ArgumentCaptor<AsyncCallback<List<Avu>>> appAvuCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> voidCallbackCaptor;


    private OntologyHierarchiesPresenterImpl uut;

    @Before
    public void setUp() {
        when(avuListMock.size()).thenReturn(1);
        when(avuIteratorMock.hasNext()).thenReturn(true, false);
        when(avuIteratorMock.next()).thenReturn(avuMock);
        when(hierarchyMock.getLabel()).thenReturn("string");
        when(hierarchyMock.getLabel().toLowerCase()).thenReturn("string");
        when(hierarchyListMock.iterator()).thenReturn(hierarchyListIterator);
        when(hierarchyListMock.size()).thenReturn(1);
        when(hierarchyListIterator.hasNext()).thenReturn(true, false);
        when(hierarchyListIterator.next()).thenReturn(hierarchyMock);
        when(ontologyUtilMock.getAllPathsList(hierarchyListMock)).thenReturn(pathListMock);
        when(ontologyUtilMock.getOrCreateHierarchyPathTag(hierarchyMock)).thenReturn("id");
        when(factoryMock.create(hierarchyTreeStoreMock)).thenReturn(viewMock);
        when(appearanceMock.hierarchyLabelName(hierarchyMock)).thenReturn("string");
        when(viewMock.asWidget()).thenReturn(randomWidgetMock);
        when(viewMock.getTree()).thenReturn(hierarchyTreeMock);
        when(hierarchyTreeMock.getSelectionModel()).thenReturn(treeSelectionModelMock);
        when(hierarchyTreeMock.getStore()).thenReturn(hierarchyTreeStoreMock);
        when(hierarchyTreeStoreMock.findModelWithKey(anyString())).thenReturn(hierarchyMock);
        when(tabPanelMock.getWidgetCount()).thenReturn(2);
        when(tabPanelMock.iterator()).thenReturn(tabPanelIteratorMock);
        when(tabPanelMock.getWidget(anyInt())).thenReturn(hierarchyTreeMock);
        when(tabPanelIteratorMock.hasNext()).thenReturn(true, true, false);
        when(tabPanelIteratorMock.next()).thenReturn(hierarchyTreeMock).thenReturn(randomWidgetMock);
        when(appMock.getHierarchies()).thenReturn(hierarchyListMock);
        when(categoryTreeStoreMock.findModelWithKey(anyString())).thenReturn(appCategoryMock);
        when(categoryTreeMock.getStore()).thenReturn(categoryTreeStoreMock);
        when(categoryTreeMock.getSelectionModel()).thenReturn(categoryTreeSelectionModelMock);
        when(categoryTreeMock.getId()).thenReturn("id");
        when(unclassifiedHierarchiesMock.size()).thenReturn(2);
        when(unclassifiedHierarchiesMock.iterator()).thenReturn(hierarchyListIterator);

        uut = new OntologyHierarchiesPresenterImpl(factoryMock,
                                                   ontologyServiceMock,
                                                   eventBusMock,
                                                   appearanceMock) {
            @Override
            TreeStore<OntologyHierarchy> getHierarchyTreeStore() {
                return hierarchyTreeStoreMock;
            }

            @Override
            TreeStore<AppCategory> getCategoryTreeStore() {
                return categoryTreeStoreMock;
            }
        };
        uut.ontologyUtil = ontologyUtilMock;
        uut.announcer = announcerMock;
        uut.appDetailsDlgAsyncProvider = appDetailsDialogProviderMock;
        uut.appUserService = appUserServiceMock;
        uut.handlerManager = handlerManagerMock;
        uut.iriToHierarchyMap = iriToHierarchyMapMock;
        uut.searchRegexPattern = "test";
        uut.viewTabPanel = tabPanelMock;
        uut.unclassifiedHierarchies = unclassifiedHierarchiesMock;
    }


    @Test
    public void testGo() throws Exception {
        uut = new OntologyHierarchiesPresenterImpl(factoryMock,
                                                   ontologyServiceMock,
                                                   eventBusMock,
                                                   appearanceMock) {
            @Override
            void createViewTabs(List<OntologyHierarchy> results) {
            }
        };
        uut.ontologyUtil = ontologyUtilMock;
        uut.announcer = announcerMock;
        uut.appDetailsDlgAsyncProvider = appDetailsDialogProviderMock;
        uut.appUserService = appUserServiceMock;
        uut.handlerManager = handlerManagerMock;
        uut.iriToHierarchyMap = iriToHierarchyMapMock;

        /** CALL METHOD UNDER TEST **/
        uut.go(tabPanelMock);
        verify(ontologyServiceMock).getRootHierarchies(hierarchyListCallback.capture());

        hierarchyListCallback.getValue().onSuccess(hierarchyListMock);
        verifyNoMoreInteractions(ontologyServiceMock, ontologyUtilMock, eventBusMock);
    }

    @Test
    public void testOnAppInfoSelected_HPCApp() throws Exception {
        AppInfoSelectedEvent eventMock = mock(AppInfoSelectedEvent.class);
        when(eventMock.getApp()).thenReturn(appMock);
        when(appMock.getAppType()).thenReturn(App.EXTERNAL_APP);
        when(appMock.getGroups()).thenReturn(appCategoryListMock);
        when(appMock.getHierarchies()).thenReturn(hierarchyListMock);
        when(avuListMock.iterator()).thenReturn(avuIteratorMock);

        /** CALL METHOD UNDER TEST **/
        uut.onAppInfoSelected(eventMock);

        verify(appDetailsDialogProviderMock).get(appDetailsDialogCallback.capture());
        appDetailsDialogCallback.getValue().onSuccess(appDetailsDialogMock);

        verify(appUserServiceMock).getAppDetails(eq(appMock), appDetailsCallback.capture());
        appDetailsCallback.getValue().onSuccess(appMock);

        verify(categoryTreeStoreMock).add(appCategoryListMock);


        verify(appDetailsDialogMock).show(eq(appMock),
                                          anyString(),
                                          eq(hierarchyTreeStoreMock),
                                          eq(categoryTreeStoreMock),
                                          Matchers.<AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler>any(),
                                          Matchers.<AppRatingSelected.AppRatingSelectedHandler>any(),
                                          Matchers.<AppRatingDeselected.AppRatingDeselectedHandler>any(),
                                          Matchers.<DetailsHierarchyClicked.DetailsHierarchyClickedHandler>any(),
                                          Matchers.<DetailsCategoryClicked.DetailsCategoryClickedHandler>any());

        verifyNoMoreInteractions(ontologyServiceMock, appUserServiceMock, ontologyUtilMock);

    }

    @Test
    public void testOnAppInfoSelected_UnclassifiedApp() throws Exception {
        AppInfoSelectedEvent eventMock = mock(AppInfoSelectedEvent.class);
        when(eventMock.getApp()).thenReturn(appMock);
        when(appMock.getAppType()).thenReturn("DE");
        when(appMock.getGroups()).thenReturn(appCategoryListMock);
        when(appMock.getHierarchies()).thenReturn(hierarchyListMock);
        when(avuListMock.iterator()).thenReturn(avuIteratorMock);
        when(hierarchyListMock.size()).thenReturn(0);

        /** CALL METHOD UNDER TEST **/
        uut.onAppInfoSelected(eventMock);

        verify(appDetailsDialogProviderMock).get(appDetailsDialogCallback.capture());
        appDetailsDialogCallback.getValue().onSuccess(appDetailsDialogMock);

        verify(appUserServiceMock).getAppDetails(eq(appMock), appDetailsCallback.capture());
        appDetailsCallback.getValue().onSuccess(appMock);

        verifyZeroInteractions(categoryTreeStoreMock);

        verify(appDetailsDialogMock).show(eq(appMock),
                                          anyString(),
                                          eq(hierarchyTreeStoreMock),
                                          eq(categoryTreeStoreMock),
                                          Matchers.<AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler>any(),
                                          Matchers.<AppRatingSelected.AppRatingSelectedHandler>any(),
                                          Matchers.<AppRatingDeselected.AppRatingDeselectedHandler>any(),
                                          Matchers.<DetailsHierarchyClicked.DetailsHierarchyClickedHandler>any(),
                                          Matchers.<DetailsCategoryClicked.DetailsCategoryClickedHandler>any());

        verifyNoMoreInteractions(ontologyServiceMock, appUserServiceMock, ontologyUtilMock);
    }

    @Test
    public void testOnAppInfoSelected_ClassifiedApp() throws Exception {
        AppInfoSelectedEvent eventMock = mock(AppInfoSelectedEvent.class);
        when(eventMock.getApp()).thenReturn(appMock);
        when(appMock.getAppType()).thenReturn("DE");
        when(avuListMock.iterator()).thenReturn(avuIteratorMock);

        /** CALL METHOD UNDER TEST **/
        uut.onAppInfoSelected(eventMock);

        verify(appDetailsDialogProviderMock).get(appDetailsDialogCallback.capture());
        appDetailsDialogCallback.getValue().onSuccess(appDetailsDialogMock);

        verify(appUserServiceMock).getAppDetails(eq(appMock), appDetailsCallback.capture());
        appDetailsCallback.getValue().onSuccess(appMock);

        verifyZeroInteractions(categoryTreeStoreMock);

        verify(appDetailsDialogMock).show(eq(appMock),
                                          anyString(),
                                          eq(hierarchyTreeStoreMock),
                                          eq(categoryTreeStoreMock),
                                          Matchers.<AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler>any(),
                                          Matchers.<AppRatingSelected.AppRatingSelectedHandler>any(),
                                          Matchers.<AppRatingDeselected.AppRatingDeselectedHandler>any(),
                                          Matchers.<DetailsHierarchyClicked.DetailsHierarchyClickedHandler>any(),
                                          Matchers.<DetailsCategoryClicked.DetailsCategoryClickedHandler>any());

        verifyNoMoreInteractions(ontologyServiceMock, appUserServiceMock, ontologyUtilMock);

    }

    @Test
    public void testCreateViewTabs() throws Exception {
        when(hierarchyListMock.iterator()).thenReturn(hierarchyListIterator);

        OntologyHierarchiesPresenterImpl spy = spy(uut);

        /** CALL METHOD UNDER TEST **/
        spy.createViewTabs(hierarchyListMock);
        verify(viewMock).addOntologyHierarchySelectionChangedEventHandler(spy);
        verify(tabPanelMock).insert(eq(hierarchyTreeMock),
                                    anyInt(),
                                    isA(TabItemConfig.class),
                                    anyString());

    }

    @Test
    public void testOnAppSearchResultLoad() throws Exception {
        AppSearchResultLoadEvent evenMock = mock(AppSearchResultLoadEvent.class);
        when(evenMock.getSearchPattern()).thenReturn("string");

        uut.onAppSearchResultLoad(evenMock);
        verify(treeSelectionModelMock, times(1)).deselectAll();
    }

    @Test
    public void testOnAppRatingDeselected() throws Exception {
        AppRatingDeselected eventMock = mock(AppRatingDeselected.class);
        App appMock = mock(App.class);
        when(eventMock.getApp()).thenReturn(appMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onAppRatingDeselected(eventMock);

        verify(appUserServiceMock).deleteRating(eq(appMock), Matchers.<AsyncCallback<AppFeedback>>any());

        verifyNoMoreInteractions(appUserServiceMock);
        verifyZeroInteractions(ontologyServiceMock);
    }

    @Test
    public void testOnAppRatingSelected() throws Exception {
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
        verifyZeroInteractions(ontologyServiceMock);
    }

    @Test
    public void testOnAppFavoriteSelected() throws Exception {
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
        verifyZeroInteractions(ontologyServiceMock);
    }

    @Test
    public void testOnDetailsHierarchyClicked() {
        DetailsHierarchyClicked eventMock = mock(DetailsHierarchyClicked.class);
        when(eventMock.getHierarchy()).thenReturn(hierarchyMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onDetailsHierarchyClicked(eventMock);

        verify(tabPanelMock).getWidget(anyInt());
        verify(hierarchyTreeStoreMock).findModelWithKey(anyString());
        verify(tabPanelMock).setActiveWidget(eq(hierarchyTreeMock));
        verify(treeSelectionModelMock).select(eq(hierarchyMock), anyBoolean());
    }

    @Test
    public void testOnDetailsCategoryClicked() {
        DetailsCategoryClicked eventMock = mock(DetailsCategoryClicked.class);
        when(eventMock.getCategory()).thenReturn(appCategoryMock);
        when(tabPanelMock.getWidget(anyInt())).thenReturn(categoryTreeMock);

        /*** CALL METHOD UNDER TEST ***/
        uut.onDetailsCategoryClicked(eventMock);

        verify(tabPanelMock).getWidget(anyInt());
        verify(categoryTreeStoreMock).findModelWithKey(anyString());
        verify(tabPanelMock).setActiveWidget(eq(categoryTreeMock));
        verify(categoryTreeSelectionModelMock).select(eq(appCategoryMock), anyBoolean());
    }
}
