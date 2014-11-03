/**
 *
 */
package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.fileViewers.client.callbacks.EnsemblUtil;
import org.iplantc.de.fileViewers.client.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.fileViewers.client.callbacks.TreeUrlCallback;
import org.iplantc.de.fileViewers.client.views.cells.TreeUrlCell;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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
 * @author sriram
 */
public class ExternalVisualizationURLViewerImpl extends AbstractFileViewer implements IsMaskable {

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

    @UiField
    ColumnModel<VizUrl> cm;
    @UiField
    VerticalLayoutContainer con;
    @UiField
    Grid<VizUrl> grid;
    @UiField
    GridView<VizUrl> gridView;
    @UiField
    ListStore<VizUrl> listStore;
    @UiField
    ToolBar toolbar;

    private static TreeViewerUiBinder uiBinder = GWT.create(TreeViewerUiBinder.class);
    private final IplantDisplayStrings displayStrings;
    private final FileEditorServiceFacade fileEditorService;
    private final IplantResources iplantResources;
    private final Widget widget;

    public ExternalVisualizationURLViewerImpl(final File file,
                                              final String infoType) {
        super(file, infoType);
        this.displayStrings = I18N.DISPLAY;
        this.iplantResources = IplantResources.RESOURCES;
        this.fileEditorService = ServicesInjector.INSTANCE.getFileEditorServiceFacade();
        this.widget = uiBinder.createAndBindUi(this);
        gridView.setAutoExpandColumn(cm.getColumn(1));
        buildToolBar(DiskResourceUtil.createInfoTypeSplittable(infoType));
    }

    @Override
    public Widget asWidget() {
        return widget;
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
    public String getViewName() {
        return displayStrings.visualization() + ":" + file.getName();
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object data) {
        listStore.clear();
        List<VizUrl> urls = (List<VizUrl>) data;
        listStore.addAll(urls);
    }

    @Override
    public void unmask() {
        con.unmask();
    }

    private TextButton buildCogeButton() {
        TextButton button = new TextButton(displayStrings.sendToCogeMenuItem(), iplantResources.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(displayStrings.loadingMask());
                JSONObject obj = new JSONObject();
                JSONArray pathArr = new JSONArray();
                pathArr.set(0, new JSONString(file.getPath()));
                obj.put("paths", pathArr);
                fileEditorService.viewGenomes(obj, new LoadGenomeInCoGeCallback(ExternalVisualizationURLViewerImpl.this));
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
        ColumnConfig<VizUrl, String> label = new ColumnConfig<>(props.label(), 75);
        label.setHeader(displayStrings.label());
        configs.add(label);

        ColumnConfig<VizUrl, VizUrl> url = new ColumnConfig<>(new IdentityValueProvider<VizUrl>(), 280);
        url.setHeader("URL");
        url.setCell(new TreeUrlCell());
        configs.add(url);

        return new ColumnModel<>(configs);

    }

    private TextButton buildEnsemblButton() {
        TextButton button = new TextButton(displayStrings.sendToEnsemblMenuItem(), iplantResources.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(displayStrings.loadingMask());
                EnsemblUtil util = new EnsemblUtil(file, infoType, ExternalVisualizationURLViewerImpl.this);
                util.sendToEnsembl();
            }
        });
        return button;
    }

    private void buildToolBar(Splittable infoTypeSplittable) {
        if (DiskResourceUtil.isTreeTab(infoTypeSplittable)) {
            TextButton button = buildTreeViewerButton();
            toolbar.add(button);
        } else if (DiskResourceUtil.isGenomeVizTab(infoTypeSplittable)) {
            TextButton button = buildCogeButton();
            toolbar.add(button);
        } else if (DiskResourceUtil.isEnsemblVizTab(infoTypeSplittable)) {
            TextButton button = buildEnsemblButton();
            toolbar.add(button);
        }
    }

    private TextButton buildTreeViewerButton() {
        TextButton button = new TextButton(displayStrings.sendToTreeViewerMenuItem(), iplantResources.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(displayStrings.loadingMask());
                fileEditorService.getTreeUrl(file.getPath(),
                                             true,
                                              new TreeUrlCallback(file, ExternalVisualizationURLViewerImpl.this, ExternalVisualizationURLViewerImpl.this));

            }
        });
        return button;
    }

}
