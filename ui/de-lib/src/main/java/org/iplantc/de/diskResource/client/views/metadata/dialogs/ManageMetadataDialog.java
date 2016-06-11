package org.iplantc.de.diskResource.client.views.metadata.dialogs;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.MetadataView;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.presenters.metadata.MetadataPresenterImpl;
import org.iplantc.de.diskResource.client.views.metadata.DiskResourceMetadataViewImpl;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * @author jstroot sriram
 */
public class ManageMetadataDialog extends IPlantDialog {

    private final DiskResourceServiceFacade diskResourceService;
    private final GridView.Presenter.Appearance appearance;
    private MetadataView.Presenter mdPresenter;
    private MetadataView mdView;

    final DiskResourceUtil diskResourceUtil;
    private boolean writable;
    private DiskResource resource;
    private boolean canHide;

    @Inject
    ManageMetadataDialog(final DiskResourceServiceFacade diskResourceService,
                         final DiskResourceUtil diskResourceUtil,
                         final GridView.Presenter.Appearance appearance) {
        super(true);
        setModal(true);
        this.diskResourceService = diskResourceService;
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;
        setSize(appearance.metadataDialogWidth(), appearance.metadataDialogHeight());
        setResizable(true);
        getOkButton().setText(I18N.DISPLAY.save());
        addHelp(new HTML(appearance.metadataHelp()));
        addHandlers();
    }

    private void addHandlers() {
        addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                mdPresenter.setDiskResourceMetadata(new DiskResourceMetadataUpdateCallback(
                        ManageMetadataDialog.this));
            }
        });

        addCancelButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                checkForUnsavedChanges();
            }
        });
    }

    @Override
    public void hide() {
      if(canHide) {
          super.hide();
      } else {
          checkForUnsavedChanges();
      }
    }

    public void show(final DiskResource resource) {
        this.resource = resource;
        setHeadingText(appearance.metadata() + ":" + resource.getName());
        mdView = new DiskResourceMetadataViewImpl(diskResourceUtil.isWritable(resource));
        mdPresenter = new MetadataPresenterImpl(resource, mdView, diskResourceService);
        mdPresenter.go(this);
        writable = diskResourceUtil.isWritable(resource);
        if (writable) {
            setHideOnButtonClick(false);
        }
        super.show();
        ensureDebugId(DiskResourceModule.MetadataIds.METADATA_WINDOW);
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. "
                                                + "Use show(MetadataServiceFacade) instead.");
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        mdView.asWidget().ensureDebugId(baseID + DiskResourceModule.MetadataIds.METADATA_VIEW);
    }

    private void checkForUnsavedChanges() {
        if (mdPresenter.isDirty()) {
            final AlertMessageBox amb =
                    new AlertMessageBox("Save", "You have unsaved changes. Save now?");
            amb.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
            amb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    switch (event.getHideButton()) {
                        case YES:
                            amb.hide();
                            canHide = true;
                            mdPresenter.setDiskResourceMetadata(new DiskResourceMetadataUpdateCallback(
                                    ManageMetadataDialog.this));
                            break;
                        case NO:
                            amb.hide();
                            canHide = true;
                            ManageMetadataDialog.this.clearHandlers();
                            ManageMetadataDialog.this.hide();
                            break;

                        case CANCEL:
                            canHide = false;
                            amb.hide();
                            break;
                    }


                }
            });
            amb.show();
            amb.toFront();
        } else {
            canHide = true;
            hide();
        }
    }
}
