package org.iplantc.de.tags.client.gin.factory;

import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.tags.client.TagsView;

/**
 * @author jstroot
 */
public interface TagsViewFactory {
    TagsView createTagsView(IplantTag tag);
}
