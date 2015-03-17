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


public class UserPipeline extends IPCType{

	private static final long serialVersionUID = -3017069096125342667L;
	private int parent;
	private String name;
	private boolean isPublic;
	private int pipelineId;
	private String author;
	private String description;
	
	public UserPipeline(){}

	/**
	 * @param parent
	 * @param name
	 * @param isPublic
	 * @param pipelineId
	 * @param author
	 * @param description
	 */
	public UserPipeline(int parent, String name, boolean isPublic, int pipelineId, String author, String description,int id) {
		super();
		this.parent = parent;
		this.name = name;
		this.isPublic = isPublic;
		this.pipelineId = pipelineId;
		this.author = author;
		this.description = description;
		this.id=id;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public int getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(int pipelineId) {
		this.pipelineId = pipelineId;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
