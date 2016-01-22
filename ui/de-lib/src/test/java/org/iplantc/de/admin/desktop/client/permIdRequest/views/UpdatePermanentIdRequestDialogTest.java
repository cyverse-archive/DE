package org.iplantc.de.admin.desktop.client.permIdRequest.views;

import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestViewAppearance;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;

import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.web.bindery.autobean.shared.AutoBean;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextArea;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GxtMockitoTestRunner.class)
public class UpdatePermanentIdRequestDialogTest {

    @Mock
    Label mockCurrentStatusLabel;
    
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

    private UpdatePermanentIdRequestDialog dialog;

    @Before
    public void setUp() {
        dialog = new UpdatePermanentIdRequestDialog(mockRequest,
                                                    mockPresenter,
                                                    mockPrfactory);
        dialog.commentsEditor = mockCommentsEditor;
        dialog.currentStatusLabel = mockCurrentStatusLabel;
        dialog.statusCombo = mockStatusCombo;


    }

    @Test
    public void testGetPermanentIdRequestUpdate() {
        when(mockStatusCombo.getCurrentValue()).thenReturn(PermanentIdRequestStatus.Approved);
        when(mockCommentsEditor.getValue()).thenReturn("testing");
        when(mockCurrentStatusLabel.getText()).thenReturn(PermanentIdRequestStatus.Submitted.toString());
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
