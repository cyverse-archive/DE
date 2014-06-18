package org.iplantc.de.commons.client.comments.presenter;

import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.models.comments.CommentList;
import org.iplantc.de.client.models.comments.CommentsAutoBeanFactory;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.view.CommentsView;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class CommentsPresenter implements CommentsView.Presenter {
    final CommentsView view;
    final String resourceID;
    final MetadataServiceFacade facade;
    CommentsAutoBeanFactory cabf = GWT.create(CommentsAutoBeanFactory.class);

    public CommentsPresenter(CommentsView cv, String resourceID, MetadataServiceFacade facade) {
        this.view = cv;
        this.resourceID = resourceID;
        this.facade = facade;
        getComments();
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.getWidget());
    }

    @Override
    public void onAdd(Comment c) {
        addComment(c);

    }

    @Override
    public void onDelete(Comment c) {
        retractComment(c);

    }

    @Override
    public void loadComments(List<Comment> comments) {
        view.loadComments(comments);
    }

    private void addComment(final Comment comment) {
        facade.addComment(resourceID, comment.getCommentText(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post("Unable to add your comment!", caught);
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig("Comment added successfully."));
                AutoBean<Comment> cl = AutoBeanCodex.decode(cabf, Comment.class, result);
                view.addComment(cl.as());
            }

        });

    }

    private void retractComment(final Comment comment) {
        facade.markAsRetracted(resourceID, comment.getId(), true, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post("Unable to retract this comment!", caught);

            }

            @Override
            public void onSuccess(String result) {
                view.retractComment(comment);
            }

        });
    }

    private void getComments() {
        facade.getComments(resourceID, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post("Unable to load comments!", caught);

            }

            @Override
            public void onSuccess(String result) {
                AutoBean<CommentList> cl = AutoBeanCodex.decode(cabf, CommentList.class, result);
                loadComments(cl.as().getComments());
            }
        });
    }

}
