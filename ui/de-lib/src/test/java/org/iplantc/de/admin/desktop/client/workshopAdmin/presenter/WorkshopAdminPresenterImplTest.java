package org.iplantc.de.admin.desktop.client.workshopAdmin.presenter;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.admin.desktop.client.workshopAdmin.events.DeleteMembersClickedEvent;
import org.iplantc.de.admin.desktop.client.workshopAdmin.events.RefreshMembersClickedEvent;
import org.iplantc.de.admin.desktop.client.workshopAdmin.events.SaveMembersClickedEvent;
import org.iplantc.de.admin.desktop.client.workshopAdmin.gin.factory.WorkshopAdminViewFactory;
import org.iplantc.de.admin.desktop.client.workshopAdmin.model.MemberProperties;
import org.iplantc.de.admin.desktop.client.workshopAdmin.service.WorkshopAdminServiceFacade;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.groups.GroupAutoBeanFactory;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.client.models.groups.MemberSaveResult;
import org.iplantc.de.collaborators.client.events.UserSearchResultSelected;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import com.sencha.gxt.data.shared.ListStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.List;

/**
 * @author aramsey
 */
@RunWith(GwtMockitoTestRunner.class)
public class WorkshopAdminPresenterImplTest {

    @Mock WorkshopAdminView viewMock;
    @Mock WorkshopAdminViewFactory viewFactoryMock;
    @Mock WorkshopAdminServiceFacade serviceFacadeMock;
    @Mock GroupAutoBeanFactory factoryMock;
    @Mock WorkshopAdminView.WorkshopAdminViewAppearance appearanceMock;
    @Mock ListStore<Member> listStoreMock;
    @Mock List<Member> memberListMock;
    @Mock MemberSaveResult memberSaveResultMock;
    @Mock MemberProperties propertiesMock;
    @Mock List<String> stringListMock;
    @Mock Collaborator collaboratorMock;
    @Mock Iterator<Member> memberIteratorMock;
    @Mock Member memberMock;

    @Captor ArgumentCaptor<AsyncCallback<MemberSaveResult>> memberSaveCallbackCaptor;
    @Captor ArgumentCaptor<AsyncCallback<List<Member>>> memberListCallbackCaptor;

    private WorkshopAdminPresenterImpl uut;

    @Before
    public void setUp() throws Exception {
        when(appearanceMock.loadingMask()).thenReturn("mask");
        when(appearanceMock.partialGroupSaveMsg()).thenReturn("partial");
        when(viewFactoryMock.create(listStoreMock)).thenReturn(viewMock);

        uut = new WorkshopAdminPresenterImpl(viewFactoryMock,
                                             serviceFacadeMock,
                                             factoryMock,
                                             propertiesMock,
                                             appearanceMock) {
            @Override
            ListStore<Member> getMemberListStore(MemberProperties memberProperties) {
                return listStoreMock;
            }

        };

        uut.listStore = listStoreMock;
    }

    @Test
    public void verifyConstructor() {
        verify(viewFactoryMock).create(eq(listStoreMock));

        verify(viewMock).addGlobalEventHandler(eq(UserSearchResultSelected.TYPE), isA(
                WorkshopAdminPresenterImpl.UserSearchResultSelectedEventHandler.class));

        verify(viewMock).addLocalEventHandler(eq(DeleteMembersClickedEvent.TYPE), isA(
                WorkshopAdminPresenterImpl.DeleteMembersClickedEventHandler.class));

        verify(viewMock).addLocalEventHandler(eq(SaveMembersClickedEvent.TYPE), isA(
                WorkshopAdminPresenterImpl.SaveMembersClickedEventHandler.class));

        verify(viewMock).addLocalEventHandler(eq(RefreshMembersClickedEvent.TYPE), isA(
                RefreshMembersClickedEvent.RefreshMembersClickedEventHandler.class));

        verifyNoMoreInteractions(viewMock, viewFactoryMock, listStoreMock);
    }

    @Test
    public void testGo() throws Exception {
        HasOneWidget containerMock = mock(HasOneWidget.class);
        WorkshopAdminPresenterImpl spy = spy(uut);

        spy.go(containerMock);
        verify(containerMock).setWidget(viewMock);
        verify(spy).updateView();
    }

    @Test
    public void testUpdateView() throws Exception {

        /** CALL METHOD UNDER TEST **/
        uut.updateView();

        verify(viewMock).mask(eq(appearanceMock.loadingMask()));
        verify(serviceFacadeMock).getMembers(memberListCallbackCaptor.capture());

        memberListCallbackCaptor.getValue().onSuccess(memberListMock);
        verify(listStoreMock).replaceAll(eq(memberListMock));
        verify(viewMock).unmask();

    }

    @Test
    public void testSaveMembers_NoFailures() {

        when(stringListMock.isEmpty()).thenReturn(true);
        when(memberSaveResultMock.getFailures()).thenReturn(stringListMock);
        when(memberSaveResultMock.getMembers()).thenReturn(memberListMock);

        /** CALL METHOD UNDER TEST **/
        uut.saveMembers(memberListMock);

        verify(viewMock).mask(eq(appearanceMock.loadingMask()));
        verify(serviceFacadeMock).saveMembers(eq(memberListMock), memberSaveCallbackCaptor.capture());

        memberSaveCallbackCaptor.getValue().onSuccess(memberSaveResultMock);

        verify(listStoreMock).replaceAll(eq(memberListMock));
        verify(viewMock).unmask();
    }
}
