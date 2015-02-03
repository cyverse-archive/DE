package org.iplantc.de.diskResource.client.views.details;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.DetailsView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagListPresenterFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * View will be constructed once, not on every selection.
 * View is updated on grid selection changed.
 * View is updated when a store update event occurs
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class DetailsViewImpl extends Composite implements DetailsView {
    interface DetailsViewImplUiBinder extends UiBinder<DivElement, DetailsViewImpl> { }

    private static DetailsViewImplUiBinder ourUiBinder = GWT.create(DetailsViewImplUiBinder.class);
    private final TagListPresenterFactory tagListPresenterFactory;
    private final DiskResourceUtil diskResourceUtil;
    private final Appearance appearance;
    private final Presenter presenter;
    private VerticalLayoutContainer detailsPanel;
    private TagsView.Presenter tagPresenter;

    @Inject
    DetailsViewImpl(final TagListPresenterFactory tagListPresenterFactory,
                    final DiskResourceUtil diskResourceUtil,
                    final DetailsView.Appearance appearance,
                    @Assisted final DetailsView.Presenter presenter) {
        this.tagListPresenterFactory = tagListPresenterFactory;
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;
        this.presenter = presenter;
        DivElement rootElement = ourUiBinder.createAndBindUi(this);
    }

    @Override
    public HandlerRegistration addEditInfoTypeSelectedEventHandler(EditInfoTypeSelected.EditInfoTypeSelectedEventHandler handler) {
        return addHandler(handler, EditInfoTypeSelected.TYPE);
    }

    @Override
    public HandlerRegistration addManageSharingSelectedEventHandler(ManageSharingSelected.ManageSharingSelectedEventHandler handler) {
        return addHandler(handler, ManageSharingSelected.TYPE);
    }

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        if (event.getSelection().isEmpty()) {
            resetDetailsPanel();
        }
    }

    @Override
    public void onUpdate(StoreUpdateEvent<DiskResource> event) {
         // update view
        // MUST MATCH THE CURRENTLY BOUND DISKRESOURCE
    }

    private void resetDetailsPanel() {
        // Clear view
        detailsPanel.clear();
        FieldLabel fl = new FieldLabel();
        fl.setLabelWidth(detailsPanel.getOffsetWidth(true) - 10);
        fl.setLabelSeparator(""); //$NON-NLS-1$
        fl.setHTML(getDetailAsHtml(appearance.noDetails(), false));
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(2);
        hp.add(fl);
        detailsPanel.add(hp);
    }

    private String getDetailAsHtml(String detail, boolean bolded) {
        if (bolded) {
            return "<span style='font-size:10px;'><b>" + detail + "</b> </span>"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            return "<span style='font-size:10px;padding-left:2px;'>" + detail + "</span>"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Parses a timestamp string into a formatted date string and adds it to
     * this panel.
     *
     */
    private HorizontalPanel getDateLabel(String label, Date date) {
        String value = ""; //$NON-NLS-1$

        if (date != null) {
            DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);

            value = formatter.format(date);
        }

        return getStringLabel(label, value);

    }

    private HorizontalPanel getStringLabel(String label, String value) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(label, true));
        panel.add(fl);

        FieldLabel fv = new FieldLabel();
        fl.setWidth(100);
        fv.setLabelSeparator(""); //$NON-NLS-1$
        fv.setHTML(getDetailAsHtml(value + "", false)); //$NON-NLS-1$
        panel.add(fv);

        return panel;
    }

    private HorizontalPanel getDirFileCount(String label, int file_count, int dir_count) {
        return getStringLabel(label, file_count + " / " + dir_count); //$NON-NLS-1$
    }

    /**
     * Add permissions detail
     *
     */
    private HorizontalPanel getPermissionsLabel(String label, PermissionValue p) {
        return getStringLabel(label, p.toString());
    }

    public void updateDetails(DiskResource info) {
        detailsPanel.clear();
        List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
        // guard race condition
        // FIXME WHat race condition? I don't think it's valid anymore
        if (selection != null && selection.size() == 1) {
            Iterator<DiskResource> it = selection.iterator();
            DiskResource next = it.next();
            if (next.getId().equals(info.getId())) {
                detailsPanel.add(getDateLabel(appearance.lastModified(), info.getLastModified()));
                detailsPanel.add(getDateLabel(appearance.createdDate(), info.getDateCreated()));
                detailsPanel.add(getPermissionsLabel(appearance.permissions(), info.getPermission()));
                if (!diskResourceUtil.inTrash(next)) {
                    detailsPanel.add(getSharingLabel(appearance.share(),
                                                     info.getShareCount(),
                                                     info.getPermission()));
                }
                if (info instanceof File) {
                    addFileDetails((File)info, !diskResourceUtil.inTrash(next));

                } else {
                    addFolderDetails((Folder)info);
                }
            }

        }

        // FIXME Who does this? Tags presenter, I think.
        presenter.getTagsForSelectedResource();
        detailsPanel.add(createTagView("", true, true));
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

    private Command createOnFocusCmd(final SimplePanel boundaryBox, final String defaultStyle) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute("style", defaultStyle + " outline: -webkit-focus-ring-color auto 5px;");
            }
        };
    }

    private Command createOnBlurCmd(final SimplePanel boundaryBox, final String defaultStyle) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute("style", defaultStyle);
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

    private void addFolderDetails(Folder info) {
        detailsPanel.add(getDirFileCount(appearance.files() + " / " + appearance.folders(), //$NON-NLS-1$
                                         info.getFileCount(), info.getDirCount()));
    }

    private void addFileDetails(File info, boolean addViewerInfo) {
        detailsPanel.add(getStringLabel(appearance.size(), diskResourceUtil.formatFileSize(info.getSize() + ""))); //$NON-NLS-1$
        detailsPanel.add(getStringLabel("Type", info.getContentType()));
        detailsPanel.add(getInfoTypeLabel("Info-Type", info));
        if (addViewerInfo) {
            detailsPanel.add(getViewerInfo(info));
        }
    }


    private HorizontalPanel getViewerInfo(File info) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(appearance.sendTo(), true));
        panel.add(fl);
        IPlantAnchor link = null;
        String infoType = info.getInfoType();
        if (infoType != null && !infoType.isEmpty()) {
            Splittable manifest = diskResourceUtil.createInfoTypeSplittable(infoType);
            if (diskResourceUtil.isTreeTab(manifest)) {
                link = new IPlantAnchor(appearance.treeViewer(), 100, new TreeViewerInfoClickHandler());
            } else if (diskResourceUtil.isGenomeVizTab(manifest)) {
                link = new IPlantAnchor(appearance.coge(), 100, new CogeViewerInfoClickHandler());
            } else if (diskResourceUtil.isEnsemblVizTab(manifest)) {
                link = new IPlantAnchor(appearance.ensembl(), 100, new EnsemblViewerInfoClickHandler());
            }
        }
        if (link == null) {
            panel.add(new HTML("-"));
        } else {
            panel.add(link);
        }
        return panel;
    }

    private HorizontalPanel getSharingLabel(String label, int shareCount, PermissionValue permissions) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(label, true));
        panel.add(fl);
        if (permissions.equals(PermissionValue.own)) {
            IPlantAnchor link;
            if (shareCount == 0) {
                link = new IPlantAnchor(appearance.nosharing(), 100, new SharingLabelClickHandler());
            } else {
                link = new IPlantAnchor("" + shareCount, 100, new SharingLabelClickHandler()); //$NON-NLS-1$
            }
            panel.add(link);
        } else {
            panel.add(new HTML("-"));
        }

        return panel;
    }

    private HorizontalPanel getInfoTypeLabel(String label, File info) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(label, true));
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
                infoLbl.setHTML(getDetailAsHtml(infoType, false));
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

    private final class RemoveInfoTypeClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            // FIXME Who listens here?
//            presenter.resetInfoType();
        }
    }
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

    private class SharingLabelClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            DiskResource boundResource = null;
            fireEvent(new ManageSharingSelected(boundResource));
        }
    }

    private class TreeViewerInfoClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            DiskResource boundResource = null;
            presenter.sendSelectedResourcesToTreeViewer(boundResource);
        }

    }

    private class CogeViewerInfoClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            DiskResource boundResource = null;
            presenter.sendSelectedResourcesToCoge(boundResource);

        }

    }

    private class EnsemblViewerInfoClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            DiskResource boundResource = null;
            presenter.sendSelectedResourceToEnsembl(boundResource);

        }
    }

    private HorizontalPanel buildRow() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHeight("25px"); //$NON-NLS-1$
        panel.setSpacing(1);
        return panel;
    }


}