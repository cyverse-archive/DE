package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyMetadata;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.Grid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

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
    @Mock AdminAppsGridView.Presenter gridPresenterMock;
    @Mock OntologyAutoBeanFactory beanFactoryMock;
    @Mock ListStore<App> listStoreMock;
    @Mock Grid<App> gridMock;
    @Mock AdminAppsGridView gridViewMock;
    @Mock AdminCategoriesView.Presenter categoriesPresenterMock;
    @Mock AdminCategoriesView categoriesViewMock;
    @Mock OntologiesViewFactory factoryMock;
    @Mock List<Ontology> listOntologyMock;
    @Mock List<OntologyHierarchy> ontologyHierarchyListMock;
    @Mock List<App> appListMock;

    @Captor ArgumentCaptor<AsyncCallback<List<Ontology>>> asyncCallbackOntologyListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<OntologyHierarchy>>> asyncOntologyHierarchyListCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OntologyHierarchy>> asyncOntologyHierarchyCaptor;
    @Captor ArgumentCaptor<AsyncCallback<OntologyVersionDetail>> asyncOntologyDetailCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<App>>> asyncAppListCaptor;

    private OntologiesPresenterImpl uut;


    @Before
    public void setUp() {
        when(appearanceMock.successTopicSaved()).thenReturn("success");
        when(appearanceMock.successOperationSaved()).thenReturn("success");
        when(appearanceMock.setActiveOntologySuccess()).thenReturn("success");
        when(gridMock.getStore()).thenReturn(listStoreMock);
        when(gridViewMock.getGrid()).thenReturn(gridMock);
        when(gridPresenterMock.getView()).thenReturn(gridViewMock);
        when(categoriesPresenterMock.getView()).thenReturn(categoriesViewMock);
        when(factoryMock.create(treeStoreMock, categoriesViewMock, gridViewMock)).thenReturn(viewMock);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          beanFactoryMock,
                                          factoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          gridPresenterMock);
        uut.announcer = announcerMock;
        uut.listStore = listStoreMock;
        uut.properties = propertiesMock;

        verifyConstructor(uut);
    }

    void verifyConstructor(OntologiesPresenterImpl uut) {
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(gridPresenterMock));
        verify(categoriesViewMock).addAppCategorySelectedEventHandler(eq(gridViewMock));
        verify(gridPresenterMock).addStoreRemoveHandler(eq(categoriesPresenterMock));
        verify(viewMock).addViewOntologyVersionEventHandler(eq(uut));
        verify(viewMock).addSelectOntologyVersionEventHandler(eq(uut));
        verify(viewMock).addHierarchySelectedEventHandler(eq(uut));
        verify(viewMock).addHierarchySelectedEventHandler(eq(gridViewMock));
        verify(viewMock).addSaveOntologyHierarchyEventHandler(eq(uut));
        verify(viewMock).addPublishOntologyClickEventHandler(eq(uut));
    }

    @Test
    public void testGetOntologies() {

        /** CALL METHOD UNDER TEST **/
        uut.getOntologies();

        verify(serviceFacadeMock).getOntologies(asyncCallbackOntologyListCaptor.capture());

        asyncCallbackOntologyListCaptor.getValue().onSuccess(listOntologyMock);
        verify(viewMock).showOntologyVersions(eq(listOntologyMock));
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

        verify(treeStoreMock).clear();

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
                                          beanFactoryMock,
                                          factoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          gridPresenterMock) {
            @Override
            void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
            }
        };

        /** CALL METHOD UNDER TEST **/
        uut.onSelectOntologyVersion(eventMock);

        verify(treeStoreMock).clear();

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

        when(propertiesMock.getEdamTopicIri()).thenReturn("topic");
        when(propertiesMock.getEdamOperationIri()).thenReturn("operation");
        when(ontologyMock.getVersion()).thenReturn("version");
        when(eventMock.getOntology()).thenReturn(ontologyMock);

        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          beanFactoryMock,
                                          factoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          gridPresenterMock) {
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
        final OntologyMetadata ontologyMetadataMock = mock(OntologyMetadata.class);
        OntologyHierarchy ontologyHierarchyMock = mock(OntologyHierarchy.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(ontologyHierarchyMock.getIri()).thenReturn("iri");
        when(eventMock.getHierarchy()).thenReturn(ontologyHierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);


        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          beanFactoryMock,
                                          factoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          gridPresenterMock) {
            @Override
            boolean isUnclassified(OntologyHierarchy hierarchy) {
                return false;
            }

            @Override
            OntologyMetadata getOntologyMetadata(OntologyHierarchy hierarchy) {
                return ontologyMetadataMock;
            }
        };

        /** CALL METHOD UNDER TEST **/
        uut.onHierarchySelected(eventMock);

        verify(serviceFacadeMock).getAppsByHierarchy(eq(ontologyHierarchyMock.getIri()), eq(ontologyMetadataMock), asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);
        verify(listStoreMock).clear();
        verify(listStoreMock).addAll(eq(appListMock));
        verify(gridViewMock).unmask();
    }


    @Test
    public void testOnHierarchySelected_unclassified() {

        HierarchySelectedEvent eventMock = mock(HierarchySelectedEvent.class);
        final OntologyMetadata ontologyMetadataMock = mock(OntologyMetadata.class);
        OntologyHierarchy ontologyHierarchyMock = mock(OntologyHierarchy.class);
        Ontology ontologyMock = mock(Ontology.class);
        when(ontologyHierarchyMock.getIri()).thenReturn("iri");
        when(eventMock.getHierarchy()).thenReturn(ontologyHierarchyMock);
        when(eventMock.getEditedOntology()).thenReturn(ontologyMock);


        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          beanFactoryMock,
                                          factoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          gridPresenterMock) {
            @Override
            boolean isUnclassified(OntologyHierarchy hierarchy) {
                return true;
            }

            @Override
            void getUnclassifiedApps(OntologyHierarchy hierarchy, Ontology editedOntology) {
            }
        };

        /** CALL METHOD UNDER TEST **/
        uut.onHierarchySelected(eventMock);
        //Testing the unclassifiedApps method separately
        verifyZeroInteractions(listStoreMock, serviceFacadeMock);
    }


    @Test
    public void testGetUnclassifiedApps() {
        OntologyHierarchy ontologyHierarchyMock = mock(OntologyHierarchy.class);
        Ontology ontologyMock = mock(Ontology.class);

        when(ontologyHierarchyMock.getIri()).thenReturn("iri");
        when(ontologyMock.getVersion()).thenReturn("version");


        uut = new OntologiesPresenterImpl(serviceFacadeMock,
                                          treeStoreMock,
                                          beanFactoryMock,
                                          factoryMock,
                                          appearanceMock,
                                          categoriesPresenterMock,
                                          gridPresenterMock) {
            @Override
            String getParentIri(OntologyHierarchy hierarchy) {
                return "iri";
            }
        };

        /** CALL METHOD UNDER TEST **/
        uut.getUnclassifiedApps(ontologyHierarchyMock, ontologyMock);

        verify(gridViewMock).mask(anyString());
        verify(serviceFacadeMock).getUnclassifiedApps(eq(ontologyMock.getVersion()), anyString(), asyncAppListCaptor.capture());

        asyncAppListCaptor.getValue().onSuccess(appListMock);

        verify(listStoreMock).clear();
        verify(listStoreMock).addAll(appListMock);
        verify(gridViewMock).unmask();
    }
}
