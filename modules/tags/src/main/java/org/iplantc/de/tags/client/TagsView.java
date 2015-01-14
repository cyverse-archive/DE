package org.iplantc.de.tags.client;

import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.tags.client.proxy.TagSuggestionLoadConfig;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.ListLoadResult;

import java.util.List;

/**
 * @author jstroot
 */
public interface TagsView extends IsWidget {

    interface Presenter extends TagListHandlers {

        void buildTagCloudForSelectedResource(List<IplantTag> tags);

        IsWidget getTagListView();

        void removeAll();

        void setEditable(boolean editable);

        void setOnBlurCmd(Command onBlurCmd);

        void setOnFocusCmd(Command onFocusCmd);

        void setRemovable(boolean removable);
    }

    interface TagSuggestionProxy extends DataProxy<TagSuggestionLoadConfig, ListLoadResult<IplantTag>> {


    }

    /**
     *
     * @author cbopp
     *
     */
    interface TagListHandlers {

        public enum InsertionPoint {
            BEFORE, AFTER
        }

        void onCreateTag(IplantTag tag);

        void onAddTag(IplantTag tag);

        void onRemoveTag(TagsView tagView);

        void onEditTag(TagsView tagView);

        void onRelocateTag(TagsView tagViewToRelocate,
                           TagsView tagViewRelocationRef,
                           InsertionPoint insertionPoint);
        void onFocus();

        void onBlur();

        void onSelectTag(TagsView tagView);

    }

    IplantTag getTag();

    void setEditable(boolean editable);

    void setRemovable(boolean removable);

    void setUiHandlers(TagListHandlers tagListHandlers);
}
