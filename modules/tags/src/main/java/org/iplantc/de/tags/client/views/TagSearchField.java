package org.iplantc.de.tags.client.views;

import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.proxy.TagSuggestionLoadConfig;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A widget to search for tags. The widget will load suggestions based on user query. Caller must supply
 * Selection Handler and valuechange handler to determine the widget's behavior
 * 
 * @author sriram
 * 
 */
public class TagSearchField implements IsWidget {

    interface TagTemplate extends XTemplates {
        @XTemplate("<div style='font-size:.75em;color:#DB6619;font-weight:bold;'>{tag.value}</div>")
        SafeHtml render(IplantTag tag);
    }

    private ComboBox<IplantTag> tagSearchCbo;
    private ListStore<IplantTag> store;

    Logger logger = Logger.getLogger("list view logger");

    private final TagsView.TagSuggestionProxy proxy;
    private Command createTagCommand;

    @Inject
    public TagSearchField(final TagsView.TagSuggestionProxy proxy) {
        this.proxy = proxy;
        initStore();

        ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> loader = initLoader();
        final TagTemplate template = GWT.create(TagTemplate.class);
        ListView<IplantTag, IplantTag> view = initView(template);
        ComboBoxCell<IplantTag> cell = initCell(view);
        initCombo(cell, loader);
    }

    public void addSelectionHandler(SelectionHandler<IplantTag> handler) {
        tagSearchCbo.addSelectionHandler(handler);
    }

    public void addValueChangeHandler(ValueChangeHandler<IplantTag> handler) {
        tagSearchCbo.addValueChangeHandler(handler);
    }

    public void setCreateTagCommand(Command cmd) {
        this.createTagCommand = cmd;
    }

    @Override
    public Widget asWidget() {
        return tagSearchCbo;
    }

    private void initCombo(ComboBoxCell<IplantTag> cell,
                           ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> loader) {
        tagSearchCbo = new ComboBox<>(cell);
        tagSearchCbo.setHideTrigger(true);
        tagSearchCbo.setEditable(true);
        tagSearchCbo.setLoader(loader);
        tagSearchCbo.setMinChars(3);
        tagSearchCbo.setEmptyText(I18N.DISPLAY.search());
    }

    private void initStore() {
        store = new ListStore<>(new ModelKeyProvider<IplantTag>() {
            @Override
            public String getKey(IplantTag item) {
                return item.getId();
            }
        });
    }

    private ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> initLoader() {
        ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> loader = new ListLoader<>(proxy);
        loader.useLoadConfig(new TagSuggestionLoadConfig());
        loader.addBeforeLoadHandler(new BeforeLoadHandler<TagSuggestionLoadConfig>() {
            @Override
            public void onBeforeLoad(BeforeLoadEvent<TagSuggestionLoadConfig> event) {
                String query = tagSearchCbo.getText();
                if (query != null) {
                    event.getLoadConfig().setQuery(query);
                }
            }
        });
        loader.addLoadHandler(new LoadResultListStoreBinding<TagSuggestionLoadConfig, IplantTag, ListLoadResult<IplantTag>>(store));
        return loader;
    }

    private ComboBoxCell<IplantTag> initCell(ListView<IplantTag, IplantTag> view) {
        ComboBoxCell<IplantTag> cell = new ComboBoxCell<IplantTag>(store,
                                                                   new StringLabelProvider<IplantTag>() {

                                                                       @Override
                                                                       public String
                                                                               getLabel(IplantTag item) {
                                                                           return item.getValue();
                                                                       }
                                                                   }, view) {

            @Override
            protected void onEnterKeyDown(Context context,
                                          Element parent,
                                          IplantTag value,
                                          NativeEvent event,
                                          ValueUpdater<IplantTag> valueUpdater) {
                IplantTag tag = tagSearchCbo.getCurrentValue();
                logger.log(Level.SEVERE, "from enter key -->" + tagSearchCbo.getText() + "<--"
                        + "value before=>" + tagSearchCbo.getValue());
                TagSearchField.this.setValue(tag);
            }
        };
        return cell;
    }

    private ListView<IplantTag, IplantTag> initView(final TagTemplate template) {
        ListView<IplantTag, IplantTag> view = new ListView<>(store,
                                                                                 new IdentityValueProvider<IplantTag>());

        view.setCell(new AbstractCell<IplantTag>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context,
                               IplantTag value,
                               SafeHtmlBuilder sb) {
                sb.append(template.render(value));
            }

        });
        return view;
    }

    public void setValue(IplantTag tag) {
        if (tag == null) {
            String text = tagSearchCbo.getText();
            if (!Strings.isNullOrEmpty(text)) {
                if (createTagCommand != null) {
                    createTagCommand.execute();
                }
            }
        } else {
            tagSearchCbo.setValue(tag, true);
        }
    }

    public void clear() {
        tagSearchCbo.clear();
    }

    public String getText() {
        return tagSearchCbo.getText();
    }

    public IplantTag getValue() {
        return tagSearchCbo.getValue();
    }

}
