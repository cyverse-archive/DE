package org.iplantc.de.commons.client.comments.presenter;

import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.commons.client.comments.view.CommentsView;

import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.List;

public class CommentsPresenter implements CommentsView.Presenter {
    final CommentsView view;

    public CommentsPresenter(CommentsView cv) {
        this.view = cv;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.getWidget());
    }

    @Override
    public void onAdd(Comment c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDelete(Comment c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadComments(List<Comment> comments) {
        view.loadComments(comments);

    }

}
