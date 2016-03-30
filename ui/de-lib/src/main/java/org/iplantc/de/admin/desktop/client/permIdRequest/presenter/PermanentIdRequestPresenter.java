package org.iplantc.de.admin.desktop.client.permIdRequest.presenter;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermanentIdRequestAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestPresenterAppearance;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.Presenter;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.UpdatePermanentIdRequestDialog;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestDetails;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestList;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IplantErrorDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * 
 * 
 * @author sriram
 * 
 */
public class PermanentIdRequestPresenter implements Presenter {

    PermanentIdRequestView view;

    final DiskResourceServiceFacade drsvc;

    final PermanentIdRequestAdminServiceFacade prsvc;

    private PermanentIdRequest selectedRequest;

    private final PermanentIdRequestAutoBeanFactory factory;

    private final PermanentIdRequestPresenterAppearance appearance;

    @Inject
    public PermanentIdRequestPresenter(DiskResourceServiceFacade drsvc,
                                       PermanentIdRequestAdminServiceFacade prsvc,
                                       PermanentIdRequestAutoBeanFactory factory,
                                       PermanentIdRequestView view,
                                       PermanentIdRequestPresenterAppearance appearance) {
        this.drsvc = drsvc;
        this.prsvc = prsvc;
        this.view = view;
        this.factory = factory;
        this.appearance = appearance;
        view.setPresenter(this);
    }

    @Override
    public void fetchMetadata() {
        final Folder selectedFolder = selectedRequest.getFolder();
        if (selectedFolder != null) {
            view.fetchMetadata(selectedFolder, appearance, drsvc);
        } else {
            final String errMessage = appearance.folderNotFound(selectedRequest.getOriginalPath());
            IplantAnnouncer.getInstance().schedule(new ErrorAnnouncementConfig(errMessage));
        }
    }

    @Override
    public void setSelectedRequest(PermanentIdRequest request) {
        this.selectedRequest = request;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
        loadPermIdRequests();

    }

    @Override
    public void getPermIdRequests() {
        view.mask(I18N.DISPLAY.loadingMask());
        prsvc.getPermanentIdRequests(new AsyncCallback<PermanentIdRequestList>() {

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig(appearance.requestLoadFailure()));
            }

            @Override
            public void onSuccess(PermanentIdRequestList result) {
               view.unmask();
               view.loadRequests(result.getRequests());
            }
        });

    }

    @Override
    public void loadPermIdRequests() {
        getPermIdRequests();
    }

    @Override
    public void doUpdateRequest(final PermanentIdRequestUpdate update) {
        if (selectedRequest != null && update != null) {
            view.mask(I18N.DISPLAY.loadingMask());
            prsvc.updatePermanentIdRequestStatus(selectedRequest.getId(),
                                                 update,
                                                 new AsyncCallback<String>() {

                                                     @Override
                                                     public void onFailure(Throwable caught) {
                                                         view.unmask();
                                                         IplantAnnouncer.getInstance()
                                                                        .schedule(new ErrorAnnouncementConfig(
                                                                                appearance.statusUpdateFailure()));

                                                     }

                                                     @Override
                                                     public void onSuccess(String result) {
                                                         view.unmask();
                                                         IplantAnnouncer.getInstance()
                                                                        .schedule(new SuccessAnnouncementConfig(
                                                                                appearance.statusUpdateSuccess()));
                                                         selectedRequest.setStatus(update.getStatus());
                                                         view.update(selectedRequest);
                                                     }
                                                 });
        }
    }

    @Override
    public void onUpdateRequest() {
        getRequestDetails(new AsyncCallback<PermanentIdRequestDetails>() {
            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                IplantErrorDialog ied = new IplantErrorDialog(I18N.DISPLAY.error(), caught.getMessage());
                ied.show();
            }

            @Override
            public void onSuccess(PermanentIdRequestDetails result) {
                view.unmask();
                final UpdatePermanentIdRequestDialog dialog = new UpdatePermanentIdRequestDialog(
                        selectedRequest.getStatus(),
                        result,
                        factory);
                dialog.setHeadingText(appearance.updateStatus());
                dialog.getOkButton().setText(appearance.update());
                dialog.getOkButton().addSelectHandler(new SelectEvent.SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        final PermanentIdRequestUpdate update = dialog.getPermanentIdRequestUpdate();
                        doUpdateRequest(update);
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public void createPermanentId() {
        if (selectedRequest != null) {
            view.mask(I18N.DISPLAY.loadingMask());
            prsvc.createPermanentId(selectedRequest.getId(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    view.unmask();
                    loadPermIdRequests();
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.createPermIdFailure()));
                    ErrorHandler.post(appearance.createPermIdFailure(), caught);
                }

                @Override
                public void onSuccess(String result) {
                    view.unmask();
                    IplantAnnouncer.getInstance()
                                   .schedule(new SuccessAnnouncementConfig(appearance.createPermIdSucess()));

                    //refresh page
                    loadPermIdRequests();

                }
            });
        }
    }

    @Override
    public void getRequestDetails(AsyncCallback<PermanentIdRequestDetails> callback) {
        if (selectedRequest != null) {
            view.mask(I18N.DISPLAY.loadingMask());
            prsvc.getRequestDetails(selectedRequest.getId(), callback);
        }

    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.PermIds.VIEW);
    }
}
