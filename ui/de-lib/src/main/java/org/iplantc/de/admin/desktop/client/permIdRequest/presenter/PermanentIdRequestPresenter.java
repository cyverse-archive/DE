package org.iplantc.de.admin.desktop.client.permIdRequest.presenter;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermanentIdRequestAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestPresenterAppearance;
import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.Presenter;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestAutoBeanFactory;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestList;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.presenters.metadata.MetadataPresenterImpl;
import org.iplantc.de.diskResource.client.views.metadata.DiskResourceMetadataViewImpl;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * 
 * 
 * @author sriram
 * 
 */
public class PermanentIdRequestPresenter implements Presenter {

    private class CancelSelectHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            metadataDialog.hide();
        }
    }

    private class OkSelectHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {

            if (!metadataView.isValid()) {
                ConfirmMessageBox cmb = new ConfirmMessageBox("Error",
                                                              "Metadata Invlid! Please fix the errors!");
                cmb.addDialogHideHandler(new DialogHideHandler() {

                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if (event.getHideButton().equals(PredefinedButton.YES)) {
                            metadataDialog.mask(I18N.DISPLAY.loadingMask());
                            meta_pre.setDiskResourceMetadata(new DiskResourceMetadataUpdateCallback(metadataDialog));
                        }

                    }
                });
                cmb.show();
            } else {
                metadataDialog.mask(I18N.DISPLAY.loadingMask());
                meta_pre.setDiskResourceMetadata(new DiskResourceMetadataUpdateCallback(metadataDialog));
            }
        }
    }

    PermanentIdRequestView view;

    final DiskResourceServiceFacade drsvc;

    final PermanentIdRequestAdminServiceFacade prsvc;

    private PermanentIdRequest selectedRequest;

    MetadataView metadataView;

    final DiskResourceUtil diskResourceUtil = DiskResourceUtil.getInstance();

    private final IPlantDialog metadataDialog;

    private final PermanentIdRequestAutoBeanFactory factory;

    private MetadataView.Presenter meta_pre;

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
        metadataDialog = new IPlantDialog();
        metadataDialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        metadataDialog.setHeadingHtml("Metadata");
        metadataDialog.setSize("600px", "400px");
        metadataDialog.getButton(PredefinedButton.OK).addSelectHandler(new OkSelectHandler());
        metadataDialog.getButton(PredefinedButton.CANCEL).addSelectHandler(new CancelSelectHandler());
        view.setPresenter(this);
    }

    @Override
    public void fetchMetadata() {
        metadataView = new DiskResourceMetadataViewImpl(diskResourceUtil.isWritable(selectedRequest.getFolder()));
        meta_pre = new MetadataPresenterImpl(selectedRequest.getFolder(), metadataView, drsvc);
        meta_pre.go(metadataDialog);
        metadataDialog.show();
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
        prsvc.updatePermanentIdRequestStatus(selectedRequest.getId(),
                                             update,
                                             new AsyncCallback<String>() {

                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     IplantAnnouncer.getInstance()
                                                                    .schedule(new ErrorAnnouncementConfig(appearance.statusUpdateFailure()));

                                                 }

                                                 @Override
                                                 public void onSuccess(String result) {
                                                     IplantAnnouncer.getInstance()
                                                                    .schedule(new SuccessAnnouncementConfig(appearance.statusUpdateSuccess()));
                                                     selectedRequest.setStatus(update.getStatus());
                                                     view.update(selectedRequest);
                                                 }
                                             });
    }

    @Override
    public void createPermanentId() {
        prsvc.createPermanentId(selectedRequest.getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig(appearance.createPermIdFailure()));
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance()
                               .schedule(new SuccessAnnouncementConfig(appearance.createPermIdSucess()));

            }
        });
    }

}
