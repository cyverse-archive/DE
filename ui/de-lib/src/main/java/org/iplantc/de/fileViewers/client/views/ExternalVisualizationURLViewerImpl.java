/**
 *
 */
package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.fileViewers.client.callbacks.EnsemblUtil;
import org.iplantc.de.fileViewers.client.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.fileViewers.client.callbacks.TreeUrlCallback;
import org.iplantc.de.fileViewers.client.views.cells.TreeUrlCell;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class ExternalVisualizationURLViewerImpl extends AbstractFileViewer implements IsMaskable {

    public interface ExternalVisualizationURLViewerAppearance {

        ImageResource arrowUp();

        String containerHeight();

        String label();

        int labelColumnWidth();

        String sendToCogeLoadingMask();

        String sendToCogeMenuItem();

        String sendToEnsemblMenuItem();

        String sendToEnsemblLoadingMask();

        String sendToTreeViewerMenuItem();

        String sentToTreeViewerLoadingMask();

        String toolbarHeight();

        String urlColumnHeaderLabel();

        int urlColumnHeaderWidth();

        String viewName(String fileName);
    }

    private class TreeUrlKeyProvider implements ModelKeyProvider<VizUrl> {
        private int index;

        @Override
        public String getKey(VizUrl item) {
            return index++ + "";
        }
    }

    @UiTemplate("ExternalVisualizationURLViewer.ui.xml")
    interface TreeViewerUiBinder extends UiBinder<Widget, ExternalVisualizationURLViewerImpl> { }

    Logger logger = Logger.getLogger("Viz");

    @UiField ColumnModel<VizUrl> cm;
    @UiField VerticalLayoutContainer con;
    @UiField Grid<VizUrl> grid;
    @UiField GridView<VizUrl> gridView;
    @UiField ListStore<VizUrl> listStore;
    @UiField ToolBar toolbar;
    @UiField(provided = true) ExternalVisualizationURLViewerAppearance appearance = GWT.create(ExternalVisualizationURLViewerAppearance.class);

    private static TreeViewerUiBinder uiBinder = GWT.create(TreeViewerUiBinder.class);
    private final FileEditorServiceFacade fileEditorService;
    private final DiskResourceServiceFacade diskResourceServiceFacade;
    private final DiskResourceUtil diskResourceUtil;

    public ExternalVisualizationURLViewerImpl(final File file,
                                              final String infoType,
                                              final FileEditorServiceFacade fileEditorService,
                                              final DiskResourceServiceFacade diskResourceServiceFacade) {
        super(file, infoType);
        this.fileEditorService = fileEditorService;
        this.diskResourceServiceFacade = diskResourceServiceFacade;
        this.diskResourceUtil = DiskResourceUtil.getInstance();
        initWidget(uiBinder.createAndBindUi(this));
        gridView.setAutoExpandColumn(cm.getColumn(1));
        buildToolBar(diskResourceUtil.createInfoTypeSplittable(infoType));
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        con.fireEvent(event);
    }

    @Override
    public String getEditorContent() {
        return null;
    }

    @Override
    public String getViewName(String fileName) {
        Preconditions.checkNotNull(fileName, "Filename cannot be null for this viewer");
        return appearance.viewName(fileName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object data) {
        listStore.clear();
        List<VizUrl> urls = (List<VizUrl>) data;
        listStore.addAll(urls);
    }

    private TextButton buildCogeButton() {
        TextButton button = new TextButton(appearance.sendToCogeMenuItem(), appearance.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(appearance.sendToCogeLoadingMask());
                JSONObject obj = new JSONObject();
                JSONArray pathArr = new JSONArray();
                pathArr.set(0, new JSONString(file.getPath()));
                obj.put("paths", pathArr);
                fileEditorService.loadGenomesInCoge(obj, new LoadGenomeInCoGeCallback(ExternalVisualizationURLViewerImpl.this));
            }
        });
        return button;
    }

    @UiFactory
    ListStore<VizUrl> buildListStore() {
        return new ListStore<>(new TreeUrlKeyProvider());
    }
    @UiFactory
    ColumnModel<VizUrl> buildColumnModel() {
        VizUrlProperties props = GWT.create(VizUrlProperties.class);
        List<ColumnConfig<VizUrl, ?>> configs = new LinkedList<>();
        ColumnConfig<VizUrl, String> label = new ColumnConfig<>(props.label(), appearance.labelColumnWidth());
        label.setHeader(appearance.label());
        configs.add(label);

        ColumnConfig<VizUrl, VizUrl> url = new ColumnConfig<>(new IdentityValueProvider<VizUrl>(), appearance.urlColumnHeaderWidth());
        url.setHeader(appearance.urlColumnHeaderLabel());
        url.setCell(new TreeUrlCell());
        configs.add(url);

        return new ColumnModel<>(configs);

    }

    private TextButton buildEnsemblButton() {
        TextButton button = new TextButton(appearance.sendToEnsemblMenuItem(), appearance.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(appearance.sendToEnsemblLoadingMask());
                EnsemblUtil util = new EnsemblUtil(file,
                                                   infoType,
                                                   ExternalVisualizationURLViewerImpl.this);
                util.sendToEnsembl(diskResourceServiceFacade);
            }
        });
        return button;
    }

    private void buildToolBar(Splittable infoTypeSplittable) {
        if (diskResourceUtil.isTreeTab(infoTypeSplittable)) {
            TextButton button = buildTreeViewerButton();
            toolbar.add(button);
        } else if (diskResourceUtil.isGenomeVizTab(infoTypeSplittable)) {
            TextButton button = buildCogeButton();
            toolbar.add(button);
        } else if (diskResourceUtil.isEnsemblVizTab(infoTypeSplittable)) {
            TextButton button = buildEnsemblButton();
            toolbar.add(button);
        }
    }

    private TextButton buildTreeViewerButton() {
        TextButton button = new TextButton(appearance.sendToTreeViewerMenuItem(), appearance.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(appearance.sentToTreeViewerLoadingMask());
                fileEditorService.getTreeUrl(file.getPath(),
                                             true,
                                              new TreeUrlCallback(file, ExternalVisualizationURLViewerImpl.this, ExternalVisualizationURLViewerImpl.this));

            }
        });
        return button;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

}
