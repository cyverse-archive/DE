package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestViewAppearance;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwtmockito.GxtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collections;

/**
 * 
 * @author sriram
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
public class PermanentIdRequestViewImplTest {

    @Mock
    PermanentIdRequestAutoBeanFactory mockFactory;

    @Mock
    PermanentIdRequestViewAppearance mockAppearance;

    @Mock
    PermanentIdRequestView.Presenter mockPresenter;

    @Mock
    PermanentIdRequestProperties mockPr_props;

    @Mock
    TextButton mockUpdateBtn, mockMetadataBtn, mockCreateDOIBtn;

    @Mock
    Grid<PermanentIdRequest> mockGrid;

    @Mock
    ListStore<PermanentIdRequest> mockStore;

    @Mock
    GridSelectionModel<PermanentIdRequest> mockSelModel;

    @Mock
    SelectionChangedEvent<PermanentIdRequest> mockSelEvent;

    private PermanentIdRequestViewImpl prv;

    @Before
    public void setUp() {
        prv = new PermanentIdRequestViewImpl(mockPr_props, mockFactory, mockAppearance);
        prv.setPresenter(mockPresenter);
        mockMenuItems(prv);
    }

    private void mockMenuItems(PermanentIdRequestViewImpl prv) {
        prv.updateBtn = mockUpdateBtn;
        prv.metadataBtn = mockMetadataBtn;
        prv.createDOIBtn = mockCreateDOIBtn;
    }

    @Test
    public void testOnRequestSelectionChanged_zeroSelected() {
        when(mockSelEvent.getSelection()).thenReturn(Collections.<PermanentIdRequest> emptyList());
        prv.onSelectionChange(mockSelEvent);
        verify(mockSelEvent).getSelection();
        verify(mockPresenter).setSelectedRequest(eq((PermanentIdRequest)null));
        verify(mockUpdateBtn).setEnabled(eq(false));
        verify(mockMetadataBtn).setEnabled(eq(false));
        verify(mockCreateDOIBtn).setEnabled(eq(false));
    }

    @Test
    public void testOnRequestSelectionChanged_oneSelected() {
        final PermanentIdRequest mock = mock(PermanentIdRequest.class);
        when(mockSelEvent.getSelection()).thenReturn(Lists.newArrayList(mock));
        prv.onSelectionChange(mockSelEvent);
        verify(mockSelEvent).getSelection();
        verify(mockPresenter).setSelectedRequest(eq(mock));
        verify(mockUpdateBtn).setEnabled(eq(true));
        verify(mockMetadataBtn).setEnabled(eq(true));
        verify(mockCreateDOIBtn).setEnabled(eq(true));
    }

}
