/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.VizUrl;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.viewer.callbacks.EnsemblUtil;
import org.iplantc.de.client.viewer.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.client.viewer.callbacks.TreeUrlCallback;
import org.iplantc.de.client.viewer.views.cells.TreeUrlCell;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
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
 * 
 */
public class ExternalVizualizationURLViwerImpl extends AbstractFileViewer implements IsMaskable {

    private static TreeViwerUiBinder uiBinder = GWT.create(TreeViwerUiBinder.class);

    @UiTemplate("ExternalVizualizationURLViwer.ui.xml")
    interface TreeViwerUiBinder extends UiBinder<Widget, ExternalVizualizationURLViwerImpl> {
    }

    private final Widget widget;

    Grid<VizUrl> grid;

    @UiField(provided = true)
    ListStore<VizUrl> listStore;

    @UiField(provided = true)
    ColumnModel<VizUrl> cm;

    @UiField
    GridView<VizUrl> gridView;

    @UiField
    ToolBar toolbar;

    @UiField
    VerticalLayoutContainer con;

    Logger logger = Logger.getLogger("Viz");

    public ExternalVizualizationURLViwerImpl(File file, String infoType) {
        super(file, infoType);
        this.cm = buildColumnModel();
        this.listStore = new ListStore<VizUrl>(new TreeUrlKeyProvider());
        this.widget = uiBinder.createAndBindUi(this);
        con.setScrollMode(ScrollMode.AUTOY);
        gridView.setAutoExpandColumn(cm.getColumn(1));
        buildToolBar(infoType);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(Object data) {
        listStore.clear();
        List<VizUrl> urls = (List<VizUrl>)data;
        listStore.addAll(urls);
    }

    @Override
    public void loadData() {
        // do nothing intentionally

    }

    private void buildToolBar(String infoType) {
        JSONObject manifest = new JSONObject();
        manifest.put("info-type", new JSONString(infoType));
        if (DiskResourceUtil.isTreeTab(manifest)) {
            TextButton button = buildTreeViewerButton();
            toolbar.add(button);
        } else if (DiskResourceUtil.isGenomeVizTab(manifest)) {
            TextButton button = buildCogeButton();
            toolbar.add(button);
        } else if (DiskResourceUtil.isEnsemblVizTab(manifest)) {
            TextButton button = buildEnsemblButton();
            toolbar.add(button);
        }
    }

    private TextButton buildEnsemblButton() {
        TextButton button = new TextButton(I18N.DISPLAY.sendToEnsemblMenuItem(), IplantResources.RESOURCES.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(I18N.DISPLAY.loadingMask());
                EnsemblUtil util = new EnsemblUtil(file, infoType, ExternalVizualizationURLViwerImpl.this);
                util.sendToEnsembl();
            }
        });
        return button;
    }

    private TextButton buildCogeButton() {
        TextButton button = new TextButton(I18N.DISPLAY.sendToCogeMenuItem(), IplantResources.RESOURCES.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(I18N.DISPLAY.loadingMask());
                JSONObject obj = new JSONObject();
                JSONArray pathArr = new JSONArray();
                pathArr.set(0, new JSONString(file.getPath()));
                obj.put("paths", pathArr);
                ServicesInjector.INSTANCE.getFileEditorServiceFacade().viewGenomes(obj, new LoadGenomeInCoGeCallback(ExternalVizualizationURLViwerImpl.this));

            }
        });
        return button;
    }

    private TextButton buildTreeViewerButton() {
        TextButton button = new TextButton(I18N.DISPLAY.sendToTreeViewerMenuItem(), IplantResources.RESOURCES.arrowUp());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                mask(I18N.DISPLAY.loadingMask());
                ServicesInjector.INSTANCE.getFileEditorServiceFacade().getTreeUrl(file.getId(), true,
                        new TreeUrlCallback(file, ExternalVizualizationURLViwerImpl.this, ExternalVizualizationURLViwerImpl.this));

            }
        });
        return button;
    }

    private ColumnModel<VizUrl> buildColumnModel() {
        VizUrlProperties props = GWT.create(VizUrlProperties.class);
        List<ColumnConfig<VizUrl, ?>> configs = new LinkedList<ColumnConfig<VizUrl, ?>>();
        ColumnConfig<VizUrl, String> label = new ColumnConfig<VizUrl, String>(props.label(), 75);
        label.setHeader(org.iplantc.de.resources.client.messages.I18N.DISPLAY.label());
        configs.add(label);

        ColumnConfig<VizUrl, VizUrl> url = new ColumnConfig<VizUrl, VizUrl>(new IdentityValueProvider<VizUrl>(), 280);
        url.setHeader("URL");
        url.setCell(new TreeUrlCell());
        configs.add(url);

        return new ColumnModel<VizUrl>(configs);

    }

    private class TreeUrlKeyProvider implements ModelKeyProvider<VizUrl> {
        private int index;

        @Override
        public String getKey(VizUrl item) {
            return index++ + "";
        }

    }

    @Override
    public String getViewName() {
        return org.iplantc.de.resources.client.messages.I18N.DISPLAY.visualization() + ":" + file.getName();
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);

    }

    @Override
    public void unmask() {
        con.unmask();

    }
}
