package org.iplantc.de.client.models.comments;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface Comment extends HasId {

    @PropertyName("comment_time")
    long getTimestamp();

    @PropertyName("comment_time")
    void setTimestamp(long timestamp);

    @PropertyName("commenter")
    String getCommentedBy();

    @PropertyName("commenter")
    void setCommentedBy(String user);

    @PropertyName("comment")
    String getCommentText();

    @PropertyName("comment")
    void setCommentText(String commenttext);

    @PropertyName("retracted")
    void setRetracted(boolean retracted);

    @PropertyName("retracted")
    boolean isRetracted();

    void setId(String id);

}
