package org.iplantc.de.admin.desktop.client.workshopAdmin.presenter;

import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.util.tools.shared.StringUtils;
import com.google.inject.Inject;
import com.sencha.gxt.data.shared.ListStore;
import org.iplantc.de.admin.desktop.client.workshopAdmin.WorkshopAdminView;
import org.iplantc.de.admin.desktop.client.workshopAdmin.events.DeleteMembersClickedEvent;
import org.iplantc.de.admin.desktop.client.workshopAdmin.events.SaveMembersClickedEvent;
import org.iplantc.de.admin.desktop.client.workshopAdmin.gin.factory.WorkshopAdminViewFactory;
import org.iplantc.de.admin.desktop.client.workshopAdmin.model.MemberProperties;
import org.iplantc.de.admin.desktop.client.workshopAdmin.service.WorkshopAdminServiceFacade;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.client.models.groups.GroupAutoBeanFactory;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.client.models.groups.MemberSaveResult;
import org.iplantc.de.collaborators.client.events.UserSearchResultSelected;
import org.iplantc.de.commons.client.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dennis
 */
public class WorkshopAdminPresenterImpl implements WorkshopAdminView.Presenter {

    private final WorkshopAdminView view;
    private final WorkshopAdminServiceFacade serviceFacade;
    private final GroupAutoBeanFactory groupAutoBeanFactory;
    private final WorkshopAdminView.WorkshopAdminViewAppearance appearance;
    private final ListStore<Member> listStore;

    private final class UserSearchResultSelectedEventHandler
            implements UserSearchResultSelected.UserSearchResultSelectedEventHandler {

        private Member memberFromCollaborator(Collaborator collaborator) {
            Member member = groupAutoBeanFactory.getMember().as();
            member.setId(collaborator.getUserName());
            member.setAttributes(new ArrayList<String>());
            member.setEmail(collaborator.getEmail());
            member.setFirstName(collaborator.getFirstName());
            member.setInstitution(collaborator.getInstitution());
            member.setLastName(collaborator.getLastName());
            member.setName(collaborator.getName());
            return member;
        }

        private boolean listContainsMember(List<Member> members, Member newMember) {
            String newMemberId = newMember.getId();
            if (newMemberId != null) {
                for (Member member : members) {
                    if (member.getId() != null && member.getId().equals(newMemberId)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onUserSearchResultSelected(UserSearchResultSelected event) {

            // Ignore the event if it wasn't initiated by the workshop admin panel.
            if (!event.matchesTag(view.userSearchEventTag)) {
                return;
            }

            // Add the user to the list if not there already.
            Member member = memberFromCollaborator(event.getCollaborator());
            if (!listContainsMember(listStore.getAll(), member)) {
                listStore.add(member);
            }
        }
    }

    private final class DeleteMembersClickedEventHandler
            implements DeleteMembersClickedEvent.DeleteMembersClickedEventHandler {

        @Override
        public void onDeleteMembersClicked(DeleteMembersClickedEvent event) {
            for (Member member : event.getMembers()) {
                listStore.remove(member);
            }
        }
    }

    private final class SaveMembersClickedEventHandler
            implements SaveMembersClickedEvent.SaveMembersClickedEventHandler {

        @Override
        public void onSaveMembersClicked(SaveMembersClickedEvent event) {
            saveMembers(event.getMembers());
        }
    }

    @Inject
    public WorkshopAdminPresenterImpl(final WorkshopAdminViewFactory viewFactory,
                                      final WorkshopAdminServiceFacade serviceFacade,
                                      final GroupAutoBeanFactory groupAutoBeanFactory,
                                      final MemberProperties memberProperties,
                                      final WorkshopAdminView.WorkshopAdminViewAppearance appearance) {
        listStore = new ListStore<>(memberProperties.id());
        view = viewFactory.create(listStore);
        this.serviceFacade = serviceFacade;
        this.groupAutoBeanFactory = groupAutoBeanFactory;
        this.appearance = appearance;
        registerEventHandlers();
    }

    private void registerEventHandlers() {
        view.addGlobalEventHandler(UserSearchResultSelected.TYPE, new UserSearchResultSelectedEventHandler());
        view.addLocalEventHandler(DeleteMembersClickedEvent.TYPE, new DeleteMembersClickedEventHandler());
        view.addLocalEventHandler(SaveMembersClickedEvent.TYPE, new SaveMembersClickedEventHandler());
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
        updateView();
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.WorkshopAdminIds.VIEW);
    }

    private void updateView() {
        view.mask(appearance.loadingMask());
        serviceFacade.getMembers(new AsyncCallback<List<Member>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(List<Member> members) {
                listStore.replaceAll(members);
                view.unmask();
            }
        });
    }

    private void saveMembers(List<Member> members) {
        view.mask(appearance.loadingMask());
        serviceFacade.saveMembers(members, new AsyncCallback<MemberSaveResult>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }

            @Override
            public void onSuccess(MemberSaveResult result) {
                if (!result.getFailures().isEmpty()) {
                    ErrorHandler.post(appearance.partialGroupSaveMsg());
                }
                listStore.replaceAll(result.getMembers());
                view.unmask();
            }
        });
    }
}
