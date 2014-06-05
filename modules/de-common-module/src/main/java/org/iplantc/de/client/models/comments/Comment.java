package org.iplantc.de.client.models.comments;

import org.iplantc.de.client.models.HasId;

public interface Comment extends HasId {

    long getTimestamp();

    void setTimestamp(long timestamp);

    String getCommentedBy();

    void setCommentedBy(String user);

    String getCommentText();

    void setCommentText(String commenttext);

    void setRetracted(boolean retracted);

    boolean isRetracted();

    void setId(String id);

}
