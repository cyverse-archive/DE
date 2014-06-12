package org.iplantc.de.commons.client.tags.views;

import org.iplantc.de.commons.client.tags.models.IpalntTagAutoBeanFactory;
import org.iplantc.de.commons.client.tags.models.IplantTag;
import org.iplantc.de.commons.client.tags.presenter.TagListHandlers;
import org.iplantc.de.commons.client.tags.proxy.TagSuggestionLoadConfig;
import org.iplantc.de.commons.client.tags.proxy.TagSuggestionRpcProxy;
import org.iplantc.de.commons.client.tags.resources.CustomIplantTagResources;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IplantTagListView extends Composite implements IsWidget {

    private static IplantTagListViewUiBinder uiBinder = GWT.create(IplantTagListViewUiBinder.class);

    interface IplantTagListViewUiBinder extends UiBinder<Widget, IplantTagListView> {
    }

    interface TagTemplate extends XTemplates {
        @XTemplate("<div style='font-size:.75em;color:#DB6619;font-weight:bold;'>{tag.value}</div>")
        SafeHtml render(IplantTag tag);
    }

    private TagListHandlers uiHandlers;

    @UiField
    VerticalLayoutContainer tagListPanel;

    @UiField
    ComboBox<IplantTag> tagSearchCbo;

    @UiField
    TagsPanel tagsPanel;

    private ListStore<IplantTag> store;

    private final TagSuggestionRpcProxy proxy;
    Logger logger = Logger.getLogger("list view logger");

    IpalntTagAutoBeanFactory factory = GWT.create(IpalntTagAutoBeanFactory.class);

    @Inject
    public IplantTagListView(TagSuggestionRpcProxy proxy) {
        this.proxy = proxy;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setUiHandlers(TagListHandlers tagListHandlers) {
        this.uiHandlers = tagListHandlers;
    }

    @UiFactory
    ComboBox<IplantTag> buildTagSearchCbo() {
        initStore();

        final TagTemplate template = GWT.create(TagTemplate.class);
        ListView<IplantTag, IplantTag> view = initView(template);

        ComboBoxCell<IplantTag> cell = initCell(view);

        final ComboBox<IplantTag> combo = initCombo(cell);

        ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> loader = initLoader(combo);
        combo.setLoader(loader);

        combo.addSelectionHandler(new SelectionHandler<IplantTag>() {

            @Override
            public void onSelection(SelectionEvent<IplantTag> event) {
                combo.setValue(event.getSelectedItem(), true);
            }
        });

        combo.addValueChangeHandler(new ValueChangeHandler<IplantTag>() {

            @Override
            public void onValueChange(ValueChangeEvent<IplantTag> event) {
                IplantTag tag = event.getValue();
                if (tag == null) {
                    String text = combo.getText();
                    AutoBean<IplantTag> tagBean = AutoBeanCodex.decode(factory, IplantTag.class, "{}");
                    tag = tagBean.as();
                    tag.setValue(text);
                    uiHandlers.onCreateTag(tag);
                } else {
                    uiHandlers.onAddTag(tag);
                }

            }
        });

        return combo;
    }

    private ComboBox<IplantTag> initCombo(ComboBoxCell<IplantTag> cell) {
        final ComboBox<IplantTag> combo = new ComboBox<IplantTag>(cell);
        combo.setHideTrigger(true);
        combo.setFinishEditOnEnter(true);
        combo.setEditable(true);
        combo.setForceSelection(false);
        combo.setMinChars(3);
        combo.setTypeAhead(true);
        return combo;
    }

    private ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> initLoader(final ComboBox<IplantTag> combo) {
        ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> loader = new ListLoader<TagSuggestionLoadConfig, ListLoadResult<IplantTag>>(proxy);
        loader.useLoadConfig(new TagSuggestionLoadConfig());
        loader.addBeforeLoadHandler(new BeforeLoadHandler<TagSuggestionLoadConfig>() {
            @Override
            public void onBeforeLoad(BeforeLoadEvent<TagSuggestionLoadConfig> event) {
                String query = combo.getText();
                if (query != null) {
                    event.getLoadConfig().setQuery(query);
                }
            }
        });
        loader.addLoadHandler(new LoadResultListStoreBinding<TagSuggestionLoadConfig, IplantTag, ListLoadResult<IplantTag>>(store));
        return loader;
    }

    private ComboBoxCell<IplantTag> initCell(ListView<IplantTag, IplantTag> view) {
        ComboBoxCell<IplantTag> cell = new ComboBoxCell<IplantTag>(store, new LabelProvider<IplantTag>() {

            @Override
            public String getLabel(IplantTag item) {
                return item.getValue();
            }
        }, view) {

            @Override
            protected void onEnterKeyDown(Context context, Element parent, IplantTag value, NativeEvent event, ValueUpdater<IplantTag> valueUpdater) {
                IplantTag tag = tagSearchCbo.getCurrentValue();
                if (tag == null) {
                    String text = tagSearchCbo.getText();
                    logger.log(Level.SEVERE, "-->" + text + "<--");
                    if (!Strings.isNullOrEmpty(text)) {
                        AutoBean<IplantTag> tagBean = AutoBeanCodex.decode(factory, IplantTag.class, "{}");
                        tag = tagBean.as();
                        tag.setValue(text);
                        uiHandlers.onCreateTag(tag);
                    }
                } else {
                    uiHandlers.onAddTag(tag);
                }
                super.onEnterKeyDown(context, parent, value, event, valueUpdater);
            }
        };
        return cell;
    }

    private ListView<IplantTag, IplantTag> initView(final TagTemplate template) {
        ListView<IplantTag, IplantTag> view = new ListView<IplantTag, IplantTag>(store, new IdentityValueProvider<IplantTag>());

        view.setCell(new AbstractCell<IplantTag>() {

            @Override
            public void render(com.google.gwt.cell.client.Cell.Context context, IplantTag value, SafeHtmlBuilder sb) {
                sb.append(template.render(value));
            }

        });
        return view;
    }

    private void initStore() {
        store = new ListStore<IplantTag>(new ModelKeyProvider<IplantTag>() {
            @Override
            public String getKey(IplantTag item) {
                return item.getId();
            }
        });
    }

    /**
     * @return the tagsPanel
     */
    public TagsPanel getTagsPanel() {
        return tagsPanel;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public void setEditable(boolean editable) {
        tagSearchCbo.setVisible(editable);
        tagSearchCbo.setEnabled(editable);
        this.tagListPanel.getElement().setPropertyBoolean("disabled", !editable);

        if (editable)
            this.tagListPanel.addStyleName(CustomIplantTagResources.INSTANCE.style().tagListEditable());
        else
            this.tagListPanel.removeStyleName(CustomIplantTagResources.INSTANCE.style().tagListEditable());

    }

}
