package org.iplantc.de.diskResource.client.presenters.callbacks;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.RestoreResponse;
import org.iplantc.de.client.models.diskResources.RestoreResponse.RestoredResource;
import org.iplantc.de.client.models.errors.diskResources.DiskResourceErrorAutoBeanFactory;
import org.iplantc.de.client.models.errors.diskResources.ErrorDiskResourceMove;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.Set;

/**
 * A DiskResourceServiceCallback for data service "restore" endpoint requests.
 * 
 * @author psarando
 * 
 */
public class DiskResourceRestoreCallback extends DiskResourceServiceCallback<String> {
    private final DiskResourceView.Presenter presenter;
    private final DiskResourceAutoBeanFactory drFactory;
    private final Set<DiskResource> selectedResources;

    public DiskResourceRestoreCallback(DiskResourceView.Presenter presenter,
            DiskResourceAutoBeanFactory drFactory, Set<DiskResource> selectedResources) {
        super(presenter);

        this.drFactory = drFactory;
        this.selectedResources = selectedResources;
        this.presenter = presenter;
    }

    @Override
    protected String getErrorMessageDefault() {
        return I18N.ERROR.restoreDefaultMsg();
    }

    @Override
    public void onSuccess(String result) {
        super.onSuccess(result);

        checkForPartialRestore(result);
        presenter.doRefresh(presenter.getSelectedFolder());
    }

    @Override
    public void onFailure(Throwable caught) {
        unmaskCaller();
        DiskResourceErrorAutoBeanFactory factory = GWT.create(DiskResourceErrorAutoBeanFactory.class);
        AutoBean<ErrorDiskResourceMove> errorBean = AutoBeanCodex.decode(factory, ErrorDiskResourceMove.class, caught.getMessage());

        ErrorHandler.post(errorBean.as(), caught);
    }
    
    private void checkForPartialRestore(String result) {
        RestoreResponse response = AutoBeanCodex.decode(drFactory, RestoreResponse.class, result).as();
        Splittable restored = response.getRestored();

        for (DiskResource resource : selectedResources) {
            Splittable restoredResourceJson = restored.get(resource.getId());

            if (restoredResourceJson != null) {
                RestoredResource restoredResource = AutoBeanCodex.decode(drFactory,
                        RestoredResource.class, restoredResourceJson).as();

                if (restoredResource.isPartialRestore()) {
                   IplantAnnouncer.getInstance().schedule(I18N.DISPLAY.partialRestore());
                   break;
                } else {
                    IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.restoreMsg()));
                    break;
                }
            }
        }
    }
}
