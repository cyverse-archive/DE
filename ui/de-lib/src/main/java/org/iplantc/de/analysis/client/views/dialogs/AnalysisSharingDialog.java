package org.iplantc.de.analysis.client.views.dialogs;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.List;

public class AnalysisSharingDialog extends IPlantDialog implements SelectHandler {

    private final AnalysisServiceFacade analysisService;

    private SharingPresenter sharingPresenter;

    @Inject
    CollaboratorsUtil collaboratorsUtil;
    @Inject
    JsonUtil jsonUtil;

    @Inject
    AnalysisSharingDialog(final AnalysisServiceFacade analysisService) {
        super(true);
        this.analysisService = analysisService;
        setPixelSize(600, 500);
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        // addHelp(new HTML(appearance.sharePermissionsHelp()));
        setHeadingText("Manage Sharing");
        setOkButtonText("Done");
        addOkButtonSelectHandler(this);
    }

    @Override
    public void onSelect(SelectEvent event) {
        Preconditions.checkNotNull(sharingPresenter);
        sharingPresenter.processRequest();
    }

    public void show(final List<Analysis> resourcesToShare) {
        sharingPresenter.go(this);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. ");
    }

    public void setPresenter(SharingPresenter sharingPresenter) {
        this.sharingPresenter = sharingPresenter;
    }
}
