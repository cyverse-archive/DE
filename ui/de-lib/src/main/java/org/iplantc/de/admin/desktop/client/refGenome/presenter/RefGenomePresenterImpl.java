package org.iplantc.de.admin.desktop.client.refGenome.presenter;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.admin.desktop.client.refGenome.service.ReferenceGenomeServiceFacade;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import java.util.List;

/**
 * @author jstroot
 */
public class RefGenomePresenterImpl implements RefGenomeView.Presenter {

    private final RefGenomeView view;
    private final ReferenceGenomeServiceFacade refGenService;
    private final RefGenomePresenterAppearance appearance;

    @Inject
    RefGenomePresenterImpl(final RefGenomeView view,
                           final ReferenceGenomeServiceFacade refGenService,
                           final RefGenomePresenterAppearance appearance) {
        this.view = view;
        this.refGenService = refGenService;
        this.appearance = appearance;
        this.view.setPresenter(this);
    }

    @Override
    public void go(HasOneWidget container) {
        view.mask(appearance.getReferenceGenomesLoadingMask());
        container.setWidget(view);
        refGenService.getReferenceGenomes(new AsyncCallback<List<ReferenceGenome>>() {

            @Override
            public void onSuccess(List<ReferenceGenome> result) {
                view.unmask();
                view.setReferenceGenomes(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                view.unmask();
                ErrorHandler.post(caught);
            }
        });
    }

    @Override
    public void addReferenceGenome(final ReferenceGenome referenceGenome) {
        refGenService.createReferenceGenomes(referenceGenome, new AsyncCallback<ReferenceGenome>() {

            @Override
            public void onSuccess(ReferenceGenome result) {
                view.addReferenceGenome(result);
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.addReferenceGenomeSuccess()));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

        });
    }

    @Override
    public void editReferenceGenome(ReferenceGenome referenceGenome) {
        refGenService.editReferenceGenomes(referenceGenome, new AsyncCallback<ReferenceGenome>() {

            @Override
            public void onSuccess(ReferenceGenome result) {
                view.updateReferenceGenome(result);
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.updateReferenceGenomeSuccess()));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }
        });

    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId + Belphegor.RefGenomeIds.VIEW);
    }

}
