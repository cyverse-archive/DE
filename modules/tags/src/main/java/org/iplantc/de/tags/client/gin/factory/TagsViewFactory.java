package org.iplantc.de.tags.client.gin.factory;

import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.tags.client.TagsView;

import com.sencha.gxt.data.shared.ListStore;

/**
 * Created by jstroot on 2/5/15.
 * @author jstroot
 */
public interface TagsViewFactory {
    TagsView create(ListStore<Tag> listStore,
                    TagsView.Presenter presenter);
}
