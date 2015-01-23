package org.iplantc.de.tags.client.presenter;

import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.Taggable;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagsViewFactory;
import org.iplantc.de.tags.client.resources.CustomIplantTagResources;
import org.iplantc.de.tags.client.views.IplantTagListView;

import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

import java.util.Iterator;
import java.util.List;


public class IplantTagListPresenter implements TagsView.Presenter {

    public class TagItem {
        private final IplantTag tag;
        private final TagsView tagView;

        public TagItem(IplantTag tag, TagsView tagView) {
            this.tag = tag;
            this.tagView = tagView;
        }

        public IplantTag getTag() {
            return tag;
        }

        public TagsView getTagView() {
            return tagView;
        }
    }

    private final JsonUtil jsonUtil;
    private final TagsServiceFacade tagsService;
    private final CustomIplantTagResources resources;
    private final List<TagItem> tagItems = Lists.newArrayList();
    private final IplantTagListView tagListView;
    private final TagsViewFactory tagsViewFactory;
    private final Taggable taggable;
    private boolean editable;
    private Command onBlurCmd;
    private Command onFocusCmd;
    private Command onChangeCmd;
    private boolean removable;

    /**
     * Use {@link org.iplantc.de.tags.client.TagsView#setEditable(boolean)} to enable/disable tag creation on an existing TagList.
     */
    @Inject
    IplantTagListPresenter(final IplantTagListView tagListView,
                           final CustomIplantTagResources resources,
                           final TagsServiceFacade tagsService,
                           final JsonUtil jsonUtil,
                           final TagsViewFactory tagsViewFactory,
                           @Assisted final Taggable taggable) {
        this.tagsViewFactory = tagsViewFactory;
        this.taggable = taggable;
        this.resources = resources;
        this.tagsService = tagsService;
        this.jsonUtil = jsonUtil;
        this.tagListView = tagListView;

        this.resources.style().ensureInjected();
        this.getTagListView().setUiHandlers(this);
    }

    public boolean addTag(IplantTag tag) {
        for (TagItem tagItem : tagItems) {
            if (tagItem.getTag().getValue().equals(tag.getValue())) {
                return false;
            }
        }

        TagsView tagView = tagsViewFactory.createTagsView(tag);
        tagView.setUiHandlers(this);
        tagView.setEditable(editable);
        tagView.setRemovable(removable);

        getTagListView().getTagsPanel().add(tagView);
        tagItems.add(new TagItem(tag, tagView));
        taggable.attachTag(tag);

        return true;
    }

    /**
     * A method t build view containing tags for selected resource. No associated call backs and events
     * will be called.
     */
    public void buildTagCloudForSelectedResource(List<IplantTag> tags) {
        if (tags != null) {
            for (IplantTag tag : tags) {
                TagsView tagView = tagsViewFactory.createTagsView(tag);
                tagView.setUiHandlers(this);

                getTagListView().getTagsPanel().add(tagView);
                tagItems.add(new TagItem(tag, tagView));
            }
        }

        setEditable(editable);
        setRemovable(removable);
    }

    public void clear() {
        for (Iterator<TagItem> tagItemIt = tagItems.iterator(); tagItemIt.hasNext(); ) {
            TagItem tagItem = tagItemIt.next();
            getTagListView().getTagsPanel().remove(tagItem.getTagView());
            tagItemIt.remove();
        }
    }

    public IplantTagListView getTagListView() {
        return tagListView;
    }

    public List<IplantTag> getTags() {
        List<IplantTag> tags = Lists.newArrayList();
        for (TagItem tagItem : tagItems) {
            tags.add(tagItem.getTag());
        }
        return tags;
    }

    @Override
    public void onAddTag(IplantTag tag) {
        addTag(tag);

        if (onChangeCmd != null) {
            onChangeCmd.execute();
        }
    }

    @Override
    public void onBlur() {
        if (onBlurCmd != null)
            onBlurCmd.execute();
    }

    @Override
    public void onCreateTag(final IplantTag tag) {
        tagsService.createTag(tag, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.tagCreateError(), caught);

            }

            @Override
            public void onSuccess(String result) {
                JSONObject resultObj = jsonUtil.getObject(result);
                tag.setId(jsonUtil.getString(resultObj, "id"));
                // this is a side-effect ?!?
                onAddTag(tag);
            }
        });

    }

    @Override
    public void onEditTag(TagsView tagView) {
        for (TagItem tagItem : tagItems) {
            if (tagItem.getTagView().equals(tagView)) {
                final IplantTag tag = tagItem.tag;
                final String tagId = tag.getId();
                final TextArea tb = new TextArea();
                tb.setSize("250", "200");
                final String description = tag.getDescription();
                tb.setValue(description);
                Dialog pop = new Dialog();
                pop.setHideOnButtonClick(true);
                pop.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
                pop.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {

                    @Override
                    public void onSelect(SelectEvent event) {
                        if (tb.getCurrentValue() != null && !tb.getCurrentValue().equals(description)) {
                            tag.setDescription(tb.getCurrentValue());
                            updateTagDescription(tagId, tb.getCurrentValue());
                        }

                    }
                });
                pop.setSize("300", "250");
                pop.setHeadingText("Edit Tag Description for " + tagItem.getTag().getValue().toString());
                pop.setWidget(tb);
                pop.show();
            }
        }

    }

    @Override
    public void onFocus() {
        if (onFocusCmd != null)
            onFocusCmd.execute();
    }

    @Override
    public void onRelocateTag(TagsView tagViewToRelocate,
                              TagsView tagViewRelocationRef,
                              InsertionPoint insertionPoint) {
        // Prevent modification between two similar TagLists
        if (!containsTagView(tagViewToRelocate)
                || !containsTagView(tagViewRelocationRef))
            return;

        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < tagItems.size(); i++)
            if (tagItems.get(i).getTagView().equals(tagViewToRelocate))
                fromIndex = i;
            else if (tagItems.get(i).getTagView().equals(tagViewRelocationRef))
                if (InsertionPoint.BEFORE == insertionPoint)
                    toIndex = i;
                else
                    toIndex = i + 1;

        // Reorder tagItem list
        tagItems.add(toIndex, tagItems.get(fromIndex));
        tagItems.remove(fromIndex > toIndex ? fromIndex + 1 : fromIndex);

        // Reset TagListView
        for (TagItem tagItem : tagItems) {
            getTagListView().getTagsPanel().remove(tagItem.getTagView());
            getTagListView().getTagsPanel().add(tagItem.getTagView());
        }
    }

    @Override
    public void onRemoveTag(TagsView tagView) {
        for (Iterator<TagItem> tagItemIt = tagItems.iterator(); tagItemIt.hasNext(); ) {
            TagItem tagItem = tagItemIt.next();
            if (tagItem.getTagView().equals(tagView)) {
                getTagListView().getTagsPanel().remove(tagItem.getTagView());
                tagItemIt.remove();
                taggable.detachTag(tagItem.getTag());
            }
        }

        if (onChangeCmd != null) {
            onChangeCmd.execute();
        }
    }

    @Override
    public void onSelectTag(TagsView tagView) {
        for (TagItem tagItem : tagItems) {
            if (tagItem.getTagView().equals(tagView)) {
                taggable.selectTag(tagItem.getTag());
                break;
            }
        }
    }

    public void removeAll() {
        if (tagListView != null) {
            tagListView.getTagsPanel().clear();
        }

        tagItems.clear();
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;

        getTagListView().setEditable(editable);
        for (TagItem tagItem : tagItems) {
            tagItem.getTagView().setEditable(editable);
        }
    }

    /**
     * @param onBlurCmd the onBlurCmd to set
     */
    public void setOnBlurCmd(Command onBlurCmd) {
        this.onBlurCmd = onBlurCmd;
    }

    /**
     * @param onFocusCmd the onFocusCmd to set
     */
    public void setOnFocusCmd(Command onFocusCmd) {
        this.onFocusCmd = onFocusCmd;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;

        getTagListView().setEditable(removable);
        for (TagItem tagItem : tagItems) {
            tagItem.getTagView().setRemovable(removable);
        }
    }

    private boolean containsTagView(TagsView tagView) {
        for (TagItem tagItem : tagItems)
            if (tagItem.getTagView().equals(tagView))
                return true;
        return false;
    }

    private void updateTagDescription(String tagId, String newDesc) {
        tagsService.updateTagDescription(tagId, newDesc, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post("Unable to update tag description", caught);
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig("Tag description updated successfully."));
            }
        });
    }
}
