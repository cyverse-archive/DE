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


public class Output extends IPCType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5186634523380448088L;
	private String value;
	private String type;
	private String description;
	private String name;
	private String ID;
	private String appId;
	private PipeComponent parent;
	public Output(){}
	public Output(String name,String type,String description,String value,int id){
		this.name=name;
		this.type=type;
		this.description=description;
		this.id=id;
		this.value=value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Output clone(){
		Output ret = new  Output();
		ret.setId(id);
		ret.setName(name);
		ret.setValue(value);
		ret.setType(type);
		return ret;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getAppId(){
		return appId;
	}
	
	public void setParent(PipeComponent parent){
		this.parent=parent;
	}
	public PipeComponent getParent(){
		return parent;
	}
	public void setAppId(String appId){
		this.appId=appId;
	}
}
