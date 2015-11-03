package org.iplantc.de.tags.client.gin.factory;

import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.tags.client.TagsView;

/**
 * @author jstroot
 */
public interface TagItemFactory {
    TagsView.TagItem createTagItem(Tag tag);
}
