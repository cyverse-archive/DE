package org.iplantc.de.admin.desktop.client.systemMessage.presenter;

import org.iplantc.de.admin.desktop.client.systemMessage.SystemMessageView;
import org.iplantc.de.admin.desktop.client.systemMessage.service.SystemMessageServiceFacade;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.systemMessages.SystemMessage;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.List;

/**
 * @author jstroot
 */
public class SystemMessagePresenterImpl implements SystemMessageView.Presenter {

    private final SystemMessageServiceFacade sysMsgService;
    private final SystemMessagePresenterAppearance appearance;
    private final SystemMessageView view;
    private List<String> systemMessageTypes = Lists.newArrayList();

    @Inject
    public SystemMessagePresenterImpl(final SystemMessageView view,
                                      final SystemMessageServiceFacade sysMsgService,
                                      final SystemMessagePresenterAppearance appearance) {
        this.view = view;
        this.sysMsgService = sysMsgService;
        this.appearance = appearance;
        view.setPresenter(this);

        // Fetch all system messages
        sysMsgService.getSystemMessageTypes(new AsyncCallback<List<String>>() {

            @Override
            public void onSuccess(List<String> result) {
                if ((result != null) && !result.isEmpty()) {
                    systemMessageTypes.addAll(result);
                } else {
                    systemMessageTypes = Collections.emptyList();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }


    @Override
    public void go(HasOneWidget container) {
        view.mask(appearance.getSystemMessagesLoadingMask());
        container.setWidget(view);
        sysMsgService.getSystemMessages(new AsyncCallback<List<SystemMessage>>() {
    
            @Override
            public void onSuccess(List<SystemMessage> result) {
                view.unmask();
                view.setSystemMessages(result);
            }
    
            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                ErrorHandler.post(caught);
            }
        });
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.SystemMessageIds.VIEW);
    }


    @Override
    public void addSystemMessage(SystemMessage msg) {
        sysMsgService.addSystemMessage(msg, new AsyncCallback<SystemMessage>() {

            @Override
            public void onSuccess(SystemMessage result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.addSystemMessageSuccessMessage()));
                view.addSystemMessage(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    @Override
    public void editSystemMessage(SystemMessage msg) {
        sysMsgService.updateSystemMessage(msg, new AsyncCallback<SystemMessage>() {

            @Override
            public void onSuccess(SystemMessage result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.editSystemMessageSuccessMessage()));
                view.updateSystemMessage(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    @Override
    public void deleteSystemMessage(final SystemMessage msg) {
        sysMsgService.deleteSystemMessage(msg, new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.deleteSystemMessageSuccessMessage()));
                view.deleteSystemMessage(msg);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    @Override
    public List<String> getAnnouncementTypes() {
        return systemMessageTypes;
    }

}
