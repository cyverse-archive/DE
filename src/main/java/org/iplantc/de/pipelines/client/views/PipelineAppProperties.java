package org.iplantc.de.pipelines.client.views;

import org.iplantc.de.client.models.pipelines.PipelineApp;

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
public interface PipelineAppProperties extends PropertyAccess<PipelineApp> {
    @Path("name")
    LabelProvider<PipelineApp> label();

    ValueProvider<PipelineApp, String> name();

    ValueProvider<PipelineApp, String> description();

    ValueProvider<PipelineApp, Integer> step();
}
