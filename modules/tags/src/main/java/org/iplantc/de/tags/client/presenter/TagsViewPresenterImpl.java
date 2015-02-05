package org.iplantc.de.tags.client.presenter;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.events.RequestCreateTag;
import org.iplantc.de.tags.client.events.selection.EditTagSelected;
import org.iplantc.de.tags.client.events.selection.RemoveTagSelected;
import org.iplantc.de.tags.client.events.selection.TagSelected;
import org.iplantc.de.tags.client.gin.factory.TagsViewFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;

import java.util.List;

/**
 * @author jstroot
 */
public class TagsViewPresenterImpl implements TagsView.Presenter,
                                              RequestCreateTag.RequestCreateTagHandler,
                                              EditTagSelected.EditTagSelectedHandler,
                                              RemoveTagSelected.RemoveTagSelectedHandler,
                                              TagSelected.TagSelectedHandler {

    @Inject TagsServiceFacade tagsService;
    @Inject MetadataServiceFacade metadataService;
    private final TagsView view;
    private boolean editable;
    private final ListStore<Tag> listStore;
    private boolean removable;

    /**
     * Use {@link org.iplantc.de.tags.client.TagsView.TagItem#setEditable(boolean)} to enable/disable tag creation on an existing TagList.
     */
    @Inject
    TagsViewPresenterImpl(final TagsViewFactory tagsViewFactory) {
        listStore = new ListStore<>(new ModelKeyProvider<Tag>() {
            @Override
            public String getKey(Tag item) {
                return item.getId();
            }
        });
        view = tagsViewFactory.create(listStore);
        view.addRequestCreateTagHandler(this);
        view.addEditTagSelectedHandler(this);
        view.addRemoveTagSelectedHandler(this);
        view.addTagSelectedHandler(this);
    }

    public void addTag(Tag tag) {
        listStore.add(tag);
    }

    @Override
    public void fetchTagsForResource(DiskResource resource) {

        listStore.clear();
        metadataService.getTags(resource, new AsyncCallback<List<Tag>>() {
            @Override
            public void onFailure(Throwable caught) {
                // FIXME Move to appearance
                ErrorHandler.post("Unable to retrieve tags!", caught);
            }

            @Override
            public void onSuccess(List<Tag> result) {
                listStore.addAll(result);
            }
        });
    }

    @Override
    public TagsView getView() {
        return view;
    }

    public List<Tag> getTags() {
        return listStore.getAll();
    }

    @Override
    public void onEditTagSelected(EditTagSelected event) {

    }

    @Override
    public void onRemoveTagSelected(RemoveTagSelected event) {
        listStore.remove(event.getTag());
    }

    @Override
    public void onRequestCreateTag(RequestCreateTag event) {
        String tagText = event.getNewTagText();
        tagsService.createTag(tagText.trim(), new AsyncCallback<Tag>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.tagCreateError(), caught);
            }

            @Override
            public void onSuccess(Tag result) {
                listStore.add(result);
            }
        });
    }

    @Override
    public void onTagSelected(TagSelected event) {

    }

    // Later, if we want to broadcast Tag CRUD events, we can forward them from this service
    // FIXME Blur, focus needs to be taken care of
//    @Override
//    public void onEditTag(TagsView.TagItem tagView) {
//        for (TagToTagViewEntry tagItem : tagItems) {
//            if (tagItem.getTagView().equals(tagView)) {
//                final Tag tag = tagItem.tag;
//                final String tagId = tag.getId();
//                final TextArea tb = new TextArea();
//                tb.setSize("250", "200");
//                final String description = tag.getDescription();
//                tb.setValue(description);
//                Dialog pop = new Dialog();
//                pop.setHideOnButtonClick(true);
//                pop.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
//                pop.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
//
//                    @Override
//                    public void onSelect(SelectEvent event) {
//                        if (tb.getCurrentValue() != null && !tb.getCurrentValue().equals(description)) {
//                            tag.setDescription(tb.getCurrentValue());
//                            updateTagDescription(tagId, tb.getCurrentValue());
//                        }
//
//                    }
//                });
//                pop.setSize("300", "250");
//                pop.setHeadingText("Edit Tag Description for " + tagItem.getTag().getValue().toString());
//                pop.setWidget(tb);
//                pop.show();
//            }
//        }
//
//    }
//
//    @Override
//    public void onRelocateTag(TagsView.TagItem tagViewToRelocate,
//                              TagsView.TagItem tagViewRelocationRef,
//                              InsertionPoint insertionPoint) {
//        // Prevent modification between two similar TagLists
//        if (!containsTagView(tagViewToRelocate)
//                || !containsTagView(tagViewRelocationRef))
//            return;
//
//        int fromIndex = -1;
//        int toIndex = -1;
//
//        for (int i = 0; i < tagItems.size(); i++)
//            if (tagItems.get(i).getTagView().equals(tagViewToRelocate))
//                fromIndex = i;
//            else if (tagItems.get(i).getTagView().equals(tagViewRelocationRef))
//                if (InsertionPoint.BEFORE == insertionPoint)
//                    toIndex = i;
//                else
//                    toIndex = i + 1;
//
//        // Reorder tagItem list
//        tagItems.add(toIndex, tagItems.get(fromIndex));
//        tagItems.remove(fromIndex > toIndex ? fromIndex + 1 : fromIndex);
//
//        // Reset TagListView
//        for (TagToTagViewEntry tagItem : tagItems) {
//            view.getTagsPanel().remove(tagItem.getTagView());
//            view.getTagsPanel().add(tagItem.getTagView());
//        }
//    }
//
//    @Override
//    public void onRemoveTag(TagsView.TagItem tagView) {
//        for (Iterator<TagToTagViewEntry> tagItemIt = tagItems.iterator(); tagItemIt.hasNext(); ) {
//            TagToTagViewEntry tagItem = tagItemIt.next();
//            if (tagItem.getTagView().equals(tagView)) {
//                view.getTagsPanel().remove(tagItem.getTagView());
//                tagItemIt.remove();
//                taggable.detachTag(tagItem.getTag());
//            }
//        }
//
//        if (onChangeCmd != null) {
//            onChangeCmd.execute();
//        }
//    }
//
    // FIXME Fire tag selected events from view
//    @Override
//    public void onSelectTag(TagsView.TagItem tagView) {
//        for (TagToTagViewEntry tagItem : tagItems) {
//            if (tagItem.getTagView().equals(tagView)) {
//                taggable.selectTag(tagItem.getTag());
//                break;
//            }
//        }
//    }

    public void removeAll() {
        listStore.clear();
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;

        view.setEditable(editable);
    }

//    /**
//     * @param onBlurCmd the onBlurCmd to set
//     */
//    public void setOnBlurCmd(Command onBlurCmd) {
//        this.onBlurCmd = onBlurCmd;
//    }
//
//    /**
//     * @param onFocusCmd the onFocusCmd to set
//     */
//    public void setOnFocusCmd(Command onFocusCmd) {
//        this.onFocusCmd = onFocusCmd;
//    }

    public void setRemovable(boolean removable) {
        this.removable = removable;

        view.setEditable(removable);
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
