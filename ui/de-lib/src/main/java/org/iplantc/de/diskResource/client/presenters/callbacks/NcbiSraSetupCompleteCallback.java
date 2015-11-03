package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorCreateFolder;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.events.selection.RefreshFolderSelected;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class NcbiSraSetupCompleteCallback extends DiskResourceServiceCallback<String> {

    private final Folder parentFolder;
    private final DiskResourceCallbackAppearance appearance = GWT.create(DiskResourceCallbackAppearance.class);
    private final RefreshFolderSelected.RefreshFolderSelectedHandler refreshHandler;


    public NcbiSraSetupCompleteCallback(RefreshFolderSelected.RefreshFolderSelectedHandler refreshHandler,
                                        Folder parentFolder,
                                        IsMaskable maskedCaller) {
        super(maskedCaller);
        this.parentFolder = parentFolder;
        this.refreshHandler = refreshHandler;
    }

    @Override
    public void onSuccess(String result) {
        unmaskCaller();
        refreshHandler.onRefreshFolderSelected(new RefreshFolderSelected(parentFolder));
        IplantAnnouncer.getInstance()
                       .schedule(new SuccessAnnouncementConfig(appearance.ncbiCreateFolderStructureSuccess()));

    }

    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();
        DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        AutoBean<ErrorCreateFolder> errorBean = AutoBeanCodex.decode(factory,
                                                                     ErrorCreateFolder.class,
                                                                     caught.getMessage());

        ErrorHandler.post(errorBean.as(), caught);
    }

    @Override
    protected String getErrorMessageDefault() {
        return appearance.createFolderFailed();
    }

}
