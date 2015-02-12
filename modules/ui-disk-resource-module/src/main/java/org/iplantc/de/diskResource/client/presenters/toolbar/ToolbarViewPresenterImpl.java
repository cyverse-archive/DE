package org.iplantc.de.diskResource.client.presenters.toolbar;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.PathListWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.diskResource.client.DataLinkView;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkPresenterFactory;
import org.iplantc.de.diskResource.client.gin.factory.ToolbarViewFactory;
import org.iplantc.de.diskResource.client.views.dialogs.CreateFolderDialog;
import org.iplantc.de.diskResource.client.views.widgets.TabFileConfigDialog;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.sencha.gxt.widget.core.client.Dialog.PredefinedButton.CANCEL;
import static com.sencha.gxt.widget.core.client.Dialog.PredefinedButton.OK;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.List;

/**
 * @author jstroot
 */
public class ToolbarViewPresenterImpl implements ToolbarView.Presenter {

    @Inject ToolbarView.Presenter.Appearance appearance;
    @Inject DataLinkPresenterFactory dataLinkPresenterFactory;
    @Inject EventBus eventBus;
    private final DiskResourceView.Presenter parentPresenter;
    private final ToolbarView view;

    @Inject
    ToolbarViewPresenterImpl(final ToolbarViewFactory viewFactory,
                             @Assisted DiskResourceView.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
        this.view = viewFactory.create(this);
    }

    @Override
    public ToolbarView getView() {
        return view;
    }

    @Override
    public void onCreateNewDelimitedFileSelected() {
        final TabFileConfigDialog d = new TabFileConfigDialog();
        d.setPredefinedButtons(OK, CANCEL);
        d.setModal(true);
        d.getButton(OK).addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                TabularFileViewerWindowConfig config = ConfigFactory.newTabularFileViewerWindowConfig();
                config.setEditing(true);
                config.setVizTabFirst(true);
                config.setSeparator(d.getSeparator());
                config.setColumns(d.getNumberOfColumns());
                config.setContentType(MimeType.PLAIN);
                eventBus.fireEvent(new CreateNewFileEvent(config));
            }
        });
        d.setHideOnButtonClick(true);
        d.setSize(appearance.createDelimitedFileDialogWidth(),
                  appearance.createDelimitedFileDialogHeight());
        d.show();
    }

    @Override
    public void onCreateNewFileSelected(final Folder selectedFolder, final MimeType mimeType) {
        FileViewerWindowConfig config = ConfigFactory.fileViewerWindowConfig(null);
        config.setEditing(true);
        config.setParentFolder(selectedFolder);
        config.setContentType(mimeType);
        eventBus.fireEvent(new CreateNewFileEvent(config));
    }

    @Override
    public void onCreateNewFolderSelected(final Folder selectedFolder) {
        // FIXME Analyze consistency between using selection events and calling parent presenter
        final CreateFolderDialog dlg = new CreateFolderDialog(selectedFolder);
        dlg.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                parentPresenter.doCreateNewFolder(selectedFolder, dlg.getFieldText());
            }
        });
        dlg.show();
    }

    @Override
    public void onCreateNewPathListSelected() {
        PathListWindowConfig config = ConfigFactory.newPathListWindowConfig();
        config.setEditing(true);
        eventBus.fireEvent(new CreateNewFileEvent(config));
    }

    @Override
    public void onCreatePublicLinkSelected(final List<DiskResource> selectedDiskResources) {
        IPlantDialog dlg = new IPlantDialog(true);
        dlg.setPredefinedButtons(OK);
        dlg.setHeadingText(appearance.manageDataLinks());
        dlg.setHideOnButtonClick(true);
        dlg.setWidth(appearance.manageDataLinksDialogWidth());
        dlg.setHeight(appearance.manageDataLinksDialogHeight());
        dlg.setOkButtonText(appearance.done());
        DataLinkView.Presenter dlPresenter = dataLinkPresenterFactory.createDataLinkPresenter(selectedDiskResources);
        dlPresenter.go(dlg);
        dlg.addHelp(new HTML(appearance.manageDataLinksHelp()));
        dlg.show();
    }

    @Override
    public void onEditFileSelected(final List<DiskResource> selectedDiskResources) {
        Preconditions.checkState(selectedDiskResources.size() == 1,
                                 "Only one file should be selected, but there are %i", selectedDiskResources.size());
        final DiskResource next = selectedDiskResources.iterator().next();
        Preconditions.checkState(next instanceof File, "Selected item should be a file, but is not.");
        Preconditions.checkState(PermissionValue.own.equals(next.getPermission())
                                     || PermissionValue.write.equals(next.getPermission()), "User should have either own or write permissions for the selected item");

        eventBus.fireEvent(new ShowFilePreviewEvent((File) next, this));
    }

    @Override
    public void onImportFromUrlSelected(final Folder selectedFolder) {
        eventBus.fireEvent(new RequestImportFromUrlEvent(selectedFolder));
    }

    @Override
    public void onOpenNewWindowAtLocationSelected(final Folder selectedFolder) {
        final String selectedFolderPath = selectedFolder == null ? null : selectedFolder.getPath();
        OpenFolderEvent openFolderEvent = new OpenFolderEvent(selectedFolderPath);
        openFolderEvent.requestNewView(true);
        eventBus.fireEvent(openFolderEvent);
    }

    @Override
    public void onOpenNewWindowSelected() {
        OpenFolderEvent openFolderEvent = new OpenFolderEvent(null);
        openFolderEvent.requestNewView(true);
        eventBus.fireEvent(openFolderEvent);
    }

    @Override
    public void onOpenTrashFolderSelected() {
        parentPresenter.selectTrashFolder();
    }

}
