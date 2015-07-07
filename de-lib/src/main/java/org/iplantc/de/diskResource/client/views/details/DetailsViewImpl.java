package org.iplantc.de.diskResource.client.views.details;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.diskResource.client.events.selection.Md5ValueClicked;
import org.iplantc.de.diskResource.client.events.selection.ResetInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToCogeSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToEnsemblSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToTreeViewerSelected;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.events.TagAddedEvent;
import org.iplantc.de.tags.client.events.TagCreated;
import org.iplantc.de.tags.client.events.TagCreated.TagCreatedHandler;
import org.iplantc.de.tags.client.events.selection.RemoveTagSelected;
import org.iplantc.de.tags.client.events.selection.TagSelected;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DateLabel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.Composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * View is updated on grid selection changed.
 * View is updated when a store update event occurs
 *
 * @author jstroot
 */
public class DetailsViewImpl extends Composite implements DetailsView,
                                                          Editor<DiskResource>,
                                                          TagSelected.TagSelectedHandler,
                                                          RemoveTagSelected.RemoveTagSelectedHandler,
                                                          TagCreatedHandler,
                                                          TagAddedEvent.TagAddedEventHandler {

    interface DetailsViewImplUiBinder extends UiBinder<HTMLPanel, DetailsViewImpl> {
    }

    interface EditorDriver extends SimpleBeanEditorDriver<DiskResource, DetailsViewImpl> {
    }
    @UiField(provided = true) final Appearance appearance;
    @UiField DateLabel dateCreated;
    @Inject DiskResourceUtil diskResourceUtil;
    @UiField DivElement emptyDetails;
    @Inject SearchAutoBeanFactory factory;
    @UiField @Ignore InlineLabel fileFolderNum;
    @UiField TableRowElement fileFolderNumRow;
    @UiField @Ignore InlineHyperlink infoType;
    @UiField TableRowElement infoTypeRow;
    @UiField DateLabel lastModified;
    @UiField @Ignore InlineLabel mimeType;
    @UiField TableRowElement mimeTypeRow;
    @UiField @Ignore InlineLabel permission;
    @UiField Image resetInfoTypeIcon;
    @UiField InlineHyperlink sendTo;
    @UiField TableRowElement sendToRow;
    @UiField TableRowElement shareRow;
    @UiField InlineHyperlink sharing;
    @UiField @Ignore InlineLabel size;
    @UiField TableRowElement sizeRow;
    @UiField TableElement table;
    @UiField
    TableRowElement md5Row;
    @UiField
    InlineHyperlink md5link;
    @UiField(provided = true) TagsView tagListView;
    private static final DetailsViewImplUiBinder ourUiBinder = GWT.create(DetailsViewImplUiBinder.class);
    private final Logger LOG = Logger.getLogger(DetailsViewImpl.class.getSimpleName());
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private final DetailsView.Presenter presenter;
    private final TagsView.Presenter tagsPresenter;
    private DiskResource boundValue;

    @Inject
    DetailsViewImpl(final DetailsView.Appearance appearance,
                    final TagsView.Presenter tagsPresenter,
                    @Assisted final DetailsView.Presenter presenter) {
        this.tagsPresenter = tagsPresenter;
        this.tagListView = tagsPresenter.getView();
        this.appearance = appearance;
        this.presenter = presenter;

        tagsPresenter.setEditable(true);
        tagsPresenter.setRemovable(true);
        this.tagListView = tagsPresenter.getView();
        this.tagListView.addTagSelectedHandler(this);
        this.tagListView.addRemoveTagSelectedHandler(this);
        this.tagListView.addTagAddedEventHandler(this);
        this.tagListView.addTagCreatedHandler(this);
        this.tagListView.asWidget().getElement().addClassName(appearance.css().tagSearch());

        initWidget(ourUiBinder.createAndBindUi(this));
        dateCreated.setValue(new Date());
        lastModified.setValue(new Date());

        editorDriver.initialize(this);
    }

    //<editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addEditInfoTypeSelectedEventHandler(EditInfoTypeSelected.EditInfoTypeSelectedEventHandler handler) {
        return addHandler(handler, EditInfoTypeSelected.TYPE);
    }

    @Override
    public HandlerRegistration addManageSharingSelectedEventHandler(ManageSharingSelected.ManageSharingSelectedEventHandler handler) {
        return addHandler(handler, ManageSharingSelected.TYPE);
    }

    @Override
    public HandlerRegistration addResetInfoTypeSelectedHandler(ResetInfoTypeSelected.ResetInfoTypeSelectedHandler handler) {
        return addHandler(handler, ResetInfoTypeSelected.TYPE);
    }

    @Override
    public HandlerRegistration addMd5ValueClickedHandler(Md5ValueClicked.Md5ValueClickedHandler handler) {
        return addHandler(handler, Md5ValueClicked.TYPE);
    }

    @Override
    public HandlerRegistration addSendToCogeSelectedHandler(SendToCogeSelected.SendToCogeSelectedHandler handler) {
        return addHandler(handler, SendToCogeSelected.TYPE);
    }

    @Override
    public HandlerRegistration addSendToEnsemblSelectedHandler(SendToEnsemblSelected.SendToEnsemblSelectedHandler handler) {
        return addHandler(handler, SendToEnsemblSelected.TYPE);
    }

    @Override
    public HandlerRegistration addSendToTreeViewerSelectedHandler(SendToTreeViewerSelected.SendToTreeViewerSelectedHandler handler) {
        return addHandler(handler, SendToTreeViewerSelected.TYPE);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler handler) {
        return addHandler(handler, SubmitDiskResourceQueryEvent.TYPE);
    }
    //</editor-fold>

    //<editor-fold desc="Event Handlers">
    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        if (event.getSelection().isEmpty()
                || event.getSelection().size() != 1) {
            bind(null);
            // Hide table
            table.addClassName(appearance.css().hidden());
            emptyDetails.removeClassName(appearance.css().hidden());
            return;
        }
        table.removeClassName(appearance.css().hidden());
        emptyDetails.addClassName(appearance.css().hidden());
        // UPDATE ROW VISIBILITIES
        DiskResource singleSelection = event.getSelection().iterator().next();
        if (singleSelection instanceof File) {
            // Show rows
            sizeRow.removeClassName(appearance.css().hidden());
            mimeTypeRow.removeClassName(appearance.css().hidden());
            infoTypeRow.removeClassName(appearance.css().hidden());
            sendToRow.removeClassName(appearance.css().hidden());
            md5Row.removeClassName(appearance.css().hidden());

            // Hide rows
            fileFolderNumRow.addClassName(appearance.css().hidden());

        } else if (singleSelection instanceof Folder) {
            // Show rows
            fileFolderNumRow.removeClassName(appearance.css().hidden());

            // Hide rows
            sizeRow.addClassName(appearance.css().hidden());
            mimeTypeRow.addClassName(appearance.css().hidden());
            infoTypeRow.addClassName(appearance.css().hidden());
            sendToRow.addClassName(appearance.css().hidden());
        }

        if (diskResourceUtil.inTrash(singleSelection)) {
            shareRow.addClassName(appearance.css().hidden());
            sendToRow.addClassName(appearance.css().hidden());
        } else {
            shareRow.removeClassName(appearance.css().hidden());
            sendToRow.removeClassName(appearance.css().hidden());
        }

        bind(singleSelection);
        tagsPresenter.fetchTagsForResource(singleSelection);
    }

    @Override
    public void onRemoveTagSelected(RemoveTagSelected event) {
        Preconditions.checkNotNull(boundValue, "Bound value should not be null right now");
        presenter.removeTagFromResource(event.getTag(), boundValue);
    }

    @Override
    public void onTagAdded(TagAddedEvent event) {
        Preconditions.checkNotNull(boundValue, "Bound value should not be null right now");
        presenter.attachTagToResource(event.getTag(), boundValue);
    }

    @Override
    public void onTagCreated(TagCreated event) {
        Preconditions.checkNotNull(boundValue, "Bound value should not be null right now");
        presenter.attachTagToResource(event.getTag(), boundValue);
    }

    @Override
    public void onTagSelected(TagSelected event) {
        DiskResourceQueryTemplate queryTemplate = factory.dataSearchFilter().as();
        queryTemplate.setTagQuery(Sets.newHashSet(event.getTag()));
        fireEvent(new SubmitDiskResourceQueryEvent(queryTemplate));
    }

    @Override
    public void onUpdate(StoreUpdateEvent<DiskResource> event) {
        // Must match the currently bound DiskResource
        if (event.getItems().size() != 1
                || event.getItems().iterator().next() != boundValue) {
            return;
        }
        bind(event.getItems().iterator().next());
    }
    //</editor-fold>

    //<editor-fold desc="UI Handlers">
    @UiHandler("infoType")
    void onInfoTypeClicked(ClickEvent event) {
        if (boundValue == null
                || !(boundValue instanceof File)) {
            return;
        }
        fireEvent(new EditInfoTypeSelected(Lists.newArrayList(boundValue)));
    }

    @UiHandler("md5link")
    void onMd5Clicked(ClickEvent event) {
        fireEvent(new Md5ValueClicked(boundValue));
    }

    @UiHandler("resetInfoTypeIcon")
    void onResetInfoTypeClicked(ClickEvent event) {
        if (boundValue == null) {
            return;
        }
        fireEvent(new ResetInfoTypeSelected(boundValue));
    }

    @UiHandler("sendTo")
    void onSendToClicked(ClickEvent event) {
        if (boundValue == null) {
            return;
        }
        InfoType resInfoType = InfoType.fromTypeString(boundValue.getInfoType());
        if (resInfoType == null) {
            return;
        }

        final ArrayList<DiskResource> resources = Lists.newArrayList(boundValue);
        if (diskResourceUtil.isTreeInfoType(resInfoType)) {
            fireEvent(new SendToTreeViewerSelected(resources));
        } else if (diskResourceUtil.isGenomeVizInfoType(resInfoType)) {
            fireEvent(new SendToCogeSelected(resources));
        } else if (diskResourceUtil.isEnsemblInfoType(resInfoType)) {
            fireEvent(new SendToEnsemblSelected(resources));
        }

        LOG.fine("Send to clicked");
    }

    @UiHandler("sharing")
    void onSharingClicked(ClickEvent event) {
        if (boundValue == null) {
            return;
        }
        fireEvent(new ManageSharingSelected(boundValue));
        if (boundValue.getShareCount() == 0) {
            LOG.fine("Begin sharing");
        }
        LOG.fine("Sharing clicked");
    }
    //</editor-fold>

    void bind(final DiskResource resource) {
        this.boundValue = resource;
        // Update editor
        editorDriver.edit(resource);

        // Clear previous values
        tagsPresenter.removeAll();
        permission.setText("");
        size.setText("");
        fileFolderNum.setText("");
        mimeType.setText("");
        sharing.setText("");
        infoType.setText("");
        sendTo.setText("");
        md5link.setText("");

        if (resource == null) {
            return;
        }
        // Manually populate
        permission.setText(resource.getPermission().name());
        if (resource instanceof File) {
            File file = (File) resource;
            size.setText(diskResourceUtil.formatFileSize(file.getSize() + ""));
            mimeType.setText(file.getContentType());
            infoType.setText(file.getInfoType());
            md5link.setText(Format.ellipse(file.getMd5(), 10));
        } else if (resource instanceof Folder) {
            Folder folder = (Folder) resource;
            // file/folder count
            fileFolderNum.setText(folder.getFileCount() + " / " + folder.getDirCount());
            md5Row.addClassName(appearance.css().hidden());
        }

        // Update sharing label
        if (PermissionValue.own.equals(resource.getPermission())) {
            sharing.removeStyleName(appearance.css().disabledHyperlink());
            if (resource.getShareCount() > 0) {
                sharing.setText(Integer.toString(resource.getShareCount()));
            } else {
                sharing.setText(appearance.beginSharing());
            }
        } else {
            sharing.setText(appearance.sharingDisabled());
            sharing.addStyleName(appearance.css().disabledHyperlink());
        }

        // Update SendTo
        InfoType resInfoType = InfoType.fromTypeString(resource.getInfoType());
        if (resInfoType != null) {
            sendTo.removeStyleName(appearance.css().disabledHyperlink());
            if (diskResourceUtil.isTreeInfoType(resInfoType)) {
                sendTo.setText(appearance.treeViewer());
            } else if (diskResourceUtil.isGenomeVizInfoType(resInfoType)) {
                sendTo.setText(appearance.coge());
            } else if (diskResourceUtil.isEnsemblInfoType(resInfoType)) {
                sendTo.setText(appearance.ensembl());
            }

        } else {
            sendTo.setText(appearance.viewersDisabled());
            sendTo.addStyleName(appearance.css().disabledHyperlink());
        }

        PermissionValue permission = resource.getPermission();
        // Update Infotype
        if (resource instanceof File) {
            if (PermissionValue.own.equals(permission)
                    || PermissionValue.write.equals(permission)) {
                infoType.removeStyleName(appearance.css().disabledHyperlink());

                // Display Infotype
                if (resInfoType != null) {
                    infoType.setText(resInfoType.toString());
                    // display deselect icon
                    resetInfoTypeIcon.setVisible(true);

                } else {
                    infoType.setText(appearance.selectInfoType());
                    // hide deselect icon
                    resetInfoTypeIcon.setVisible(false);
                }
            } else {
                infoType.addStyleName(appearance.css().disabledHyperlink());
                // hide deselect icon
                resetInfoTypeIcon.setVisible(false);

                if (resInfoType != null) {
                    infoType.setText(resInfoType.toString());
                } else {
                    infoType.setText(appearance.infoTypeDisabled());
                }
            }
        }
    }

    @UiFactory
    @Ignore
    DateLabel createDateLabel() {
        return new DateLabel(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM));
    }


}