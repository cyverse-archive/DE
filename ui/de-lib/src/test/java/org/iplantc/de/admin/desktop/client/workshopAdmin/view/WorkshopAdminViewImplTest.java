package org.iplantc.de.admin.desktop.client.workshopAdmin.view;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.admin.desktop.client.workshopAdmin.model.MemberProperties;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.collaborators.client.util.UserSearchField;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwtmockito.GxtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

import com.sencha.gxt.data.shared.ListStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author aramseyds
 */
@RunWith(GxtMockitoTestRunner.class)
@WithClassesToStub(UserSearchField.class)
public class WorkshopAdminViewImplTest {

    @Mock WorkshopAdminView.WorkshopAdminViewAppearance appearanceMock;
    @Mock MemberProperties propertiesMock;
    @Mock ListStore<Member> listStoreMock;
    @Mock ArrayList<HandlerRegistration> globalRegistrationMock;
    @Mock Iterator<HandlerRegistration> registrationIteratorMock;
    @Mock HandlerRegistration handlerMock;

    private WorkshopAdminViewImpl uut;

    @Before
    public void setUp() {
        when(globalRegistrationMock.size()).thenReturn(2);
        when(globalRegistrationMock.iterator()).thenReturn(registrationIteratorMock);
        when(registrationIteratorMock.hasNext()).thenReturn(true, true, false);
        when(registrationIteratorMock.next()).thenReturn(handlerMock, handlerMock);

        uut = new WorkshopAdminViewImpl(appearanceMock, propertiesMock, listStoreMock);
        uut.globalHandlerRegistrations = globalRegistrationMock;
    }

    @Test
    public void testOnUnload() {
        /** CALL METHOD UNDER TEST **/
        uut.onUnload();

        verify(handlerMock, times(2)).removeHandler();
        verify(globalRegistrationMock).clear();
    }
}
