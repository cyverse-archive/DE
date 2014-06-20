package org.iplantc.de.commons.client.comments.presenter;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.models.comments.CommentList;
import org.iplantc.de.client.models.comments.CommentsAutoBeanFactory;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.view.CommentsView;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

import java.util.List;

public class CommentsPresenter implements CommentsView.Presenter {
    final CommentsView view;
    final String resourceID;
    final MetadataServiceFacade facade;
    CommentsAutoBeanFactory cabf = GWT.create(CommentsAutoBeanFactory.class);
    private final boolean isResourceOwner;

    public CommentsPresenter(CommentsView cv, String resourceID, boolean owner, MetadataServiceFacade facade) {
        this.view = cv;
        this.resourceID = resourceID;
        this.facade = facade;
        this.isResourceOwner = owner;
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
    public void onDelete(final Comment c) {
        ConfirmMessageBox mbox = new ConfirmMessageBox(I18N.DISPLAY.confirmAction(),
                                                       I18N.DISPLAY.retractCommentConfirm());
        mbox.addDialogHideHandler(new DialogHideHandler() {

            @Override
            public void onDialogHide(DialogHideEvent event) {
                if (event.getHideButton().toString().equalsIgnoreCase("YES")) {
                    retractComment(c);
                }

            }
        });

        mbox.show();
    }

    @Override
    public void loadComments(List<Comment> comments) {
        view.loadComments(comments);
    }

    private void addComment(final Comment comment) {
        facade.addComment(resourceID, comment.getCommentText(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.addCommentError(), caught);
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.addComment()));
                JSONObject obj = JsonUtil.getObject(result);
                JSONObject comObj = JsonUtil.getObject(obj, "comment");
                AutoBean<Comment> cl = AutoBeanCodex.decode(cabf, Comment.class, comObj.toString());
                view.addComment(cl.as());
            }

        });

    }

    private void retractComment(final Comment comment) {
        facade.markAsRetracted(resourceID, comment.getId(), true, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.retractCommentEorror(), caught);

            }

            @Override
            public void onSuccess(String result) {
                comment.setCommentText(I18N.DISPLAY.commentRetracted());
                comment.setRetracted(true);
                view.retractComment(comment);
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(I18N.DISPLAY.commentRetracted()));
                onSelect(comment);
            }

        });
    }

    private void getComments() {
        facade.getComments(resourceID, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.commentsError(), caught);

            }

            @Override
            public void onSuccess(String result) {
                AutoBean<CommentList> cl = AutoBeanCodex.decode(cabf, CommentList.class, result);
                loadComments(cl.as().getComments());
            }
        });
    }

    @Override
    public void onSelect(Comment comment) {
        if ((comment.getCommentedBy().equalsIgnoreCase(UserInfo.getInstance().getUsername()) || isResourceOwner) && !(comment.isRetracted())) {
            view.enableDelete();
        } else {
            view.disableDelete();
        }

    }

}
