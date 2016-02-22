package org.iplantc.de.admin.desktop.client.permIdRequest.presenter;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermanentIdRequestAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestPresenterAppearance;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GxtMockitoTestRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * 
 * @author sriram
 * 
 */
@RunWith(GxtMockitoTestRunner.class)
public class PermanentIdRequestPresenterTest {

    @Mock
    DiskResourceServiceFacade mockDrFacade;

    @Mock
    PermanentIdRequestAdminServiceFacade mockPrFacade;

    @Mock
    PermanentIdRequest mockSelectedRequest;

    @Mock
    PermanentIdRequestView mockView;

    @Mock
    PermanentIdRequestAutoBeanFactory mockFactory;

    @Captor
    ArgumentCaptor<AsyncCallback<String>> stirngCallbackCaptor;

    @Mock
    PermanentIdRequestPresenterAppearance mockAppearance;

    @Mock
    PermanentIdRequestUpdate mockRequestUpdate;

    private PermanentIdRequestPresenter presenter;


    @Before
    public void setUp() {
        presenter = new PermanentIdRequestPresenter(mockDrFacade,
                                                    mockPrFacade,
                                                    mockFactory,
                                                    mockView,
                                                    mockAppearance);

    }

    @Test
    public void testCreatePermanentId() {
        presenter.setSelectedRequest(mockSelectedRequest);
        when(mockSelectedRequest.getId()).thenReturn("101010101");
        presenter.createPermanentId();
        verify(mockPrFacade).createPermanentId(eq(mockSelectedRequest.getId()),
                                               stirngCallbackCaptor.capture());
    }

    @Test
    public void testCreatePermanentId_NoRequestSelected() {
        presenter.setSelectedRequest(null);
        presenter.createPermanentId();
        verifyZeroInteractions(mockPrFacade);
    }

    @Test
    @Ignore
    public void testUpdateRequest() {
        presenter.setSelectedRequest(mockSelectedRequest);
        when(mockSelectedRequest.getId()).thenReturn("101010101");
        presenter.doUpdateRequest(mockRequestUpdate);
        verify(mockPrFacade).updatePermanentIdRequestStatus(mockSelectedRequest.getId(),
                                                            mockRequestUpdate,
                                                            stirngCallbackCaptor.capture());
    }

    @Test
    public void testUpdateRequest_nullUpdate() {
        presenter.setSelectedRequest(mockSelectedRequest);
        when(mockSelectedRequest.getId()).thenReturn("101010101");
        presenter.doUpdateRequest(null);
        verifyZeroInteractions(mockPrFacade);
    }

}
