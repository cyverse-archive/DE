package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.PipelineTask;

import com.google.gwt.editor.client.Editor.Path;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * PropertyAccess interface for PipelineApp models.
 * 
 * @author psarando
 * 
 */
public interface PipelineAppProperties extends PropertyAccess<PipelineTask> {
    @Path("name")
    LabelProvider<PipelineTask> label();

    ValueProvider<PipelineTask, String> name();

    ValueProvider<PipelineTask, String> description();

    ValueProvider<PipelineTask, Integer> step();
}
