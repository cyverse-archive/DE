package org.iplantc.de.diskResource.client.presenters.metadata;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.events.TemplateDownloadEvent;
import org.iplantc.de.diskResource.client.events.selection.DownloadTemplateSelectedEvent;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.MetadataTemplateViewDialog;
import org.iplantc.de.diskResource.client.views.metadata.dialogs.SelectMetadataTemplateDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

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
public class MetadataPresenterImpl implements MetadataView.Presenter{

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

        public TemplateViewOkSelectHandler(boolean writable,
                                           MetadataTemplateViewDialog mdView) {
            this.writable = writable;
            this.mdView = mdView;
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
            ArrayList<Avu> mdList = mdView.getMetadataFromTemplate();
            view.updateMetadataFromTemplateView(mdList);
        }
    }

    private int unique_avu_id = 0;
    private final DiskResource resource;
    private final MetadataView view;
    private final DiskResourceServiceFacade drService;
    private List<MetadataTemplateInfo> templates;
    private MetadataTemplateViewDialog templateView;
    private List<Avu> userMdList;

    private MetadataView.Presenter.Appearance appearance =
            GWT.create(MetadataView.Presenter.Appearance.class);
    private static DiskResourceAutoBeanFactory autoBeanFactory =
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
        drService.getDiskResourceMetaData(resource, new DiskResourceMetadataListAsyncCallback());
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

        DiskResourceMetadataList umd = autoBeanFactory.metadataList().as();
                
        umd.setAvus(view.getUserMetadata());
        umd.setOtherMetadata(view.getAvus());
        drService.setDiskResourceMetaData(resource, umd, batchAvuCallback);
    }

    @Override
    public void onSelectTemplate() {
        final SelectMetadataTemplateDialog view =
                new SelectMetadataTemplateDialog(templates, appearance, true);
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
        drService.getMetadataTemplate(templateId, new MetadataTemplateAsyncCallback());

    }

    @Override
    public DiskResource getSelectedResource() {
        return resource;
    }

    @Override
    public void onImport(final List<Avu> selectedItems) {
        ConfirmMessageBox cmb = new ConfirmMessageBox(appearance.importMd(), appearance.importMdMsg());
        cmb.addDialogHideHandler(new DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                switch (event.getHideButton()) {
                    case YES:
                        view.mask();
                        view.addToUserMetadata(selectedItems);
                        view.removeImportedMetadataFromStore(selectedItems);
                        view.unmask();
                        break;
                    case NO:
                        break;
                    default:
                        //error, button added with no specific action ready
                }
            }});
        cmb.show();
    }

    @Override
    public boolean isDirty() {
        List<Avu> userMetadata = view.getUserMetadata();
        if(userMdList != null && userMetadata != null && userMdList.size() != userMetadata.size()) {
               return true;
         } else {
             return view.isDirty();
         }

    }

    @Override
    public void downloadTemplate(String templateid) {
        final String encodedSimpleDownloadURL =
                drService.downloadTemplate(templateid);
       WindowUtil.open(encodedSimpleDownloadURL, "width=100,height=100");
    }

    public static Avu newMetadata(String attr, String value, String unit) {
        // FIXME Move to presenter. Autobean factory doesn't belong in view.
        Avu avu = autoBeanFactory.avu().as();

        avu.setAttribute(attr);
        avu.setValue(value);
        avu.setUnit(unit);

        return avu;
    }

    @Override
    public Avu  setAvuModelKey(Avu avu) {
        if (avu != null) {
            final AutoBean<Avu> avuBean = AutoBeanUtils.getAutoBean(avu);
            avuBean.setTag(AVU_BEAN_TAG_MODEL_KEY, String.valueOf(unique_avu_id++));
            return avuBean.as();
        }
        return null;
    }




    private class DiskResourceMetadataListAsyncCallback
            implements AsyncCallback<DiskResourceMetadataList> {
        @Override
        public void onSuccess(final DiskResourceMetadataList result) {
            view.loadMetadata(result.getOtherMetadata());
            userMdList =  result.getAvus();
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
    }

    private class MetadataTemplateAsyncCallback implements AsyncCallback<MetadataTemplate> {

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
            templateView = new MetadataTemplateViewDialog(MetadataPresenterImpl.this,
                                                          view.getUserMetadata(),
                                                          isWritable(),
                                                          result.getAttributes());
            templateView.addOkButtonSelectHandler(new TemplateViewOkSelectHandler(isWritable(),
                                                                                  templateView));
            templateView.addCancelButtonSelectHandler(new TemplateViewCancelSelectHandler(templateView));
            templateView.setHeadingText(result.getName());
            templateView.setModal(false);
            templateView.setSize("600px", "400px");
            templateView.show();

        }

        private boolean isWritable() {
            return DiskResourceUtil.getInstance().isWritable(resource);
        }
    }
}
