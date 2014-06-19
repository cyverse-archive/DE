package org.iplantc.de.commons.client.comments.view;

import org.iplantc.de.client.models.comments.Comment;

import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public interface CommentsView {
    
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        void onAdd(Comment c);

        void onDelete(Comment c);

        void loadComments(List<Comment> comments);

        void onSelect(Comment comment);
    }

    void setPresenter(Presenter p);

    void loadComments(List<Comment> comments);

    Widget getWidget();

    void addComment(Comment c);

    void retractComment(Comment c);

    void enableDelete();

    void disableDelete();
}
