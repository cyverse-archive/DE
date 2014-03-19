package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorDiskResourceMove;
import org.iplantc.de.client.models.services.DiskResourceMove;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.Set;

public class DiskResourceMoveCallback extends DiskResourceServiceCallback<DiskResourceMove> {

    private final Set<DiskResource> resourcesToMove;
    private final Folder destFolder;
    private final boolean moveContents;
    private final Folder sourceFolder;

    public DiskResourceMoveCallback(final IsMaskable maskedCaller, final boolean moveContents, final Folder srcFolder, final Folder destFolder, final Set<DiskResource> resourcesToMove) {
        super(maskedCaller);
        this.destFolder = destFolder;
        this.resourcesToMove = resourcesToMove;
        this.moveContents = moveContents;
        this.sourceFolder = srcFolder;
    }

    @Override
    public void onSuccess(DiskResourceMove result) {
        unmaskCaller();
        /*
         * JDS Result should have a "sources" key
         * and a "dest" key.
         *
         * TODO JDS Verify returned keys to the objects we have already.
         */
        EventBus.getInstance().fireEvent(new DiskResourcesMovedEvent(sourceFolder, destFolder, resourcesToMove, moveContents));
    }

    @Override
    public void onFailure(Throwable caught){
        unmaskCaller();
        DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        AutoBean<ErrorDiskResourceMove> errorBean = AutoBeanCodex.decode(factory, ErrorDiskResourceMove.class, caught.getMessage());

        ErrorHandler.post(errorBean.as(), caught);
    }

    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.moveFailed();
    }

}
