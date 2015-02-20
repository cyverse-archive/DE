package org.iplantc.de.analysis.client.views.dialogs;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.form.TextArea;

/**
 * @author jstroot
 */
public class AnalysisCommentsDialog extends IPlantDialog {

    private final Analysis analysis;
    private final TextArea ta;

    public AnalysisCommentsDialog(final Analysis analysis){
        this(analysis,
             GWT.<AnalysesView.Presenter.Appearance> create(AnalysesView.Presenter.Appearance.class));
    }

    public AnalysisCommentsDialog(final Analysis analysis,
                                  final AnalysesView.Presenter.Appearance appearance) {
        this.analysis = analysis;

        String comments = analysis.getComments();
        setHeadingText(appearance.comments());
        setSize("350px", "300px");
        ta = new TextArea();
        ta.setValue(comments);
        add(ta);
    }

    public String getComment() {
        return ta.getValue();
    }

    public boolean isCommentChanged() {
        return !getComment().equals(analysis.getComments());
    }
}
