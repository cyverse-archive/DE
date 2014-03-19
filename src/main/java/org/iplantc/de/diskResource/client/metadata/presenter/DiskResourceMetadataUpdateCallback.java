package org.iplantc.de.diskResource.client.metadata.presenter;

import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorUpdateMetadata;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceServiceCallback;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class DiskResourceMetadataUpdateCallback extends DiskResourceServiceCallback<String> {

    public DiskResourceMetadataUpdateCallback() {
        super(null);
    }
    
    @Override
    public void onSuccess(String result) {
        super.onSuccess(result);
        IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.metadataSuccess()));
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
        return I18N.ERROR.metadataUpdateFailed();
    }

}
