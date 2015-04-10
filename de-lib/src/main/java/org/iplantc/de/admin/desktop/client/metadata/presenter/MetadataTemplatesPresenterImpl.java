package org.iplantc.de.admin.desktop.client.metadata.presenter;

import org.iplantc.de.admin.desktop.client.metadata.service.MetadataTemplateAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.metadata.view.EditMetadataTemplateView;
import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

import java.util.List;

public class MetadataTemplatesPresenterImpl implements TemplateListingView.Presenter {

    private final TemplateListingView view;
    private final DiskResourceServiceFacade drSvcFac;
    private final MetadataTemplateAdminServiceFacade mdSvcFac;
    private final EditMetadataTemplateView editView;

    @Inject
    MetadataTemplatesPresenterImpl(final TemplateListingView view,
                                   final EditMetadataTemplateView editView,
                                   final DiskResourceServiceFacade drSvcFac,
                                   final MetadataTemplateAdminServiceFacade mdSvcFac) {
        this.view = view;
        this.editView = editView;
        this.drSvcFac = drSvcFac;
        this.mdSvcFac = mdSvcFac;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
        view.mask("loading");
        view.setPresenter(this);
        drSvcFac.getMetadataTemplateListing(new AsyncCallback<List<MetadataTemplateInfo>>() {

            @Override
            public void onSuccess(List<MetadataTemplateInfo> result) {
                view.unmask();
                view.loadTemplates(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                ErrorHandler.post("Unable to retrieve Templates!", caught);

            }
        });
    }

    @Override
    public void deleteTemplate(final MetadataTemplateInfo template) {
        ConfirmMessageBox cmb = new ConfirmMessageBox("Confirm", "Delete this template ?");
        cmb.addDialogHideHandler(new DialogHideHandler(){

            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton().equals(PredefinedButton.OK)
                        || event.getHideButton().equals(PredefinedButton.YES)) {
                    mdSvcFac.deleteTemplate(template.getId(), new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ErrorHandler.post("Unable to delete template!", caught);

                        }

                        @Override
                        public void onSuccess(String result) {
                            IplantAnnouncer.getInstance()
                                           .schedule(new SuccessAnnouncementConfig("Template deleted!"));
                            view.remove(template);

                        }
                    });
                }

            }
            
        });
        cmb.show();
    }

    @Override
    public void editTemplate(MetadataTemplateInfo template) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTemplate() {
        IPlantDialog d = new IPlantDialog();
        d.add(editView.asWidget());
        d.setSize("800px", "600px");
        d.show();

    }

}
