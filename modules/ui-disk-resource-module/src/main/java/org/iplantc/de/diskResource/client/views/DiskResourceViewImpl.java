package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelectedEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagListPresenterFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * FIXME Factor out appearance. This class is not testable in it's current form.
 * FIXME Factor out details panel.
 *
 * @author jstroot, sriram, psarando
 */
public class DiskResourceViewImpl extends Composite implements DiskResourceView {

    private final class RemoveInfoTypeClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            presenter.resetInfoType();
        }
    }

    @UiTemplate("DiskResourceView.ui.xml")
    interface DiskResourceViewUiBinder extends UiBinder<Widget, DiskResourceViewImpl> {
    }

    private static DiskResourceViewUiBinder BINDER = GWT.create(DiskResourceViewUiBinder.class);

    private final DiskResourceUtil diskResourceUtil;
    private final GridView.Presenter gridViewPresenter;
    private final TagListPresenterFactory tagListPresenterFactory;
    private Presenter presenter;

    ToolbarView toolbar;

    private final IplantDisplayStrings displayStrings;

    @UiField BorderLayoutContainer con;
    @UiField VerticalLayoutContainer detailsPanel;
    @UiField BorderLayoutData westData;
    @UiField BorderLayoutData centerData;
    @UiField BorderLayoutData eastData;
    @UiField BorderLayoutData northData;
    @UiField BorderLayoutData southData;

    @UiField(provided = true) NavigationView navigationView;
    @UiField(provided = true) GridView centerGridView;

    private TagsView.Presenter tagPresenter;

    Logger LOG = Logger.getLogger("DRV");

    @Inject
    DiskResourceViewImpl(final ToolbarView viewToolbar,
                         final IplantDisplayStrings displayStrings,
                         final DiskResourceUtil diskResourceUtil,
                         final TagListPresenterFactory tagListPresenterFactory,
                         @Assisted final DiskResourceView.Presenter presenter,
                         @Assisted final NavigationView.Presenter navigationPresenter,
                         @Assisted final GridView.Presenter gridViewPresenter) {
        this.navigationView = navigationPresenter.getView();
        this.centerGridView = gridViewPresenter.getView();
        this.gridViewPresenter = gridViewPresenter;
        this.toolbar = viewToolbar;
        this.displayStrings = displayStrings;
        this.diskResourceUtil = diskResourceUtil;
        this.tagListPresenterFactory = tagListPresenterFactory;
        this.presenter = presenter;

        initWidget(BINDER.createAndBindUi(this));

        toolbar.init(presenter, this);

        detailsPanel.setScrollMode(ScrollMode.AUTO);

        // by default no details to show...
        resetDetailsPanel();

        con.setNorthWidget(toolbar, northData);

    }

    @Override
    public HandlerRegistration addManageSharingSelectedEventHandler(ManageSharingSelectedEvent.ManageSharingSelectedEventHandler handler) {
        // FIXME Migrate to details view
        return addHandler(handler, ManageSharingSelectedEvent.TYPE);
    }

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        if (event.getSelection().isEmpty()) {
            resetDetailsPanel();
        }
    }

    @UiFactory
    public ValueProvider<Folder, String> createValueProvider() {
        return new ValueProvider<Folder, String>() {

            @Override
            public String getValue(Folder object) {
                return object.getName();
            }

            @Override
            public void setValue(Folder object, String value) {}

            @Override
            public String getPath() {
                return "name"; //$NON-NLS-1$
            }
        };
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolbar.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.MENU_BAR);
    }

    @Override
    public void setEastWidgetHidden(boolean hideEastWidget) {
        eastData.setHidden(hideEastWidget);
    }

    @Override
    public void setNorthWidgetHidden(boolean hideNorthWidget) {
        northData.setHidden(hideNorthWidget);
    }

    @Override
    public void setSouthWidget(IsWidget widget) {
        southData.setHidden(false);
        con.setSouthWidget(widget, southData);
    }

    @Override
    public void setSouthWidget(IsWidget widget, double size) {
        southData.setHidden(false);
        southData.setSize(size);
        con.setSouthWidget(widget, southData);
    }

    @Override
    public ToolbarView getToolbar() {
        return toolbar;
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);
    }

    @Override
    public void unmask() {
        con.unmask();
    }

    @Override
    public void resetDetailsPanel() {
        detailsPanel.clear();
        FieldLabel fl = new FieldLabel();
        fl.setLabelWidth(detailsPanel.getOffsetWidth(true) - 10);
        fl.setLabelSeparator(""); //$NON-NLS-1$
        fl.setHTML(getDetailAsHtml(displayStrings.noDetails(), false));
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

    private HorizontalPanel buildRow() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHeight("25px"); //$NON-NLS-1$
        panel.setSpacing(1);
        return panel;
    }

    @Override
    public void updateDetails(DiskResource info) {
        detailsPanel.clear();
        List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
        // guard race condition
        if (selection != null && selection.size() == 1) {
            Iterator<DiskResource> it = selection.iterator();
            DiskResource next = it.next();
            if (next.getId().equals(info.getId())) {
                detailsPanel.add(getDateLabel(displayStrings.lastModified(), info.getLastModified()));
                detailsPanel.add(getDateLabel(displayStrings.createdDate(), info.getDateCreated()));
                detailsPanel.add(getPermissionsLabel(displayStrings.permissions(), info.getPermission()));
                if (!diskResourceUtil.inTrash(next)) {
                    detailsPanel.add(getSharingLabel(displayStrings.share(),
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

        presenter.getTagsForSelectedResource();
        detailsPanel.add(createTagView("", true, true));
    }

    @Override
    public void updateTags(List<IplantTag> tags) {
        tagPresenter.buildTagCloudForSelectedResource(tags);
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
        detailsPanel.add(getDirFileCount(displayStrings.files() + " / " + displayStrings.folders(), //$NON-NLS-1$
                info.getFileCount(), info.getDirCount()));
    }

    private void addFileDetails(File info, boolean addViewerInfo) {
        detailsPanel.add(getStringLabel(displayStrings.size(), diskResourceUtil.formatFileSize(info.getSize() + ""))); //$NON-NLS-1$
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
        fl.setHTML(getDetailAsHtml(displayStrings.sendTo(), true));
        panel.add(fl);
        IPlantAnchor link = null;
        String infoType = info.getInfoType();
        if (infoType != null && !infoType.isEmpty()) {
            Splittable manifest = diskResourceUtil.createInfoTypeSplittable(infoType);
            if (diskResourceUtil.isTreeTab(manifest)) {
                link = new IPlantAnchor(displayStrings.treeViewer(), 100, new TreeViewerInfoClickHandler());
            } else if (diskResourceUtil.isGenomeVizTab(manifest)) {
                link = new IPlantAnchor(displayStrings.coge(), 100, new CogeViewerInfoClickHandler());
            } else if (diskResourceUtil.isEnsemblVizTab(manifest)) {
                link = new IPlantAnchor(displayStrings.ensembl(), 100, new EnsemblViewerInfoClickHandler());
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
                link = new IPlantAnchor(displayStrings.nosharing(), 100, new SharingLabelClickHandler());
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
                rmImg.setTitle(displayStrings.delete());
                rmImg.getElement().getStyle().setCursor(Cursor.POINTER);
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

    @Override
    public void maskDetailsPanel() {
        detailsPanel.mask(displayStrings.loadingMask());
    }

    @Override
    public void unmaskDetailsPanel() {
        detailsPanel.unmask();
    }

    private class InfoTypeClickHandler implements ClickHandler {

        private final String infoType;

        public InfoTypeClickHandler(String type) {
            this.infoType = type;
        }

        @Override
        public void onClick(ClickEvent arg0) {
            List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
            Iterator<DiskResource> it = selection.iterator();
            presenter.onInfoTypeClick(it.next(), infoType);
        }

    }

    private class SharingLabelClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            // FIXME This will be migrated to Details MVP
            final List<DiskResource> selectedDiskResources = gridViewPresenter.getSelectedDiskResources();
            Preconditions.checkArgument(selectedDiskResources.size() == 1);
            fireEvent(new ManageSharingSelectedEvent(selectedDiskResources.get(0)));
        }
    }

    private class TreeViewerInfoClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            presenter.sendSelectedResourcesToTreeViewer();
        }

    }

    private class CogeViewerInfoClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            presenter.sendSelectedResourcesToCoge();

        }

    }

    private class EnsemblViewerInfoClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            presenter.sendSelectedResourceToEnsembl();

        }
    }

    @Override
    public void maskSendToCoGe() {
        toolbar.maskSendToCoGe();
    }

    @Override
    public void unmaskSendToCoGe() {
        toolbar.unmaskSendToCoGe();
    }

    @Override
    public void maskSendToEnsembl() {
        toolbar.maskSendToEnsembl();
    }

    @Override
    public void unmaskSendToEnsembl() {
        toolbar.unmaskSendToEnsembl();
    }

    @Override
    public void maskSendToTreeViewer() {
        toolbar.maskSendToTreeViewer();
    }

    @Override
    public void unmaskSendToTreeViewer() {
        toolbar.unmaskSendToTreeViewer();
    }

    @Override
    public void attachTag(IplantTag tag) {
        presenter.attachTag(tag);
    }

    @Override
    public void detachTag(IplantTag tag) {
        presenter.detachTag(tag);
    }

    @Override
    public void selectTag(IplantTag tag) {
        LOG.fine("tag selected ==>" + tag.getValue());
        presenter.doSearchTaggedWithResources(Sets.newHashSet(tag));
    }

}
