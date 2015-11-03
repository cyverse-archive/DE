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

public class PipelineApp extends PipeComponent {

	private static final long serialVersionUID = -8055577782943179061L;
	private Pipeline pipeline;
	private int pipelineId;

	public PipelineApp() {
	}

	public PipelineApp(int id, int pipelineId, int position) {
		this.id = id;
		this.setPipelineId(pipelineId);
		this.position = position;
	}

	public int getPipelineId() {
		if(pipeline!=null)
			return pipeline.getId();
		return pipelineId;
	}

	public void setPipelineId(int pipelineId) {
		this.pipelineId = pipelineId;
	}

	public Pipeline getPipeline() {
		return pipeline;
	}

	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	@Override
	public Vector<Input> getInputs() {
		return pipeline.getInputs();
	}
	@Override
	public String getName() {
		return pipeline.getName();
	}

	@Override
	public Vector<Output> getOutputs() {
		return pipeline.getOutputs();
	}


}
