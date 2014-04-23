package org.iplantc.de.apps.client.views.widgets;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.proxy.AppLoadConfig;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class AppsViewToolbarImplTest {

    @Mock AppAutoBeanFactory mockAppFactory;
    @Mock AppSearchAutoBeanFactory mockAppSearchFactory;
    @Mock AppServiceFacade mockAppService;
    @Mock IplantDisplayStrings mockDisplayStrings;
    @Mock UserInfo mockUserInfo;

    @Mock MenuItem mockAppRun;
    @Mock MenuItem mockCopyApp;
    @Mock MenuItem mockCopyWf;
    @Mock MenuItem mockCreateNewApp;
    @Mock MenuItem mockCreateWorkflow;
    @Mock MenuItem mockDeleteApp;
    @Mock MenuItem mockDeleteWf;
    @Mock MenuItem mockEditApp;
    @Mock MenuItem mockEditWf;
    @Mock MenuItem mockRequestTool;
    @Mock MenuItem mockSubmitApp;
    @Mock MenuItem mockSubmitWf;
    @Mock MenuItem mockWfRun;

    @Mock TextButton mockAppMenu;
    @Mock TextButton mockWfMenu;

    @Mock AutoBean<AppLoadConfig> mockLoadConfigAb;
    @Mock AppLoadConfig mockLoadConfig;

    private AppsViewToolbarImpl uut;
    @Before public void setUp() {
        when(mockAppSearchFactory.loadConfig()).thenReturn(mockLoadConfigAb);
        when(mockLoadConfigAb.as()).thenReturn(mockLoadConfig);
        uut = new AppsViewToolbarImpl(mockAppService, mockDisplayStrings, mockAppSearchFactory, mockAppFactory, mockUserInfo);
        mockMenuItems(uut);
    }

    void mockMenuItems(AppsViewToolbarImpl uut){
        uut.wf_menu = mockWfMenu;
        uut.app_menu = mockAppMenu;
        uut.appRun = mockAppRun;
        uut.copyApp = mockCopyApp;
        uut.copyWf = mockCopyWf;
        uut.createNewApp = mockCreateNewApp;
        uut.createWorkflow = mockCreateWorkflow;
        uut.deleteApp = mockDeleteApp;
        uut.deleteWf = mockDeleteWf;
        uut.editApp = mockEditApp;
        uut.editWf = mockEditWf;
        uut.requestTool = mockRequestTool;
        uut.submitApp = mockSubmitApp;
        uut.submitWf = mockSubmitWf;
        uut.wfRun = mockWfRun;
    }

    @Test public void testOnAppSelectionChanged() {

    }

    @Test public void testOnAppGroupSelectionChanged() {

    }

}