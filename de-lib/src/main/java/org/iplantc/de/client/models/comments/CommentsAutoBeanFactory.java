package org.iplantc.de.client.models.comments;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface CommentsAutoBeanFactory extends AutoBeanFactory {
    AutoBean<CommentList> comments();

    AutoBean<Comment> comment();

}
