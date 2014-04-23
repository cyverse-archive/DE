package org.iplantc.admin.belphegor.client.apps.views.widgets;

import org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.proxy.AppSearchAutoBeanFactory;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.widget.core.client.button.TextButton;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collections;

@RunWith(GxtMockitoTestRunner.class)
public class BelphegorAppsToolbarImplTest {
    @Mock AppAutoBeanFactory mockAppFactory;
    @Mock AppSearchAutoBeanFactory mockAppSearchFactory;
    @Mock AppServiceFacade mockAppService;
    @Mock IplantDisplayStrings mockDisplayStrings;

    @Mock TextButton mockAddCategory;
    @Mock TextButton mockCategorizeApp;
    @Mock TextButton mockDelete;
    @Mock TextButton mockRenameCategory;
    @Mock TextButton mockRestoreApp;

    @Mock AppSelectionChangedEvent mockAppSelectionChangedEvent;
    @Mock AppGroupSelectionChangedEvent mockAppGrpSelectionChangedEvent;

    private BelphegorAppsToolbarImpl uut;

    @Before public void setUp(){
        uut = new BelphegorAppsToolbarImpl(mockAppService, mockAppSearchFactory, mockAppFactory, mockDisplayStrings);
        mockMenuItems(uut);
    }

    private void mockMenuItems(BelphegorAppsToolbarImpl uut){
        uut.addCategory = mockAddCategory;
        uut.categorizeApp = mockCategorizeApp;
        uut.delete = mockDelete;
        uut.renameCategory = mockRenameCategory;
        uut.restoreApp = mockRestoreApp;
    }

    @Test public void testOnAppSelectionChanged_zeroSelected() {
        when(mockAppSelectionChangedEvent.getAppSelection()).thenReturn(Collections.<App>emptyList());
        uut.onAppSelectionChanged(mockAppSelectionChangedEvent);

        verify(mockAddCategory).setEnabled(eq(false));
        verify(mockCategorizeApp).setEnabled(eq(false));
        verify(mockDelete).setEnabled(eq(false));
        verify(mockRenameCategory).setEnabled(eq(false));
        verify(mockRestoreApp).setEnabled(eq(false));
    }

    @Test public void testOnAppSelectionChanged_oneSelected_notDeleted() {
        final App mock = mock(App.class);
        when(mock.isDeleted()).thenReturn(false);
        when(mockAppSelectionChangedEvent.getAppSelection()).thenReturn(Lists.newArrayList(mock));
        uut.onAppSelectionChanged(mockAppSelectionChangedEvent);

        verify(mockAddCategory).setEnabled(eq(false));
        verify(mockCategorizeApp).setEnabled(eq(true));
        verify(mockDelete).setEnabled(eq(true));
        verify(mockRenameCategory).setEnabled(eq(false));
        verify(mockRestoreApp).setEnabled(eq(false));
    }

    @Test public void testOnAppSelectionChanged_oneSelected_isDeleted() {
        final App mock = mock(App.class);
        when(mock.isDeleted()).thenReturn(true);
        when(mockAppSelectionChangedEvent.getAppSelection()).thenReturn(Lists.newArrayList(mock));
        uut.onAppSelectionChanged(mockAppSelectionChangedEvent);

        verify(mockAddCategory).setEnabled(eq(false));
        verify(mockCategorizeApp).setEnabled(eq(false));
        verify(mockDelete).setEnabled(eq(true));
        verify(mockRenameCategory).setEnabled(eq(false));
        verify(mockRestoreApp).setEnabled(eq(true));
    }

    @Test public void testOnAppGroupSelectionChanged_zeroSelected() {
        when(mockAppGrpSelectionChangedEvent.getAppGroupSelection()).thenReturn(Collections.<AppGroup>emptyList());
        uut.onAppGroupSelectionChanged(mockAppGrpSelectionChangedEvent);

        verify(mockAddCategory).setEnabled(eq(true));
        verify(mockCategorizeApp).setEnabled(eq(false));
        verify(mockDelete).setEnabled(eq(false));
        verify(mockRenameCategory).setEnabled(eq(false));
        verify(mockRestoreApp).setEnabled(eq(false));
    }

    @Test public void testOnAppGroupSelectionChanged_oneSelected() {
        when(mockAppGrpSelectionChangedEvent.getAppGroupSelection()).thenReturn(Lists.newArrayList(mock(AppGroup.class)));
        uut.onAppGroupSelectionChanged(mockAppGrpSelectionChangedEvent);

        verify(mockAddCategory).setEnabled(eq(true));
        verify(mockCategorizeApp).setEnabled(eq(false));
        verify(mockDelete).setEnabled(eq(true));
        verify(mockRenameCategory).setEnabled(eq(true));
        verify(mockRestoreApp).setEnabled(eq(false));
    }
}