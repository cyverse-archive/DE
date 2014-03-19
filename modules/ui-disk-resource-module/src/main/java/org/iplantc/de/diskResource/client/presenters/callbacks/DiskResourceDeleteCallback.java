package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorDiskResourceDelete;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.Collection;

public class DiskResourceDeleteCallback extends DiskResourceServiceCallback<HasPaths> {

    private final Collection<DiskResource> resources;
    private final Folder parentFolder;
    private final String announce;

    public DiskResourceDeleteCallback(Collection<DiskResource> resources, Folder parentFolder, IsMaskable maskedCaller, String announce) {
        super(maskedCaller);
        this.resources = resources;
        this.parentFolder = parentFolder;
        this.announce = announce;
    }

    @Override
    public void onSuccess(final HasPaths unused) {
        unmaskCaller();
        IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(announce));
        EventBus.getInstance().fireEvent(new DiskResourcesDeletedEvent(resources, parentFolder));
    }

    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();
        DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        AutoBean<ErrorDiskResourceDelete> errorBean = AutoBeanCodex.decode(factory,
                ErrorDiskResourceDelete.class, caught.getMessage());

        ErrorHandler.post(errorBean.as(), caught);

    }

    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.deleteFailed();
    }

}
