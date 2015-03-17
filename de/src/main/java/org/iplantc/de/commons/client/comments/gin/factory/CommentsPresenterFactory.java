package org.iplantc.de.commons.client.comments.gin.factory;

import org.iplantc.de.commons.client.comments.CommentsView;

/**
 * Created by jstroot on 1/21/15.
 * @author jstroot
 */
public interface CommentsPresenterFactory {
    CommentsView.Presenter createCommentsPresenter(String resourceId, boolean isOwner);
}
