package org.iplantc.de.diskResource.client.presenters.metadata;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadata;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.DiskResourceUserMetadata;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.MetadataTemplateViewDialog;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.SelectMetadataTemplateDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jstroot sriram
 */
public class MetadataPresenterImpl implements MetadataView.Presenter {


    private class TemplateViewCancelSelectHandler implements SelectEvent.SelectHandler {

        private MetadataTemplateViewDialog mdView;

        public TemplateViewCancelSelectHandler(MetadataTemplateViewDialog mdView) {
            this.mdView = mdView;
        }

        @Override
        public void onSelect(SelectEvent event) {
            mdView.hide();
        }
    }

    private class TemplateViewOkSelectHandler implements SelectEvent.SelectHandler {

        private MetadataTemplateViewDialog mdView;
        private boolean writable;
        private MetadataView.Presenter mdPresenter;

        public TemplateViewOkSelectHandler(boolean writable,
                                           MetadataView.Presenter mdPresenter,
                                           MetadataTemplateViewDialog mdView) {
            this.writable = writable;
            this.mdView = mdView;
            this.mdPresenter = mdPresenter;
        }

        @Override
        public void onSelect(SelectEvent event) {
            if (!writable) {
                return;
            }

            if (!mdView.isValid()) {
                ConfirmMessageBox cmb =
                        new ConfirmMessageBox(appearance.error(), appearance.incomplete());
                cmb.addDialogHideHandler(new DialogHideHandler() {

                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if (event.getHideButton().equals(PredefinedButton.YES)) {
                            updateMetadataFromTemplateView();
                        }

                    }
                });
                cmb.show();
            } else {
                updateMetadataFromTemplateView();
            }
        }

        private void updateMetadataFromTemplateView() {
            mdView.mask(I18N.DISPLAY.loadingMask());
            ArrayList<DiskResourceMetadata> mdList = mdView.getMetadataFromTemplate();
            view.updateMetadataFromTemplateView(mdList);
        }
    }

    private final DiskResource resource;
    private final MetadataView view;
    private final DiskResourceServiceFacade drService;
    private List<MetadataTemplateInfo> templates;
    private List<DiskResourceMetadata> templateMd;
    private MetadataTemplateViewDialog templateView;

    final MetadataView.Presenter.Appearance appearance =
            GWT.create(MetadataView.Presenter.Appearance.class);
    private final static DiskResourceAutoBeanFactory autoBeanFactory =
            GWT.create(DiskResourceAutoBeanFactory.class);

    public MetadataPresenterImpl(final DiskResource selected,
                                 final MetadataView view,
                                 final DiskResourceServiceFacade drService) {
        this.resource = selected;
        this.view = view;
        this.drService = drService;
        view.setPresenter(this);
        view.mask();
        drService.getMetadataTemplateListing(new AsyncCallback<List<MetadataTemplateInfo>>() {
            @Override
            public void onFailure(Throwable arg0) {
                view.unmask();
                ErrorHandler.post(appearance.templateListingError(), arg0);
            }

            @Override
            public void onSuccess(final List<MetadataTemplateInfo> result) {
                templates = result;
                loadMetadata();
            }
        });
    }

    private void loadMetadata() {
        drService.getDiskResourceMetaData(resource, new AsyncCallback<DiskResourceMetadataList>() {
            @Override
            public void onSuccess(final DiskResourceMetadataList result) {
                view.loadMetadata(result.getOtherMetadata());

                final List<DiskResourceMetadata> userMdList = result.getUserMetadata();
                if (userMdList != null) {
                    if (templates != null && !templates.isEmpty()) {
                        view.loadUserMetadata(userMdList);
                    }
                }

                view.unmask();
            }


            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                ErrorHandler.post(caught);
            }
        });
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
    }

    @Override
    public void setDiskResourceMetadata(final DiskResourceMetadataUpdateCallback callback) {
        AsyncCallback<String> batchAvuCallback = new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        };

        DiskResourceUserMetadata umd =
                AutoBeanCodex.decode(autoBeanFactory, DiskResourceUserMetadata.class, "{}").as();
        umd.setAvus(view.getUserMetadata());
        drService.setDiskResourceMetaData(resource, umd, view.getAvus(), batchAvuCallback);
    }

    @Override
    public void onSelectTemplate() {
        final SelectMetadataTemplateDialog view =
                new SelectMetadataTemplateDialog(templates, appearance);
        view.addHideHandler(new HideEvent.HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                MetadataTemplateInfo selectedTemplate = view.getSelectedTemplate();
                if (selectedTemplate != null) {
                    onTemplateSelected(selectedTemplate.getId());
                }
            }
        });
        view.setModal(false);
        view.setSize("400px", "400px");
        view.setHeadingText(appearance.selectTemplate());
        view.show();

    }

    @Override
    public void onTemplateSelected(String templateId) {
        drService.getMetadataTemplate(templateId, new AsyncCallback<MetadataTemplate>() {

            @Override
            public void onFailure(Throwable arg0) {
                ErrorHandler.post(appearance.templateinfoError(), arg0);
            }

            @Override
            public void onSuccess(MetadataTemplate result) {
                //close exisitng view before opening one...
                if (templateView != null) {
                    templateView.hide();
                }
                templateView =
                        new MetadataTemplateViewDialog(templateMd, isWritable(), result.getAttributes());
                templateView.addOkButtonSelectHandler(new TemplateViewOkSelectHandler(isWritable(),
                                                                                      MetadataPresenterImpl.this,
                                                                                      templateView));
                templateView.addCancelButtonSelectHandler(new TemplateViewCancelSelectHandler(
                        templateView));
                templateView.setHeadingText(result.getName());
                templateView.setModal(false);
                templateView.setSize("600px", "400px");
                templateView.show();

            }

            private boolean isWritable() {
                return DiskResourceUtil.getInstance().isWritable(resource);
            }
        });

    }

    @Override
    public DiskResource getSelectedResource() {
        return resource;
    }

    @Override
    public void onImport(List<DiskResourceMetadata> selectedItems) {
        view.mask();
        view.addToUserMetadata(selectedItems);
        view.removeImportedMetadataFromStore(selectedItems);
        view.unmask();
    }

    public static DiskResourceMetadata newMetadata(String attr, String value, String unit) {
        // FIXME Move to presenter. Autobean factory doesn't belong in view.
        DiskResourceMetadata avu = autoBeanFactory.metadata().as();

        avu.setAttribute(attr);
        avu.setValue(value);
        avu.setUnit(unit);

        return avu;
    }


}
