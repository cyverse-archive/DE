package org.iplantc.de.diskResource.client.views.details;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.DetailsView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.tags.client.Taggable;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagListPresenterFactory;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DateLabel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import java.util.Date;
import java.util.logging.Logger;

/**
 * View will be constructed once, not on every selection.
 * View is updated on grid selection changed.
 * View is updated when a store update event occurs
 * <p/>
 * <p/>
 * <p/>
 * Created by jstroot on 2/2/15.
 * TODO Possibly remove Taggable interface. The tag widget mayber?
 * FIXME Finish style to match
 * @author jstroot
 */
public class DetailsViewImpl extends Composite implements DetailsView,
                                                          Editor<DiskResource>,
                                                          Taggable {

    //region Click Handlers
    private class InfoTypeClickHandler implements ClickHandler {

        private final String infoType;

        public InfoTypeClickHandler(String type) {
            this.infoType = type;
        }

        @Override
        public void onClick(ClickEvent arg0) {
            DiskResource boundResource = null;
            fireEvent(new EditInfoTypeSelected(Lists.newArrayList(boundResource)));
        }

    }

    private final class RemoveInfoTypeClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            // FIXME Who listens here?
//            presenter.resetInfoType();
        }
    }

    //endregion

    interface EditorDriver extends SimpleBeanEditorDriver<DiskResource, DetailsViewImpl> { }

    interface DetailsViewImplUiBinder extends UiBinder<HTMLPanel, DetailsViewImpl> { }

    @UiField(provided = true) Appearance appearance;
    @UiField DateLabel dateCreated;
    @UiField DateLabel lastModified;
    @UiField @Ignore InlineLabel permission;
    @UiField @Ignore InlineLabel size;
    @UiField @Ignore InlineLabel fileFolderNum;
    @UiField InlineHyperlink sharing;
    @UiField InlineHyperlink sendTo;

    @UiField TableRowElement mimeTypeRow;
    @UiField TableRowElement infoTypeRow;
    @UiField TableRowElement fileFolderNumRow;
    @UiField TableRowElement sendToRow;
    @UiField TableRowElement shareRow;
    @UiField TableRowElement sizeRow;
    @UiField TableElement table;
    @UiField @Ignore InlineLabel mimeType;
    @UiField @Ignore InlineHyperlink infoType;
    @UiField Image deselectInfoTypeIcon;

    private static DetailsViewImplUiBinder ourUiBinder = GWT.create(DetailsViewImplUiBinder.class);
    private final EditorDriver editorDriver = GWT.create(EditorDriver.class);
    private final DiskResourceUtil diskResourceUtil;
    private final Presenter presenter;
    private final TagListPresenterFactory tagListPresenterFactory;
    private DiskResource boundValue;
    private TagsView.Presenter tagPresenter;

    private final Logger LOG = Logger.getLogger(DetailsViewImpl.class.getSimpleName());

    @Inject
    DetailsViewImpl(final TagListPresenterFactory tagListPresenterFactory,
                    final DiskResourceUtil diskResourceUtil,
                    final DetailsView.Appearance appearance,
                    @Assisted final DetailsView.Presenter presenter) {
        this.tagListPresenterFactory = tagListPresenterFactory;
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;
        this.presenter = presenter;

        initWidget(ourUiBinder.createAndBindUi(this));
        dateCreated.setValue(new Date());
        lastModified.setValue(new Date());

        editorDriver.initialize(this);
    }

    //region Handler Registration
    @Override
    public HandlerRegistration addEditInfoTypeSelectedEventHandler(EditInfoTypeSelected.EditInfoTypeSelectedEventHandler handler) {
        return addHandler(handler, EditInfoTypeSelected.TYPE);
    }

    @Override
    public HandlerRegistration addManageSharingSelectedEventHandler(ManageSharingSelected.ManageSharingSelectedEventHandler handler) {
        return addHandler(handler, ManageSharingSelected.TYPE);
    }
    //endregion

    //region Taggable Methods
    @Override
    public void attachTag(IplantTag tag) {

    }

    @Override
    public void detachTag(IplantTag tag) {

    }

    @Override
    public void selectTag(IplantTag tag) {

    }
    //endregion

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        if (event.getSelection().isEmpty()
                || event.getSelection().size() != 1) {
            bind(null);
            // Hide table
            table.addClassName(appearance.css().hidden());
            return;
        }
        table.removeClassName(appearance.css().hidden());
        // UPDATE ROW VISIBILITIES
        DiskResource singleSelection = event.getSelection().iterator().next();
        if (singleSelection instanceof File) {
            // Show rows
            sizeRow.removeClassName(appearance.css().hidden());
            mimeTypeRow.removeClassName(appearance.css().hidden());
            infoTypeRow.removeClassName(appearance.css().hidden());
            sendToRow.removeClassName(appearance.css().hidden());

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

        if(diskResourceUtil.inTrash(singleSelection)){
            shareRow.addClassName(appearance.css().hidden());
            sendToRow.addClassName(appearance.css().hidden());
        } else {
            shareRow.removeClassName(appearance.css().hidden());
            sendToRow.removeClassName(appearance.css().hidden());
        }

       bind(singleSelection);
    }

    void bind(final DiskResource resource){
        this.boundValue = resource;
         // Update editor
        editorDriver.edit(resource);

        if(resource == null){
            return;
        }
//        if(resource == null){
            permission.setText("");
            size.setText("");
            fileFolderNum.setText("");
            mimeType.setText("");
            sharing.setText("");
            infoType.setText("");
            sendTo.setText("");
//            return;
//        }
        // Manually populate
        permission.setText(resource.getPermission().name());
        if(resource instanceof File){
            File file = (File) resource;
            size.setText(diskResourceUtil.formatFileSize(file.getSize() + ""));
            mimeType.setText(file.getContentType());
            infoType.setText(file.getInfoType());
        } else if(resource instanceof Folder){
            Folder folder = (Folder) resource;
            // file/folder count
            fileFolderNum.setText(folder.getFileCount() + " / " + folder.getDirCount());
        }

        // Update sharing label
        if(PermissionValue.own.equals(resource.getPermission())){
            sharing.removeStyleName(appearance.css().disabledHyperlink());
            if(resource.getShareCount() > 0){
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
        if(resInfoType != null){
            sendTo.removeStyleName(appearance.css().disabledHyperlink());
            if(diskResourceUtil.isTreeInfoType(resInfoType)){
                sendTo.setText(appearance.treeViewer());
            } else if(diskResourceUtil.isGenomeVizInfoType(resInfoType)){
                sendTo.setText(appearance.coge());
            } else if(diskResourceUtil.isEnsemblInfoType(resInfoType)) {
                sendTo.setText(appearance.ensembl());
            }

        } else {
            sendTo.setText(appearance.viewersDisabled());
            sendTo.addStyleName(appearance.css().disabledHyperlink());
        }

        PermissionValue permission = resource.getPermission();
        // Update Infotype
        if(resource instanceof File) {
            if(PermissionValue.own.equals(permission)
                   || PermissionValue.write.equals(permission)){
                infoType.removeStyleName(appearance.css().disabledHyperlink());

                // Display Infotype
                if(resInfoType != null) {
                    infoType.setText(resInfoType.toString());
                    // display deselect icon
                    deselectInfoTypeIcon.setVisible(true);

                } else {
                    infoType.setText(appearance.selectInfoType());
                    // hide deselect icon
                    deselectInfoTypeIcon.setVisible(false);
                }
            } else {
                infoType.addStyleName(appearance.css().disabledHyperlink());
                // hide deselect icon
                deselectInfoTypeIcon.setVisible(false);

                if(resInfoType != null){
                    infoType.setText(resInfoType.toString());
                } else {
                    infoType.setText(appearance.infoTypeDisabled());
                }
            }
        }

    }

    private HorizontalPanel getInfoTypeLabel(String label, File info) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
//        fl.setHTML(getDetailAsHtml(label, true));
        panel.add(fl);
        String infoType = info.getInfoType();

        IPlantAnchor link;
        if (infoType != null && !infoType.isEmpty()) {
            if (info.getPermission().equals(PermissionValue.own) || info.getPermission().equals(PermissionValue.write)) {
                link = new IPlantAnchor(infoType, 60, new InfoTypeClickHandler(infoType));
                panel.add(link);
                Image rmImg = new Image(IplantResources.RESOURCES.deleteIcon());
                rmImg.addClickHandler(new RemoveInfoTypeClickHandler());
                rmImg.setTitle(appearance.delete());
                rmImg.getElement().getStyle().setCursor(Style.Cursor.POINTER);
                panel.add(rmImg);
            } else {
                FieldLabel infoLbl = new FieldLabel();
                infoLbl.setLabelSeparator("");
//                infoLbl.setHTML(getDetailAsHtml(infoType, false));
                panel.add(infoLbl);
            }
        } else {
            if (info.getPermission().equals(PermissionValue.own) || info.getPermission().equals(PermissionValue.write)) {
                link = new IPlantAnchor("Select", 100, new InfoTypeClickHandler(""));
                panel.add(link);
            } else {
                FieldLabel infoLbl = new FieldLabel();
                infoLbl.setLabelSeparator("");
                infoLbl.setHTML("-");
                panel.add(infoLbl);
            }
        }

        return panel;
    }

    //region UIHandlers

    @UiHandler("sharing")
    void onSharingClicked(ClickEvent event){
        if(boundValue == null){
            return;
        }
        fireEvent(new ManageSharingSelected(boundValue));
        if(boundValue.getShareCount() == 0){
            LOG.fine("Begin sharing");
        }
        LOG.fine("Sharing clicked");
    }

    @UiHandler("sendTo")
    void onSendToClicked(ClickEvent event){
        if(boundValue == null){
            return;
        }
        InfoType resInfoType = InfoType.fromTypeString(boundValue.getInfoType());
        if (resInfoType == null) {
            return;
        }

        if(diskResourceUtil.isTreeInfoType(resInfoType)){
            presenter.sendSelectedResourcesToTreeViewer(boundValue);
        } else if(diskResourceUtil.isGenomeVizInfoType(resInfoType)){
            presenter.sendSelectedResourcesToCoge(boundValue);
        } else if(diskResourceUtil.isEnsemblInfoType(resInfoType)) {
            presenter.sendSelectedResourceToEnsembl(boundValue);
        }

        LOG.fine("Send to clicked");
    }

    @UiHandler("infoType")
    void onInfoTypeClicked(ClickEvent event){
        if(boundValue == null
            || !(boundValue instanceof File)){
            return;
        }
        fireEvent(new EditInfoTypeSelected(Lists.newArrayList(boundValue)));
    }

    @UiHandler("deselectInfoTypeIcon")
    void onDeselectInfoTypeClicked(ClickEvent event){
        // FIXME Fire event
//        presenter.resetInfoType();
        LOG.fine("Deselect Clicked");
    }

    //endregion

    @Override
    public void onUpdate(StoreUpdateEvent<DiskResource> event) {
        // MUST MATCH THE CURRENTLY BOUND DISKRESOURCE
        if(event.getItems().size() != 1
            || event.getItems().iterator().next() != boundValue){
            return;
        }
        bind(event.getItems().iterator().next());
    }


    public void updateDetails(DiskResource info) {
        // FIXME Who does this? Tags presenter, I think.
//        presenter.getTagsForSelectedResource();
        // FIXME
//        detailsPanel.add(createTagView("", true, true));
    }

    @UiFactory
    @Ignore
    DateLabel createDateLabel() {
        return new DateLabel(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM));
    }

    private HorizontalPanel buildRow() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHeight("25px"); //$NON-NLS-1$
        panel.setSpacing(1);
        return panel;
    }

    private Command createOnBlurCmd(final SimplePanel boundaryBox, final String defaultStyle) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute("style", defaultStyle);
            }
        };
    }

    private Command createOnFocusCmd(final SimplePanel boundaryBox, final String defaultStyle) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute("style", defaultStyle + " outline: -webkit-focus-ring-color auto 5px;");
            }
        };
    }

    private TagsView.Presenter createTagListPresenter(boolean editable,
                                                      boolean removeable,
                                                      Command onFocusCmd,
                                                      Command onBlurCmd) {
        TagsView.Presenter tagsPresenter = tagListPresenterFactory.createTagListPresenter(this);
        tagsPresenter.setEditable(editable);
        tagsPresenter.setRemovable(removeable);
        tagsPresenter.setOnFocusCmd(onFocusCmd);
        tagsPresenter.setOnBlurCmd(onBlurCmd);

        return tagsPresenter;
    }

    private Widget createTagView(String containerStyle,
                                 boolean editable,
                                 boolean removable) {
        HorizontalPanel hp = new HorizontalPanel();
        SimplePanel boundaryBox = new SimplePanel();
        if (tagPresenter == null) {
            tagPresenter = createTagListPresenter(editable,
                                                  removable,
                                                  createOnFocusCmd(boundaryBox, containerStyle),
                                                  createOnBlurCmd(boundaryBox, containerStyle));

        }
        tagPresenter.removeAll();
        boundaryBox.setWidget(tagPresenter.getTagListView());
        hp.add(boundaryBox);
        return hp;
    }

}