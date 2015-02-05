package org.iplantc.de.tags.client.views;

import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.events.RequestCreateTag;
import org.iplantc.de.tags.client.events.selection.EditTagSelected;
import org.iplantc.de.tags.client.events.selection.RemoveTagSelected;
import org.iplantc.de.tags.client.events.selection.TagSelected;
import org.iplantc.de.tags.client.gin.factory.TagItemFactory;
import org.iplantc.de.tags.client.resources.CustomIplantTagResources;

import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import java.util.Map;
import java.util.logging.Logger;

/**
 * @author jstroot
 */
public class TagsViewImpl extends Composite implements TagsView,
                                                       StoreAddEvent.StoreAddHandler<Tag>,
                                                       StoreRemoveEvent.StoreRemoveHandler<Tag>,
                                                       StoreClearEvent.StoreClearHandler<Tag>,
                                                       ValueChangeHandler<Tag>,
                                                       EditTagSelected.EditTagSelectedHandler,
                                                       RemoveTagSelected.RemoveTagSelectedHandler,
                                                       SelectionHandler<Tag> {


    interface TagsViewUiBinder extends UiBinder<Widget, TagsViewImpl> { }

    private static TagsViewUiBinder uiBinder = GWT.create(TagsViewUiBinder.class);

    @UiField VerticalLayoutContainer tagListPanel;
    @UiField(provided = true) TagSearchField tagSearchField;
    @UiField TagsPanel tagsPanel;
    // FIXME This field needs to go away
    @UiField FieldLabel taglbl;

    private final TagItemFactory tagItemFactory;
    private final CustomIplantTagResources resources;
    private final Presenter presenter;

    private final Map<Tag, TagItem> tagItemMap = Maps.newHashMap();


    Logger logger = Logger.getLogger("list view logger");

    @Inject
    TagsViewImpl(final TagSearchField tagSearchField,
                 final TagItemFactory tagItemFactory,
                 final CustomIplantTagResources resources,
                 @Assisted final ListStore<Tag> listStore,
                 @Assisted final TagsView.Presenter presenter) {
        this.tagSearchField = tagSearchField;
        this.tagItemFactory = tagItemFactory;
        this.resources = resources;
        this.presenter = presenter;
        listStore.addStoreAddHandler(this);
        listStore.addStoreRemoveHandler(this);
        listStore.addStoreClearHandler(this);

        initWidget(uiBinder.createAndBindUi(this));

        taglbl.setHTML("<span style='font-size:10px; font-weight:bold;'>" + I18N.DISPLAY.tags()
                + "</span>");
        tagsPanel.setStyleName(resources.style().tagPanel());
        tagSearchField.addValueChangeHandler(this);
    }

    @Override
    public HandlerRegistration addEditTagSelectedHandler(EditTagSelected.EditTagSelectedHandler handler) {
        return addHandler(handler, EditTagSelected.TYPE);
    }

    @Override
    public HandlerRegistration addRemoveTagSelectedHandler(RemoveTagSelected.RemoveTagSelectedHandler handler) {
        return addHandler(handler, RemoveTagSelected.TYPE);
    }

    @Override
    public HandlerRegistration addRequestCreateTagHandler(RequestCreateTag.RequestCreateTagHandler handler) {
        return tagSearchField.addRequestCreateTagHandler(handler);
    }

    @Override
    public HandlerRegistration addTagSelectedHandler(TagSelected.TagSelectedHandler handler) {
        return addHandler(handler, TagSelected.TYPE);
    }

    @Override
    public void onAdd(StoreAddEvent<Tag> event) {
        // add stuff to panel
        for(Tag tag : event.getItems()){
            final TagItem tagItem = tagItemFactory.createTagItem(tag);
            // Wire up tag item events here
            tagItem.addEditTagSelectedHandler(this);
            tagItem.addRemoveTagSelectedHandler(this);
            tagItem.addSelectionHandler(this);
            tagsPanel.add(tagItem);
            tagItemMap.put(tag, tagItem);
        }
        tagSearchField.clear();
    }

    @Override
    public void onClear(StoreClearEvent<Tag> event) {
        // Clear panel
        tagsPanel.clear();
    }

    @Override
    public void onEditTagSelected(EditTagSelected event) {
        // refire
        fireEvent(event);
    }

    @Override
    public void onRemove(StoreRemoveEvent<Tag> event) {
        // remove stuff from panel
        final Tag tag = event.getItem();
        final TagItem tagItem = tagItemMap.remove(tag);
        tagsPanel.remove(tagItem);
    }

    @Override
    public void onRemoveTagSelected(RemoveTagSelected event) {
        // refire
        fireEvent(event);
    }

    /**
     * Handles selection events from individual {@link org.iplantc.de.tags.client.TagsView.TagItem}s
     * and refires a {@link org.iplantc.de.tags.client.events.selection.TagSelected} event.
     * @param event
     */
    @Override
    public void onSelection(SelectionEvent<Tag> event) {
        fireEvent(new TagSelected(event.getSelectedItem()));
    }

    @Override
    public void onValueChange(ValueChangeEvent<Tag> event) {
        // Search field found tag results, and one was selected
        presenter.addTag(event.getValue());
    }

    public void setEditable(boolean editable) {
        this.tagListPanel.getElement().setPropertyBoolean("disabled", !editable);

        if (editable) {
            this.tagListPanel.addStyleName(resources.style().tagListEditable());
        } else {
            this.tagListPanel.removeStyleName(resources.style().tagListEditable());
        }

    }

}
