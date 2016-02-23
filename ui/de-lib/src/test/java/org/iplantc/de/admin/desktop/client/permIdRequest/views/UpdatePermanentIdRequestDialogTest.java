package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.iplantc.de.client.models.UserBootstrap;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestDetails;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;

import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class UpdatePermanentIdRequestDialogTest {

    @Mock
    IPlantAnchor mockCurrentStatusLabel;

    @Mock
    Label mockUserEmail;
    
    @Mock
    SimpleComboBox<PermanentIdRequestStatus> mockStatusCombo;
    
    @Mock
    TextArea mockCommentsEditor;
    
    @Mock
    PermanentIdRequestAutoBeanFactory mockPrfactory;

    @Mock
    PermanentIdRequest mockRequest;

    @Mock
    PermanentIdRequestView.Presenter mockPresenter;

    @Mock
    PermanentIdRequestUpdate mockStatusUpdate;

    @Mock
    AutoBean<PermanentIdRequestUpdate> mockAutoBeanStatus;

    @Mock
    PermanentIdRequestDetails mockDetails;

    @Mock
    UserBootstrap mockStrap;

    private UpdatePermanentIdRequestDialog dialog;

    @Before
    public void setUp() {
       when(mockDetails.getRequestor()).thenReturn(mockStrap);
        when(mockStrap.getEmail()).thenReturn("foo@bar.com");
        dialog = new UpdatePermanentIdRequestDialog(PermanentIdRequestStatus.Submitted.toString(),
                                                    mockDetails,
                                                    mockPrfactory);
        dialog.commentsEditor = mockCommentsEditor;
        dialog.currentStatusLabel = mockCurrentStatusLabel;
        dialog.statusCombo = mockStatusCombo;
        dialog.userEmail = mockUserEmail;

    }

    @Test
    public void testGetPermanentIdRequestUpdate() {
        when(mockStatusCombo.getCurrentValue()).thenReturn(PermanentIdRequestStatus.Approved);
        when(mockCommentsEditor.getValue()).thenReturn("testing");
        when(mockCurrentStatusLabel.getText()).thenReturn(PermanentIdRequestStatus.Submitted.toString());
        when(mockUserEmail.getText()).thenReturn("foo@bar.com");


        when(mockPrfactory.getStatus()).thenReturn(mockAutoBeanStatus);
        when(mockAutoBeanStatus.as()).thenReturn(mockStatusUpdate);
        final PermanentIdRequestUpdate pru = mock(PermanentIdRequestUpdate.class);
        pru.setComments("testing");
        pru.setStatus(PermanentIdRequestStatus.Approved.toString());

        // call unit under test
        PermanentIdRequestUpdate result = dialog.getPermanentIdRequestUpdate();
        assertEquals(pru.getComments(), result.getComments());
        assertEquals(pru.getStatus(), result.getStatus());

    }

}
