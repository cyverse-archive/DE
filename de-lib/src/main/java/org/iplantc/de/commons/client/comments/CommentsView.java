package org.iplantc.de.commons.client.comments;

import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.services.MetadataServiceFacade;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author jstroot
 */
public interface CommentsView {

    public interface CommentsViewAppearance {

        String commentColumnHeader();

        int commentColumnWidth();

        ImageResource retractIcon();

        String retractComment();

        ImageResource addCommentIcon();

        String addComment();

        String commentBoxEmptyText();

        String commentBoxWidth();

        String commentBoxHeight();
    }
    
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        public interface CommentsPresenterAppearance {

            String addComment();

            String addCommentError();

            String commentRetracted();

            String commentsError();

            String confirmAction();

            String retractCommentConfirm();

            String retractCommentError();
        }

        void go(final HasOneWidget container, MetadataServiceFacade facade);

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
