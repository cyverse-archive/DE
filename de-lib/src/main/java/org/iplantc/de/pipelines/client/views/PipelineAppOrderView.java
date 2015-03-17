package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.PipelineTask;

import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.data.shared.ListStore;

/**
 * A PipelineStepEditorView for adding, removing, and ordering Apps in the Pipeline.
 * 
 * @author psarando
 * 
 */
public interface PipelineAppOrderView extends IsWidget {
    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {

        public void onAddAppsClicked();

        public void onRemoveAppClicked();

        public void onMoveUpClicked();

        public void onMoveDownClicked();

        /**
         * Gets the given App's workflow step name, based on its position in the workflow and its ID.
         * 
         * @param app
         * @return the PipelineApp's step name.
         */
        public String getStepName(PipelineTask app);
    }

    public void setPresenter(Presenter presenter);

    public ListStore<PipelineTask> getPipelineAppStore();

    public PipelineTask getOrderGridSelectedApp();
}
