package org.iplantc.de.apps.widgets.client.view;

import org.iplantc.de.client.models.apps.integration.JobExecution;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface LaunchAnalysisView extends IsWidget, Editor<JobExecution> {

    void edit(JobExecution je);

    JobExecution flushJobExecution();

    List<EditorError> getErrors();

    boolean hasErrors();

}
