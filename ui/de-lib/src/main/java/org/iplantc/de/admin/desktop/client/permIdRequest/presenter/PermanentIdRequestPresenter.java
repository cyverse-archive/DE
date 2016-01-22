package org.iplantc.de.admin.desktop.client.permIdRequest.presenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermanentIdRequestAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestPresenterAppearance;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.Presenter;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestList;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IplantErrorDialog;
import org.iplantc.de.resources.client.messages.I18N;

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
        view.fetchMetadata(selectedRequest.getFolder(), appearance, drsvc);
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
        prsvc.getPermanentIdRequests(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig(appearance.requestLoadFailure()));
            }

            @Override
            public void onSuccess(String result) {
                final AutoBean<PermanentIdRequestList> decode = AutoBeanCodex.decode(factory,
                                                                                     PermanentIdRequestList.class,
                                                                                     result);

                view.loadRequests(decode.as().getRequests());
            }
        });

    }

    @Override
    public void loadPermIdRequests() {
        getPermIdRequests();
    }

    @Override
    public void updateRequest(final PermanentIdRequestUpdate update) {
        if (selectedRequest != null && update != null) {
            view.mask(I18N.DISPLAY.loadingMask());
            prsvc.updatePermanentIdRequestStatus(selectedRequest.getId(),
                                                 update,
                                                 new AsyncCallback<String>() {

                                                     @Override
                                                     public void onFailure(Throwable caught) {
                                                         view.unmask();
                                                         IplantAnnouncer.getInstance()
                                                                        .schedule(new ErrorAnnouncementConfig(appearance.statusUpdateFailure()));

                                                     }

                                                     @Override
                                                     public void onSuccess(String result) {
                                                         view.unmask();
                                                         IplantAnnouncer.getInstance()
                                                                        .schedule(new SuccessAnnouncementConfig(appearance.statusUpdateSuccess()));
                                                         selectedRequest.setStatus(update.getStatus());
                                                         view.update(selectedRequest);
                                                     }
                                                 });
        }
    }

    @Override
    public void createPermanentId() {
        if (selectedRequest != null) {
            view.mask(I18N.DISPLAY.loadingMask());
            prsvc.createPermanentId(selectedRequest.getId(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    view.unmask();
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.createPermIdFailure()));
                    IplantErrorDialog ied = new IplantErrorDialog(I18N.DISPLAY.error(), caught.getMessage());
                    ied.show();
                }

                @Override
                public void onSuccess(String result) {
                    view.unmask();
                    IplantAnnouncer.getInstance()
                                   .schedule(new SuccessAnnouncementConfig(appearance.createPermIdSucess()));

                }
            });
        }
    }

}
