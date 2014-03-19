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


public class Input extends IPCType implements Comparable<Input> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3993829649526050021L;
	private String description;
	private String name;
	private String value;
	private String defaultValue;
	private String flag;
	private boolean required;
	private int order;
	private String displayType;
	private String type;
	private String ID;
	private Output mapped;
	private PipeComponent parent;


	public Input(int id, String description, String name, String value, String flag, boolean required, int order, String displayType, String type) {
		this.defaultValue = value;
		this.description = description;
		this.name = name;
		this.flag = flag;
		this.required = required;
		this.order = order;
		this.displayType = displayType;
		this.type = type;
		this.id = id;
	}

	public Input() {
		description = "";
		name = "";
		value = "";
		defaultValue = "";
		flag = "";
		required = true;
		order = 0;
		displayType = "Default";
		type = "";
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

	public String getValue() {
		if (value == null) {
			if(defaultValue==null)
				return "";
			return defaultValue;
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getDisplayType() {
		return displayType;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int compareTo(Input o) {
		return (getOrder() - o.getOrder());
	}
	
	public Input clone(){
		Input in= new Input(this.id,description,name,defaultValue,flag,required,order,displayType,type);
		in.setValue(getValue());
		return in;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public Output getMapped() {
		return mapped;
	}

	public void setMapped(Output mapped) {
		this.mapped = mapped;
	}
	public void setParent(PipeComponent parent){
		this.parent=parent;
	}
	public PipeComponent getParent(){
		return parent;
	}

}
