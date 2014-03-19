package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorCreateFolder;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.events.FolderCreatedEvent;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class CreateFolderCallback extends DiskResourceServiceCallback<Folder> {

    private final Folder parentFolder;

    public CreateFolderCallback(final Folder parentFolder, final IsMaskable maskable) {
        super(maskable);
        this.parentFolder = parentFolder;
    }

    @Override
    public void onSuccess(Folder result) {
        unmaskCaller();
        EventBus.getInstance().fireEvent(new FolderCreatedEvent(parentFolder, result));
    }

    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();
        DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        AutoBean<ErrorCreateFolder> errorBean = AutoBeanCodex.decode(factory, ErrorCreateFolder.class,
                caught.getMessage());

        ErrorHandler.post(errorBean.as(), caught);
    }

    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.createFolderFailed();
    }

}
