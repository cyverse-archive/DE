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

public class UserApp extends IPCType{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3377865527436282526L;
	private String name;
	public void setName(String name) {
		this.name = name;
	}

	private boolean isPublic;
	private String author;
	private int wrapperId;
	private int parent;
	
	protected UserApp(){}

	public UserApp(String name,int wrapperId,String author,boolean isPublic,int parent,int id){
		this.name=name;
		this.wrapperId=wrapperId;
		this.author=author;
		this.isPublic=isPublic;
		this.parent=parent;
		super.id=id;
	}
	public String getName() {
		return name;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getAuthor() {
		return author;
	}

	public int getWrapperId() {
		return wrapperId;
	}

	public int getParent() {
		return parent;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
