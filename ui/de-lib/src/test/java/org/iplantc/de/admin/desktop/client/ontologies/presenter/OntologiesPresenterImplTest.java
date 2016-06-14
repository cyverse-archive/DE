package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeHierarchiesToAppEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppToOntologyHierarchyDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyHierarchyToAppDND;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.data.shared.TreeStore;
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
    @Mock TreeStore<OntologyHierarchy> treeStoreMock;
    @Mock OntologiesView.OntologiesViewAppearance appearanceMock;
    @Mock AdminAppsGridView.Presenter oldGridPresenterMock;
    @Mock AdminAppsGridView.Presenter newGridPresenterMock;
    @Mock OntologyAutoBeanFactory beanFactoryMock;
    @Mock Grid<App> oldGridMock;
    @Mock AdminAppsGridView oldGridViewMock;
    @Mock AdminAppsGridView newGridViewMock;
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


    @Captor ArgumentCaptor<AsyncCallback<List<Ontology>>> asyncCallbackOntologyListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<OntologyHierarchy>>> asyncOntologyHierarchyListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OntologyHierarchy>> asyncOntologyHierarchyCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OntologyVersionDetail>> asyncOntologyDetailCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<App>>> asyncAppListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<Avu>>> asyncAvuListCaptor;

    private OntologiesPresenterImpl uut;


    @Before
    public void setUp() {
        when(appearanceMock.successTopicSaved()).thenReturn("success");
        when(appearanceMock.successOperationSaved()).thenReturn("success");
        when(appearanceMock.setActiveOntologySuccess()).thenReturn("success");
        when(appearanceMock.appClassified(anyString(), anyString())).thenReturn("success");
        when(appearanceMock.appClassified(anyString(), Matchers.<List<Avu>>any())).thenReturn("success");
        when(appearanceMock.appAvusCleared(Matchers.<App> any())).thenReturn("success");
        when(oldGridViewMock.getGrid()).thenReturn(oldGridMock);
        when(oldGridPresenterMock.getView()).thenReturn(oldGridViewMock);
        when(newGridPresenterMock.getView()).thenReturn(newGridViewMock);
        when(categoriesPresenterMock.getView()).thenReturn(categoriesViewMock);
        when(utilMock.convertHierarchiesToAvus(ontologyHierarchyListMock)).thenReturn(avuListBeanMock);
        when(utilMock.convertHierarchiesToAvus(hierarchyMock)).thenReturn(avuListBeanMock);
        when(utilMock.convertHierarchyToAvu(hierarchyMock)).thenReturn(avuMock);
        when(utilMock.getUnclassifiedParentIri(hierarchyMock)).thenReturn("parent");
        when(hierarchyMock.getLabel()).thenReturn("label");
        when(autoBeanAvuMock.as()).thenReturn(avuListBeanMock);
        when(avuFactoryMock.getAvuList()).thenReturn(autoBeanAvuMock);
        when(iriListMock.size()).thenReturn(2);
        when(iriIteratorMock.hasNext()).thenReturn(true, true, false);
        when(iriIteratorMock.next()).thenReturn("iri1").thenReturn("iri2");
        when(listOntologyMock.size()).thenReturn(2);
        when(listOntologyMock.iterator()).thenReturn(ontologyIteratorMock);
        when(ontologyIteratorMock.hasNext()).thenReturn(true, true, false);
        when(ontologyIteratorMock.next()).thenReturn(ontologyMock).thenReturn(activeOntologyMock);
        when(ontologyMock.isActive()).thenReturn(false);
        when(activeOntologyMock.isActive()).thenReturn(true);
        when(factoryMock.create(Matchers.<TreeStore<OntologyHierarchy>>any(),
                                isA(AppCategoriesView.class),
                                isA(AdminAppsGridView.class),
                                isA(AdminAppsGridView.class),
                                isA(OntologyHierarchyToAppDND.class),
                                isA(AppToOntologyHierarchyDND.class))).thenReturn(viewMock);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          oldGridPresenterMock,
                                          newGridPresenterMock,
                                          categorizeViewMock);
        uut.announcer = announcerMock;
        uut.properties = propertiesMock;
        uut.ontologyUtil = utilMock;

        verifyConstructor(uut);
    }

    void verifyConstructor(OntologiesPresenterImpl uut) {
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(oldGridPresenterMock));
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(oldGridViewMock));
        verify(oldGridPresenterMock).addStoreRemoveHandler(eq(categoriesPresenterMock));
        verify(oldGridViewMock).addAppSelectionChangedEventHandler(eq(viewMock));
        verify(newGridViewMock).addAppSelectionChangedEventHandler(eq(viewMock));

        verify(viewMock).addRefreshOntologiesEventHandler(eq(uut));
        verify(viewMock).addSelectOntologyVersionEventHandler(eq(uut));
        verify(viewMock).addHierarchySelectedEventHandler(eq(uut));
        verify(viewMock).addHierarchySelectedEventHandler(eq(newGridViewMock));
        verify(viewMock).addSaveOntologyHierarchyEventHandler(eq(uut));
        verify(viewMock).addPublishOntologyClickEventHandler(eq(uut));
        verify(viewMock).addCategorizeButtonClickedEventHandler(eq(uut));
    }

    @Test
    public void testGetOntologies_doNotSelectActiveOntology() {

        /** CALL METHOD UNDER TEST **/
        uut.getOntologies(false);

        verify(serviceFacadeMock).getOntologies(asyncCallbackOntologyListCaptor.capture());

        asyncCallbackOntologyListCaptor.getValue().onSuccess(listOntologyMock);
        verify(viewMock).showOntologyVersions(eq(listOntologyMock));
        verify(viewMock).unMaskHierarchyTree();
        verifyNoMoreInteractions(viewMock);
    }

    @Test
    public void testGetOntologies_selectActiveOntology() {

        /** CALL METHOD UNDER TEST **/
        uut.getOntologies(true);

        verify(serviceFacadeMock).getOntologies(asyncCallbackOntologyListCaptor.capture());

        asyncCallbackOntologyListCaptor.getValue().onSuccess(listOntologyMock);
        verify(viewMock).showOntologyVersions(eq(listOntologyMock));
        verify(viewMock).unMaskHierarchyTree();
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
                                          treeStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          oldGridPresenterMock,
                                          newGridPresenterMock,
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

        verify(viewMock).clearStore();

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
                                          treeStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          oldGridPresenterMock,
                                          newGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
            }
        };

        /** CALL METHOD UNDER TEST **/
        uut.onSelectOntologyVersion(eventMock);

        verify(viewMock).clearStore();

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
                                          treeStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          oldGridPresenterMock,
                                          newGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
            }
        };
        uut.properties = propertiesMock;
        uut.announcer = announcerMock;

        /** CALL METHOD UNDER TEST **/
        uut.onSaveOntologyHierarchy(eventMock);
        verify(serviceFacadeMock, times(2)).saveOntologyHierarchy(eq(eventMock.getOntology().getVersion()),
                                                        anyString(),
                                                        asyncOntologyHierarchyCaptor.capture());

        asyncOntologyHierarchyCaptor.getValue().onSuccess(ontologyHierarchyMock);

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
    public void testOnHierarchySelected_classified() {

        HierarchySelectedEvent eventMock = mock(HierarchySelectedEvent.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(hierarchyMock.getIri()).thenReturn("iri");
        when(eventMock.getHierarchy()).thenReturn(hierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);
        when(utilMock.isUnclassified(hierarchyMock)).thenReturn(false);

        /** CALL METHOD UNDER TEST **/
        uut.onHierarchySelected(eventMock);

        verify(serviceFacadeMock).getAppsByHierarchy(eq(hierarchyMock.getIri()), eq(avuMock), asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);
        verify(newGridViewMock).clearAndAdd(appListMock);
        verify(newGridViewMock).unmask();
    }


    @Test
    public void testOnHierarchySelected_unclassified() {

        HierarchySelectedEvent eventMock = mock(HierarchySelectedEvent.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(hierarchyMock.getIri()).thenReturn("iri");
        when(eventMock.getHierarchy()).thenReturn(hierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);
        when(utilMock.isUnclassified(hierarchyMock)).thenReturn(true);


        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          factoryMock,
                                          avuFactoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          oldGridPresenterMock,
                                          newGridPresenterMock,
                                          categorizeViewMock) {
            @Override
            void getUnclassifiedApps(OntologyHierarchy hierarchy, Ontology editedOntology) {
            }
        };
        uut.announcer = announcerMock;
        uut.properties = propertiesMock;
        uut.ontologyUtil = utilMock;

        /** CALL METHOD UNDER TEST **/
        uut.onHierarchySelected(eventMock);
        //Testing the unclassifiedApps method separately
        verifyZeroInteractions(serviceFacadeMock);
    }


    @Test
    public void testGetUnclassifiedApps() {
        Ontology ontologyMock = mock(Ontology.class);

        when(hierarchyMock.getIri()).thenReturn("iri");
        when(ontologyMock.getVersion()).thenReturn("version");

        /** CALL METHOD UNDER TEST **/
        uut.getUnclassifiedApps(hierarchyMock, ontologyMock);

        verify(newGridViewMock).mask(anyString());
        verify(serviceFacadeMock).getUnclassifiedApps(eq(ontologyMock.getVersion()), anyString(), eq(avuMock), asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);

        verify(newGridViewMock).clearAndAdd(appListMock);
        verify(newGridViewMock).unmask();
    }
}
