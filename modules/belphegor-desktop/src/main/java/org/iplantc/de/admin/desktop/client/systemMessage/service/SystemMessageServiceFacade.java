package org.iplantc.de.admin.desktop.client.systemMessage.service;

import org.iplantc.de.client.models.systemMessages.SystemMessage;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface SystemMessageServiceFacade {

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/NotificationAgent#admin---listing-system-notifications"
     * >Notification Agent Doc</a>
     * 
     * @param callback
     */
    void getSystemMessages(AsyncCallback<List<SystemMessage>> callback);

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/NotificationAgent#admin---adding-a-system-notification"
     * >Notification Agent Doc</a>
     * 
     * @param msgToAdd
     * @param callback
     */
    void addSystemMessage(SystemMessage msgToAdd, AsyncCallback<SystemMessage> callback);

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/NotificationAgent#admin---updating-a-system-notification"
     * >Notification Agent Doc</a>
     * 
     * @param updatedMsg
     * @param callback
     */
    void updateSystemMessage(SystemMessage updatedMsg, AsyncCallback<SystemMessage> callback);

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/NotificationAgent#admin---deleting-a-system-notification-by-uuid"
     * >Notification Agent Doc</a>
     * 
     * @param msgToDelete
     * @param callback
     */
    void deleteSystemMessage(SystemMessage msgToDelete, AsyncCallback<Void> callback);

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/NotificationAgent#admin---getting-all-system-notification-types"
     * >Notification Agent Doc</a>
     * 
     * @param callback
     */
    void getSystemMessageTypes(AsyncCallback<List<String>> callback);

}
