/* * Copyright 2012 Oregon State University.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package org.iplantc.de.pipelineBuilder.client.builder;

import org.iplantc.de.client.models.pipelines.PipelineApp;
import org.iplantc.de.client.models.pipelines.PipelineAppMapping;
import org.iplantc.de.client.models.pipelines.PipelineAutoBeanFactory;
import org.iplantc.de.pipelineBuilder.client.Resources;
import org.iplantc.de.pipelineBuilder.client.dnd.DragCreator;
import org.iplantc.de.pipelineBuilder.client.json.App;
import org.iplantc.de.pipelineBuilder.client.json.Input;
import org.iplantc.de.pipelineBuilder.client.json.Output;
import org.iplantc.de.pipelineBuilder.client.json.PipeApp;
import org.iplantc.de.pipelineBuilder.client.json.PipeComponent;
import org.iplantc.de.pipelineBuilder.client.json.Pipeline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PipelineCreator extends Composite {

	PipelineWorkspace workspace;
	HorizontalPanel main = new HorizontalPanel();
    private final PipelineAutoBeanFactory factory = GWT.create(PipelineAutoBeanFactory.class);;

	public PipelineCreator() {
	   Resources.INSTANCE.css().ensureInjected();

	    workspace = new PipelineWorkspace(new Pipeline("", "", false, 0));
		workspace.setHeight("100%");
		workspace.setWidth("100%");
		main.setStyleName("pipe-table");
		main.add(workspace);
		main.setCellHeight(workspace, "100%");
		main.setHeight("100%");
		initWidget(main);
	}

    /**
     * This will load an existing pipeline into the creator.
     * 
     * @param json The json representation of a Pipeline
     */
    public void loadPipeline(org.iplantc.de.client.models.pipelines.Pipeline json) {
        main.remove(workspace);
        workspace = new PipelineWorkspace(buildPipeline(json));
        workspace.setHeight("100%");
        workspace.setWidth("100%");
        main.insert(workspace, 0);
    }

    private Pipeline buildPipeline(org.iplantc.de.client.models.pipelines.Pipeline json) {
        Pipeline ret = new Pipeline();
        List<PipelineApp> apps = json.getApps();

        ret.setWorkflowId(json.getId());
        ret.setDescription(json.getDescription());
        ret.setName(json.getName());

        if (apps != null) {
            int i = 0;
            for (PipelineApp appObj : apps) {
                App app = DragCreator.createApp(appObj);

                List<PipelineAppMapping> mappingsA = appObj.getMappings();
                if (mappingsA != null) {
                    for (PipelineAppMapping map : mappingsA) {
                        int step = map.getStep();
                        PipeComponent stepC = ret.getSteps().get(step - 1);
                        App appM = ((PipeApp)stepC).getApp();
                        Map<String, String> maps = map.getMap();
                        for (String inputId : maps.keySet()) {
                            String outputId = maps.get(inputId);
                            for (Output output : appM.getOutputs()) {
                                if (output.getID().equals(outputId)) {
                                    output.setParent(stepC);
                                    for (Input input : app.getInputs()) {
                                        if (input.getID().equals(inputId)) {
                                            input.setMapped(output);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                PipeApp pipeApp = new PipeApp(1, 1, i++);
                pipeApp.setApp(app);
                ret.addStep(pipeApp);
            }
        }

        return ret;
    }

    public void appendApp(PipelineApp app) {
        workspace.appendApp(DragCreator.createApp(app));
    }

    /**
     * This will return the Pipeline AutoBean that represents the pipeline that is being built.
     * 
     * @return the AutoBean of the new pipeline
     */
    public org.iplantc.de.client.models.pipelines.Pipeline getPipeline() {
        org.iplantc.de.client.models.pipelines.Pipeline ret = factory.pipeline().as();
        Pipeline pipeline = workspace.getPipeline();

        ret.setId(pipeline.getWorkflowId());
        ret.setName(pipeline.getName());
        ret.setDescription(pipeline.getDescription());

        List<PipelineApp> apps = new ArrayList<PipelineApp>();
        Vector<PipeComponent> steps = pipeline.getSteps();
        for (PipeComponent step : steps) {
            App app = ((PipeApp)step).getApp();

            PipelineApp jsonApp = factory.app().as();
            jsonApp.setId(app.getID());
            jsonApp.setTemplateId(app.getTemplateId());
            jsonApp.setName(app.getName());
            jsonApp.setDescription(app.getDescription());
            jsonApp.setStep(step.getPosition() + 1);

            HashMap<PipeComponent, ArrayList<Input>> mappings = new HashMap<PipeComponent, ArrayList<Input>>();
            for (Input input : step.getInputs()) {
                if (input.getMapped() != null) {
                    PipeComponent parent = input.getMapped().getParent();
                    ArrayList<Input> maps = mappings.get(parent);
                    if (maps == null)
                        maps = new ArrayList<Input>();
                    maps.add(input);
                    mappings.put(parent, maps);
                }
            }

            List<PipelineAppMapping> jsonMappings = new ArrayList<PipelineAppMapping>();
            jsonApp.setMappings(jsonMappings);

            for (PipeComponent mappedTo : mappings.keySet()) {
                App mappedApp = ((PipeApp)mappedTo).getApp();
                PipelineAppMapping jsonMap = factory.appMapping().as();
                jsonMap.setStep(mappedTo.getPosition() + 1);
                jsonMap.setId(mappedApp.getTemplateId());
                ArrayList<Input> inputs = mappings.get(mappedTo);
                Map<String, String> map = new HashMap<String, String>();
                for (Input input : inputs) {
                    map.put(input.getID(), input.getMapped().getID());
                }
                jsonMap.setMap(map);
                jsonMappings.add(jsonMap);
            }

            jsonApp.setInputs(app.getAppDataInputs());
            jsonApp.setOutputs(app.getAppDataOutputs());

            apps.add(jsonApp);
        }

        ret.setApps(apps);

        return ret;
    }

}