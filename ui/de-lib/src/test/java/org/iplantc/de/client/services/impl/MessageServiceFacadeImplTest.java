package org.iplantc.de.client.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.shared.services.DiscEnvApiService;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class MessageServiceFacadeImplTest {

    @Mock NotificationAutoBeanFactory notesFactoryMock;
    @Mock DEProperties dePropertiesMock;
    @Mock DiscEnvApiService deServiceFacadeMock;
    @Mock UserInfo userInfoMock;
    @Mock DiskResourceUtil diskResourceUtilMock;
    @Mock AsyncCallback<String> asyncCallbackMock;

    private MessageServiceFacadeImpl uut;

    @Before
    public void setUp() {

        when(dePropertiesMock.getMuleServiceBaseUrl()).thenReturn("sampleUrl");
        uut = new MessageServiceFacadeImpl(deServiceFacadeMock,
                                           dePropertiesMock,
                                           notesFactoryMock,
                                           userInfoMock);
    }

    @Test
    public void testDeleteAll_noFilter() {
        String filter = "ALL";
        uut.deleteAll(filter, asyncCallbackMock);
        assertEquals(filter.toLowerCase(), NotificationCategory.ALL.toString().toLowerCase());
    }

    @Test
    public void testDeleteAll_filterData() {
        String filter = "DATA";
        uut.deleteAll(filter, asyncCallbackMock);
        assertNotEquals(filter.toLowerCase(), NotificationCategory.ALL.toString().toLowerCase());
        assertEquals(filter.toLowerCase(), NotificationCategory.DATA.toString().toLowerCase());
    }

    @Test
    public void testDeleteAll_filterToolRequests() {
        String filter = "TOOL REQUEST";
        uut.deleteAll(filter, asyncCallbackMock);
        assertNotEquals(filter.toLowerCase(), NotificationCategory.ALL.toString().toLowerCase());
        assertEquals(filter.toLowerCase(), NotificationCategory.TOOLREQUEST.toString().toLowerCase());
    }

    @Test
    public void testDeleteAll_filterApps() {
        String filter = "APPS";
        uut.deleteAll(filter, asyncCallbackMock);
        assertNotEquals(filter.toLowerCase(), NotificationCategory.ALL.toString().toLowerCase());
        assertEquals(filter.toLowerCase(), NotificationCategory.APPS.toString().toLowerCase());
    }

    @Test
    public void testDeleteAll_filterAnalysis() {
        String filter = "ANALYSIS";
        uut.deleteAll(filter, asyncCallbackMock);
        assertNotEquals(filter.toLowerCase(), NotificationCategory.ALL.toString().toLowerCase());
        assertEquals(filter.toLowerCase(), NotificationCategory.ANALYSIS.toString().toLowerCase());
    }

    @Test
    public void testDeleteAll_filterPermID() {
        String filter = "PERMANENT ID REQUEST";
        uut.deleteAll(filter, asyncCallbackMock);
        assertNotEquals(filter.toLowerCase(), NotificationCategory.ALL.toString().toLowerCase());
        assertEquals(filter.toLowerCase(), NotificationCategory.PERMANENTIDREQUEST.toString().toLowerCase());
    }

}
