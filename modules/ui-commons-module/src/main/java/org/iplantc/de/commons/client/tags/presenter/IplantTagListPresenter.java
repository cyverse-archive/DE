package org.iplantc.de.commons.client.tags.presenter;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.gin.CommonsInjector;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.tags.Taggable;
import org.iplantc.de.commons.client.tags.resources.CustomIplantTagResources;
import org.iplantc.de.commons.client.tags.views.IplantTagListView;
import org.iplantc.de.commons.client.tags.views.TagView;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IplantTagListPresenter implements TagListHandlers {

    private final CustomIplantTagResources resources;
    private final IplantTagListView tagListView;
    private final List<TagItem> tagItems = new ArrayList<TagItem>();

    private boolean editable;
    private Command onFocusCmd;
    private Command onBlurCmd;
    private Command onChangeCmd;
    private final MetadataServiceFacade mdataService;
    private final Taggable taggable;

    /**
     * @return the editable
     */
    public boolean isEditable() {
        return this.editable;
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;

        this.getTagListView().setEditable(editable);
        for (TagItem tagItem : this.tagItems)
            tagItem.getTagView().setEditable(true);
    }

    /**
     * @return the onFocusCmd
     */
    public Command getOnFocusCmd() {
        return this.onFocusCmd;
    }

    /**
     * @param onFocusCmd the onFocusCmd to set
     */
    public void setOnFocusCmd(Command onFocusCmd) {
        this.onFocusCmd = onFocusCmd;
    }

    /**
     * @return the onBlurCmd
     */
    public Command getOnBlurCmd() {
        return this.onBlurCmd;
    }

    /**
     * @param onBlurCmd the onBlurCmd to set
     */
    public void setOnBlurCmd(Command onBlurCmd) {
        this.onBlurCmd = onBlurCmd;
    }

    /**
     * @return the onChangeCmd
     */
    public Command getOnChangeCmd() {
        return this.onChangeCmd;
    }

    /**
     * @param onChangeCmd the onChangeCmd to set
     */
    public void setOnChangeCmd(Command onChangeCmd) {
        this.onChangeCmd = onChangeCmd;
    }

    /**
     * Creates a non editable TagList with default styles.
     * 
     * <p>
     * Use {@link TagList#setEditable(boolean)} to enable/disable tag creation on an existing TagList.
     * You have to set a {@link TagCreationCodex} to successfully enable tag creation.
     */
    public IplantTagListPresenter(Taggable taggable) {
        this(taggable, CustomIplantTagResources.INSTANCE);
    }

    /**
     * Creates a non editable TagList with custom styles.
     */
    public IplantTagListPresenter(Taggable taggable, CustomIplantTagResources resources) {
        super();
        this.taggable = taggable;
        this.resources = resources;
        this.resources.style().ensureInjected();

        this.tagListView = CommonsInjector.INSTANCE.getIplantTagListView();
        this.getTagListView().setUiHandlers(this);
        this.mdataService = ServicesInjector.INSTANCE.getMetadataService();
    }

    /**
     * A method t build view containing tags for selected resource. No associated call backs and events
     * will be called.
     * 
     * @param tags
     */
    public void buildTagCloudForSelectedResource(List<IplantTag> tags) {
        if (tags != null) {
            for (IplantTag tag : tags) {
                TagView tagView = new TagView(this.resources, tag);
                tagView.setUiHandlers(this);
                tagView.setEditable(this.editable);

                this.getTagListView().getTagsPanel().add(tagView);
                this.tagItems.add(new TagItem(tag, tagView));
            }
        }
    }

    public List<IplantTag> getTags() {
        List<IplantTag> tags = new ArrayList<IplantTag>();
        for (TagItem tagItem : this.tagItems) {
            tags.add(tagItem.getTag());
        }
        return tags;
    }

    public boolean addTag(IplantTag tag) {
        for (TagItem tagItem : this.tagItems) {
            if (tagItem.getTag().getValue().equals(tag.getValue())) {
                return false;
            }
        }

        TagView tagView = new TagView(this.resources, tag);
        tagView.setUiHandlers(this);
        tagView.setEditable(this.editable);

        this.getTagListView().getTagsPanel().add(tagView);
        this.tagItems.add(new TagItem(tag, tagView));
        taggable.attachTag(tag);

        return true;
    }

    public boolean addTags(List<IplantTag> tags) {
        boolean hasChanged = false;
        if (tags != null) {
            for (IplantTag tag : tags) {
                hasChanged |= this.addTag(tag);
            }
        }

        return hasChanged;
    }

    public boolean removeTag(IplantTag tag) {
        for (Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext();) {
            TagItem tagItem = tagItemIt.next();
            if (tagItem.getTag().equals(tag)) {
                this.getTagListView().getTagsPanel().remove(tagItem.getTagView());
                tagItemIt.remove();
                taggable.detachTag(tag);
                return true;
            }
        }

        return false;
    }

    public void clear() {
        for (Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext();) {
            TagItem tagItem = tagItemIt.next();
            this.getTagListView().getTagsPanel().remove(tagItem.getTagView());
            tagItemIt.remove();
        }
    }

    @Override
    public void onAddTag(IplantTag tag) {
        this.addTag(tag);

        if (this.onChangeCmd != null) {
            this.onChangeCmd.execute();
        }
    }

    @Override
    public void onRemoveTag(TagView tagView) {
        for (Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext();) {
            TagItem tagItem = tagItemIt.next();
            if (tagItem.getTagView().equals(tagView)) {
                this.getTagListView().getTagsPanel().remove(tagItem.getTagView());
                tagItemIt.remove();
                taggable.detachTag(tagItem.getTag());
            }
        }

        if (this.onChangeCmd != null) {
            this.onChangeCmd.execute();
        }
    }

    @Override
    public void onRelocateTag(TagView tagViewToRelocate, TagView tagViewRelocationRef, InsertionPoint insertionPoint) {
        // Prevent modification between two similar TagLists
        if (!this.containsTagView(tagViewToRelocate) || !this.containsTagView(tagViewRelocationRef))
            return;

        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < this.tagItems.size(); i++)
            if (this.tagItems.get(i).getTagView().equals(tagViewToRelocate))
                fromIndex = i;
            else if (this.tagItems.get(i).getTagView().equals(tagViewRelocationRef))
                if (InsertionPoint.BEFORE == insertionPoint)
                    toIndex = i;
                else
                    toIndex = i + 1;

        // Reorder tagItem list
        this.tagItems.add(toIndex, this.tagItems.get(fromIndex));
        this.tagItems.remove(fromIndex > toIndex ? fromIndex + 1 : fromIndex);

        // Reset TagListView
        for (TagItem tagItem : this.tagItems) {
            this.getTagListView().getTagsPanel().remove(tagItem.getTagView());
            this.getTagListView().getTagsPanel().add(tagItem.getTagView());
        }
    }

    private boolean containsTagView(TagView tagView) {
        for (TagItem tagItem : this.tagItems)
            if (tagItem.getTagView().equals(tagView))
                return true;
        return false;
    }

    @Override
    public void onFocus() {
        if (this.onFocusCmd != null)
            this.onFocusCmd.execute();
    }

    @Override
    public void onBlur() {
        if (this.onBlurCmd != null)
            this.onBlurCmd.execute();
    }

    public class TagItem {
        private final IplantTag tag;
        private final TagView tagView;

        public TagItem(IplantTag tag, TagView tagView) {
            this.tag = tag;
            this.tagView = tagView;
        }

        public TagView getTagView() {
            return this.tagView;
        }

        public IplantTag getTag() {
            return this.tag;
        }
    }

    @Override
    public void onEditTag(TagView tagView) {
        for (Iterator<TagItem> tagItemIt = this.tagItems.iterator(); tagItemIt.hasNext();) {
            TagItem tagItem = tagItemIt.next();
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

    private void updateTagDescription(String tagId, String newDesc) {
        mdataService.updateTagDescription(tagId, newDesc, new AsyncCallback<String>() {

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

    public IplantTagListView getTagListView() {
        return tagListView;
    }

    @Override
    public void onCreateTag(final IplantTag tag) {
        mdataService.createTag(tag, new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject resultObj = JsonUtil.getObject(result);
                tag.setId(JsonUtil.getString(resultObj, "id"));
                // this is a side-effect ?!?
                onAddTag(tag);
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post("Unable to create this tag!", caught);

            }
        });

    }
}
