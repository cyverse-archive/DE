package org.iplantc.de.admin.desktop.client.permIdRequest.presenter;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermIdRequestAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermIdRequestView;
import org.iplantc.de.admin.desktop.client.permIdRequest.view.PermIdRequestView.Presenter;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplateList;
import org.iplantc.de.client.models.identifiers.PermanentIdRequest;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.views.metadata.DiskResourceMetadataViewImpl;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.List;

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
                            // mdPresenter.setDiskResourceMetadata(new
                            // DiskResourceMetadataUpdateCallback(ManageMetadataDialog.this));
                        }

                    }
                });
                cmb.show();
            } else {
                metadataDialog.mask(I18N.DISPLAY.loadingMask());
                // mdPresenter.setDiskResourceMetadata(new
                // DiskResourceMetadataUpdateCallback(ManageMetadataDialog.this));
            }
        }
    }

    @Inject
    PermIdRequestView view;

    final DiskResourceServiceFacade drsvc;

    final PermIdRequestAdminServiceFacade prsvc;

    private PermanentIdRequest selectedRequest;

    MetadataView metadataView;

    final DiskResourceUtil diskResourceUtil = DiskResourceUtil.getInstance();

    private final Dialog metadataDialog;

    @Inject
    public PermanentIdRequestPresenter(DiskResourceServiceFacade drsvc,
                                       PermIdRequestAdminServiceFacade prsvc) {
        this.drsvc = drsvc;
        this.prsvc = prsvc;
        metadataDialog = new Dialog();
        metadataDialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
        metadataDialog.setHeadingHtml("Metadata");
        metadataDialog.setSize("600px", "400px");
        metadataDialog.getButton(PredefinedButton.OK).addSelectHandler(new OkSelectHandler());
        metadataDialog.getButton(PredefinedButton.CANCEL).addSelectHandler(new CancelSelectHandler());
    }

    @Override
    public void fetchMetadata() {
        drsvc.getDiskResourceMetaData(selectedRequest.getFolder(),
                                      new AsyncCallback<DiskResourceMetadataList>() {
                                          @Override
                                          public void onSuccess(final DiskResourceMetadataList result) {
                                              metadataView = new DiskResourceMetadataViewImpl(diskResourceUtil.isWritable(selectedRequest.getFolder()));
                                              metadataView.loadMetadata(result.getMetadata());

                                              final DiskResourceMetadataTemplateList metadataTemplateList = result.getMetadataTemplates();
                                              if (metadataTemplateList != null) {
                                                  final List<DiskResourceMetadataTemplate> templates = metadataTemplateList.getTemplates();
                                                  if (templates != null && !templates.isEmpty()) {
                                                      metadataView.loadMetadataTemplate(templates.get(0));
                                                  }
                                              }

                                              metadataDialog.setWidget(metadataView);
                                              metadataDialog.show();

                                          }

                                          @Override
                                          public void onFailure(Throwable caught) {
                                              ErrorHandler.post(caught);
                                          }
                                      });

    }

    @Override
    public void saveMetadata() {

    }

    @Override
    public void setSelectedRequest(PermanentIdRequest request) {
        this.selectedRequest = request;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void getPermIdRequests() {
        prsvc.getPermanentIdRequests(new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig("Unable to retrieve DOI request!"));
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig("got requests!"));

            }
        });

    }

    @Override
    public void loadPermIdRequests() {
        // TODO Auto-generated method stub

    }

}
