package org.iplantc.de.commons.client.tags;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import com.virilis_software.gwt.taglist.client.comp.TagListHandlers;
import com.virilis_software.gwt.taglist.client.comp.taglist.TagsPanel;
import com.virilis_software.gwt.taglist.client.resource.Resources;
import com.virilis_software.gwt.taglist.client.tag.StringTag;

public class IplantTagListView extends Composite {

    private static IplantTagListViewUiBinder uiBinder = GWT.create(IplantTagListViewUiBinder.class);

    interface IplantTagListViewUiBinder extends UiBinder<Widget, IplantTagListView> {
    }

    private TagListHandlers uiHandlers;
    
    @UiField
    VerticalLayoutContainer tagListPanel;
    
    @UiField
    ComboBox<StringTag> tagSearchCbo;
    
    @UiField
    TagsPanel tagsPanel;
    
    private ListStore<StringTag> store;

    public IplantTagListView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setUiHandlers(TagListHandlers tagListHandlers) {
        this.uiHandlers = tagListHandlers;
    }

    @UiFactory
    ComboBox<StringTag> buildTagSearchCbo() {
        store = new ListStore<StringTag>(new ModelKeyProvider<StringTag>() {
            @Override
            public String getKey(StringTag item) {
                return item.getValue();
            }});

        return new ComboBox<StringTag>(store, new LabelProvider<StringTag>() {

            @Override
            public String getLabel(StringTag item) {
                return item.getValue();
            }
        });

    }

    /**
     * @return the tagsPanel
     */
    public TagsPanel getTagsPanel() {
        return tagsPanel;
    }

    public IplantTagListView(Resources resources) {
        initWidget(uiBinder.createAndBindUi(this));

    }

    public void setEditable(boolean editable) {
        tagSearchCbo.setVisible(editable);
        tagSearchCbo.setEnabled(editable);
        this.tagListPanel.getElement().setPropertyBoolean("disabled", !editable);

        if (editable)
            this.tagListPanel.addStyleName(Resources.INSTANCE.style().tagListEditable());
        else
            this.tagListPanel.removeStyleName(Resources.INSTANCE.style().tagListEditable());

    }

}
