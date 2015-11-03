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


public class PipeApp extends PipeComponent {

	private static final long serialVersionUID = 1802943036794705136L;
	private int wrapperId;
	private App wrapper;
	private String jobOptions="";

	public PipeApp() {
	}

	public PipeApp(int id, int wrapperId,int position) {
		this.id=id;
		this.wrapperId=wrapperId;
		this.position=position;
	}

	public int getAppId() {
		return wrapperId;
	}

	public void setAppId(int wrapperId) {
		this.wrapperId = wrapperId;
	}

	public App getApp() {
		return wrapper;
	}

	public void setApp(App wrapper) {
		this.wrapper = wrapper;
	}
	
	
	public String getJobOptions() {
		return jobOptions;
	}

	public void setJobOptions(String jobOptions) {
		this.jobOptions = jobOptions;
	}
	
	@Override
	public Vector<Input> getInputs() {
		return wrapper.getInputs();
	}
	
	@Override
	public String getName() {
		return wrapper.getName();
	}

	@Override
	public Vector<Output> getOutputs() {
		return wrapper.getOutputs();
	}


}
