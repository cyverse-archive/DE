package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorUpdateMetadata;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author jstroot
 */
public class DiskResourceMetadataUpdateCallback extends DiskResourceServiceCallback<String> {

    private final IPlantDialog dialog;
    private final DiskResourceCallbackAppearance appearance = GWT.create(DiskResourceCallbackAppearance.class);

    public DiskResourceMetadataUpdateCallback(IPlantDialog dialog) {
        super(dialog);

        this.dialog = dialog;
    }

    @Override
    public void onSuccess(String result) {

        super.onSuccess(result);

        if (dialog != null) {
            dialog.hide();
        }

        IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.metadataSuccess()));
    }

    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();
        DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        AutoBean<ErrorUpdateMetadata> errorBean = AutoBeanCodex.decode(factory,
                ErrorUpdateMetadata.class, caught.getMessage());

        ErrorHandler.post(errorBean.as(), caught);

    }

    @Override
    protected String getErrorMessageDefault() {
        return appearance.metadataUpdateFailed();
    }

}
