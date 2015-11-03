package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.Pipeline;
import org.iplantc.de.client.models.pipelines.PipelineTask;
import org.iplantc.de.pipelineBuilder.client.builder.PipelineCreator;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import java.util.List;

/**
 * A View for Pipeline editors.
 * 
 * @author psarando
 * 
 */
public interface PipelineView extends IsWidget, Editor<Pipeline> {

    public interface Presenter extends org.iplantc.de.commons.client.presenter.Presenter {
        public Pipeline getPipeline();

        public void setPipeline(Pipeline pipeline);

        public void setPipeline(Splittable serviceWorkflowJson);
        
        public String getPublishJson(Pipeline pipeline);

        public void onInfoClick();

        public void onAppOrderClick();

        public void onMappingClick();
        
        public void saveOnClose();
    }

    void setPresenter(final Presenter presenter);

    /**
     * Checks if the current state of the Pipeline is valid.
     * 
     * @return boolean true if the Pipeline is valid, false otherwise.
     */
    public boolean isValid();

    public List<EditorError> getErrors();

    public void clearInvalid();

    /**
     * Gets the current state of the Pipeline.
     * 
     * @return Pipeline current state.
     */
    public Pipeline getPipeline();

    /**
     * Initializes the Pipeline from the given state.
     * 
     * @param pipeline
     */
    public void setPipeline(Pipeline pipeline);

    public void setNorthWidget(IsWidget widget);

    public IsWidget getActiveView();

    public void setActiveView(IsWidget view);

    public BorderLayoutContainer getBuilderPanel();

    public SimpleContainer getBuilderDropContainer();

    public PipelineCreator getPipelineCreator();

    public BorderLayoutContainer getStepEditorPanel();

    public SimpleContainer getAppsContainer();

    public CardLayoutContainer getStepPanel();

    public IsWidget getInfoPanel();

    public PipelineAppOrderView getAppOrderPanel();

    @Editor.Ignore
    public PipelineAppMappingView getMappingPanel();

    public ListStore<PipelineTask> getPipelineAppStore();

    public PipelineTask getOrderGridSelectedApp();

    @Editor.Ignore
    public ToggleButton getInfoBtn();

    @Editor.Ignore
    public ToggleButton getAppOrderBtn();

    @Editor.Ignore
    public ToggleButton getMappingBtn();

    public HtmlLayoutContainer getHelpContainer();

    public void markInfoBtnValid();

    public void markInfoBtnInvalid(String error);

    public void markAppOrderBtnValid();

    public void markAppOrderBtnInvalid(String error);

    public void markMappingBtnValid();

    public void markMappingBtnInvalid(String error);
}
