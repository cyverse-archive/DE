/**
 *
 */
package org.iplantc.de.commons.client.collaborators.util;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.collaborators.events.UserSearchResultSelected;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * @author sriram
 *
 */
public class UserSearchField implements IsWidget {

    private final UserSearchRPCProxy searchProxy;

    private ComboBox<Collaborator> combo;

    private final UserSearchResultSelected.USER_SEARCH_EVENT_TAG tag;

    interface UserTemplate extends XTemplates {
        @XTemplate(source = "UserSearchResult.html")
        SafeHtml render(Collaborator c);
    }

    public interface UsersLoadConfig extends PagingLoadConfig {
        String getQuery();

        void setQuery(String query);
    }

    interface UserSearchAutoBeanFactory extends AutoBeanFactory {
        static UserSearchAutoBeanFactory instance = GWT.create(UserSearchAutoBeanFactory.class);

        AutoBean<UsersLoadConfig> loadConfig();

    }

    public UserSearchField(UserSearchResultSelected.USER_SEARCH_EVENT_TAG tag) {
        this.tag = tag;
        this.searchProxy = new UserSearchRPCProxy();
        PagingLoader<UsersLoadConfig, PagingLoadResult<Collaborator>> loader = buildLoader();

        ListStore<Collaborator> store = buildStore();
        loader.addLoadHandler(new LoadResultListStoreBinding<UsersLoadConfig, Collaborator, PagingLoadResult<Collaborator>>(
                store));

        final UserTemplate template = GWT.create(UserTemplate.class);

        ListView<Collaborator, Collaborator> view = buildView(store, template);

        ComboBoxCell<Collaborator> cell = buildComboCell(store, view);
        initCombo(loader, cell);
    }

    private void initCombo(PagingLoader<UsersLoadConfig, PagingLoadResult<Collaborator>> loader,
            ComboBoxCell<Collaborator> cell) {
        combo = new ComboBox<Collaborator>(cell);
        combo.setLoader(loader);
        combo.setMinChars(3);
        combo.setWidth(250);
        combo.setHideTrigger(true);
        combo.setEmptyText(I18N.DISPLAY.searchCollab());
        combo.addSelectionHandler(new SelectionHandler<Collaborator>() {

            @Override
            public void onSelection(SelectionEvent<Collaborator> event) {
                EventBus bus = EventBus.getInstance();
                UserSearchResultSelected usrs = new UserSearchResultSelected(UserSearchField.this.tag
                        .toString(), combo
                                .getListView().getSelectionModel().getSelectedItem());
                bus.fireEvent(usrs);

            }
        });
    }

    private ComboBoxCell<Collaborator> buildComboCell(ListStore<Collaborator> store,
            ListView<Collaborator, Collaborator> view) {
        ComboBoxCell<Collaborator> cell = new ComboBoxCell<Collaborator>(store,
                new StringLabelProvider<Collaborator>() {

                    @Override
                    public String getLabel(Collaborator c) {
                        return c.getFirstName() + " " + c.getLastName();
                    }

                }, view) {
            @Override
            protected void onEnterKeyDown(Context context, Element parent, Collaborator value,
                    NativeEvent event, ValueUpdater<Collaborator> valueUpdater) {
                if (isExpanded()) {
                    super.onEnterKeyDown(context, parent, value, event, valueUpdater);
                }
            }

        };

        return cell;
    }

    private ListView<Collaborator, Collaborator> buildView(ListStore<Collaborator> store,
            final UserTemplate template) {
        ListView<Collaborator, Collaborator> view = new ListView<Collaborator, Collaborator>(store,
                new IdentityValueProvider<Collaborator>());

        view.setCell(new AbstractCell<Collaborator>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, Collaborator value,
                    SafeHtmlBuilder sb) {
                sb.append(template.render(value));
            }

        });
        return view;
    }

    private PagingLoader<UsersLoadConfig, PagingLoadResult<Collaborator>> buildLoader() {
        PagingLoader<UsersLoadConfig, PagingLoadResult<Collaborator>> loader = new PagingLoader<UsersLoadConfig, PagingLoadResult<Collaborator>>(
                searchProxy);
        UsersLoadConfig loadConfig = UserSearchAutoBeanFactory.instance.loadConfig().as();
        loader.useLoadConfig(loadConfig);
        loader.addBeforeLoadHandler(new BeforeLoadHandler<UsersLoadConfig>() {

            @Override
            public void onBeforeLoad(BeforeLoadEvent<UsersLoadConfig> event) {
                String query = combo.getText();
                if (query != null && !query.equals("")) {
                    event.getLoadConfig().setQuery(query);
                }

            }
        });
        return loader;
    }

    private ListStore<Collaborator> buildStore() {
        ListStore<Collaborator> store = new ListStore<Collaborator>(
                new ModelKeyProvider<Collaborator>() {

                    @Override
                    public String getKey(Collaborator item) {
                        return item.getId();
                    }

                });
        return store;
    }

    @Override
    public Widget asWidget() {
        return combo;
    }

}
