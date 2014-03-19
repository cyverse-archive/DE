package org.iplantc.de.apps.widgets.client.view.deployedComponents;

import org.iplantc.de.apps.widgets.client.view.deployedComponents.proxy.DCSearchRPCProxy;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
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
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

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
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import java.util.List;

public class DCSearchField implements IsWidget, HasSelectionHandlers<DeployedComponent>, HasValueChangeHandlers<DeployedComponent> {

    interface DCTemplate extends XTemplates {
        @XTemplate(source = "DCSearchResult.html")
        SafeHtml render(DeployedComponent c);
    }

    ComboBox<DeployedComponent> combo;
    
    private final DCSearchRPCProxy searchProxy;
    
    public DCSearchField() {
        searchProxy = new DCSearchRPCProxy();
        PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> loader = buildLoader();

        ListStore<DeployedComponent> store = buildStore();
        
        loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, DeployedComponent, PagingLoadResult<DeployedComponent>>(
                store));

        final DCTemplate template = GWT.create(DCTemplate.class);

        ListView<DeployedComponent, DeployedComponent> view = buildView(store, template);

        ComboBoxCell<DeployedComponent> cell = buildComboCell(store, view);
        initCombo(loader, cell);
        
    }
    
    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<DeployedComponent> handler) {
        return combo.addSelectionHandler(handler);
    }
    
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DeployedComponent> handler) {
        return combo.addValueChangeHandler(handler);
    }
    
    @Override
    public Widget asWidget() {
       return combo;
    }

    public void clear() {
        combo.clear();
    }
    
    @Override
    public void fireEvent(GwtEvent<?> event) {
        throw new UnsupportedOperationException("DCSearchField.fireEvent not supported.");
    }
    

    public DeployedComponent getValue() {
        return combo.getValue();
    }


    public void setValue(DeployedComponent value) {
        combo.setValue(value);
    }


    private ComboBoxCell<DeployedComponent> buildComboCell(ListStore<DeployedComponent> store,
            ListView<DeployedComponent, DeployedComponent> view) {
        ComboBoxCell<DeployedComponent> cell = new ComboBoxCell<DeployedComponent>(store,
                new StringLabelProvider<DeployedComponent>() {

                    @Override
                    public String getLabel(DeployedComponent c) {
                        return c.getName() +  " " + c.getVersion();
                    }

                }, view) {
            @Override
            protected void onEnterKeyDown(Context context, Element parent, DeployedComponent value,
                    NativeEvent event, ValueUpdater<DeployedComponent> valueUpdater) {
                if (isExpanded()) {
                    super.onEnterKeyDown(context, parent, value, event, valueUpdater);
                }
            }

        };

        return cell;
    }


    private PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> buildLoader() {
        final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>>(
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

    private ListStore<DeployedComponent> buildStore() {
        ListStore<DeployedComponent> store = new ListStore<DeployedComponent>(
                new ModelKeyProvider<DeployedComponent>() {

                    @Override
                    public String getKey(DeployedComponent item) {
                        return item.getId();
                    }

                });
        return store;
    }

    private ListView<DeployedComponent, DeployedComponent> buildView(ListStore<DeployedComponent> store,
            final DCTemplate template) {
        ListView<DeployedComponent, DeployedComponent> view = new ListView<DeployedComponent, DeployedComponent>(store,
                new IdentityValueProvider<DeployedComponent>());

        view.setCell(new AbstractCell<DeployedComponent>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, DeployedComponent value,
                    SafeHtmlBuilder sb) {
                sb.append(template.render(value));
            }

        });
        return view;
    }

    private void initCombo(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<DeployedComponent>> loader, ComboBoxCell<DeployedComponent> cell) {
        combo = new ComboBox<DeployedComponent>(cell);
        combo.setLoader(loader);
        combo.setMinChars(3);
        combo.setWidth(250);
        combo.setHideTrigger(true);
        combo.setEmptyText(I18N.DISPLAY.searchEmptyText());
        combo.addSelectionHandler(new SelectionHandler<DeployedComponent>() {

            @Override
            public void onSelection(SelectionEvent<DeployedComponent> event) {
                GWT.log("Selected " + event.getSelectedItem().getName());
            }
        });

    }

}
