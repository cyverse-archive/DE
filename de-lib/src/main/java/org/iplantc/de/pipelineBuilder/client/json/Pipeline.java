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
package org.iplantc.de.pipelineBuilder.client.json;

import java.util.Vector;


public class Pipeline extends IPCType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4189104767853411003L;

	private Vector<PipeComponent> steps;
    private String workflowId;
	private String name;
	private String description;
	private String creator;
	private boolean isPublic;
	private int rating = 0;
	private boolean stared = false;
	private Vector<Input> inputs;
	private Vector<Output> outputs;

	public Pipeline() {
		steps = new Vector<PipeComponent>();
	}

	/**
	 * @param name
	 *          the name of the pipeline
	 * @param description
	 *          of the pipeline
	 * @param creator
	 *          the id of the creator of the pipeline
	 * @param isPublic
	 *          boolean for if the pipeline is public
	 */
	public Pipeline(String name, String description, boolean isPublic, int id) {
		super();
		this.name = name;
		this.description = description;
		this.isPublic = isPublic;
		this.id = id;
		steps = new Vector<PipeComponent>();
		inputs = new Vector<Input>();
		outputs= new Vector<Output>();
	}

	public Vector<PipeComponent> getSteps() {
		return steps;
	}

	public void setSteps(Vector<PipeComponent> steps) {
		this.steps = steps;
	}

	public void addStep(PipeComponent step) {
		steps.add(step);
	}

	/**
     * @return The Workflow service ID.
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * @param workflowId the Workflow service ID to set.
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	/**
	 * @return
	 */
	public int getRating() {
		return rating;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public boolean isStared() {
		return stared;
	}

	public void setStared(boolean stared) {
		this.stared = stared;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Vector<Input> getInputs() {
		return inputs;
	}

	public void addInput(Input in) {
		inputs.add(in);
	}

	public void setInputs(Vector<Input> data) {
		inputs=data;
	}
	public Vector<Output> getOutputs() {
		return outputs;
	}

	public void addOutput(Output in) {
		outputs.add(in);
	}

	public void setOutputs(Vector<Output> data) {
		outputs=data;
	}
	public void setInput(int inputId, String value) {
		for (Input input : inputs) {
			if (input.getId() == inputId) {
				input.setValue(value);
				return;
			}
		}
	}
	
}
