package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplateList;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.Endpoints;
import org.iplantc.de.client.services.ReservedBuckets;
import org.iplantc.de.client.services.SearchServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

@RunWith(GxtMockitoTestRunner.class)
public class SearchServiceFacadeImplTest {
    
    @Mock DEServiceFacade deServiceFacadeMock;
    @Mock DEProperties deProperties;
    @Mock SearchAutoBeanFactory searchAbFactoryMock;
    @Mock DiskResourceAutoBeanFactory drFactoryMock;
    @Mock Endpoints endpointMock;
    @Mock ReservedBuckets bucketsMock;
    @Mock UserInfo userInfoMock;
    @Mock AutoBean<DiskResourceQueryTemplateList> qtlistAbMock;
    
    @Mock AsyncCallback<List<DiskResourceQueryTemplate>> asyncQtListMock;

    @Captor ArgumentCaptor<AsyncCallback<List<DiskResourceQueryTemplate>>> asyncQtCaptor;
    @Captor ArgumentCaptor<AsyncCallbackConverter<String, List<DiskResourceQueryTemplate>>> asyncStringCaptor;
    @Captor ArgumentCaptor<List<DiskResourceQueryTemplate>> qtListCaptor;

    private SearchServiceFacade unitUnderTest;

    @Before public void setUp() {
        unitUnderTest = new SearchServiceFacadeImpl(deServiceFacadeMock, deProperties, searchAbFactoryMock, drFactoryMock, endpointMock, bucketsMock, userInfoMock);
    }

    /**
     * Verifies the correct address formation and HTTP type submitted to the DE Service.
     * 
     * @see SearchServiceFacade#getSavedQueryTemplates(AsyncCallback)
     */
    @Test public void testGetSavedQueryTemplates_Case1() {
        
        final String bucketAddy = "buckets";
        final String userName = "genericUser";
        final String queryTemplateBucket = "savedQueryTemplatesBucket";
        when(endpointMock.buckets()).thenReturn(bucketAddy);
        when(userInfoMock.getUsername()).thenReturn(userName);
        when(bucketsMock.queryTemplates()).thenReturn(queryTemplateBucket);

        // Call method under test
        unitUnderTest.getSavedQueryTemplates(asyncQtListMock);

        /* Verify proper construction of service call wrapper */
        ArgumentCaptor<ServiceCallWrapper> wrapperCaptor = ArgumentCaptor.forClass(ServiceCallWrapper.class);
        verify(deServiceFacadeMock).getServiceData(wrapperCaptor.capture(), asyncStringCaptor.capture());
        verify(userInfoMock).getUsername();
        verify(bucketsMock).queryTemplates();

        final String expectedAddress = bucketAddy + "/" + userName + "/reserved/" + queryTemplateBucket;
        /*
         * FIXME Fix this verify. Easiest way would be to inject DEProperties instead of statically
         * getting the instance.
         */
        // assertEquals("Verify expected address construction", expectedAddress,
        // wrapperCaptor.getValue().getAddress());
        assertEquals("Verify that it is a GET", Type.GET, wrapperCaptor.getValue().getType());
    }

    /**
     * Verifies the expected address construction and HTTP type submitted to the DE Service
     * 
     * @see SearchServiceFacade#saveQueryTemplates(List, AsyncCallback)
     */
    @Test public void testSaveQueryTemplates_Case1() {
        final String bucketAddy = "buckets";
        final String userName = "testSaveUsername";
        final String queryTemplatesBucket = "testSaveQuertyTemplatesBucket";
        when(endpointMock.buckets()).thenReturn(bucketAddy);
        when(userInfoMock.getUsername()).thenReturn(userName);
        when(bucketsMock.queryTemplates()).thenReturn(queryTemplatesBucket);
        
        final ArrayList<DiskResourceQueryTemplate> newArrayList = Lists.newArrayList(mock(DiskResourceQueryTemplate.class));
        unitUnderTest.saveQueryTemplates(newArrayList, asyncQtListMock);

        /* Verify proper construction of service call wrapper */
        ArgumentCaptor<ServiceCallWrapper> wrapperCaptor = ArgumentCaptor.forClass(ServiceCallWrapper.class);
        verify(deServiceFacadeMock).getServiceData(wrapperCaptor.capture(), asyncStringCaptor.capture());
        verify(userInfoMock).getUsername();
        verify(bucketsMock).queryTemplates();

        final String expectedAddress = bucketAddy + "/" + userName + "/reserved/" + queryTemplatesBucket;
        assertTrue("Verify expected endpoint construction", wrapperCaptor.getValue().getAddress().endsWith(expectedAddress));
        /* Verify that it is a POST */
        assertEquals(Type.POST, wrapperCaptor.getValue().getType());
    }

    /**
     * Verifies proper body construction.
     * 
     * FIXME Verify that all "files" and "folders" collections are reset before saving
     * 
     * Ignored until a means of dealing with Autobeans is discovered
     * 
     * @see SearchServiceFacade#saveQueryTemplates(List, AsyncCallback)
     */
    @Ignore("Unable to appropriately mock out Autobean factory")
    @Test public void testSaveQueryTemplates_Case2() {

        DiskResourceQueryTemplate mock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate mock2 = mock(DiskResourceQueryTemplate.class);
        when(mock1.getName()).thenReturn("a name");
        final ArrayList<DiskResourceQueryTemplate> newArrayList = Lists.newArrayList(mock1, mock2);
        
        // Call method under test
        unitUnderTest.saveQueryTemplates(newArrayList, asyncQtListMock);

        /* Verify proper construction of service call wrapper */
        ArgumentCaptor<ServiceCallWrapper> wrapperCaptor = ArgumentCaptor.forClass(ServiceCallWrapper.class);
        verify(deServiceFacadeMock).getServiceData(wrapperCaptor.capture(), asyncStringCaptor.capture());
        String expectedBody = "";
        assertEquals(expectedBody, wrapperCaptor.getValue().getBody());
    }

    /**
     * Verifies the expected address constructions and HTTP type submitted to the DE Service
     * 
     * @see DiskResourceQueryTemplate, FilterPagingLoadConfigBean, AsyncCallback)
     */
    @Ignore("To Be Implemented")
    @Test public void testSubmitSearchFromQueryTemplate_Case1() {
        // TODO create test
        // TODO verify that query string is url encoded.
        // TODO verify that total returned from service is set on template returned via callback
        // TODO verify that querytemplate will only contain files/folders returned from service

    }

    @Ignore("To Be Implemented")
    @Test public void testCreateFrozenList() {
        // TODO Verify necessity of this method
    }
    
        
    /**
     * Verifies that the returned list of {@link DiskResourceQueryTemplate}s have all
     * <code>isDirty()</code> flags set to false.
     * 
     * This test currently breaks because of autobean codex
     * 
     * @see SearchServiceFacade#getSavedQueryTemplates(AsyncCallback)
     */
    @Ignore("Unable to appropriately mock out Autobean factory")
    @Test public void testQueryTemplateListCallbackConverter_Case1() {
    
        unitUnderTest.getSavedQueryTemplates(asyncQtListMock);
        verify(deServiceFacadeMock).getServiceData(any(ServiceCallWrapper.class), asyncStringCaptor.capture());
        DiskResourceQueryTemplate qtMock1 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate qtMock2 = mock(DiskResourceQueryTemplate.class);
        DiskResourceQueryTemplate qtMock3 = mock(DiskResourceQueryTemplate.class);
        when(qtMock1.isDirty()).thenReturn(true);
        when(qtMock1.isDirty()).thenReturn(false);
        when(qtMock1.isDirty()).thenReturn(true);
        final DiskResourceQueryTemplateList qtListMock = mock(DiskResourceQueryTemplateList.class);
        when(qtListMock.getQueryTemplateList()).thenReturn(Lists.newArrayList(qtMock1, qtMock2, qtMock3));
        when(qtlistAbMock.as()).thenReturn(qtListMock);
        when(searchAbFactoryMock.create(DiskResourceQueryTemplateList.class)).thenReturn(qtlistAbMock);
    
        // Trigger pre-canned response
        final String endpointResponse = "{\"fileSizeRange\":{},\"total\":22,\"files\":[],\"modifiedWithin\":{},\"fileQuery\":\"and nudda \",\"permissions\":{\"write\":true,\"read\":true,\"own\":true},\"path\":\"/savedFilters/\",\"folders\":[],\"label\":\"T3\",\"createdWithin\":{}},{\"fileSizeRange\":{},\"files\":[],\"modifiedWithin\":{},\"fileQuery\":\"asdf\",\"permissions\":{\"write\":true,\"read\":true,\"own\":true},\"path\":\"/savedFilters/\",\"folders\":[],\"label\":\"asdf\",\"createdWithin\":{}},{\"fileSizeRange\":{},\"files\":[],\"modifiedWithin\":{},\"fileQuery\":\"asdf\",\"permissions\":{\"write\":true,\"read\":true,\"own\":true},\"path\":\"/savedFilters/\",\"folders\":[],\"label\":\"TwoNEW\",\"createdWithin\":{}},{\"fileSizeRange\":{},\"files\":[],\"modifiedWithin\":{},\"fileQuery\":\"asdf\",\"permissions\":{\"write\":true,\"read\":true,\"own\":true},\"path\":\"/savedFilters/\",\"folders\":[],\"label\":\"Freeeee\",\"createdWithin\":{}}]";
        asyncStringCaptor.getValue().onSuccess(endpointResponse);
    
        verify(asyncQtListMock).onSuccess(qtListCaptor.capture());
        for (DiskResourceQueryTemplate qt : qtListCaptor.getValue()) {
            /* Verify that all isDirty() methods of return templates return false. */
            verify(qt).setDirty(eq(false));
        }
    }

    @Ignore("To Be Implemented")
    @Test public void testBooleanCallbackConverter_Case1() {

    }

    @Ignore("To Be Implemented")
    @Test public void testSubmitSearchCallbackConverter_Case1() {

    }

}
