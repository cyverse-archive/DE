package org.iplantc.de.commons.client.comments.view.cells;



import org.iplantc.de.client.models.comments.Comment;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.Date;

public class CommentsCell extends AbstractCell<Comment> {

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<div style='white-space: normal;'> <p>On <b>{1}</b>, <i>{0} </i> wrote: </p> <p>{2}</p> </dvi>")
        SafeHtml commentCell(String user, String timestamp, String comment);

    }

    private static Templates templates = GWT.create(Templates.class);

    public CommentsCell() {

    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Comment value, SafeHtmlBuilder sb) {
        sb.append(templates.commentCell(value.getCommentedBy(), DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date(value.getTimestamp())), value.getCommentText()));
    }

}
