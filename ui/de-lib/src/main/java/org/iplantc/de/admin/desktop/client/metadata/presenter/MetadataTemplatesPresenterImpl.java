package org.iplantc.de.admin.desktop.client.metadata.presenter;

import org.iplantc.de.admin.desktop.client.metadata.service.MetadataTemplateAdminServiceFacade;
import org.iplantc.de.admin.desktop.client.metadata.view.EditMetadataTemplateView;
import org.iplantc.de.admin.desktop.client.metadata.view.TemplateListingView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Strings;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetadataTemplatesPresenterImpl implements TemplateListingView.Presenter {

    private final TemplateListingView view;
    private final DiskResourceServiceFacade drSvcFac;
    private final MetadataTemplateAdminServiceFacade mdSvcFac;
    private final EditMetadataTemplateView editView;
    private final DiskResourceAutoBeanFactory drFac;
    private final TemplateListingView.Presenter.MetadataPresenterAppearance appearance;

    Logger LOG = Logger.getLogger("MetadataTemplatesPresenterImpl");

    @Inject
    MetadataTemplatesPresenterImpl(final TemplateListingView view,
                                   final EditMetadataTemplateView editView,
                                   final DiskResourceServiceFacade drSvcFac,
                                   final MetadataTemplateAdminServiceFacade mdSvcFac,
                                   final DiskResourceAutoBeanFactory drFac,
                                   final TemplateListingView.Presenter.MetadataPresenterAppearance appearance) {
        this.view = view;
        this.editView = editView;
        this.drSvcFac = drSvcFac;
        this.mdSvcFac = mdSvcFac;
        this.drFac = drFac;
        this.appearance = appearance;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
        view.setPresenter(this);
        loadTemplates();
    }

    private void loadTemplates() {
        view.mask("loading");
        mdSvcFac.getMetadataTemplateListing(new AsyncCallback<List<MetadataTemplateInfo>>() {

            @Override
            public void onSuccess(List<MetadataTemplateInfo> result) {
                view.unmask();
                view.loadTemplates(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                ErrorHandler.post(appearance.templateRetrieveError(), caught);

            }
        });
    }

    @Override
    public void deleteTemplate(final MetadataTemplateInfo template) {
        ConfirmMessageBox cmb = new ConfirmMessageBox("Confirm", appearance.deleteTemplateConfirm());
        cmb.addDialogHideHandler(new DialogHideHandler(){

            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton().equals(PredefinedButton.OK)
                        || event.getHideButton().equals(PredefinedButton.YES)) {
                    mdSvcFac.deleteTemplate(template.getId(), new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ErrorHandler.post(appearance.deleteTemplateError(), caught);

                        }

                        @Override
                        public void onSuccess(String result) {
                            IplantAnnouncer.getInstance()
                                           .schedule(new SuccessAnnouncementConfig(appearance.deleteTemplateSuccess()));
                            loadTemplates();
                        }
                    });
                }

            }
            
        });
        cmb.show();
        setMsgBoxDebugIds(cmb);
    }

    private void setMsgBoxDebugIds(ConfirmMessageBox cmb) {
        cmb.ensureDebugId(Belphegor.MetadataIds.DELETE_MSG_BOX);
        cmb.getButton(PredefinedButton.YES).ensureDebugId(Belphegor.MetadataIds.DELETE_MSG_BOX + Belphegor.MetadataIds.YES);
        cmb.getButton(PredefinedButton.NO).ensureDebugId(Belphegor.MetadataIds.DELETE_MSG_BOX + Belphegor.MetadataIds.NO);
    }

    @Override
    public void editTemplate(MetadataTemplateInfo template) {
        drSvcFac.getMetadataTemplate(template.getId(), new AsyncCallback<MetadataTemplate>() {

            @Override
            public void onSuccess(MetadataTemplate result) {
                final IPlantDialog id = createEditDialog();
                editView.edit(result);
                id.addOkButtonSelectHandler(new SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        if (editView.validate()) {
                            MetadataTemplate template = editView.getTemplate();
                            Splittable sp = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(template));
                            LOG.log(Level.SEVERE, sp.getPayload());
                            doAddOrUpdate(id, template.getId(), sp.getPayload());
                        } else {
                            IplantAnnouncer.getInstance()
                                           .schedule(new ErrorAnnouncementConfig(appearance.enumError()));
                        }

                    }
                });

                id.addCancelButtonSelectHandler(new SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        id.hide();

                    }
                });
                id.show();
                setDialogDebugIds(id);

            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.templateRetrieveError(), caught);
            }
        });

    }

    private void doAddOrUpdate(final IPlantDialog d, final String templateid, final String template) {
        if (Strings.isNullOrEmpty(templateid)) {

        mdSvcFac.addTemplate(template, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                d.hide();
                    ErrorHandler.post(appearance.addTemplateError(), caught);

            }

            @Override
            public void onSuccess(String result) {
                    IplantAnnouncer.getInstance()
                                   .schedule(new SuccessAnnouncementConfig(appearance.addTemplateError()));
                d.hide();
                    loadTemplates();
            }
        });
        } else {
            mdSvcFac.updateTemplate(templateid, template, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    d.hide();
                    ErrorHandler.post(appearance.updateTemplateError(), caught);

                }

                @Override
                public void onSuccess(String result) {
                    IplantAnnouncer.getInstance()
                                   .schedule(new SuccessAnnouncementConfig(appearance.updateTemplateSuccess()));
                    d.hide();
                    loadTemplates();
                }
            });
        }
    }

    @Override
    public void addTemplate() {
        final IPlantDialog d = createEditDialog();
        d.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                if (editView.validate()) {
                    MetadataTemplate template = editView.getTemplate();
                    Splittable sp = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(template));
                    LOG.log(Level.SEVERE, sp.getPayload());
                    doAddOrUpdate(d, null, sp.getPayload());
                } else {
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.enumError()));
                }
            }
        });
        d.addCancelButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                d.hide();

            }
        });
        d.show();
        setDialogDebugIds(d);

    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.MetadataIds.VIEW);
    }

    private void setDialogDebugIds(IPlantDialog dialog) {
        dialog.ensureDebugId(Belphegor.MetadataIds.EDIT_DIALOG);
        dialog.getOkButton().ensureDebugId(Belphegor.MetadataIds.EDIT_DIALOG + Belphegor.MetadataIds.OK);
    }

    private IPlantDialog createEditDialog() {
        final IPlantDialog d = new IPlantDialog();
        d.setHeadingText(appearance.templateAttributeEditorHeading());
        editView.reset();
        d.add(editView.asWidget());
        d.setSize("800px", "600px");
        d.setHideOnButtonClick(false);
        return d;
    }

}
