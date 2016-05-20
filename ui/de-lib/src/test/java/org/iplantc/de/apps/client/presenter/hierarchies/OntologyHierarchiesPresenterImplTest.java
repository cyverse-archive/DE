package org.iplantc.de.apps.client.presenter.hierarchies;

import static org.mockito.Matchers.any;
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
import org.iplantc.de.apps.client.gin.factory.OntologyHierarchiesViewFactory;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.OntologyServiceFacade;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
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
    @Mock TabPanel tabPanelMock;
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
    @Mock OntologyHierarchiesPresenterImpl.AppAVUCallback appAvuCallback;
    @Mock TreeStore<OntologyHierarchy> treeStoreMock;
    @Mock OntologyHierarchiesView viewMock;
    @Mock Tree<OntologyHierarchy, String> treeMock;
    @Mock Iterator<Widget> tabPanelIteratorMock;
    @Mock Widget randomWidgetMock;
    @Mock TreeSelectionModel<OntologyHierarchy> treeSelectionModelMock;

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
        when(hierarchyListMock.size()).thenReturn(1);
        when(hierarchyListIterator.hasNext()).thenReturn(true, false);
        when(hierarchyListIterator.next()).thenReturn(hierarchyMock);
        when(ontologyUtilMock.getAllPathsList(hierarchyListMock)).thenReturn(pathListMock);
        when(factoryMock.create(treeStoreMock)).thenReturn(viewMock);
        when(appearanceMock.hierarchyLabelName(hierarchyMock)).thenReturn("string");
        when(viewMock.getTree()).thenReturn(treeMock);
        when(treeMock.getSelectionModel()).thenReturn(treeSelectionModelMock);
        when(tabPanelMock.iterator()).thenReturn(tabPanelIteratorMock);
        when(tabPanelIteratorMock.hasNext()).thenReturn(true, true, false);
        when(tabPanelIteratorMock.next()).thenReturn(treeMock).thenReturn(randomWidgetMock);

        uut = new OntologyHierarchiesPresenterImpl(factoryMock,
                                                   ontologyServiceMock,
                                                   eventBusMock,
                                                   appearanceMock,
                                                   avuFactoryMock);
        uut.ontologyUtil = ontologyUtilMock;
        uut.announcer = announcerMock;
        uut.appDetailsDlgAsyncProvider = appDetailsDialogProviderMock;
        uut.appUserService = appUserServiceMock;
        uut.handlerManager = handlerManagerMock;
        uut.iriToHierarchyMap = iriToHierarchyMapMock;
        uut.searchRegexPattern = "test";
        uut.viewTabPanel = tabPanelMock;
    }


    @Test
    public void testGo() throws Exception {
        uut = new OntologyHierarchiesPresenterImpl(factoryMock,
                                                   ontologyServiceMock,
                                                   eventBusMock,
                                                   appearanceMock,
                                                   avuFactoryMock) {
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
        verify(ontologyServiceMock).getAppHierarchies(hierarchyListCallback.capture());

        hierarchyListCallback.getValue().onSuccess(hierarchyListMock);
        verifyNoMoreInteractions(ontologyServiceMock, ontologyUtilMock, eventBusMock);
    }

    @Test
    public void testOnAppInfoSelected() throws Exception {
        AppInfoSelectedEvent eventMock = mock(AppInfoSelectedEvent.class);
        when(eventMock.getApp()).thenReturn(appMock);
        when(avuListMock.iterator()).thenReturn(avuIteratorMock);

        /** CALL METHOD UNDER TEST **/
        uut.onAppInfoSelected(eventMock);

        verify(appDetailsDialogProviderMock).get(appDetailsDialogCallback.capture());
        appDetailsDialogCallback.getValue().onSuccess(appDetailsDialogMock);

        verify(appUserServiceMock).getAppDetails(eq(appMock), appDetailsCallback.capture());
        appDetailsCallback.getValue().onSuccess(appMock);

        verify(ontologyServiceMock).getAppAVUs(eq(appMock), appAvuCallbackCaptor.capture());
        appAvuCallbackCaptor.getValue().onSuccess(avuListMock);
        verify(ontologyUtilMock).getAllPathsList(Matchers.<List<OntologyHierarchy>>any());
        verify(appDetailsDialogMock).show(eq(appMock),
                                          anyString(),
                                          Matchers.<List<List<String>>> any(),
                                          Matchers.<AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler>any(),
                                          Matchers.<AppRatingSelected.AppRatingSelectedHandler>any(),
                                          Matchers.<AppRatingDeselected.AppRatingDeselectedHandler>any());

        verifyNoMoreInteractions(ontologyServiceMock, appUserServiceMock, ontologyUtilMock);

    }

    @Test
    public void testCreateViewTabs() throws Exception {
        when(hierarchyListMock.iterator()).thenReturn(hierarchyListIterator);

        uut = new OntologyHierarchiesPresenterImpl(factoryMock,
                                                   ontologyServiceMock,
                                                   eventBusMock,
                                                   appearanceMock,
                                                   avuFactoryMock) {
            @Override
            TreeStore<OntologyHierarchy> getTreeStore(OntologyHierarchy hierarchy) {
                return treeStoreMock;
            }
        };
        uut.ontologyUtil = ontologyUtilMock;
        uut.announcer = announcerMock;
        uut.appDetailsDlgAsyncProvider = appDetailsDialogProviderMock;
        uut.appUserService = appUserServiceMock;
        uut.handlerManager = handlerManagerMock;
        uut.iriToHierarchyMap = iriToHierarchyMapMock;
        uut.viewTabPanel = tabPanelMock;

        OntologyHierarchiesPresenterImpl spy = spy(uut);

        /** CALL METHOD UNDER TEST **/
        spy.createViewTabs(hierarchyListMock);
        verify(viewMock).addOntologyHierarchySelectionChangedEventHandler(spy);
        verify(tabPanelMock).add(eq(treeMock), isA(TabItemConfig.class));

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
}
