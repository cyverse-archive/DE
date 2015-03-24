package org.iplantc.de.tags.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.tags.client.events.RequestCreateTag;
import org.iplantc.de.tags.client.events.TagAddedEvent;
import org.iplantc.de.tags.client.events.TagCreated;
import org.iplantc.de.tags.client.events.selection.EditTagSelected;
import org.iplantc.de.tags.client.events.selection.RemoveTagSelected;
import org.iplantc.de.tags.client.events.selection.TagSelected;
import org.iplantc.de.tags.client.proxy.TagSuggestionLoadConfig;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.ListLoadResult;

/**
 * Interface for a view containing a tag search field, and tag cloud.
 * Created by jstroot on 2/5/15.
 * @author jstroot
 */
public interface TagsView extends IsWidget,
                                  RequestCreateTag.HasRequestCreateTagHandlers,
                                  TagSelected.HasTagSelectedHandlers,
                                  TagCreated.HasTagCreatedHandlers,
                                  TagAddedEvent.HasTagAddedEventHandlers,
                                  RemoveTagSelected.HasRemoveTagSelectedHandlers {

    interface Presenter {

        interface Appearance {
        }

        void addTag(Tag tag);

        void fetchTagsForResource(DiskResource resource);

        TagsView getView();

        void removeAll();

        void removeTag(Tag tag);

        void setEditable(boolean editable);

        void setRemovable(boolean removable);

        void updateTagDescription(Tag tag);
    }
    /**
     * RPC proxy used for the TagViewSearch field.
     */
    interface TagSuggestionProxy extends DataProxy<TagSuggestionLoadConfig, ListLoadResult<Tag>> {
    }

    /**
     * Represents a single tag within a tag cloud.
     * @author jstroot
     */
    interface TagItem extends IsWidget,
                              HasSelectionHandlers<Tag>,
                              EditTagSelected.HasEditTagSelectedHandlers,
                              RemoveTagSelected.HasRemoveTagSelectedHandlers {

        Tag getTag();

        void setEditable(boolean editable);

        void setRemovable(boolean removable);

    }

    interface TagSearchField extends IsWidget,
                                     HasValueChangeHandlers<Tag>,
                                     RequestCreateTag.HasRequestCreateTagHandlers {

    }


    void setEditable(boolean editable);

    void setRemovable(boolean removable);

    void clearSearchField();
}
