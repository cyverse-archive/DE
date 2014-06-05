package org.iplantc.de.client.models;

public interface Comment {

    long getTimestamp();

    void setTimestamp(long timestamp);

    String getCommentedBy();

    void setCommentedBy(String user);

    String getCommentText();

    void setCommentText(String commenttext);

}
