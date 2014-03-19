package org.iplantc.de.apps.widgets.client.view;

import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.de.apps.widgets.client.events.RequestAnalysisLaunchEvent.HasRequestAnalysisLaunchHandlers;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.JobExecution;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * The interface definition for the App Wizard view.
 * 
 * The App wizard is an editor which is data bound to a <code>AppTemplate</code>.
 * 
 * XXX Research ways to lazy load the view.
 * 
 * @author jstroot
 * 
 */
public interface AppLaunchView extends IsWidget, Editor<AppTemplate>, HasRequestAnalysisLaunchHandlers {

    /**
     * FIXME JDS Re-evaluate necessity for two different presenters.
     * 
     * @author jstroot
     * 
     */
    public interface BasePresenter extends org.iplantc.de.commons.client.presenter.Presenter{
    
        AppTemplate getAppTemplate();

        void go(final HasOneWidget container, final AppTemplate appTemplate);

    }

    public interface Presenter extends BasePresenter {
        void addAnalysisLaunchHandler(AnalysisLaunchEventHandler handler);
    }

    public interface RenameWindowHeaderCommand extends Command {
        void setAppTemplate(AppTemplate appTemplate);
    }

    void analysisLaunchFailed();

    void edit(AppTemplate appTemplate, JobExecution je);

}
