/**
 *
 */
package org.iplantc.de.apps.integration.client.view.tools;

import org.iplantc.de.tools.requests.client.views.dialogs.NewToolRequestDialog;
import org.iplantc.de.apps.integration.client.view.deployedComponents.cells.DCNameHyperlinkCell;
import org.iplantc.de.apps.integration.client.view.deployedComponents.proxy.ToolSearchRPCProxy;
import org.iplantc.de.apps.integration.shared.AppIntegrationModule;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.commons.client.widgets.SearchField;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AbstractHtmlLayoutContainer.HtmlData;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A grid that displays list of available deployed components (bin/tools) in Condor
 *
 * @author sriram, jstroot
 */
public class DeployedComponentsListingViewImpl extends Composite implements
                                                                 DeployedComponentsListingView {

    interface DCDetailsRenderer extends XTemplates {
        @XTemplate(source = "DCDetails.html")
        SafeHtml render();
    }

    @UiTemplate("DeployedComponentsListingView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, DeployedComponentsListingViewImpl> { }

    @UiField VerticalLayoutContainer container;
    @UiField Grid<Tool> grid;
    @UiField TextButton newToolBtn;
    @UiField SearchField<Tool> searchField;
    @UiField(provided = true) ListStore<Tool> store;

    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> loader;
    ToolSearchRPCProxy searchProxy;

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @Inject Provider<NewToolRequestDialog> newToolRequestDialogProvider;

    @Inject
    DeployedComponentsListingViewImpl(@Assisted ListStore<Tool> listStore,
                                      @Assisted SelectionChangedHandler<Tool> handler) {
        this.store = listStore;
        searchProxy = new ToolSearchRPCProxy();
        loader = buildLoader();
        initWidget(uiBinder.createAndBindUi(this));
        searchField.setEmptyText(I18N.DISPLAY.searchEmptyText());
        grid.setLoader(loader);
        grid.getSelectionModel().addSelectionChangedHandler(handler);
        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, Tool, PagingLoadResult<Tool>>(store));
        loader.load(new FilterPagingLoadConfigBean());
    }

    @Override
    public Tool getSelectedDC() {
        return grid.getSelectionModel().getSelectedItem();
    }

    @Override
    public void loadDC(List<Tool> list) {
        store.clear();
        store.addAll(list);
    }

    @Override
    public void mask() {
        container.mask(I18N.DISPLAY.loadingMask());

    }

    @Override
    public void setPresenter(DeployedComponentsListingView.Presenter presenter) {

    }

    @Override
    public void showInfo(Tool dc) {
        DCDetailsRenderer templates = GWT.create(DCDetailsRenderer.class);
        HtmlLayoutContainer c = new HtmlLayoutContainer(templates.render());
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        c.add(new Label(I18N.DISPLAY.attribution() + ": "), new HtmlData(".cell1"));
        c.add(new Label(dc.getAttribution()), new HtmlData(".cell3"));
        c.add(new Label(I18N.DISPLAY.description() + ": "), new HtmlData(".cell5"));
        c.add(new Label(dc.getDescription()), new HtmlData(".cell7"));
        Dialog d = buildDetailsDialog(dc.getName());
        vlc.add(c, new VerticalLayoutData(1, 1));
        vlc.setScrollMode(ScrollMode.AUTO);
        d.setWidget(vlc);
        d.show();
    }

    @Override
    public void unmask() {
        container.unmask();

    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        searchField.ensureDebugId(baseID + AppIntegrationModule.Ids.SEARCH);
        newToolBtn.ensureDebugId(baseID + AppIntegrationModule.Ids.NEW_TOOL_REQUEST);
    }

    @UiFactory
    SearchField<Tool> createAppSearchField() {
        return new SearchField<>(loader);
    }

    @UiFactory
    ColumnModel<Tool> createColumnModel() {
        DeployedComponentProperties properties = GWT.create(DeployedComponentProperties.class);
        IdentityValueProvider<Tool> provider = new IdentityValueProvider<>("name");
        List<ColumnConfig<Tool, ?>> configs = new LinkedList<>();

        ColumnConfig<Tool, Tool> name = new ColumnConfig<>(provider, 100);
        name.setComparator(new Comparator<Tool>() {

            @Override
            public int compare(Tool o1, Tool o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        name.setSortable(true);
        name.setHeader(I18N.DISPLAY.name());
        configs.add(name);
        name.setCell(new DCNameHyperlinkCell(this));
        name.setMenuDisabled(true);

        ColumnConfig<Tool, String> version = new ColumnConfig<>(properties.version(), 100);
        version.setHeader(I18N.DISPLAY.version());
        configs.add(version);
        version.setMenuDisabled(true);

        ColumnConfig<Tool, String> path = new ColumnConfig<>(properties.location(), 100);
        path.setHeader(I18N.DISPLAY.path());
        configs.add(path);
        path.setMenuDisabled(true);
        return new ColumnModel<>(configs);
    }

    @UiHandler({"newToolBtn"})
    void onNewToolRequestBtnClick(@SuppressWarnings("unused") SelectEvent event) {
       newToolRequestDialogProvider.get().show();
    }

    private Dialog buildDetailsDialog(String heading) {
        Dialog d = new Dialog();
        d.getButtonBar().clear();
        d.setModal(true);
        d.setSize("500px", "300px");
        d.setHeadingText(heading);
        return d;
    }

    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> buildLoader() {
        final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> loader = new PagingLoader<>(
                                                                                                                                                                                  searchProxy);
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        return loader;
    }

}
