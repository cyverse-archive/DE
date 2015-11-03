package org.iplantc.de.apps.integration.client.view.propertyEditors.util;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorContext;
import com.google.gwt.editor.client.EditorVisitor;

import com.sencha.gxt.widget.core.client.form.Field;

public final class FinishEditing extends EditorVisitor {
    @Override
    public <T> void endVisit(EditorContext<T> ctx) {
        Editor<T> editor = ctx.getEditor();
        if (editor instanceof Field<?>) {
            ((Field<?>)editor).finishEditing();
        }
    }
}