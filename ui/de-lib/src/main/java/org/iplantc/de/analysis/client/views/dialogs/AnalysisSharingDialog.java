package org.iplantc.de.analysis.client.views.dialogs;

import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AnalysisSharingDialog extends IPlantDialog implements SelectHandler {

    private SharingPresenter sharingPresenter;

    @Inject
    CollaboratorsUtil collaboratorsUtil;
    @Inject
    JsonUtil jsonUtil;

    @Inject
    public AnalysisSharingDialog() {
        super(false);
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

    @Override
    public void show() throws UnsupportedOperationException {
        sharingPresenter.go(this);
        super.show();
    }

    public void setPresenter(SharingPresenter sharingPresenter) {
        this.sharingPresenter = sharingPresenter;
    }
}
