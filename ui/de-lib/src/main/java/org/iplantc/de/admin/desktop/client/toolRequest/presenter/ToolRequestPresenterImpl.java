package org.iplantc.de.admin.desktop.client.toolRequest.presenter;

import org.iplantc.de.admin.desktop.client.toolRequest.ToolRequestView;
import org.iplantc.de.admin.desktop.client.toolRequest.service.ToolRequestServiceFacade;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import java.util.List;

/**
 * @author jstroot
 */
public class ToolRequestPresenterImpl implements ToolRequestView.Presenter {

    private final ToolRequestView view;
    private final ToolRequestServiceFacade toolReqService;
    private final UserInfo userInfo;
    private final ToolRequestPresenterAppearance appearance;

    @Inject
    ToolRequestPresenterImpl(final ToolRequestView view,
                             final ToolRequestServiceFacade toolReqService,
                             final UserInfo userInfo,
                             final ToolRequestPresenterAppearance appearance) {
        this.view = view;
        this.toolReqService = toolReqService;
        this.userInfo = userInfo;
        this.appearance = appearance;
        view.setPresenter(this);
    }

    @Override
    public void updateToolRequest(String id, final ToolRequestUpdate update) {
        toolReqService.updateToolRequest(id, update, new AsyncCallback<ToolRequestDetails>() {

            @Override
            public void onSuccess(ToolRequestDetails result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.toolRequestUpdateSuccessMessage()));
                view.update(update, result);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    @Override
    public void fetchToolRequestDetails(ToolRequest toolRequest) {
        view.maskDetailsPanel(appearance.getToolRequestDetailsLoadingMask());
        toolReqService.getToolRequestDetails(toolRequest, new AsyncCallback<ToolRequestDetails>() {

            @Override
            public void onSuccess(ToolRequestDetails result) {
                view.unmaskDetailsPanel();
                view.setDetailsPanel(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                view.unmaskDetailsPanel();
                ErrorHandler.post(caught);
            }
        });
    }

    @Override
    public void go(HasOneWidget container) {
        view.mask(appearance.getToolRequestsLoadingMask());
        container.setWidget(view);
        toolReqService.getToolRequests(null, userInfo.getUsername(), new AsyncCallback<List<ToolRequest>>() {

            @Override
            public void onSuccess(List<ToolRequest> result) {
                view.unmask();
                view.setToolRequests(result);
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
        view.asWidget().ensureDebugId(baseId + Belphegor.ToolRequestIds.VIEW);
    }
}
