package org.iplantc.de.apps.integration.client.presenter.visitors;

import org.iplantc.de.apps.widgets.client.view.HasLabelOnlyEditMode;

import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;

public class InitLabelOnlyEditMode extends EditorVisitor {

    private final boolean onlyLabelEditMode;

    public InitLabelOnlyEditMode(boolean onlyLabelEditMode) {
        this.onlyLabelEditMode = onlyLabelEditMode;
    }

    @Override
    public <T> void endVisit(EditorContext<T> ctx) {
        if (ctx.getEditor() instanceof HasLabelOnlyEditMode) {
            ((HasLabelOnlyEditMode)ctx.getEditor()).setLabelOnlyEditMode(onlyLabelEditMode);
        }
    }

}
