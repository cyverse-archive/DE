package org.iplantc.de.theme.base.client.commons.comments.presenter;

import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.commons.comments.CommentsDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class CommentsPresenterDefaultAppearance implements CommentsView.Presenter.CommentsPresenterAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final IplantErrorStrings iplantErrorStrings;
    private final CommentsDisplayStrings displayStrings;

    public CommentsPresenterDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class),
             GWT.<CommentsDisplayStrings> create(CommentsDisplayStrings.class));
    }
    public CommentsPresenterDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                              final IplantErrorStrings iplantErrorStrings,
                                              final CommentsDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.iplantErrorStrings = iplantErrorStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String addComment() {
        return displayStrings.addComment();
    }

    @Override
    public String addCommentError() {
        return displayStrings.addCommentError();
    }

    @Override
    public String commentRetracted() {
        return displayStrings.commentRetracted();
    }

    @Override
    public String commentsError() {
        return displayStrings.commentsError();
    }

    @Override
    public String confirmAction() {
        return iplantDisplayStrings.confirmAction();
    }

    @Override
    public String retractCommentConfirm() {
        return displayStrings.retractCommentConfirm();
    }

    @Override
    public String retractCommentError() {
        return displayStrings.retractCommentError();
    }
}
