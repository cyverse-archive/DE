package org.iplantc.de.apps.integration.client.view.tools;

import org.iplantc.de.apps.integration.client.view.deployedComponents.proxy.ToolSearchRPCProxy;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterConfigBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import java.util.List;

public class ToolSearchField extends Composite implements HasSelectionHandlers<Tool>, HasValueChangeHandlers<Tool> {

    interface DCTemplate extends XTemplates {
        @XTemplate(source = "DCSearchResult.html")
        SafeHtml render(Tool c);
    }

    ComboBox<Tool> combo;
    
    private final ToolSearchRPCProxy searchProxy;

    public ToolSearchField() {
        searchProxy = new ToolSearchRPCProxy();
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> loader = buildLoader();

        ListStore<Tool> store = buildStore();

        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, Tool, PagingLoadResult<Tool>>(
                store));

        final DCTemplate template = GWT.create(DCTemplate.class);

        ListView<Tool, Tool> view = buildView(store, template);

        ComboBoxCell<Tool> cell = buildComboCell(store, view);
        initCombo(loader, cell);
        initWidget(combo);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Tool> handler) {
        return combo.addSelectionHandler(handler);
    }
    
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Tool> handler) {
        return combo.addValueChangeHandler(handler);
    }
    
    public void clear() {
        combo.clear();
    }

    public Tool getValue() {
        return combo.getValue();
    }
    
    public void setValue(Tool value) {
        combo.setValue(value);
    }

    private ComboBoxCell<Tool> buildComboCell(ListStore<Tool> store,
            ListView<Tool, Tool> view) {
        ComboBoxCell<Tool> cell = new ComboBoxCell<Tool>(store,
                new StringLabelProvider<Tool>() {

                    @Override
                    public String getLabel(Tool c) {
                        return c.getName() +  " " + c.getVersion();
                    }

                }, view) {
            @Override
            protected void onEnterKeyDown(Context context, Element parent, Tool value,
                    NativeEvent event, ValueUpdater<Tool> valueUpdater) {
                if (isExpanded()) {
                    super.onEnterKeyDown(context, parent, value, event, valueUpdater);
                }
            }

        };

        return cell;
    }


    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> buildLoader() {
        final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>>(
                searchProxy);
        loader.useLoadConfig(new FilterPagingLoadConfigBean());
        loader.addBeforeLoadHandler(new BeforeLoadHandler<FilterPagingLoadConfig>() {

            @Override
            public void onBeforeLoad(BeforeLoadEvent<FilterPagingLoadConfig> event) {
                String query = combo.getText();
                if (query != null && !query.equals("")) {
                    FilterPagingLoadConfig config = loader.getLastLoadConfig();
                    if(config == null) {
                        config = new FilterPagingLoadConfigBean();
                    }
                    List<FilterConfig> filters = config.getFilters();
                    if (filters.size() == 0) {
                        FilterConfigBean filter = new FilterConfigBean();
                        filter.setValue(query);
                        filters.add(filter);
                    } 
                    
                    filters.get(0).setValue(query);
                    
                    
                }

            }
        });
        return loader;
    }

    private ListStore<Tool> buildStore() {
        ListStore<Tool> store = new ListStore<Tool>(
                new ModelKeyProvider<Tool>() {

                    @Override
                    public String getKey(Tool item) {
                        return item.getId();
                    }

                });
        return store;
    }

    private ListView<Tool, Tool> buildView(ListStore<Tool> store,
            final DCTemplate template) {
        ListView<Tool, Tool> view = new ListView<Tool, Tool>(store,
                new IdentityValueProvider<Tool>());

        view.setCell(new AbstractCell<Tool>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Tool value,
                    SafeHtmlBuilder sb) {
                sb.append(template.render(value));
            }

        });
        return view;
    }

    private void initCombo(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Tool>> loader, ComboBoxCell<Tool> cell) {
        combo = new ComboBox<Tool>(cell);
        combo.setLoader(loader);
        combo.setMinChars(3);
        combo.setWidth(250);
        combo.setHideTrigger(true);
        combo.setEmptyText(I18N.DISPLAY.searchEmptyText());
        combo.addSelectionHandler(new SelectionHandler<Tool>() {

            @Override
            public void onSelection(SelectionEvent<Tool> event) {
                GWT.log("Selected " + event.getSelectedItem().getName());
            }
        });

    }

}
