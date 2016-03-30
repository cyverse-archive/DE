package org.iplantc.de.admin.desktop.client.systemMessage;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.systemMessages.SystemMessage;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface SystemMessageView extends IsWidget, IsMaskable {

    public interface SystemMessageViewAppearance {

        String activationDateColumnLabel();

        int activationDateColumnWidth();

        String add();

        ImageResource addIcon();

        String createSystemMsgDlgHeading();

        String deactivationDateColumnLabel();

        int deactivationDateColumnWidth();

        String delete();

        ImageResource deleteIcon();

        String dismissibleColumnLabel();

        int dismissibleColumnWidth();

        String editSystemMsgDlgHeading();

        int editSystemMsgDlgWidth();

        String messageColumnLabel();

        int messageColumnWidth();

        String submitButtonText();

        String typeColumnLabel();

        int typeColumnWidth();

        String systemMsgDlgTypeLabel();

        String systemMsgDlgMessageLabel();

        String systemMsgDlgActivationDateLabel();

        String systemMsgDlgActivationTimeLabel();

        String systemMsgDlgDeactivationDateLabel();

        String systemMsgDeactivationTimeLabel();

        String systemMsgDlgDismissibleLabel();

        String systemMsgDlgLoginsDisabledLabel();
    }
    public interface Presenter {
        public interface SystemMessagePresenterAppearance {

            String addSystemMessageSuccessMessage();

            String deleteSystemMessageSuccessMessage();

            String editSystemMessageSuccessMessage();

            String getSystemMessagesLoadingMask();
        }

        /**
         * Adds a system message by calling the {@link org.iplantc.de.admin.desktop.client.systemMessage.service.SystemMessageServiceFacade#addSystemMessage}
         * endpoint.
         * 
         * Upon success, the view will be updated with the resulting System message.
         */
        void addSystemMessage(SystemMessage msg);

        /**
         * Submits the given systems message to be updated by calling the
         * {@link org.iplantc.de.admin.desktop.client.systemMessage.service.SystemMessageServiceFacade#updateSystemMessage} endpoint.
         * 
         * Upon success, the view will be updated appropriately.
         */
        void editSystemMessage(SystemMessage msg);

        /**
         * Submits the given system message to be deleted by calling the
         * {@link org.iplantc.de.admin.desktop.client.systemMessage.service.SystemMessageServiceFacade#deleteSystemMessage} endpoint.
         * 
         * Upon success, the message will be removed from the view.
         */
        void deleteSystemMessage(SystemMessage msg);

        void go(HasOneWidget container);

        void setViewDebugId(String baseId);

        List<String> getAnnouncementTypes();

    }

    void setPresenter(Presenter presenter);

    void setSystemMessages(List<SystemMessage> systemMessages);

    void addSystemMessage(SystemMessage systemMessage);

    void updateSystemMessage(SystemMessage updatedSystemMessage);

    void editSystemMessage(SystemMessage sysMsgToEdit);

    void deleteSystemMessage(SystemMessage msgToDelete);

}
