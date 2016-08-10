package org.iplantc.de.client.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.shared.DEProperties;

import com.google.gwt.json.client.JSONObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class OntologyUtilTest {

    private OntologyUtil uut;
    private OntologyUtil spy;

    private static final String HIERARCHY_PARENT_MODEL_KEY = "parent_key";
    private static final String HIERARCHY_MODEL_KEY = "model_key";

    @Mock AvuAutoBeanFactory avuAutoBeanFactoryMock;
    @Mock OntologyAutoBeanFactory ontologyAutoBeanFactoryMock;
    @Mock List<OntologyHierarchy> ontologyHierarchyListMock;
    @Mock Iterator<OntologyHierarchy> hierarchyIteratorMock;
    @Mock AutoBean<OntologyHierarchy> hierarchyAutoBeanMock;
    @Mock OntologyHierarchy hierarchyMock;
    @Mock Avu avuMock;
    @Mock Avu subAvuMock;
    @Mock AutoBean<AvuList> avuListAutoBeanMock;
    @Mock AvuList avuListMock;
    @Mock AutoBean<Avu> avuAutoBeanMock;
    @Mock AutoBean<Avu> subAvuAutoBeanMock;
    @Mock OntologyHierarchy unclassifiedMock;
    @Mock DEProperties propertiesMock;
    @Mock JsonUtil jsonUtilMock;
    @Mock Map<String, String> iriToAttrMapMock;
    @Mock JSONObject mapMock;
    @Mock Set<String> keySetMock;
    @Mock Iterator<String> stringIteratorMock;


    @Before
    public void setUp() {
        when(avuAutoBeanMock.as()).thenReturn(avuMock);
        when(subAvuAutoBeanMock.as()).thenReturn(subAvuMock);
        when(avuListAutoBeanMock.as()).thenReturn(avuListMock);
        when(avuAutoBeanFactoryMock.getAvu()).thenReturn(avuAutoBeanMock).thenReturn(subAvuAutoBeanMock);
        when(avuAutoBeanFactoryMock.getAvuList()).thenReturn(avuListAutoBeanMock);
        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/topic_1234");
        when(hierarchyMock.getSubclasses()).thenReturn(ontologyHierarchyListMock);
        when(ontologyHierarchyListMock.size()).thenReturn(2);
        when(hierarchyIteratorMock.hasNext()).thenReturn(true, true, false);
        when(hierarchyIteratorMock.next()).thenReturn(hierarchyMock).thenReturn(hierarchyMock);
        when(ontologyHierarchyListMock.iterator()).thenReturn(hierarchyIteratorMock);
        when(hierarchyAutoBeanMock.as()).thenReturn(unclassifiedMock);
        when(ontologyAutoBeanFactoryMock.getHierarchy()).thenReturn(hierarchyAutoBeanMock);
        when(mapMock.keySet()).thenReturn(keySetMock);
        when(mapMock.size()).thenReturn(2);
        when(iriToAttrMapMock.size()).thenReturn(2);
        when(iriToAttrMapMock.keySet()).thenReturn(keySetMock);
        when(keySetMock.size()).thenReturn(2);
        when(keySetMock.iterator()).thenReturn(stringIteratorMock);
        when(iriToAttrMapMock.get("http://edamontology.org/topic.*")).thenReturn("http://edamontology.org/has_topic");
        when(iriToAttrMapMock.get("http://edamontology.org/operation.*")).thenReturn("rdf:type");
        when(stringIteratorMock.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(stringIteratorMock.next()).thenReturn("http://edamontology.org/operation.*").thenReturn("http://edamontology.org/topic.*");
        when(jsonUtilMock.getString(mapMock, "string")).thenReturn("string");

        uut = OntologyUtil.getInstance();

        uut.factory = ontologyAutoBeanFactoryMock;
        uut.avuFactory = avuAutoBeanFactoryMock;
        uut.jsonUtil = jsonUtilMock;
        uut.properties = propertiesMock;
        uut.iriToAttrMap = iriToAttrMapMock;

        spy = spy(uut);
    }

    @After
    public void resetSpy() {
        reset(spy);
    }

    @Test
    public void testAddUnclassifiedChild() {
        uut.addUnclassifiedChild(ontologyHierarchyListMock);

        verify(unclassifiedMock, times(2)).setLabel("Unclassified");
        verify(unclassifiedMock, times(2)).setIri(eq(hierarchyMock.getIri() + "_unclassified"));
        verify(ontologyHierarchyListMock, times(2)).add(unclassifiedMock);
    }

    @Test
    public void testIsUnclassified() {
        OntologyHierarchy hierarchyMock = mock(OntologyHierarchy.class);
        when(hierarchyMock.getIri()).thenReturn("");
        assertFalse(uut.isUnclassified(hierarchyMock));

        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/topic_1234");
        assertFalse(uut.isUnclassified(hierarchyMock));

        when(hierarchyMock.getIri()).thenReturn("http://unclassified.org/topic_1234");
        assertFalse(uut.isUnclassified(hierarchyMock));

        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/topic_1234_unclassified");
        assertTrue(uut.isUnclassified(hierarchyMock));
    }

    @Test
    public void testGetUnclassifiedParentIri() {
        OntologyHierarchy hierarchyMock = mock(OntologyHierarchy.class);
        when(hierarchyMock.getIri()).thenReturn("");
        assertEquals("", uut.getUnclassifiedParentIri(hierarchyMock));

        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/topic_1234");
        assertEquals("http://edamontology.org/topic_1234", uut.getUnclassifiedParentIri(hierarchyMock));

        when(hierarchyMock.getIri()).thenReturn("http://unclassified.org/topic_1234");
        assertEquals("http://unclassified.org/topic_1234", uut.getUnclassifiedParentIri(hierarchyMock));

        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/topic_1234_unclassified");
        assertEquals("http://edamontology.org/topic_1234", uut.getUnclassifiedParentIri(hierarchyMock));
    }

    @Test
    public void testGetAttr_topic() {
        OntologyHierarchy hierarchyMock = mock(OntologyHierarchy.class);
        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/topic_0003");
        assertEquals("http://edamontology.org/has_topic", uut.getAttr(hierarchyMock));
    }

    @Test
    public void testGetAttr_operation() {
        OntologyHierarchy hierarchyMock = mock(OntologyHierarchy.class);
        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/operation_1234");
        assertEquals("rdf:type", uut.getAttr(hierarchyMock));
    }

    @Test
    public void testGetAttr_notFound() {
        OntologyHierarchy hierarchyMock = mock(OntologyHierarchy.class);
        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/data_1234");
        assertEquals("", uut.getAttr(hierarchyMock));
    }

    @Test
    public void testConvertHierarchyToAvu() {
        OntologyHierarchy hierarchyMock = mock(OntologyHierarchy.class);
        when(hierarchyMock.getIri()).thenReturn("http://edamontology.org/operation_1234");
        when(hierarchyMock.getLabel()).thenReturn("Label");

        uut.convertHierarchyToAvu(hierarchyMock);
        verify(avuMock).setAttribute(anyString());
        verify(avuMock).setValue(eq(hierarchyMock.getIri()));

        verify(subAvuMock).setAttribute("rdfs:label");
        verify(subAvuMock).setValue(eq(hierarchyMock.getLabel()));
        verify(subAvuMock).setUnit(anyString());

        verify(avuMock).setAvus(anyList());

    }

    @Test
    public void testGetHierarchyPathTag_nullHierarchy() {
        uut.getOrCreateHierarchyPathTag(null);
        verifyZeroInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_noTagsSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn(null);
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn(null);

        when(hierarchyMock.getLabel()).thenReturn("Label");

        when(spy.getHierarchyAutoBean(hierarchyMock)).thenReturn(hierarchyAutoBeanMock);

        spy.getOrCreateHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verify(hierarchyAutoBeanMock, times(3)).setTag(eq(HIERARCHY_PARENT_MODEL_KEY), eq("Label"));
        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_MODEL_KEY), eq("Label"));
        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_parentTagSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn("Parent");
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn(null);

        when(hierarchyMock.getLabel()).thenReturn("Label");

        when(spy.getHierarchyAutoBean(hierarchyMock)).thenReturn(hierarchyAutoBeanMock);

        spy.getOrCreateHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_MODEL_KEY), eq("Parent/Label"));
    }

    @Test
    public void testGetHierarchyPathTag_childTagSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn(null);
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn("Child");

        when(hierarchyMock.getLabel()).thenReturn("Label");

        when(spy.getHierarchyAutoBean(hierarchyMock)).thenReturn(hierarchyAutoBeanMock);

        spy.getOrCreateHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verify(hierarchyAutoBeanMock, times(3)).setTag(eq(HIERARCHY_PARENT_MODEL_KEY), eq("Label"));
        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_MODEL_KEY), eq("Label"));
        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_bothTagsSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn("Parent");
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn("Child");

        when(hierarchyMock.getLabel()).thenReturn("Label");

        when(spy.getHierarchyAutoBean(hierarchyMock)).thenReturn(hierarchyAutoBeanMock);

        spy.getOrCreateHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testCreateIriToAttrMap_nullProperties() {
        when(propertiesMock.getOntologyAttrs()).thenReturn(null);

        /** CALL METHOD UNDER TEST **/
        uut.createIriToAttrMap(ontologyHierarchyListMock);
        verify(propertiesMock).getOntologyAttrs();
        verifyZeroInteractions(iriToAttrMapMock);
    }

    @Test
    public void testCreateIriToAttrMap_withProperties() {
        when(propertiesMock.getOntologyAttrs()).thenReturn("string");
        when(jsonUtilMock.getObject(anyString())).thenReturn(mapMock);

        /** CALL METHOD UNDER TEST **/
        spy.createIriToAttrMap(ontologyHierarchyListMock);
        verify(propertiesMock).getOntologyAttrs();
        verify(iriToAttrMapMock).clear();
        verify(spy).buildIriToAttrMap(anyString());
    }

    @Test
    public void testBuildIriToAttrMap() {
        when(jsonUtilMock.getObject("rdf:type")).thenReturn(mapMock);

        /** CALL METHOD UNDER TEST **/
        uut.buildIriToAttrMap("rdf:type");

        verify(jsonUtilMock).getObject(anyString());
        verify(mapMock).keySet();
        verify(jsonUtilMock, times(2)).getString(eq(mapMock), anyString());
        verify(iriToAttrMapMock, times(2)).put(anyString(), anyString());
    }

}
