package org.iplantc.de.theme.base.client.tags.presenter;

import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.theme.base.client.tags.TagDisplayMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class TagsViewPresenterDefaultAppearance implements TagsView.Presenter.Appearance {
    private final TagDisplayMessages displayMessages;

    public TagsViewPresenterDefaultAppearance() {
        this(GWT.<TagDisplayMessages> create(TagDisplayMessages.class));
    }

    TagsViewPresenterDefaultAppearance(final TagDisplayMessages displayMessages) {
        this.displayMessages = displayMessages;
    }

}
