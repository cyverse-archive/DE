package org.iplantc.de.tags.client.views;

import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.events.RequestCreateTag;
import org.iplantc.de.tags.client.proxy.TagSuggestionLoadConfig;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
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
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import java.util.logging.Logger;

/**
 * A widget to search for tags. The widget will load suggestions based on user query. Caller must supply
 * Selection Handler and valuechange handler to determine the widget's behavior
 *
 * @author sriram
 */
public class TagSearchFieldImpl extends Composite implements TagsView.TagSearchField,
                                                             SelectionHandler<Tag> {

    interface TagTemplate extends XTemplates {
        @XTemplate("<div style='font-size:.75em;color:#DB6619;font-weight:bold;'>{tag.value}</div>")
        SafeHtml render(Tag tag);
    }
    Logger logger = Logger.getLogger(TagSearchFieldImpl.class.getName());
    private final TagsView.TagSuggestionProxy proxy;
    private ListStore<Tag> store;
    private ComboBox<Tag> tagSearchCbo;

    @Inject
    TagSearchFieldImpl(final TagsView.TagSuggestionProxy proxy) {
        this.proxy = proxy;
        initStore();

        ListLoader<TagSuggestionLoadConfig, ListLoadResult<Tag>> loader = initLoader();
        final TagTemplate template = GWT.create(TagTemplate.class);
        ListView<Tag, Tag> view = initView(template);
        ComboBoxCell<Tag> cell = initCell(view);
        initCombo(cell, loader);
        initWidget(tagSearchCbo);
        tagSearchCbo.addSelectionHandler(this);
    }

    @Override
    public HandlerRegistration addRequestCreateTagHandler(RequestCreateTag.RequestCreateTagHandler handler) {
        return addHandler(handler, RequestCreateTag.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Tag> handler) {
        return tagSearchCbo.addValueChangeHandler(handler);
    }

    public void clear() {
        tagSearchCbo.clear();
    }

    public String getText() {
        return tagSearchCbo.getText();
    }

    public Tag getValue() {
        return tagSearchCbo.getValue();
    }

    @Override
    public void onSelection(SelectionEvent<Tag> event) {
        // When there is a selection in the combo, set value
        setValue(event.getSelectedItem());
    }

    public void setValue(Tag tag) {
        if (tag == null) {
            String text = tagSearchCbo.getText();
            if (!Strings.isNullOrEmpty(text)) {
                fireEvent(new RequestCreateTag(text));
            }
        } else {
            tagSearchCbo.setValue(tag, true);
        }
    }

    private ComboBoxCell<Tag> initCell(ListView<Tag, Tag> view) {
        ComboBoxCell<Tag> cell = new ComboBoxCell<Tag>(store,
                                                       new StringLabelProvider<Tag>() {

                                                           @Override
                                                           public String
                                                           getLabel(Tag item) {
                                                               return item.getValue();
                                                           }
                                                       }, view) {

            @Override
            protected void onEnterKeyDown(Context context,
                                          Element parent,
                                          Tag value,
                                          NativeEvent event,
                                          ValueUpdater<Tag> valueUpdater) {
                Tag tag = tagSearchCbo.getCurrentValue();
                logger.fine("from enter key -->" + tagSearchCbo.getText() + "<--"
                                + "value before=>" + tagSearchCbo.getValue());
                TagSearchFieldImpl.this.setValue(tag);
            }
        };
        return cell;
    }

    private void initCombo(ComboBoxCell<Tag> cell,
                           ListLoader<TagSuggestionLoadConfig, ListLoadResult<Tag>> loader) {
        tagSearchCbo = new ComboBox<>(cell);
        tagSearchCbo.setHideTrigger(true);
        tagSearchCbo.setEditable(true);
        tagSearchCbo.setLoader(loader);
        tagSearchCbo.setMinChars(3);
        tagSearchCbo.setEmptyText(I18N.DISPLAY.search());
    }

    private ListLoader<TagSuggestionLoadConfig, ListLoadResult<Tag>> initLoader() {
        ListLoader<TagSuggestionLoadConfig, ListLoadResult<Tag>> loader = new ListLoader<>(proxy);
        loader.useLoadConfig(new TagSuggestionLoadConfig());
        loader.addBeforeLoadHandler(new BeforeLoadHandler<TagSuggestionLoadConfig>() {
            @Override
            public void onBeforeLoad(BeforeLoadEvent<TagSuggestionLoadConfig> event) {
                String query = tagSearchCbo.getText();
                if (query != null) {
                    event.getLoadConfig().setQuery(query.trim());
                }
            }
        });
        loader.addLoadHandler(new LoadResultListStoreBinding<TagSuggestionLoadConfig, Tag, ListLoadResult<Tag>>(store));
        return loader;
    }

    private void initStore() {
        store = new ListStore<>(new ModelKeyProvider<Tag>() {
            @Override
            public String getKey(Tag item) {
                return item.getId();
            }
        });
    }

    private ListView<Tag, Tag> initView(final TagTemplate template) {
        ListView<Tag, Tag> view = new ListView<>(store,
                                                 new IdentityValueProvider<Tag>());

        view.setCell(new AbstractCell<Tag>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context,
                               Tag value,
                               SafeHtmlBuilder sb) {
                sb.append(template.render(value));
            }

        });
        return view;
    }

}
