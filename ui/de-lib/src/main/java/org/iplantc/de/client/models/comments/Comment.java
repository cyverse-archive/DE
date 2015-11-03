package org.iplantc.de.client.models.comments;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author jstroot
 */
public interface Comment extends HasId {
    String COMMENT_TEXT_KEY = "comment";

    @PropertyName("post_time")
    long getTimestamp();

    @PropertyName("post_time")
    void setTimestamp(long timestamp);

    @PropertyName("commenter")
    String getCommentedBy();

    @PropertyName("commenter")
    void setCommentedBy(String user);

    @PropertyName(COMMENT_TEXT_KEY)
    String getCommentText();

    @PropertyName(COMMENT_TEXT_KEY)
    void setCommentText(String commentText);

    @PropertyName("retracted")
    void setRetracted(boolean retracted);

    @PropertyName("retracted")
    boolean isRetracted();

    @PropertyName("id")
    void setId(String id);

}
