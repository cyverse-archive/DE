package org.iplantc.de.admin.desktop.client.ontologies.model;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.List;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class OntologyHierarchyTreeStoreProviderTest {

    private static final String HIERARCHY_PARENT_MODEL_KEY = "parent_key";
    private static final String HIERARCHY_MODEL_KEY = "model_key";

    @Mock OntologyHierarchy hierarchyMock;
    @Mock List<OntologyHierarchy> hierarchyListMock;
    @Mock Iterator<OntologyHierarchy> hierarchyIteratorMock;
    @Mock AutoBean<OntologyHierarchy> hierarchyAutoBeanMock;

    OntologyHierarchyTreeStoreProvider uut;

    @Before
    public void setUp() {
        when(hierarchyListMock.size()).thenReturn(2);
        when(hierarchyListMock.iterator()).thenReturn(hierarchyIteratorMock);
        when(hierarchyIteratorMock.hasNext()).thenReturn(true, true, false);

        uut = new OntologyHierarchyTreeStoreProvider() {
            @Override
            AutoBean<OntologyHierarchy> getHierarchyAutoBean(OntologyHierarchy hierarchy) {
                return hierarchyAutoBeanMock;
            }
        };
    }

    @Test
    public void testGetHierarchyPathTag_nullHierarchy() {
        uut.getHierarchyPathTag(null);
        verifyZeroInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_noTagsSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn(null);
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn(null);

        when(hierarchyMock.getLabel()).thenReturn("Label");

        uut.getHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_PARENT_MODEL_KEY), eq("Label"));
        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_MODEL_KEY), eq("Label"));
        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_parentTagSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn("Parent");
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn(null);

        when(hierarchyMock.getLabel()).thenReturn("Label");

        uut.getHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_MODEL_KEY), eq("Parent/Label"));
        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_childTagSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn(null);
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn("Child");

        when(hierarchyMock.getLabel()).thenReturn("Label");

        uut.getHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_PARENT_MODEL_KEY), eq("Label"));
        verify(hierarchyAutoBeanMock).setTag(eq(HIERARCHY_MODEL_KEY), eq("Label"));
        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

    @Test
    public void testGetHierarchyPathTag_bothTagsSet() {
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_PARENT_MODEL_KEY)).thenReturn("Parent");
        when(hierarchyAutoBeanMock.getTag(HIERARCHY_MODEL_KEY)).thenReturn("Child");

        when(hierarchyMock.getLabel()).thenReturn("Label");

        uut.getHierarchyPathTag(hierarchyMock);
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_PARENT_MODEL_KEY));
        verify(hierarchyAutoBeanMock).getTag(eq(HIERARCHY_MODEL_KEY));

        verifyNoMoreInteractions(hierarchyAutoBeanMock);
    }

}