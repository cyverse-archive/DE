package org.iplantc.de.tags.client.presenter;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;
import org.iplantc.de.client.services.TagsServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.events.RequestCreateTag;
import org.iplantc.de.tags.client.events.TagCreated;
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
                                              RequestCreateTag.RequestCreateTagHandler {

    @Inject IplantAnnouncer announcer;
    @Inject TagsView.Presenter.Appearance appearance;
    @Inject FileSystemMetadataServiceFacade metadataService;
    @Inject TagsServiceFacade tagsService;
    private final ListStore<Tag> listStore;
    private final TagsView view;
    private boolean editable;
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
        view = tagsViewFactory.create(listStore, this);
        view.addRequestCreateTagHandler(this);
    }

    @Override
    public void addTag(Tag tag) {
        if (listStore.findModel(tag) == null) {
            listStore.add(tag);
        }

        view.clearSearchField();

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

    public List<Tag> getTags() {
        return listStore.getAll();
    }

    @Override
    public TagsView getView() {
        return view;
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
                view.asWidget().fireEvent(new TagCreated(result));
                // Service won't return error on double tagging. So add only if it doesn't exits.
                if (listStore.findModel(result) == null) {
                    listStore.add(result);
                }
            }
        });
    }

    @Override
    public void removeAll() {
        listStore.clear();
    }

    @Override
    public void removeTag(Tag tag) {
        listStore.remove(tag);
    }


    /**
     * @param editable the editable to set
     */
    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;

        view.setEditable(editable);
    }

    @Override
    public void setRemovable(boolean removable) {
        this.removable = removable;

        view.setRemovable(removable);
    }

    @Override
    public void updateTagDescription(final Tag tag) {
        tagsService.updateTagDescription(tag, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post("Unable to update tag description", caught);
            }

            @Override
            public void onSuccess(Void result) {
                announcer.schedule(new SuccessAnnouncementConfig("Tag description updated successfully."));
            }
        });
    }

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

}
