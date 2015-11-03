package org.iplantc.de.diskResource.client.presenters.metadata;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataList;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplate;
import org.iplantc.de.client.models.diskResources.DiskResourceMetadataTemplateList;
import org.iplantc.de.client.models.diskResources.MetadataTemplate;
import org.iplantc.de.client.models.diskResources.MetadataTemplateInfo;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.List;

/**
 * @author jstroot
 */
public class MetadataPresenterImpl implements MetadataView.Presenter {

    private final DiskResource resource;
    private final MetadataView view;
    private final DiskResourceServiceFacade drService;

    public MetadataPresenterImpl(final DiskResource selected,
                                 final MetadataView view,
                                 final DiskResourceServiceFacade drService) {
        this.resource = selected;
        this.view = view;
        this.drService = drService;
        view.setPresenter(this);
        drService.getMetadataTemplateListing(new AsyncCallback<List<MetadataTemplateInfo>>() {

            @Override
            public void onFailure(Throwable arg0) {
                ErrorHandler.post("Unable to retrieve templates!", arg0);
            }

            @Override
            public void onSuccess(final List<MetadataTemplateInfo> result) {
                view.populateTemplates(result);
                loadMetadata();
            }
        });
    }

    private void loadMetadata() {
        drService.getDiskResourceMetaData(resource, new AsyncCallback<DiskResourceMetadataList>() {
            @Override
            public void onSuccess(final DiskResourceMetadataList result) {
                view.loadMetadata(result.getMetadata());

                final DiskResourceMetadataTemplateList metadataTemplateList = result.getMetadataTemplates();
                if (metadataTemplateList != null) {
                    final List<DiskResourceMetadataTemplate> templates = metadataTemplateList.getTemplates();
                    if (templates != null && !templates.isEmpty()) {
                        view.loadMetadataTemplate(templates.get(0));
                    }
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
        container.setWidget(view.asWidget());
    }

    @Override
    public void setDiskResourceMetadata(final DiskResourceMetadataUpdateCallback callback) {
        AsyncCallback<String> batchAvuCallback = new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                saveMetadataTemplateAvus(callback);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        };

        drService.setDiskResourceMetaData(resource,
                                          view.getMetadataToAdd(),
                                          view.getMetadataToDelete(),
                                          batchAvuCallback);
    }

    private void saveMetadataTemplateAvus(final DiskResourceMetadataUpdateCallback callback) {
        // CORE-5602 Setting a data item's AVUs on one template automatically removes its AVUs from other
        // templates. So we only need to set or delete here, but not both.
        if (view.getMetadataTemplateToAdd() != null) {
            setMetadataTemplateAvus(callback);
        } else {
            deleteMetadataTemplateAvus(callback);
        }
    }

    private void setMetadataTemplateAvus(final DiskResourceMetadataUpdateCallback callback) {
        AsyncCallback<String> templateAvuCallback = new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        };

        DiskResourceMetadataTemplate metadataTemplateToAdd = view.getMetadataTemplateToAdd();
        if (metadataTemplateToAdd != null) {
            drService.setMetadataTemplateAvus(resource, metadataTemplateToAdd, templateAvuCallback);
        } else {
            templateAvuCallback.onSuccess(null);
        }
    }

    private void deleteMetadataTemplateAvus(final DiskResourceMetadataUpdateCallback callback) {
        AsyncCallback<String> templateAvuCallback = new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        };

        DiskResourceMetadataTemplate avusToDelete = view.getMetadataTemplateToDelete();
        if (avusToDelete != null) {
            drService.deleteMetadataTemplateAvus(resource, avusToDelete, templateAvuCallback);
        } else {
            templateAvuCallback.onSuccess(null);
        }
    }

    @Override
    public void onTemplateSelected(String templateId) {
        drService.getMetadataTemplate(templateId, new AsyncCallback<MetadataTemplate>() {

            @Override
            public void onFailure(Throwable arg0) {
                ErrorHandler.post("Unable to retrieve template attributes!", arg0);
            }

            @Override
            public void onSuccess(MetadataTemplate result) {
                view.loadTemplateAttributes(result.getAttributes());
            }
        });

    }

    @Override
    public DiskResource getSelectedResource() {
        return resource;
    }

}
