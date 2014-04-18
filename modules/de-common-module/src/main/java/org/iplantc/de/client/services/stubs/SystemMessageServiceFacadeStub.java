package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.sysMsgs.IdList;
import org.iplantc.de.client.models.sysMsgs.MessageList;
import org.iplantc.de.client.services.SystemMessageServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SystemMessageServiceFacadeStub implements SystemMessageServiceFacade {
    @Override
    public void getAllMessages(AsyncCallback<MessageList> callback) {

    }

    @Override
    public void getNewMessages(AsyncCallback<MessageList> callback) {

    }

    @Override
    public void getUnseenMessages(AsyncCallback<MessageList> callback) {

    }

    @Override
    public void markAllReceived(AsyncCallback<Void> callback) {

    }

    @Override
    public void markReceived(IdList msgIds, AsyncCallback<Void> callback) {

    }

    @Override
    public void acknowledgeMessages(IdList msgIds, AsyncCallback<Void> callback) {

    }

    @Override
    public void hideMessages(IdList msgIds, AsyncCallback<Void> callback) {

    }
}
