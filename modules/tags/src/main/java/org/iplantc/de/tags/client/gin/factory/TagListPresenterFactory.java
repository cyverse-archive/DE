package org.iplantc.de.tags.client.gin.factory;

import org.iplantc.de.tags.client.Taggable;
import org.iplantc.de.tags.client.TagsView;

/**
 * @author jstroot
 */
public interface TagListPresenterFactory {
    TagsView.Presenter createTagListPresenter(Taggable taggable);
}
