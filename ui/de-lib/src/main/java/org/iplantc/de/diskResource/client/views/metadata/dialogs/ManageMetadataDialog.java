package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.presenters.metadata.MetadataPresenterImpl;
import org.iplantc.de.diskResource.client.views.metadata.DiskResourceMetadataViewImpl;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * @author jstroot
 */
public class ManageMetadataDialog extends IPlantDialog {
	
	
	private class OkSelectHandler implements SelectEvent.SelectHandler {

		@Override
		public void onSelect(SelectEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class CancelSelectHandler implements SelectEvent.SelectHandler {

		@Override
		public void onSelect(SelectEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	

    private final DiskResourceServiceFacade diskResourceService;
    private final GridView.Presenter.Appearance appearance;
    private MetadataView.Presenter mdPresenter;
    private MetadataView mdView;

    final DiskResourceUtil diskResourceUtil;
    private boolean writable;

    @Inject
    ManageMetadataDialog(final DiskResourceServiceFacade diskResourceService,
                         final DiskResourceUtil diskResourceUtil,
                         final GridView.Presenter.Appearance appearance){
        super(true);
        setModal(false);
        this.diskResourceService = diskResourceService;
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;
        setSize(appearance.metadataDialogWidth(), appearance.metadataDialogHeight());
        setResizable(true);
        addHelp(new HTML(appearance.metadataHelp()));
        addOkButtonSelectHandler(new OkSelectHandler());
        addCancelButtonSelectHandler(new CancelSelectHandler());
    }

    public void show(final DiskResource resource){
        setHeadingText(appearance.metadata() + ":" + resource.getName());
        mdView = new DiskResourceMetadataViewImpl(diskResourceUtil.isWritable(resource));
        mdPresenter = new MetadataPresenterImpl(resource, mdView, diskResourceService);
        mdPresenter.go(this);
        writable = diskResourceUtil.isWritable(resource);
        if(writable){
            setHideOnButtonClick(false);
        }

        super.show();
        ensureDebugId(DiskResourceModule.MetadataIds.METADATA_WINDOW);

    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. " +
                                                    "Use show(MetadataServiceFacade) instead.");
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        mdView.asWidget().ensureDebugId(baseID + DiskResourceModule.MetadataIds.METADATA_VIEW);
    }
}
