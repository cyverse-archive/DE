package org.iplantc.de.diskResource.client.presenters.details;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.gin.factory.DetailsViewFactory;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * @author jstroot
 */
public class DetailsViewPresenterImpl implements DetailsView.Presenter {

    @Inject IplantAnnouncer announcer;
    @Inject FileSystemMetadataServiceFacade metadataService;
    @Inject DetailsView.Presenter.Appearance appearance;

    private final DetailsView view;

    @Inject
    DetailsViewPresenterImpl(final DetailsViewFactory viewFactory) {
        this.view = viewFactory.create(this);
    }

    @Override
    public void attachTagToResource(final Tag tag,
                                    final DiskResource resource) {
           metadataService.attachTags(Lists.newArrayList(tag), resource, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.tagAttachError(), caught);
            }

            @Override
            public void onSuccess(Void result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.tagAttached(resource.getName(), tag.getValue())));
            }
        });
    }

    @Override
    public void removeTagFromResource(final Tag tag,
                                      final DiskResource resource) {
        metadataService.detachTags(Lists.newArrayList(tag), resource, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.tagDetachError(), caught);
            }

            @Override
            public void onSuccess(Void result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.tagDetached(tag.getValue(), resource.getName())));
            }
        });
    }

    @Override
    public DetailsView getView() {
        return view;
    }

}
