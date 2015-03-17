package org.iplantc.de.theme.base.client.commons.comments;

import com.google.gwt.i18n.client.Messages;

/**
 * Created by jstroot on 1/20/15.
 * @author jstroot
 */
public interface CommentsDisplayStrings extends Messages{
    @Key("addComment")
    String addComment();

    @Key("addCommentError")
    String addCommentError();

    @Key("commentColumnHeader")
    String commentColumnHeader();

    @Key("commentRetracted")
    String commentRetracted();

    @Key("commentsBoxEmptyText")
    String commentsBoxEmptyText();

    @Key("commentsError")
    String commentsError();

    @Key("retract")
    String retract();

    @Key("retractCommentConfirm")
    String retractCommentConfirm();

    @Key("retractCommentError")
    String retractCommentError();
}
