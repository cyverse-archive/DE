package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeHierarchiesToAppEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteOntologyButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RestoreAppButtonClicked;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppToOntologyHierarchyDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyHierarchyToAppDND;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.shared.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.shared.DEProperties;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.grid.Grid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.List;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class OntologiesPresenterImplTest {

    @Mock DEProperties propertiesMock;
    @Mock IplantAnnouncer announcerMock;
    @Mock OntologiesView viewMock;
    @Mock OntologyServiceFacade serviceFacadeMock;
    @Mock TreeStore<OntologyHierarchy> editorStoreMock;
    @Mock TreeStore<OntologyHierarchy> previewStoreMock;
    @Mock OntologiesView.OntologiesViewAppearance appearanceMock;
    @Mock AdminAppsGridView.Presenter previewGridPresenterMock;
    @Mock AdminAppsGridView.Presenter editorGridPresenterMock;
    @Mock OntologyAutoBeanFactory beanFactoryMock;
    @Mock Grid<App> oldGridMock;
    @Mock AdminAppsGridView previewGridViewMock;
    @Mock AdminAppsGridView editorGridViewMock;
    @Mock AdminCategoriesView.Presenter categoriesPresenterMock;
    @Mock AdminCategoriesView categoriesViewMock;
    @Mock OntologiesViewFactory factoryMock;
    @Mock List<Ontology> listOntologyMock;
    @Mock List<OntologyHierarchy> ontologyHierarchyListMock;
    @Mock List<App> appListMock;
    @Mock AvuAutoBeanFactory avuFactoryMock;
    @Mock AppCategorizeView categorizeViewMock;
    @Mock OntologyHierarchyToAppDND hierarchyToAppDNDMock;
    @Mock AppToOntologyHierarchyDND appToHierarchyDNDMock;
    @Mock OntologyUtil utilMock;
    @Mock List<Avu> avuListMock;
    @Mock AvuList avuListBeanMock;
    @Mock OntologyHierarchy hierarchyMock;
    @Mock AutoBean<AvuList> autoBeanAvuMock;
    @Mock Avu avuMock;
    @Mock List<String> iriListMock;
    @Mock Iterator<String> iriIteratorMock;
    @Mock Ontology activeOntologyMock;
    @Mock Ontology ontologyMock;
    @Mock Iterator<Ontology> ontologyIteratorMock;
    @Mock Iterator<OntologyHierarchy> hierarchyIteratorMock;
    @Mock Iterator<App> appIteratorMock;
    @Mock App appMock;
    @Mock AppAdminServiceFacade adminAppServiceMock;
    @Mock AppServiceFacade appServiceMock;
    @Mock AppSearchRpcProxy proxyMock;
    @Mock PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loaderMock;
    @Mock OntologyHierarchy trashHierarchyMock;

    @Captor ArgumentCaptor<AsyncCallback<List<Ontology>>> asyncCallbackOntologyListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<OntologyHierarchy>>> asyncOntologyHierarchyListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OntologyHierarchy>> asyncOntologyHierarchyCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OntologyVersionDetail>> asyncOntologyDetailCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<App>>> asyncAppListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<App>> asyncAppCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<Avu>>> asyncAvuListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<Void>> asyncVoidCaptor;

    private OntologiesPresenterImpl uut;


    @Before
    public void setUp() {
        when(appearanceMock.successTopicSaved()).thenReturn("success");
        when(appearanceMock.successOperationSaved()).thenReturn("success");
        when(appearanceMock.setActiveOntologySuccess()).thenReturn("success");
        when(appearanceMock.appClassified(anyString(), anyString())).thenReturn("success");
        when(appearanceMock.appClassified(anyString(), Matchers.<List<Avu>>any())).thenReturn("success");
        when(appearanceMock.appAvusCleared(Matchers.<App> any())).thenReturn("success");
        when(appearanceMock.ontologyDeleted(anyString())).thenReturn("success");
        when(appearanceMock.hierarchyDeleted(anyString())).thenReturn("success");
        when(appearanceMock.ontologyAttrMatchingError()).thenReturn("fail");
        when(previewGridViewMock.getGrid()).thenReturn(oldGridMock);
        when(previewGridPresenterMock.getView()).thenReturn(previewGridViewMock);
        when(editorGridPresenterMock.getView()).thenReturn(editorGridViewMock);
        when(appearanceMock.restoreAppSuccessMsgTitle()).thenReturn("success title");
        when(appearanceMock.restoreAppSuccessMsg(anyString(), anyString())).thenReturn("success");
        when(categoriesPresenterMock.getView()).thenReturn(categoriesViewMock);
        when(utilMock.convertHierarchiesToAvus(ontologyHierarchyListMock)).thenReturn(avuListBeanMock);
        when(utilMock.convertHierarchiesToAvus(hierarchyMock)).thenReturn(avuListBeanMock);
        when(utilMock.convertHierarchyToAvu(hierarchyMock)).thenReturn(avuMock);
        when(utilMock.getUnclassifiedParentIri(hierarchyMock)).thenReturn("parent");
        when(utilMock.getHierarchyObject()).thenReturn(trashHierarchyMock);
        when(hierarchyMock.getLabel()).thenReturn("label");
        when(hierarchyMock.getIri()).thenReturn("iri");
        when(autoBeanAvuMock.as()).thenReturn(avuListBeanMock);
        when(avuFactoryMock.getAvuList()).thenReturn(autoBeanAvuMock);
        when(iriListMock.size()).thenReturn(2);
        when(iriIteratorMock.hasNext()).thenReturn(true, true, false);
        when(iriIteratorMock.next()).thenReturn("iri1").thenReturn("iri2");
        when(listOntologyMock.size()).thenReturn(2);
        when(listOntologyMock.iterator()).thenReturn(ontologyIteratorMock);
        when(appListMock.size()).thenReturn(1);
        when(appIteratorMock.hasNext()).thenReturn(true, false);
        when(appIteratorMock.next()).thenReturn(appMock);
        when(appListMock.size()).thenReturn(1);
        when(appListMock.iterator()).thenReturn(appIteratorMock);
        when(appMock.getHierarchies()).thenReturn(ontologyHierarchyListMock);
        when(propertiesMock.getDefaultTrashAppCategoryId()).thenReturn("id");

        when(ontologyIteratorMock.hasNext()).thenReturn(true, true, false);
        when(ontologyIteratorMock.next()).thenReturn(ontologyMock).thenReturn(activeOntologyMock);
        when(ontologyMock.isActive()).thenReturn(false);
        when(activeOntologyMock.isActive()).thenReturn(true);
        when(ontologyHierarchyListMock.size()).thenReturn(2);
        when(ontologyHierarchyListMock.iterator()).thenReturn(hierarchyIteratorMock);
        when(hierarchyIteratorMock.hasNext()).thenReturn(true, true, false);
        when(hierarchyIteratorMock.next()).thenReturn(hierarchyMock).thenReturn(hierarchyMock);
        when(factoryMock.create(Matchers.<TreeStore<OntologyHierarchy>>any(),
                                Matchers.<TreeStore<OntologyHierarchy>>any(),
                                Matchers.<PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>>>any(),
                                isA(AdminAppsGridView.class),
                                isA(AdminAppsGridView.class),
                                isA(OntologyHierarchyToAppDND.class),
                                isA(AppToOntologyHierarchyDND.class))).thenReturn(viewMock);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          appServiceMock,
                                          editorStoreMock,
                                          previewStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock, previewGridPresenterMock,
                                          editorGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            AppSearchRpcProxy getProxy(AppServiceFacade appService) {
                return proxyMock;
            }

            @Override
            PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> getPagingLoader() {
                return loaderMock;
            }

            void displayErrorToAdmin() {}
        };
        uut.announcer = announcerMock;
        uut.properties = propertiesMock;
        uut.ontologyUtil = utilMock;
        uut.adminAppService = adminAppServiceMock;
        uut.proxy = proxyMock;
        uut.loader = loaderMock;
        uut.appService = appServiceMock;

        verifyConstructor();
    }

    void verifyConstructor() {
        verify(previewGridViewMock).addAppSelectionChangedEventHandler(eq(viewMock));
        verify(editorGridViewMock).addAppSelectionChangedEventHandler(eq(viewMock));

        verify(proxyMock).setHasHandlers(eq(viewMock));

        verify(viewMock).addRefreshOntologiesEventHandler(eq(uut));
        verify(viewMock).addSelectOntologyVersionEventHandler(eq(uut));
        verify(viewMock).addSelectOntologyVersionEventHandler(eq(previewGridViewMock));
        verify(viewMock).addSelectOntologyVersionEventHandler(eq(editorGridViewMock));
        verify(viewMock).addHierarchySelectedEventHandler(eq(uut));
        verify(viewMock).addHierarchySelectedEventHandler(eq(editorGridViewMock));
        verify(viewMock).addPreviewHierarchySelectedEventHandler(eq(uut));
        verify(viewMock).addPreviewHierarchySelectedEventHandler(eq(previewGridViewMock));
        verify(viewMock).addSaveOntologyHierarchyEventHandler(eq(uut));
        verify(viewMock).addPublishOntologyClickEventHandler(eq(uut));
        verify(viewMock).addCategorizeButtonClickedEventHandler(eq(uut));
        verify(viewMock).addDeleteHierarchyEventHandler(eq(uut));
        verify(viewMock).addDeleteOntologyButtonClickedEventHandler(eq(uut));
        verify(viewMock).addDeleteAppsSelectedHandler(eq(uut));
        verify(viewMock).addRefreshPreviewButtonClickedHandler(eq(uut));
        verify(viewMock).addRestoreAppButtonClickedHandlers(eq(uut));

        verify(viewMock).addAppSearchResultLoadEventHandler(eq(uut));
        verify(viewMock).addAppSearchResultLoadEventHandler(eq(previewGridPresenterMock));
        verify(viewMock).addAppSearchResultLoadEventHandler(eq(previewGridViewMock));
        verify(viewMock).addBeforeAppSearchEventHandler(eq(previewGridViewMock));
    }

    @Test
    public void testGetOntologies_doNotSelectActiveOntology() {

        /** CALL METHOD UNDER TEST **/
        uut.getOntologies(false);

        verify(editorGridViewMock).clearAndAdd(null);
        verify(previewGridViewMock).clearAndAdd(null);
        verify(viewMock).clearTreeStore(isA(OntologiesView.TreeType.class));
        verify(serviceFacadeMock).getOntologies(asyncCallbackOntologyListCaptor.capture());

        asyncCallbackOntologyListCaptor.getValue().onSuccess(listOntologyMock);
        verify(viewMock).showOntologyVersions(eq(listOntologyMock));
        verify(viewMock).unmaskTree(isA(OntologiesView.TreeType.class));
        verifyNoMoreInteractions(viewMock);
    }

    @Test
    public void testGetOntologies_selectActiveOntology() {

        /** CALL METHOD UNDER TEST **/
        uut.getOntologies(true);

        verify(serviceFacadeMock).getOntologies(asyncCallbackOntologyListCaptor.capture());

        asyncCallbackOntologyListCaptor.getValue().onSuccess(listOntologyMock);
        verify(viewMock).showOntologyVersions(eq(listOntologyMock));
        verify(viewMock).unmaskTree(isA(OntologiesView.TreeType.class));
        verify(ontologyMock).isActive();
        verify(activeOntologyMock).isActive();
        verify(viewMock).selectActiveOntology(activeOntologyMock);
    }

    @Test
    public void testCategorizeHierarchiesToApp() {
        App appMock = mock(App.class);
        CategorizeHierarchiesToAppEvent eventMock = mock(CategorizeHierarchiesToAppEvent.class);
        when(eventMock.getTargetApp()).thenReturn(appMock);
        when(eventMock.getSelectedHierarchies()).thenReturn(ontologyHierarchyListMock);

        /** CALL METHOD UNDER TEST **/
        uut.categorizeHierarchiesToApp(eventMock);
        verify(serviceFacadeMock).setAppAVUs(eq(appMock), eq(avuListBeanMock), asyncAvuListCaptor.capture());

        asyncAvuListCaptor.getValue().onSuccess(avuListMock);
        verify(announcerMock).schedule(any(SuccessAnnouncementConfig.class));
    }

    @Test
    public void testHierarchyToDNDToApp_Classified() {
        App targetAppMock = mock(App.class);
        when(utilMock.isUnclassified(hierarchyMock)).thenReturn(false);
        when(targetAppMock.getName()).thenReturn("name");

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          appServiceMock,
                                          editorStoreMock,
                                          previewStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock, previewGridPresenterMock,
                                          editorGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            boolean previewTreeHasHierarchy(OntologyHierarchy hierarchy) {
                return false;
            }

            @Override
            void getFilteredOntologyHierarchies(String version, List<OntologyHierarchy> result) {
            }

            @Override
            public Ontology getSelectedOntology() {
                return ontologyMock;
            }
        };
        uut.announcer = announcerMock;
        uut.properties = propertiesMock;
        uut.ontologyUtil = utilMock;


        /** CALL METHOD UNDER TEST **/
        uut.hierarchyDNDtoApp(hierarchyMock, targetAppMock);

        verify(serviceFacadeMock).addAVUsToApp(eq(targetAppMock), eq(avuListBeanMock), asyncAvuListCaptor.capture());

        asyncAvuListCaptor.getValue().onSuccess(avuListMock);
        verify(announcerMock).schedule(any(SuccessAnnouncementConfig.class));
    }

    @Test
    public void testHierarchyToDNDToApp_UnClassified() {
        App targetAppMock = mock(App.class);
        when(utilMock.isUnclassified(hierarchyMock)).thenReturn(true);
        when(targetAppMock.getName()).thenReturn("name");

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          appServiceMock,
                                          editorStoreMock, previewStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock, previewGridPresenterMock,
                                          editorGridPresenterMock,
                                          categorizeViewMock){
            @Override
            void clearAvus(App targetApp) {}
        };
        uut.announcer = announcerMock;
        uut.properties = propertiesMock;
        uut.ontologyUtil = utilMock;

        /** CALL METHOD UNDER TEST **/
        uut.hierarchyDNDtoApp(hierarchyMock, targetAppMock);

        verifyZeroInteractions(serviceFacadeMock);
    }

    @Test
    public void testClearAvus() {
        App targetApp = mock(App.class);
        /** CALL METHOD UNDER TEST **/
        uut.clearAvus(targetApp);

        verify(serviceFacadeMock).setAppAVUs(eq(targetApp), eq(avuListBeanMock), asyncAvuListCaptor.capture());

        asyncAvuListCaptor.getValue().onSuccess(avuListMock);
        verify(announcerMock).schedule(any(SuccessAnnouncementConfig.class));


    }

    @Test
    public void testOnSelectOntologyVersion_noResults() {
        SelectOntologyVersionEvent eventMock = mock(SelectOntologyVersionEvent.class);
        Ontology ontologyMock = mock(Ontology.class);

        when(ontologyMock.getVersion()).thenReturn("version");
        when(eventMock.getSelectedOntology()).thenReturn(ontologyMock);
        when(ontologyHierarchyListMock.size()).thenReturn(0);

        /** CALL METHOD UNDER TEST **/
        uut.onSelectOntologyVersion(eventMock);

        verify(viewMock).clearTreeStore(isA(OntologiesView.TreeType.class));

        verify(serviceFacadeMock).getOntologyHierarchies(eq(eventMock.getSelectedOntology()
                                                                     .getVersion()),
                                                         asyncOntologyHierarchyListCaptor.capture());

        asyncOntologyHierarchyListCaptor.getValue().onSuccess(ontologyHierarchyListMock);
        verify(viewMock).showEmptyTreePanel();
    }

    @Test
    public void testOnSelectOntologyVersion_withResults() {
        SelectOntologyVersionEvent eventMock = mock(SelectOntologyVersionEvent.class);
        Ontology ontologyMock = mock(Ontology.class);

        when(ontologyMock.getVersion()).thenReturn("version");
        when(eventMock.getSelectedOntology()).thenReturn(ontologyMock);
        when(ontologyHierarchyListMock.size()).thenReturn(1);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          appServiceMock,
                                          editorStoreMock,
                                          previewStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock, previewGridPresenterMock,
                                          editorGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            void addHierarchies(OntologiesView.TreeType type,
                                OntologyHierarchy parent,
                                List<OntologyHierarchy> children) {
            }

            @Override
            void displayErrorToAdmin() {
            }

            @Override
            void addTrashCategory() {
            }
        };

        /** CALL METHOD UNDER TEST **/
        uut.onSelectOntologyVersion(eventMock);

        verify(viewMock).clearTreeStore(isA(OntologiesView.TreeType.class));

        verify(serviceFacadeMock).getOntologyHierarchies(eq(eventMock.getSelectedOntology()
                                                                     .getVersion()),
                                                         asyncOntologyHierarchyListCaptor.capture());

        asyncOntologyHierarchyListCaptor.getValue().onSuccess(ontologyHierarchyListMock);
        verify(viewMock).showTreePanel();
    }

    @Test
    public void testOnSaveOntologyHierarchy() {
        SaveOntologyHierarchyEvent eventMock = mock(SaveOntologyHierarchyEvent.class);
        Ontology ontologyMock = mock(Ontology.class);
        OntologyHierarchy ontologyHierarchyMock = mock(OntologyHierarchy.class);

        when(ontologyMock.getVersion()).thenReturn("version");
        when(eventMock.getOntology()).thenReturn(ontologyMock);
        when(eventMock.getIris()).thenReturn(iriListMock);
        when(iriListMock.iterator()).thenReturn(iriIteratorMock);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          appServiceMock,
                                          editorStoreMock,
                                          previewStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock, previewGridPresenterMock,
                                          editorGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            void addHierarchies(OntologiesView.TreeType type,
                                OntologyHierarchy parent,
                                List<OntologyHierarchy> children) {
            }

            @Override
            boolean isValidHierarchy(OntologyHierarchy result) {
                return true;
            }

            @Override
            void displayErrorToAdmin() {
            }
        };
        uut.properties = propertiesMock;
        uut.announcer = announcerMock;
        uut.ontologyUtil = utilMock;

        /** CALL METHOD UNDER TEST **/
        uut.onSaveOntologyHierarchy(eventMock);
        verify(serviceFacadeMock, times(2)).saveOntologyHierarchy(eq(eventMock.getOntology().getVersion()),
                                                        anyString(),
                                                        asyncOntologyHierarchyCaptor.capture());

        asyncOntologyHierarchyCaptor.getValue().onSuccess(ontologyHierarchyMock);

        verify(serviceFacadeMock).getOntologyHierarchies(anyString(), asyncOntologyHierarchyListCaptor.capture());

        asyncOntologyHierarchyListCaptor.getValue().onSuccess(ontologyHierarchyListMock);

        verify(viewMock, times(2)).clearTreeStore(isA(OntologiesView.TreeType.class));
        verify(utilMock).createIriToAttrMap(eq(ontologyHierarchyListMock));
        verify(viewMock, times(5)).maskTree(isA(OntologiesView.TreeType.class));
        verify(viewMock).unmaskTree(isA(OntologiesView.TreeType.class));
        verify(viewMock).updateButtonStatus();

        verify(viewMock).showTreePanel();

    }

    @Test
    public void testOnPublishOntologyClick() {
        OntologyVersionDetail resultMock = mock(OntologyVersionDetail.class);
        PublishOntologyClickEvent eventMock = mock(PublishOntologyClickEvent.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(ontologyMock.getVersion()).thenReturn("version");
        when(eventMock.getNewActiveOntology()).thenReturn(ontologyMock);

        /** CALL METHOD UNDER TEST **/
        uut.onPublishOntologyClick(eventMock);

        verify(serviceFacadeMock).setActiveOntologyVersion(eq(eventMock.getNewActiveOntology().getVersion()), asyncOntologyDetailCaptor.capture());

        asyncOntologyDetailCaptor.getValue().onSuccess(resultMock);
    }

    @Test
    public void testOnHierarchySelected() {
        OntologiesPresenterImpl spy = spy(uut);

        HierarchySelectedEvent eventMock = mock(HierarchySelectedEvent.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(hierarchyMock.getIri()).thenReturn("iri");
        when(eventMock.getHierarchy()).thenReturn(hierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);

        /** CALL METHOD UNDER TEST **/
        spy.onHierarchySelected(eventMock);

        verify(spy).getAppsByHierarchy(eq(editorGridViewMock), eq(hierarchyMock), eq(ontologyMock));
    }

    @Test
    public void testGetAppsByHierarchy_classified() {
        HierarchySelectedEvent eventMock = mock(HierarchySelectedEvent.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(hierarchyMock.getIri()).thenReturn("iri");
        when(utilMock.getAttr(hierarchyMock)).thenReturn("attr");
        when(eventMock.getHierarchy()).thenReturn(hierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);
        when(utilMock.isUnclassified(hierarchyMock)).thenReturn(false);
        when(ontologyMock.getVersion()).thenReturn("version");

        uut.getAppsByHierarchy(editorGridViewMock, hierarchyMock, ontologyMock);

        verify(serviceFacadeMock).getAppsByHierarchy(eq(ontologyMock.getVersion()),
                                                     anyString(),
                                                     anyString(),
                                                     asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);
        verify(editorGridViewMock).clearAndAdd(appListMock);
        verify(editorGridViewMock).unmask();

    }


    @Test
    public void testGetAppsByHierarchy_unclassified() {
        Ontology ontologyMock = mock(Ontology.class);
        when(hierarchyMock.getIri()).thenReturn("iri");
        when(utilMock.isUnclassified(hierarchyMock)).thenReturn(true);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          appServiceMock,
                                          editorStoreMock,
                                          previewStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock, previewGridPresenterMock,
                                          editorGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            void getUnclassifiedApps(AdminAppsGridView gridView,
                                     OntologyHierarchy hierarchy,
                                     Ontology editedOntology) {
            }
        };
        uut.announcer = announcerMock;
        uut.properties = propertiesMock;
        uut.ontologyUtil = utilMock;

        /** CALL METHOD UNDER TEST **/
        uut.getAppsByHierarchy(editorGridViewMock, hierarchyMock, ontologyMock);
        //Testing the unclassifiedApps method separately
        verifyZeroInteractions(serviceFacadeMock);
    }


    @Test
    public void testGetUnclassifiedApps() {
        Ontology ontologyMock = mock(Ontology.class);

        when(hierarchyMock.getIri()).thenReturn("iri");
        when(ontologyMock.getVersion()).thenReturn("version");

        /** CALL METHOD UNDER TEST **/
        uut.getUnclassifiedApps(editorGridViewMock, hierarchyMock, ontologyMock);

        verify(editorGridViewMock).mask(anyString());
        verify(serviceFacadeMock).getUnclassifiedApps(eq(ontologyMock.getVersion()), anyString(), eq(avuMock), asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);

        verify(editorGridViewMock).clearAndAdd(appListMock);
        verify(editorGridViewMock).unmask();
    }

    @Test
    public void testOnDeleteOntologyButtonClicked() {
        DeleteOntologyButtonClickedEvent eventMock = mock(DeleteOntologyButtonClickedEvent.class);
        when(eventMock.getOntologyVersion()).thenReturn("version");

        OntologiesPresenterImpl spy = spy(uut);

        /** CALL METHOD UNDER TEST **/
        spy.onDeleteOntologyButtonClicked(eventMock);

        verify(serviceFacadeMock).deleteOntology(anyString(), asyncVoidCaptor.capture());

        asyncVoidCaptor.getValue().onSuccess(null);

        verify(announcerMock).schedule(any(SuccessAnnouncementConfig.class));
        verify(spy).getOntologies(eq(false));
    }

    @Test
    public void testOnDeleteHierarchy() {
        DeleteHierarchyEvent eventMock = mock(DeleteHierarchyEvent.class);
        when(eventMock.getDeletedHierarchy()).thenReturn(hierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);
        when(ontologyMock.getIri()).thenReturn("iri");

        OntologiesPresenterImpl spy = spy(uut);

        /** CALL METHOD UNDER TEST **/
        spy.onDeleteHierarchy(eventMock);
        verify(serviceFacadeMock).deleteRootHierarchy(anyString(), anyString(), asyncOntologyHierarchyListCaptor.capture());

        asyncOntologyHierarchyListCaptor.getValue().onSuccess(ontologyHierarchyListMock);
        verify(announcerMock).schedule(any(SuccessAnnouncementConfig.class));
        verify(spy).getOntologyHierarchies(anyString());

    }

    @Test
    public void testOnDeleteAppsSelected() {
        DeleteAppsSelected eventMock = mock(DeleteAppsSelected.class);
        when(eventMock.getAppsToBeDeleted()).thenReturn(appListMock);

        /** CALL METHOD UNDER TEST **/
        uut.onDeleteAppsSelected(eventMock);

        verify(viewMock).maskGrids(eq(appearanceMock.loadingMask()));
        verify(adminAppServiceMock).deleteApp(eq(appMock), asyncVoidCaptor.capture());

        asyncVoidCaptor.getValue().onSuccess(null);

        verify(viewMock).removeApp(appMock);
        verify(viewMock).unmaskGrids();
    }

    @Test
    public void testGetFilteredOntologyHierarchies() {

        when(utilMock.getAttr(hierarchyMock)).thenReturn("attr");

        /** CALL METHOD UNDER TEST **/
        uut.getFilteredOntologyHierarchies("version", ontologyHierarchyListMock);

        verify(viewMock).clearTreeStore(isA(OntologiesView.TreeType.class));
        verify(viewMock, times(2)).maskTree(isA(OntologiesView.TreeType.class));

        verify(serviceFacadeMock, times(2)).getFilteredOntologyHierarchy(anyString(),
                                                                         anyString(),
                                                                         anyString(),
                                                                         asyncOntologyHierarchyCaptor.capture());

        asyncOntologyHierarchyCaptor.getValue().onSuccess(hierarchyMock);

        verify(viewMock).reSortTree(isA(OntologiesView.TreeType.class));
        verify(viewMock).unmaskTree(isA(OntologiesView.TreeType.class));

    }

    public void testOnRestoreAppButtonClicked() {
        RestoreAppButtonClicked eventMock = mock(RestoreAppButtonClicked.class);
        when(eventMock.getApp()).thenReturn(appMock);
        when(appMock.isDeleted()).thenReturn(true);

        /** CALL METHOD UNDER TEST **/
        uut.onRestoreAppButtonClicked(eventMock);

        verify(viewMock).maskGrids(eq(appearanceMock.loadingMask()));
        verify(adminAppServiceMock).restoreApp(eq(appMock), asyncAppCaptor.capture());

        asyncAppCaptor.getValue().onSuccess(appMock);
        verify(editorGridViewMock).removeApp(appMock);
        verify(hierarchyMock, times(2)).getLabel();
        verify(announcerMock).schedule(isA(SuccessAnnouncementConfig.class));
    }

    @Test
    public void testGetTrashItems() {
        /** CALL METHOD UNDER TEST **/
        uut.getTrashItems();

        verify(viewMock).maskGrids(eq(appearanceMock.loadingMask()));
        verify(appServiceMock).getApps(anyString(), asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);
        verify(editorGridViewMock).clearAndAdd(eq(appListMock));
        verify(viewMock).unmaskGrids();
    }
}
