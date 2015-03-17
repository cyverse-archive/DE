package org.iplantc.de.commons.client.comments.presenter;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.comments.Comment;
import org.iplantc.de.client.models.comments.CommentList;
import org.iplantc.de.client.models.comments.CommentsAutoBeanFactory;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

import java.util.List;

/**
 * @author jstroot
 */
public class CommentsPresenterImpl implements CommentsView.Presenter {
    final CommentsView view;
    private final UserInfo userInfo;
    final String resourceID;
    MetadataServiceFacade facade;
    final CommentsAutoBeanFactory factory;
    private final boolean isResourceOwner;
    private final CommentsPresenterAppearance appearance;
    private final JsonUtil jsonUtil;

    @Inject
    CommentsPresenterImpl(final CommentsView cv,
                          final CommentsPresenterAppearance appearance,
                          final CommentsAutoBeanFactory factory,
                          final JsonUtil jsonUtil,
                          final UserInfo userInfo,
                          @Assisted final String resourceID,
                          @Assisted final boolean owner) {
        this.view = cv;
        this.jsonUtil = jsonUtil;
        this.userInfo = userInfo;
        this.resourceID = resourceID;
        this.isResourceOwner = owner;
        this.factory = factory;
        this.appearance = appearance;
        this.view.setPresenter(this);
    }

    @Override
    public void go(HasOneWidget container, final MetadataServiceFacade facade) {
        this.facade = facade;
        getComments();
        container.setWidget(view.getWidget());
    }

    @Override
    public void onAdd(Comment c) {
        addComment(c);
    }

    @Override
    public void onDelete(final Comment c) {
        ConfirmMessageBox mbox = new ConfirmMessageBox(appearance.confirmAction(),
                                                       appearance.retractCommentConfirm());
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
                ErrorHandler.post(appearance.addCommentError(), caught);
            }

            @Override
            public void onSuccess(String result) {
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.addComment()));
                JSONObject obj = jsonUtil.getObject(result);
                JSONObject comObj = jsonUtil.getObject(obj, Comment.COMMENT_TEXT_KEY);
                AutoBean<Comment> cl = AutoBeanCodex.decode(factory, Comment.class, comObj.toString());
                view.addComment(cl.as());
            }

        });

    }

    private void retractComment(final Comment comment) {
        facade.markAsRetracted(resourceID, comment.getId(), true, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.retractCommentError(), caught);

            }

            @Override
            public void onSuccess(String result) {
                comment.setCommentText(appearance.commentRetracted());
                comment.setRetracted(true);
                view.retractComment(comment);
                IplantAnnouncer.getInstance().schedule(new SuccessAnnouncementConfig(appearance.commentRetracted()));
                onSelect(comment);
            }

        });
    }

    private void getComments() {
        facade.getComments(resourceID, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(appearance.commentsError(), caught);

            }

            @Override
            public void onSuccess(String result) {
                AutoBean<CommentList> cl = AutoBeanCodex.decode(factory, CommentList.class, result);
                loadComments(cl.as().getComments());
            }
        });
    }

    @Override
    public void onSelect(Comment comment) {
        if ((comment.getCommentedBy().equalsIgnoreCase(userInfo.getUsername()) || isResourceOwner) && !(comment.isRetracted())) {
            view.enableDelete();
        } else {
            view.disableDelete();
        }

    }

    @Override
    public void go(HasOneWidget container) {
        throw new UnsupportedOperationException();

    }
}
