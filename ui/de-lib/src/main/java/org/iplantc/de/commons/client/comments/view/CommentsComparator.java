package org.iplantc.de.commons.client.comments.view;

import org.iplantc.de.client.models.comments.Comment;

import java.util.Comparator;

public class CommentsComparator implements Comparator<Comment> {

    @Override
    public int compare(Comment o1, Comment o2) {
        if (o1.getTimestamp() < o2.getTimestamp()) {
            return -1;
        } else if (o1.getTimestamp() > o2.getTimestamp()) {
            return 1;
        } else {
            return 0;
        }
    }

}
